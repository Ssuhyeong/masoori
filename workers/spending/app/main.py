import pika
import json
import asyncio
from pydantic import BaseModel
from typing import List
from datetime import datetime

import os
os.environ['OPENAI_API_KEY'] = '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'

from Categories.useFaiss import FaissCategorization

from Description.summarizeSpend import SummarizeSpend
from Description.contentSearch import ContentSearch
from Description.makeCardName import MakeCardName

from StableDiffusionPrompt.promptWriting import PromptWriting
from StableDiffusionPrompt.makePNG import MakePng

# rabbitMQ 변수 정리
rabbit_mq_server_domain_name = "j9b308.p.ssafy.io"
rabbit_mq_server_domain_port = 5672
pub_queue_name = "spending.res"
sub_queue_name = "spending.req"

# RabbitMQ 연결 설정
credentials = pika.PlainCredentials(username="admin", password="masoori")
connection = pika.BlockingConnection(
    pika.ConnectionParameters(
        host=rabbit_mq_server_domain_name,
        credentials=credentials,
        port=rabbit_mq_server_domain_port,
    )
)
channel = connection.channel()

# pub 를 위한 queue 선언
channel.queue_declare(queue=pub_queue_name, durable=True)


class Transaction(BaseModel):
    date: str
    content: str
    amount: int
    dealPlaceName: str


class GeneratedSpending(BaseModel):
    keyword: str
    totalAmount: int
    frequency: int


# reqDto
class SpendingRequestMessage(BaseModel):
    userId: int
    cardId: int
    date : str
    userWeeklyTransactionList: List[Transaction]


# resDto 선언
class GeneratedSpendingCard(BaseModel):
    userId: int
    cardId: int
    name: str
    imagePath: str
    description: str
    date : str
    spendings: List[GeneratedSpending]


# sub 에서 메시지를 받아 처리하는 함수
def callback(ch, method, properties, body):
    try:
        # 이거 쓰면댐 -> Anal
        request_message_dict = json.loads(body)
        # 서비스 로직 실행 -> 결과값 res 객체에 넣고 쏘면 댐.
        userId = request_message_dict['userId']
        cardId = request_message_dict['cardId']
        date = request_message_dict['date']
        spendList = request_message_dict['userWeeklyTransactionList']
        print(f"UserId : {userId}")
        print(f"CardId : {cardId}")
        print(f"Date : {date}")
        print(f"SpendList : {spendList}")

        if userId is None:
            print("User is None")
            ch.basic_ack(delivery_tag=method.delivery_tag)
            return

        if cardId is None:
            print("CardId is None")
            ch.basic_ack(delivery_tag=method.delivery_tag)
            return

        if len(spendList) == 0:
            print("SpendList Length is 0")
            ch.basic_ack(delivery_tag=method.delivery_tag)
            return

        categorization = FaissCategorization(spendList)
        print(f"Categorization : {categorization}")
        description = SummarizeSpend(categorization)
        print(f"Description : {description}")
        summary = ContentSearch(description)
        print(f"Summary : {summary}")
        name = MakeCardName(summary)
        print(f"Name : {name}")
        sorted_result = sorted(categorization, key=lambda x: x['totalAmount'], reverse=True)
        print(f"sorted_result : {sorted_result}")
        keyword = sorted_result[0]['keyword']
        print(f"Keyword : {keyword}")
        promptText = PromptWriting(keyword)
        print(f"PromptText : {promptText}")
        prompt = promptText.replace("프롬프트 : ", "")
        print(f"Prompt : {prompt}")
        time = datetime.now().strftime("%Y%m%d%H%M")
        print(f"Time : {time}")
        imageName = MakePng(str(cardId), time, prompt)
        print(f"ImagePath : {imageName}")

        res = GeneratedSpendingCard(
            userId=userId,
            cardId=cardId,
            name=name,
            imagePath=f"https://sonagi.site/outputs/{imageName}.png",
            description=description,
            date=date,
            spendings=categorization).json()
        print(f"Result : {res}")
        # 메시지 응답 큐 pub
        ch.basic_publish(exchange="", routing_key=pub_queue_name, body=res)

        # 메시지 처리 완료 시 MQ에 처리 했다고 전달하는 함수
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        print(e)


# sub 설정
channel.basic_qos(prefetch_count=1)
channel.basic_consume(
    queue=sub_queue_name, on_message_callback=callback, auto_ack=False
)
channel.start_consuming()
