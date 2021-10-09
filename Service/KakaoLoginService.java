package com.icia.web.service;

import java.net.URI;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("kakaoLoginService")
public class KakaoLoginService 
{
	private static Logger logger = LoggerFactory.getLogger(KakaoLoginService.class);
	
    @Value("#{env['kakao.login.restApi.key']}")
	private String KAKAO_LOGIN_RESTAPI_KEY;

    @Value("#{env['kakao.login.redirect.url']}")
   	private String KAKAO_LOGIN_REDIRECT_URL;

    @Value("#{env['kakao.login.authorize.url']}")
   	private String KAKAO_LOGIN_AUTHORIZE_URL;

    @Value("#{env['kakao.login.token.url']}")
   	private String KAKAO_LOGIN_TOKEN_URL;

    @Value("#{env['kakao.login.login.url']}")
   	private String KAKAO_LOGIN_LOGIN_URL;

    @Value("#{env['kakao.login.logout.url']}")
   	private String KAKAO_LOGIN_LOGOUT_URL;
	
	
	public String AuthorizeUrl() 
	{	
		logger.info("[KakaoLoginAPI] 인증코드 발급완료");
		return KAKAO_LOGIN_AUTHORIZE_URL+"client_id=" + KAKAO_LOGIN_RESTAPI_KEY + "&redirect_uri=" + KAKAO_LOGIN_REDIRECT_URL + "&response_type=code";
	}
	
	public HashMap<String,String> kakaoToken(HashMap<String,String> kakaoInfo) 
	{
		if(kakaoInfo.get("code") != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("grant_type" , "authorization_code");
			bodys.add("client_id" , KAKAO_LOGIN_RESTAPI_KEY);
			bodys.add("redirect_url", KAKAO_LOGIN_REDIRECT_URL);
			bodys.add("code", kakaoInfo.get("code"));
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(KAKAO_LOGIN_TOKEN_URL), request, String.class);

				if(result != null)
	            {					
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					
					kakaoInfo.put("accessToken" , rootNode.get("access_token").asText());
					kakaoInfo.put("refreshToken" ,rootNode.get("refresh_token").asText());
				
					logger.info("[KakaoLoginAPI] 토큰 발급완료 accessToken : " + kakaoInfo.get("accessToken"));
	            	logger.info("[KakaoLoginAPI] 토큰 발급완료 refreshToken : " + kakaoInfo.get("refreshToken"));
	            }
			}
			catch(Exception e)
			{
				logger.info("[KakaoLoginAPI] 토큰 발급오류 : " + e);
			}
		}
		else
		{
			logger.info("[KakaoLoginAPI] 인가코드 필요함");
		}	
		return kakaoInfo;
	}
	
	public HashMap<String,String> kakaoLogin(HashMap<String,String> kakaoInfo) 
	{
		if(kakaoInfo != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + kakaoInfo.get("accessToken"));
			headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);

			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(KAKAO_LOGIN_LOGIN_URL), request, String.class);

				if(result != null)
	            {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					JsonNode propertiesNode = rootNode.path("properties"); 
					
					kakaoInfo.put("id", "KAKAO_" + rootNode.get("id").asText()); 
					kakaoInfo.put("messageId", rootNode.get("id").asText()); 
					kakaoInfo.put("nickname", propertiesNode.get("nickname").asText()); 
								
					logger.info("[KakaoLoginAPI] 사용자정보 발급완료 id : " + kakaoInfo.get("id"));
					logger.info("[KakaoLoginAPI] 사용자정보 발급완료 nickname : " + kakaoInfo.get("nickname"));
	            } 
			}
			catch(Exception e)
			{
				logger.info("[KakaoLoginAPI] 사용자정보 발급오류 : " + e);
			}
		}
		else
		{
			logger.info("[KakaoLoginAPI] 매개변수 필요함");
		}	
		return kakaoInfo;
	}
	
	public int kakaoLogout(HashMap<String,String> kakaoInfo) 
	{	
		int count = 0;
		
		if(kakaoInfo != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + kakaoInfo.get("accessToken"));
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(KAKAO_LOGIN_LOGOUT_URL), request, String.class);
							
				if(result != null)
	            {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					
					kakaoInfo.put("returnId" , rootNode.get("id").asText());
					
					count = 1;
					logger.info("[KakaoLoginAPI] 로그아웃 완료 returnId : " + kakaoInfo.get("returnId"));
	            } 
			}
			catch(Exception e)
			{
				count = 0;
				
				logger.info("[KakaoLoginAPI] 로그아웃 오류 : " + e);
			}
		}
		else
		{
			logger.info("[KakaoLoginAPI] 매개변수 필요함");
		}	
		return count;
	}	
}
