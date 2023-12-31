import { postSendSignUpCode } from "@/apis/user/userAPI";
import { useMutation } from "@tanstack/react-query";
import { SendSignUpCodeProps } from "@/types/userType";

const usePostSendSignUpCode = () => {
  return useMutation(
    (SendSignUpCodeData: SendSignUpCodeProps) =>
      postSendSignUpCode(SendSignUpCodeData),
    {
      onSuccess: () => {
        console.log("SendSignUpCode Success");
      },
      onError: (err: Error) => {
        console.log(err);
      },
    }
  );
};

export { usePostSendSignUpCode };
