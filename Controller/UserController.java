package com.icia.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icia.common.util.StringUtil;
import com.icia.web.model.IdentifyCell;
import com.icia.web.model.Response;
import com.icia.web.model.User;
import com.icia.web.service.EmailService;
import com.icia.web.service.GoogleLoginService;
import com.icia.web.service.KakaoLoginService;
import com.icia.web.service.KakaoMessageService;
import com.icia.web.service.MessageService;
import com.icia.web.service.NaverLoginService;
import com.icia.web.service.ReCaptchaService;
import com.icia.web.service.UserService;
import com.icia.web.util.CookieUtil;
import com.icia.web.util.HttpUtil;
import com.icia.web.util.JsonUtil;

@Controller("userController")
public class UserController 
{
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	private static String sender = null;
	
	//쿠키명
	@Value("#{env['auth.cookie.name']}")
	private String AUTH_COOKIE_NAME;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private KakaoLoginService kakaoLoginService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private NaverLoginService naverLoginService;
	
	@Autowired
	private ReCaptchaService reCaptchaService;
	
	@Autowired
	private GoogleLoginService googleLoginService;

	@Autowired
	private KakaoMessageService kakaoMessageService;
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping(value="/user/login")
	public String login(HttpServletRequest request, HttpServletResponse response) 
	{
		String cookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
		
		if(!StringUtil.isEmpty(cookieUserId))
		{
			return "redirect:/";
		}
		return "/user/login";
	}
	
	@RequestMapping(value="/user/loginProc", method=RequestMethod.POST)
	@ResponseBody
	public Response<Object> loginProc(HttpServletRequest request, HttpServletResponse response) 
	{
		String userId = HttpUtil.get(request, "userId");
		String userPwd = HttpUtil.get(request, "userPwd");
		Response<Object> ajaxResponse = new Response<Object>();
		
		if(!StringUtil.isEmpty(userId) && !StringUtil.isEmpty(userPwd))
		{
			User user = userService.userSelect(userId);
			
			if(user != null) 
			{
				if(StringUtil.equals(user.getUserPwd(), userPwd)) 
				{
					CookieUtil.addCookie(response,"/", -1, AUTH_COOKIE_NAME, CookieUtil.stringToHex(userId));
					ajaxResponse.setResponse(0, "Success");
					
				} 
				else 
				{
					ajaxResponse.setResponse(-1, "Passwords don't match");
				}
			} 
			else 
			{	
				ajaxResponse.setResponse(404,"Not Found");
			}
		} 
		else 
		{
			ajaxResponse.setResponse(400,"Bad Request");
		}
		logger.debug("[UserController] /user/login response\n" + JsonUtil.toJsonPretty(ajaxResponse));
		return ajaxResponse;
	}

	@RequestMapping(value="/user/regForm", method=RequestMethod.POST)
	public String regForm(Model model,HttpServletRequest request, HttpServletResponse response) 
	{
		String userId = HttpUtil.get(request, "userId");
		String userName = HttpUtil.get(request, "userName");
		String userEmail = HttpUtil.get(request, "userEmail");
		String userPwd = HttpUtil.get(request, "userPwd");
		String CookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
		
		if(!StringUtil.isEmpty(CookieUserId)) 
		{
			CookieUtil.deleteCookie(request, response, AUTH_COOKIE_NAME);
			return "redirect:/";
		}
		model.addAttribute("userId",userId);
		model.addAttribute("userName",userName);
		model.addAttribute("userEmail",userEmail);
		model.addAttribute("userPwd",userPwd);
		return "/user/regForm";
	}
	
	@RequestMapping(value="/user/idCheck", method=RequestMethod.POST)
	@ResponseBody
	public Response<Object> idCheck(HttpServletRequest request, HttpServletResponse response)
	{
		String userId = HttpUtil.get(request,"userId");
		Response<Object> ajaxResponse = new Response<Object>();
		
		if(!StringUtil.isEmpty(userId))
		{
			if(userService.userSelect(userId) == null)
			{
				ajaxResponse.setResponse(0, "Success");
			}
			else
			{
				ajaxResponse.setResponse(100,"Duplicate ID");
			}
		}
		else
		{
			ajaxResponse.setResponse(400, "Bad Request");
		}
		logger.debug("[UserController] /user/idCheck response\n" + JsonUtil.toJsonPretty(ajaxResponse));
		return ajaxResponse;
	}
	
	@RequestMapping(value="/user/regProc", method=RequestMethod.POST)
	@ResponseBody
	public Response<Object> regProc(HttpServletRequest request,HttpServletResponse response) 
	{
		Response<Object> ajaxResponse = new Response<Object>();
		String userId = HttpUtil.get(request, "userId");
		String userPwd = HttpUtil.get(request, "userPwd");
		String userEmail = HttpUtil.get(request, "userEmail");
		String userName = HttpUtil.get(request, "userName");
		String userCell = HttpUtil.get(request, "userCell");
		
		if(!StringUtil.isEmpty(userId) && !StringUtil.isEmpty(userPwd) && !StringUtil.isEmpty(userEmail) && !StringUtil.isEmpty(userName))
		{	
			if(userService.userSelect(userId) == null) 
			{
				User user = new User();
				
				user.setUserId(userId);
				user.setUserPwd(userPwd);
				user.setUserEmail(userEmail);
				user.setUserName(userName);
				user.setUserCell(userCell);
				user.setStatus("Y");
				
				if(userService.userInsert(user) > 0)
				{
					ajaxResponse.setResponse(0, "Success");
				}
				else 
				{
					ajaxResponse.setResponse(500, "Internal Server ERROR");
				}
			} 
			else 
			{
				ajaxResponse.setResponse(100, "Duplicate ID");
			}
		} 
		else 
		{
			ajaxResponse.setResponse(400, "Bad Response");
		}
		logger.debug("[UserController] /user/regProc response \n" + JsonUtil.toJsonPretty(ajaxResponse));
		return ajaxResponse;
	}
	
	@RequestMapping(value="/user/loginOut", method=RequestMethod.GET)
	public String loginOut(HttpServletRequest request, HttpServletResponse response)
	{
		String CookieUserId = CookieUtil.getHexValue(request, AUTH_COOKIE_NAME);
		
		if(CookieUtil.getCookie(request, AUTH_COOKIE_NAME) != null) 
		{
			CookieUtil.deleteCookie(request, response,"/", AUTH_COOKIE_NAME);
			

			if(CookieUserId.contains("KAKAO"))
			{
				User user = userService.userSelect(CookieUserId);
		
				if(user != null) 
				{
					HashMap<String,String> kakaoInfo = new HashMap<String,String>();
					String tc = user.getUserPwd();
					int index = tc.indexOf("#");
					
					kakaoInfo.put("accessToken" , tc.substring(0,index)); 
					kakaoInfo.put("code", tc.substring(index+1,tc.length()));
					
					if(kakaoLoginService.kakaoLogout(kakaoInfo) > 0)
					{
						return "redirect:/";
					}
					else
					{
						kakaoInfo = kakaoLoginService.kakaoToken(kakaoInfo);
						if(kakaoLoginService.kakaoLogout(kakaoInfo) > 0)
						{
							logger.debug("[UserController] [/user/logoutProc] 카카오 로그아웃 성공함");
							return "redirect:/";
						}	
						else
						{
							logger.debug("[UserController] [/user/logoutProc] 카카오 로그아웃 실패함");
							return "redirect:/";
						}	
					}
				}
				return "redirect:/";
			}
		
			
			if(CookieUserId.contains("NAVER"))
			{
				logger.debug("[UserController] [/user/logoutProc] 네이버 사용자 계정 확인함");
				User user = userService.userSelect(CookieUserId);

				if(user != null) 
				{
					logger.debug("[UserController] [/user/logoutProc] 네이버 사용자 계정 가져옴");
					HashMap<String,String> naverInfo = new HashMap<String,String>();
					String tc = user.getUserPwd();
					int index = tc.indexOf("#");
					
					naverInfo.put("accessToken", tc.substring(0,index));
					naverInfo.put("code", tc.substring(index+1,tc.length()));
					
					if(naverLoginService.naverLogout(naverInfo) > 0)
					{
						logger.debug("[UserController] [/user/logoutProc] 네이버 로그아웃 성공함");
						return "redirect:/";
					}
					else
					{
						naverInfo = naverLoginService.naverToken(naverInfo);
						if(naverLoginService.naverLogout(naverInfo) > 0)
						{
							logger.debug("[UserController] [/user/logoutProc] 네이버 로그아웃 성공함");
							return "redirect:/";
						}	
						else
						{
							logger.debug("[UserController] [/user/logoutProc] 네이버 로그아웃 실패함");
							return "redirect:/";
						}	
					}
				}
				return "redirect:/";
			}
			
		}
		logger.debug("[UserController] [/user/logoutProc] 쿠키 값이 없음");
		return "redirect:/";
	}
	
	@RequestMapping(value="/user/kakao_authorizeProc",method=RequestMethod.POST)
	@ResponseBody
	public String kakao_authorizeProc(HttpServletRequest reqeust, HttpServletResponse response) 
	{
		return kakaoLoginService.AuthorizeUrl();
	}
	
	@RequestMapping(value="/user/kakaoAuth",method=RequestMethod.GET)
	public String kakaoAuth(@RequestParam Map<String,String> param,HttpServletRequest request, HttpServletResponse response)
	{
		sender = param.get("code");
		return "redirect:/user/kakao_loginProc";
	}
	
	@RequestMapping(value="/user/kakao_loginProc",method=RequestMethod.GET)
	public String kakao_loginProc(@RequestParam Map<String,String> param,Model model,HttpServletRequest request, HttpServletResponse response) 
	{
		param.put("code", sender);
		if(param.get("code") != null)
		{
			HashMap<String,String> kakaoInfo = new HashMap<String,String>();
			User user = new User();
		
			kakaoInfo.put("code", param.get("code"));
			logger.debug("[UserController] [/user/kakao_loginProc] 카카오 토큰 발급함");
			kakaoInfo = kakaoLoginService.kakaoToken(kakaoInfo);
			
			logger.debug("[UserController] [/user/kakao_loginProc] 카카오 사용자계정 발급함");
			kakaoInfo = kakaoLoginService.kakaoLogin(kakaoInfo); 
			
			if(kakaoInfo.get("id") != null && kakaoInfo.get("accessToken") != null)
			{
				logger.debug("[UserController] [/user/kakao_loginProc] 카카오 사용자계정 확인함");
				user = userService.userSelect(kakaoInfo.get("id"));
				
				if(user != null)
				{	
					user.setUserPwd(kakaoInfo.get("accessToken") + "#" + kakaoInfo.get("code"));
					if(userService.userUpdate(user) > 0) 
					{
						logger.debug("[UserController] [/user/kakao_loginProc] 카카오 로그인 성공함");
						model.addAttribute("user",user);
						CookieUtil.addCookie(response, "/", -1, AUTH_COOKIE_NAME,CookieUtil.stringToHex(kakaoInfo.get("id")));
						kakaoMessageService.kakaoLoginMessage(kakaoInfo);
					} 
					else 
					{
						logger.debug("[UserController] [/user/kakao_loginProc] 카카오 로그인 실패함");
					}		
				}
				else
				{				
					logger.debug("[UserController] [/user/kakao_loginProc] 회원가입 페이지 이동함");
					user =new User();
					user.setUserId(kakaoInfo.get("id"));
					user.setUserPwd(kakaoInfo.get("accessToken") + "#" + kakaoInfo.get("code"));
					user.setUserName(kakaoInfo.get("nickname"));
					
					model.addAttribute("user",user);
		
					return "/user/login";
				}	
			}
			else
			{
				logger.debug("[UserController] [/user/kakao_loginProc] 카카오 사용자계정 미 확인함");
				return "redirect:/";
			}	
		}
		else
		{
			logger.debug("[UserController] [/user/kakao_tokenProc] code가 넘어오지 않음");
		}
		logger.debug("[UserController] [/user/kakao_loginProc] login.jsp 이동함");
		return "redirect:/";
	}
	
	@RequestMapping(value="/user/findId")
	public String findId(HttpServletRequest request, HttpServletResponse reponse) 
	{
		return "/user/findId";
	}
	
	@RequestMapping(value="/user/findIdProc")
	@ResponseBody
	public Response<Object> findIdProc(HttpServletRequest request, HttpServletResponse rseponse) 
	{
		Response<Object> ajaxResponse = new Response<Object>();
		
		String userEmail = HttpUtil.get(request, "userEmail");
		User user = null;
		
		if(!StringUtil.isEmpty(userEmail)) 
		{
			user = userService.userSelectByEmail(userEmail);
			
			if(user != null) 
			{	
				ajaxResponse.setResponse(0,"success",user.getUserId());		
			} 
			else 
			{
				ajaxResponse.setResponse(401, "unidentified userEmail");
			}
		}
		else 
		{
			ajaxResponse.setResponse(400, "bad parameters");
		}
		return ajaxResponse;
	}
	
	@RequestMapping(value="/user/findPwd")
	public String findPwd(HttpServletRequest request, HttpServletResponse reponse) 
	{
		return "/user/findPwd";
	}
	
	@RequestMapping(value="/user/pwdFindProc")
	@ResponseBody
	public Response<Object> pwdFindProc(HttpServletRequest request, HttpServletResponse response) 
	{
		Response<Object> ajaxResponse = new Response<Object>();
		String userId = HttpUtil.get(request, "userId");
	
		System.out.println("userId :: "+userId);
		User user = null;
		
		if(!StringUtil.isEmpty(userId)) 
		{
			user = new User();
			user.setUserId(userId);
			user = userService.userSelect(userId);
			
			if(user != null) 
			{
				String randomPwd = "";
				
				for(int i = 0; i < 10; i++) 
				{
					randomPwd += (char) ((Math.random() * 26) + 97);
				}
				
				user.setUserPwd(randomPwd);
			
				if(emailService.UserPwdEmail(user)) 
				{
					if(userService.userUpdate(user) > 0) 
					{
						ajaxResponse.setResponse(0, "success");	
					} 
					else 
					{
						ajaxResponse.setResponse(500, "sql error");
					}
				} 
				else 
				{
					ajaxResponse.setResponse(403,"email error");
				}	
			} 
			else 
			{
				ajaxResponse.setResponse(402, "bad id");
			}
		} 
		else 
		{
			ajaxResponse.setResponse(400, "Bad parameters");
		}
		return ajaxResponse;	
	}
	
	@RequestMapping(value="/user/naver_authorizeProc",method=RequestMethod.POST)
	@ResponseBody
	public String naver_authorizeProc(HttpServletRequest request, HttpServletResponse response)
	{
		return naverLoginService.AuthorizeUrl();
	}
	
	
	@RequestMapping(value="/user/naver_loginProc",method=RequestMethod.GET)
	public String naver_loginProc(@RequestParam Map<String,String> param, Model model,HttpServletRequest request,HttpServletResponse response)
	{
		HashMap<String,String> naverInfo = new HashMap<String,String>();
		User user = new User();
		
		if(param.get("code") != null)
		{	
			naverInfo.put("code", param.get("code"));
			logger.debug("[UserController] [/user/naver_loginProc] 네이버 토큰 발급함");
			naverInfo = naverLoginService.naverToken(naverInfo);
			
			logger.debug("[UserController] [/user/naver_loginProc] 네이버 사용자계정 발급함");
			naverInfo = naverLoginService.naverLogin(naverInfo);
			
			if(naverInfo.get("id") != null && naverInfo.get("accessToken") != null)
			{
				logger.debug("[UserController] [/user/naver_loginProc] 네이버 사용자계정 확인함");
				user = userService.userSelect(naverInfo.get("id"));
				
				if(user != null)
				{	
					user.setUserPwd(naverInfo.get("accessToken") + "#" + naverInfo.get("code"));
					if(userService.userUpdate(user) > 0) 
					{
						logger.debug("[UserController] [/user/naver_loginProc] 네이버 로그인 성공함");
						model.addAttribute("user",user);
						CookieUtil.addCookie(response, "/", -1, AUTH_COOKIE_NAME,CookieUtil.stringToHex(naverInfo.get("id")));
					} 
					else 
					{
						logger.debug("[UserController] [/user/naver_loginProc] 네이버 로그인 실패함");
					}		
				}
				else
				{				
					logger.debug("[UserController] [/user/naver_loginProc] 회원가입 페이지 이동함");
					user =new User();
					user.setUserId(naverInfo.get("id"));
					user.setUserPwd(naverInfo.get("accessToken") + "#" + naverInfo.get("code"));
					user.setUserName(naverInfo.get("nickname"));
					
					model.addAttribute("user",user);
		
					return "/user/login";
				}	
			}
			else
			{
				logger.debug("[UserController] [/user/naver_loginProc] 네이버 사용자계정 미 확인함");
				return "redirect:/";
			}	
		}
		else
		{
			logger.debug("[UserController] [/user/naver_tokenProc] code가 넘어오지 않음");
		}
		logger.debug("[UserController] [/user/naver_tokenProc] login.jsp 이동함");
		return "redirect:/";
	}

	@RequestMapping(value="/user/recaptcha_clientKey",method=RequestMethod.POST)
	@ResponseBody
	public String recaptcha_clientKey(HttpServletRequest request, HttpServletResponse response)
	{
		return reCaptchaService.AuthorizeUrl();
	}
	
	@RequestMapping(value="/user/recaptchaProc",method=RequestMethod.POST)
	@ResponseBody
	public String recaptchaProc(HttpServletRequest request,HttpServletResponse response)
	{
		HashMap<String,String> recaptchaInfo = new HashMap<String,String>();
		
		recaptchaInfo.put("code", HttpUtil.get(request, "recaptcha"));
		
		recaptchaInfo = reCaptchaService.recaptchaConnect(recaptchaInfo);
		logger.info("[reCaptchaService] 리캡처 발급완료 success : " + recaptchaInfo.get("success"));
		logger.info("[reCaptchaService] 리캡처 발급완료 challengeTs : " + recaptchaInfo.get("challengeTs") );
		logger.info("[reCaptchaService] 리캡처 발급완료 hostName : " +  recaptchaInfo.get("hostName"));
		
		if(recaptchaInfo.get("success") == "true")	
		{
			logger.debug("[UserController] [/user/recaptchaProc] 리캡처 성공");
			return "success";
		}
		else
		{
			logger.debug("[UserController] [/user/recaptchaProc] 리캡처 실패");
			return "fail";
		}
	}
	
	@RequestMapping(value="/user/google_auth",method=RequestMethod.POST)
	@ResponseBody
	public String google_auth(HttpServletRequest request, HttpServletResponse response)
	{
		return  googleLoginService.AuthorizeUrl();
	}
	
	@RequestMapping(value="/user/google_login")
	public String google_login(Model model,HttpServletRequest request, HttpServletResponse response) 
	{
		String code = HttpUtil.get(request, "code");
		HashMap<String,String> googleInfo = new HashMap<String, String>(); 
		String accessToken = "";
		
		User user = null;
		
		accessToken = googleLoginService.googleToken(code);
		
		if(!StringUtil.isEmpty(accessToken)) 
		{
			googleInfo = googleLoginService.googleGetUserInfo(accessToken);
			
			if(googleInfo != null)
			{
				user = userService.userSelect("GOOGLE_"+googleInfo.get("id"));
				
				if(user != null) 
				{
					user.setUserPwd(accessToken);
					
					if(userService.userUpdate(user) > 0) 
					{
						CookieUtil.addCookie(response, "/", -1, AUTH_COOKIE_NAME,CookieUtil.stringToHex(user.getUserId()));
					}		
				} 
				else 
				{
					user = new User();
					user.setUserId("GOOGLE_" + googleInfo.get("id"));
					user.setUserPwd(accessToken);
					user.setUserName(googleInfo.get("name"));
					
					if(StringUtil.equals(googleInfo.get("verified_email"),"true")) {
						user.setUserEmail(googleInfo.get("email"));
					}
					
					System.out.println("email :: " + googleInfo.toString());
					
					model.addAttribute("user",user);
					return "/user/login";
				}	
			}
		}
		return "redirect:/";
	}
	
	@RequestMapping(value="/user/nameCheck", method=RequestMethod.POST)
	@ResponseBody
	public Response<Object> nameCheck(HttpServletRequest request, HttpServletResponse response)
	{
		String userName = HttpUtil.get(request,"userName");
		Response<Object> ajaxResponse = new Response<Object>();
		
		if(!StringUtil.isEmpty(userName))
		{
			if(userService.nameCheck(userName) == null)
			{
				ajaxResponse.setResponse(0, "Success");
			}
			else
			{
				ajaxResponse.setResponse(100,"Duplicate ID");
			}
		}
		else
		{
			ajaxResponse.setResponse(400, "Bad Request");
		}
		logger.debug("[UserController] /user/nameCheck response\n" + JsonUtil.toJsonPretty(ajaxResponse));
		return ajaxResponse;
	}
	
	@RequestMapping(value="/user/identifycellSend")
	@ResponseBody
	public Response<Object> identifycellSend(HttpServletRequest reqeust, HttpServletResponse response) 
	{
		String regcell = HttpUtil.get(reqeust, "regcell");
		String method = HttpUtil.get(reqeust, "method");
		Response<Object> ajaxResponse = new Response<Object>();
		IdentifyCell ic = null;
		
		int randomNumber = messageService.messageRandomCode();
		if(messageService.messageSender(regcell, randomNumber, method) > 0)
		{
			ic = messageService.cellSelect(regcell);
			
			if(ic == null) 
			{
				ic = new IdentifyCell();
				ic.setCell(regcell);
				ic.setRandomNumber(randomNumber);
				
				if(messageService.cellInsert(ic) > 0)
				{
					ajaxResponse.setResponse(0, "success");
				}
				else
				{
					ajaxResponse.setResponse(1, "fail");
				}
			} 
			else 
			{
				ic.setRandomNumber(randomNumber);
				
				if(messageService.cellUpdate(ic) > 0 ) 
				{
					
					ajaxResponse.setResponse(0, "success");
				} 
				else 
				{
					ajaxResponse.setResponse(1, "fail");
				}
			}
		}
		else
		{
			ajaxResponse.setResponse(2, "error");
		}
		return ajaxResponse;
	}
	
	@RequestMapping(value="/user/identifycellCheck")
	@ResponseBody
	public Response<Object> identifycellCheck(HttpServletRequest reqeust, HttpServletResponse response) 
	{
		String regcell = HttpUtil.get(reqeust, "regcell");
		int regcode = HttpUtil.get(reqeust, "regcode",0);
		Response<Object> ajaxResponse = new Response<Object>();
		
		if(!StringUtil.isEmpty(regcell) && regcode > 0)
		{
			IdentifyCell icInfo = messageService.cellSelect(regcell);
			
			if(icInfo != null)
			{
				System.out.println(icInfo.getRandomNumber());
				System.out.println(regcode);

				if(icInfo.getRandomNumber() == regcode)
				{
					System.out.println(icInfo.getCell());
					messageService.cellDelete(icInfo.getCell());
					ajaxResponse.setResponse(0,"identify success");
				}
				else
				{
					messageService.cellDelete(icInfo.getCell());
					ajaxResponse.setResponse(1,"identify fail");
				}
			}
			else
			{
				ajaxResponse.setResponse(2,"empty userInfo");
			}
		}
		else
		{
			ajaxResponse.setResponse(3,"empty value");	
		}
		return ajaxResponse;
	}
}
