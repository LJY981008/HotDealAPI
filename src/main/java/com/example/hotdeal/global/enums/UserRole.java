package com.example.hotdeal.global.enums;

public enum UserRole {

	ADMIN, USER;

	public static UserRole of(String role) {
		return EnumValueOf.fromName(UserRole.class, role, Enum::name);
	}

}