package com.example.hotdeal.global.enums;

import java.util.Arrays;
import java.util.function.Function;

import com.example.hotdeal.global.exception.CustomException;

public class EnumValueOf {

	public static <T extends Enum<T>> T fromName(Class<T> enumClass, String name, Function<T, String> nameExtractor) {
		return Arrays.stream(enumClass.getEnumConstants())
			.filter(e -> nameExtractor.apply(e).equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.ROLE_INVALID_FORMAT));
	}

}