package com.icia.web.service;

import java.net.URI;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

@Service("kakaoMessageService")
public class KakaoMessageService
{
	private static Logger logger = LoggerFactory.getLogger(KakaoMessageService.class);
	
    @Value("#{env['kakao.message.default.url']}")
   	private String KAKAO_MESSAGE_DEFAULT_URL;
  
	public void kakaoLoginMessage(HashMap<String,String> kakaoInfo) 
	{
		if(kakaoInfo.get("accessToken") != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			String message = kakaomInfoMessage(kakaoInfo.get("nickname").toString());
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			headers.add("Authorization", "Bearer " + kakaoInfo.get("accessToken"));
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("template_object" , message.toString());
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(KAKAO_MESSAGE_DEFAULT_URL), request, String.class);
				
				if(result != null)
	            {					
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					
					kakaoInfo.put("resultCode" , rootNode.get("result_code").asText());
				
					logger.info("[KakaoMessage] 메세지 전송완료  : " + kakaoInfo.get("result_code"));
	            }
			}
			catch(Exception e)
			{
				logger.info("[KakaoMessage] 메세지 전송오류 : " + e);
			}
		}
		else
		{
			logger.info("[KakaoMessage] 토큰이 필요함");
		}	
		
	}	
	public String kakaomInfoMessage(String nickname)
	{
		JsonObject message = new JsonObject();
		JsonObject link = new JsonObject();
		
		message.addProperty("object_type", "text");
		message.addProperty("text", "[우주정거장 자동로그인]\n\n" + nickname + "님이 '우주정거장'에 접속하셨어요!");
		message.addProperty("button_title","기기 연결 관리");
		link.addProperty("web_url", "https://accounts.kakao.com");
		link.addProperty("mobile_web_url","https://accounts.kakao.com");
		
		message.add("link", link.getAsJsonObject());
		String data = message.toString();
		
		return data;
	}
}
