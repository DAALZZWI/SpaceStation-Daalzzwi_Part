package com.icia.web.service;


import java.net.URI;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.common.util.StringUtil;


@Service("googleLoginService")
public class GoogleLoginService 
{
	private static Logger logger = LoggerFactory.getLogger(GoogleLoginService.class);
	
    
    @Value("#{env['google.login.redirect.url']}")
   	private String GOOGLE_LOGIN_REDIRECT_URL;
    
    @Value("#{env['google.login.authorize.url']}")
   	private String GOOGLE_LOGIN_AUTHORIZE_URL;
    
    @Value("#{env['google.login.client.id.key']}")
   	private String GOOGLE_LOGIN_CLIENT_ID_KEY;
    
    @Value("#{env['google.login.client.secret.key']}")
   	private String GOOGLE_LOGIN_CLIENT_SECRET_KEY;
    
    @Value("#{env['google.login.token.url']}")
   	private String GOOGLE_LOGIN_TOKEN_URL;
    
    @Value("#{env['google.login.scope.key']}")
   	private String GOOGLE_LOGIN_SCOPE_KEY;
    
    
    @Value("#{env['google.login.googleApi']}")
    private String GOOGLE_LOGIN_API_URL;
    
    
	public String AuthorizeUrl()
	{
		logger.info("[GoogleLoginAPI] 인증코드 발급완료");
		return GOOGLE_LOGIN_AUTHORIZE_URL + "?scope=" + GOOGLE_LOGIN_SCOPE_KEY + "&access_type=offline&include_granted_scopes=true&response_type=code&redirect_uri=" + GOOGLE_LOGIN_REDIRECT_URL + "&client_id=" + GOOGLE_LOGIN_CLIENT_ID_KEY;
	}
	
	public String googleToken(String code) 
	{
		
		
		if(!StringUtil.isEmpty(code))
		{
			

			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
					
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("code" , code);
			bodys.add("client_id" , GOOGLE_LOGIN_CLIENT_ID_KEY);
			bodys.add("client_secret" , GOOGLE_LOGIN_CLIENT_SECRET_KEY);
			bodys.add("redirect_uri" , GOOGLE_LOGIN_REDIRECT_URL);
			bodys.add("grant_type" , "authorization_code");
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				
				String result = restTemplate.postForObject(new URI(GOOGLE_LOGIN_TOKEN_URL), request, String.class);
				
				if(result != null)	
				{
					ObjectMapper mapper = new ObjectMapper();
					JsonNode rootNode = mapper.readTree(result);
					
					
					code = rootNode.get("access_token").asText();
				}
			}
			catch(Exception e)
			{
				logger.info("[GoogleLoginAPI] getToken Exception: " + e);
			}
		}
		else
		{
			logger.info("[GoogleLoginAPI] ");
		}
		return code;
	}
	
	public HashMap<String, String> googleGetUserInfo(String accessToken) {
		HashMap<String,String> googleUser = new HashMap<String, String>();
		
		
		if(!StringUtil.isEmpty(accessToken)) {
			
			RestTemplate restTemplate = new RestTemplate();
			
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + accessToken);
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try {
				
				ResponseEntity<String> result = restTemplate.exchange(new URI(GOOGLE_LOGIN_API_URL), HttpMethod.GET, request, String.class);

				
				if(!StringUtil.isEmpty(result)) {
					
					ObjectMapper mapper = new ObjectMapper();
					JsonNode responseNode = mapper.readTree(result.getBody().toString());
					
					
						googleUser.put("id",responseNode.get("id").asText());
						googleUser.put("email",responseNode.get("email").asText());
						googleUser.put("verified_email", responseNode.get("verified_email").asText());
						googleUser.put("name", responseNode.get("name").asText()); 
					
				}
				
			} catch (Exception e) {
				logger.info("[GoogleLoginService] join error : " + e);
			}
			
		}
		
		return googleUser;

	}
}
