package com.icia.web.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.json.JsonObject;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.web.dao.IdentifyDao;
import com.icia.web.model.Coolsms;
import com.icia.web.model.IdentifyCell;

@Service("messageService")
public class MessageService 
{
	private static Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	private IdentifyDao identifyDao;
	
	@Value("#{env['coolsms.api.id.key']}")
   	private String COOLSMS_API_ID_KEY;
	
	@Value("#{env['coolsms.api.secret.key']}")
   	private String COOLSMS_API_SECRET_KEY;
	
	@Value("#{env['naver.shortner.id.key']}")
   	private String NAVER_SHORTNER_ID_KEY;
	
	@Value("#{env['naver.shortner.secret.key']}")
   	private String NAVER_SHORTNER_SECRET_KEY;
	
	public int messageRandomCode()
	{
		Random rand  = new Random();
        String numStr = "";
        for(int i=0; i<4; i++) 
        {
            String ran = Integer.toString(rand.nextInt(10));
            numStr+=ran;
        }
		System.out.println(numStr);
		return Integer.parseInt(numStr);
	}
	
	public int messageSender(String cell , int num , String method) 
	{
		System.out.println("경로 : " + method);
        System.out.println("수신자 번호 : " + cell);
        System.out.println("인증번호 : " + num);
        int success = 0;
        String url = shortner(cell);
        
        Coolsms coolsms = new Coolsms(COOLSMS_API_ID_KEY, COOLSMS_API_SECRET_KEY);
        
		if(cell != null)
		{
	        HashMap<String, String> set = new HashMap<String, String>();
	       
	        if(method.equals("formMessage"))
	        {
	            set.put("to", cell);    // 수신전화번호
		        set.put("from", cell);    // 발신전화번호
		        set.put("type", "SMS");
	        	set.put("text", "[우주정거장] 인증코드 " + "[" + num + "]" + " 를 입력해주세요 ");
	        }
	        if(method.equals("qrMessage"))
	        {
	        	set.put("to", cell);    // 수신전화번호
		        set.put("from", cell);    // 발신전화번호
		        set.put("type", "SMS");
	        	set.put("text", "[우주정거장] qr코드 로그인 허가해주세요  " + url);
	        }	
	        
	        JSONObject result = coolsms.send(set); // 보내기&전송결과받기
	        if ((Boolean) result.get("status") == true) 
	        {
	            // 메시지 보내기 성공 및 전송결과 출력
	            System.out.println("성공");            
	            System.out.println(result.get("group_id")); // 그룹아이디
	            System.out.println(result.get("result_code")); // 결과코드
	            System.out.println(result.get("result_message"));  // 결과 메시지
	            System.out.println(result.get("success_count")); // 메시지아이디
	            System.out.println(result.get("error_count"));  // 여러개 보낼시 오류난 메시지 수
	            success = 1;
	        } else 
	        {
	            // 메시지 보내기 실패
	            System.out.println("실패");
	            System.out.println(result.get("code")); // REST API 에러코드
	            System.out.println(result.get("message")); // 에러메시지
	            success = 0;
	        }        
		}
		else
		{
			logger.info("[MessageService] 매개변수 필요함");
			success = 0;
		}
	
		return success;
	}
	
	public IdentifyCell cellSelect(String cell)
	{
		IdentifyCell ic = null;
		
		try
		{
			ic = identifyDao.cellSelect(cell);
		}
		catch(Exception e)
		{
			logger.error("[MessageService] cellSelect Exception", e);
		}
		
		return ic;
	}
	
	public int cellInsert(IdentifyCell ic)
	{
		int count = 0;
		
		try
		{
			count = identifyDao.cellInsert(ic);
		}
		catch(Exception e)
		{
			logger.error("[MessageService] cellInsert Exception", e);
		}
		
		return count;
	}
	
	public int cellDelete(String cell)
	{
		int count = 0;
		
		try
		{
			count = identifyDao.cellDelete(cell);
		}
		catch(Exception e)
		{
			logger.error("[MessageService] cellSelect Exception", e);
		}
		
		return count;
	}
	
	public String shortner(String cell)
	{
		

        String originalURL = "https://firebasestorage.googleapis.com/v0/b/spacestation-eff5b.appspot.com/o/register.html?alt=media&token=20ca5657-3f84-487c-8fbc-7f2f3e30a0fb&number=" + cell;
        String apiURL = "https://openapi.naver.com/v1/util/shorturl";
        
        String returnvalue = "";
        
        RestTemplate restTemplate = new RestTemplate();
		
		//header
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Naver-Client-Id", NAVER_SHORTNER_ID_KEY);
		headers.add("X-Naver-Client-Secret", NAVER_SHORTNER_SECRET_KEY);
		headers.add("Content-Type" , "application/x-www-form-urlencoded; charset=UTF-8");
		//body
		MultiValueMap<String, String> bodys = new LinkedMultiValueMap<String,String>();
		bodys.add("url" , originalURL);
		
		//join
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(bodys , headers);
		
		try
		{
			//send&take
			//ResponseEntity<String> result = restTemplate.exchange(new URI(apiURL), HttpMethod.GET, request, String.class);

			String result = restTemplate.postForObject(new URI(apiURL), request, String.class);
			System.out.println(result);
			if(result != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(result);
				JsonNode responseNode = rootNode.path("result");
				
				returnvalue = responseNode.get("url").asText();
			
	
				logger.info("[MessageService] 단축키 발급완료: " + returnvalue);
			
			}
		}
		catch(Exception e)
		{
			logger.info("[MessageService] 단축키 발급실패: " + e);
		}
        return returnvalue;
	}
	
	
	public int cellUpdate(IdentifyCell ic) {
		
		int count = 0;
		
		try {
			count = identifyDao.cellUpdate(ic);
			
		} catch (Exception e) {
			
			logger.error("[MessageService] cellUpdate Exception", e);
		}
		
		return count;
	}
	
}
