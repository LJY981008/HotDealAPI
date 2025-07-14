package com.example.hotdeal.global.constant;

public class TokenInfo {

	public static long accessTokenExpireTime = 1000 * 60 * 30; //엑세스 토큰 만료시간 - 30분
	public static long refreshTokenExpireTime = 1000 * 60 * 60 * 24; //리프레시 토큰 만료시간 - 1일
	public static String tokenPrefix = "Bearer ";

}