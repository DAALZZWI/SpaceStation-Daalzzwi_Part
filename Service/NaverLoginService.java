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

@Service("naverLoginServices")
public class NaverLoginService
{
	private static Logger logger = LoggerFactory.getLogger(NaverLoginService.class);

    @Value("#{env['naver.login.client.id.key']}")
   	private String NAVER_LOGIN_CLIENT_ID_KEY;

    @Value("#{env['naver.login.client.secret.key']}")
   	private String NAVER_LOGIN_CLIENT_SECRET_KEY;

    @Value("#{env['naver.login.redirect.url']}")
   	private String NAVER_LOGIN_REDIRECT_URL;

    @Value("#{env['naver.login.authorize.url']}")
   	private String NAVER_LOGIN_AUTHORIZE_URL;

    @Value("#{env['naver.login.token.url']}")
   	private String NAVER_LOGIN_TOKEN_URL;

    @Value("#{env['naver.login.login.url']}")
   	private String NAVER_LOGIN_LOGIN_URL;

	public String AuthorizeUrl()
	{
		logger.info("[NaverLoginAPI] 인증코드 발급완료");
		return NAVER_LOGIN_AUTHORIZE_URL + "client_id=" + NAVER_LOGIN_CLIENT_ID_KEY + "&redirect_uri=" + NAVER_LOGIN_REDIRECT_URL + "&response_type=code&state=0";
	}
	
	public HashMap<String,String> naverToken(HashMap<String,String> naverInfo) 
	{
		if(naverInfo.get("code") != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("grant_type" , "authorization_code");
			bodys.add("client_id" , NAVER_LOGIN_CLIENT_ID_KEY);
			bodys.add("client_secret" , NAVER_LOGIN_CLIENT_SECRET_KEY);
			bodys.add("redirect_url", NAVER_LOGIN_REDIRECT_URL);
			bodys.add("code", naverInfo.get("code"));
			bodys.add("state", "0");
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(NAVER_LOGIN_TOKEN_URL), request, String.class);
				
				if(result != null)
				{
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);

					naverInfo.put("accessToken" , rootNode.get("access_token").asText());
					naverInfo.put("refreshToken" , rootNode.get("refresh_token").asText());
					
					logger.info("[NaverLoginAPI] 토큰 발급완료 accessToken : " + naverInfo.get("accessToken"));
					logger.info("[NaverLoginAPI] 토큰 발급완료 refreshToken : " + naverInfo.get("refreshToken"));
				}
			}
			catch(Exception e)
			{
				logger.info("[NaverLoginAPI] 토큰 발급오류 : " +  e);
			}
		}
		else
		{
			logger.info("[NaverLoginAPI] 인가코드 필요함");
		}
		return naverInfo;
	}
	
	public HashMap<String,String> naverLogin(HashMap<String,String> naverInfo) 
	{
		if(naverInfo != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + naverInfo.get("accessToken"));
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);

			try 
			{
				//send&take
				String result = restTemplate.postForObject(new URI(NAVER_LOGIN_LOGIN_URL), request, String.class);
				
				System.out.println(result);
				if(result != null)
				{
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					JsonNode responseNode = rootNode.path("response");
					
					naverInfo.put("id" , "NAVER_" + responseNode.get("id").asText());
					naverInfo.put("nickname" , responseNode.get("name").asText());
					
					logger.info("[NaverLoginAPI] 사용자정보 발급완료 id : " + naverInfo.get("id"));
					logger.info("[NaverLoginAPI] 사용자정보 발급완료 nickname : " + naverInfo.get("nickname"));
				}
			} 
			catch(Exception e) 
			{
				logger.info("[NaverLoginAPI] 사용자정보 발급오류 : " + e);
			}
		}
		else
		{
			logger.info("[NaverLoginAPI] 매개변수 필요함");
		}
		return naverInfo;
	}
	
	public int naverLogout(HashMap<String,String> naverInfo) 
	{	
		int count = 0;
		if(naverInfo != null)
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("grant_type" , "delete");
			bodys.add("client_id" , NAVER_LOGIN_CLIENT_ID_KEY);
			bodys.add("client_secret" , NAVER_LOGIN_CLIENT_SECRET_KEY);
			bodys.add("access_token", naverInfo.get("accessToken"));
			bodys.add("service_provider", "NAVER");
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(NAVER_LOGIN_TOKEN_URL), request, String.class);
				
				if(result != null)
				{	
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
	
					naverInfo.put("accessToken" , rootNode.get("access_token").asText());
					naverInfo.put("result" , rootNode.get("result").asText());
				
					count = 1;
					
					logger.info("[NaverLoginAPI] 로그아웃 완료 accessToken : " + naverInfo.get("accessToken"));
					logger.info("[NaverLoginAPI] 로그아웃 완료 reult : " + naverInfo.get("result"));
				}	
			}
			catch(Exception e)
			{
				count = 0;
				logger.info("[NaverLoginAPI] 로그아웃 오류 : " + e);
			}
		}
		else
		{
			logger.info("[naverLoginAPI] 매개변수 필요함");
		}
		return count;
	}
}
