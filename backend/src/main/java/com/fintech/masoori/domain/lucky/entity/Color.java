package com.fintech.masoori.domain.lucky.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "color")
@ToString(of = {"id", "color", "colorName", "description", "imagePath"})
public class Color {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "color_id")
	private Long id;

	@Column(name = "color")
	private String color;

	@Column(name = "color_name")
	private String colorName;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_path")
	private String imagePath;
}
