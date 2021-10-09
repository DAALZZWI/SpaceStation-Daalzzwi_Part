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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


@Service("reCaptchaService")
public class ReCaptchaService 
{
	private static Logger logger = LoggerFactory.getLogger(ReCaptchaService.class);
	
	//리캡처 서버 시크릿 키
    @Value("#{env['recaptcha.server.secret.key']}")
   	private String RECAPTCHA_SERVER_SECRET_KEY;
    
	//리캡처 클라이언트 시크릿 키
    @Value("#{env['recaptcha.client.secret.key']}")
   	private String RECAPTCHA_CLIENT_SECRET_KEY;
    
    //리캡처 주소
    @Value("#{env['recaptcha.server.connect.url']}")
   	private String RECAPTCHA_SERVER_CONNECT_URL;
   
    //리캡처 클라이언트  키
    @Value("#{env['recaptcha.server.agent.key']}")
   	private String RECAPTCHA_SERVER_AGENT_KEY;
    
    public String AuthorizeUrl()
	{
		logger.info("[ReCaptchaAPI] 클라이언트 시크릿 키 발급완료");
		return RECAPTCHA_CLIENT_SECRET_KEY;
	}
    
    public HashMap<String,String> recaptchaConnect(HashMap<String,String> recaptchaInfo)
    {
		if(recaptchaInfo.get("code") == null || "".equals(recaptchaInfo.get("code")))
		{
			return recaptchaInfo;
		}
		else
		{
			RestTemplate restTemplate = new RestTemplate();
			
			//header
			HttpHeaders headers = new HttpHeaders();
			headers.add("User-Agent" , RECAPTCHA_SERVER_AGENT_KEY);
			headers.add("Accept-Language" , "en-US,en;q=0.5");
			
			//body
			MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
			bodys.add("secret" , RECAPTCHA_SERVER_SECRET_KEY);
			bodys.add("response" , recaptchaInfo.get("code"));
			
			//join
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
			
			try
			{
				//send&take
				String result = restTemplate.postForObject(new URI(RECAPTCHA_SERVER_CONNECT_URL), request, String.class);
			
				if(result != null)
				{
					JsonElement response = new JsonParser().parse(result);
					
					recaptchaInfo.put("success",response.getAsJsonObject().get("success").getAsString());
					recaptchaInfo.put("challengeTs",response.getAsJsonObject().get("challenge_ts").getAsString()); 
					recaptchaInfo.put("hostName",response.getAsJsonObject().get("hostname").getAsString());			
					
					logger.info("[ReCaptchaAPI] 리캡처 발급완료 success : " + recaptchaInfo.get("success"));
					logger.info("[ReCaptchaAPI] 리캡처 발급완료 challengeTs : " + recaptchaInfo.get("challengeTs") );
					logger.info("[ReCaptchaAPI] 리캡처 발급완료 hostName : " +  recaptchaInfo.get("hostName"));
				}
			}
			catch (Exception e) 
			{
				logger.info("[ReCaptchaAPI] 토큰 발급오류 : " +  e);
			}
			return recaptchaInfo;
		}
    }  
}
