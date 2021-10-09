<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/views/include/head.jsp" %>
<link rel="stylesheet" href="${path}/resources/css/user/login.css">
<script src="${path}/resources/js/user/login.js"></script>
<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit" async defer></script>


<c:if test="${user != null and (user.status == '' or user.status == null) }">
<script>
$(document).ready(function() {
var regSending = confirm("아이디가 존재하지 않습니다. 회원가입 하시겠습니까?");

console.log(regSending);
if(regSending == true) {
	
	document.regForm.action = "/user/regForm";
	document.regForm.submit();
}

});
</script>
</c:if>
</head>
<body>
<!-- BODY 코드 -->
<%@ include file="/WEB-INF/views/include/navigation.jsp" %>
<div class="container">
	<div class="row">
	<section class="login-form">
		<h1>로그인</h1><br><br>
		<!-- form 태그-->
		<form class="form-signin">
			<div class="int-area">
				<input type="text" name="userId" id="userId"
				autocomplete="off" required>	
				<label for="userId">USER ID</label>
				<br>
				<span id="idCheck"></span>
			</div>
			<div class="int-area">
				<input type="password" name="userPwd" id="userPwd"
				autocomplete="off" required>	
				<label for="userPwd">PASSWORD</label>
				<br>
				<span id="pwdCheck"></span>
				<br/>
			

<div class="recaptcha_wrap" style="display: flex; justify-content: center;">
  <div id="g-recaptcha">
  	</div>
  <div id="recaptchaCheck" class="text-danger">
  	</div>
</div>
			</div><br>
					<div class="btn">
			<div class="btn-a">
				<button id="naverLoginBtn" type="button">네이버 로그인</button>
			</div>
			<div class="btn-b">
				<button id="kakaoLoginBtn" type="button">카카오 로그인</button>
			</div>
			<div class="btn-c">
				<button id="googleLoginBtn" type="button">구글 로그인</button>
			</div>
			<div class="btn-area">
				<button id="loginBtn" type="button">우주정거장 로그인</button>
			</div>
		</div>
	</div>	
	</form>

	</div>		
		<!-- form 태그 종료-->	
		<div class="caption">
			<a href="/user/findId">아이디 찾기</a> &nbsp; &nbsp;
			<a href="/user/findPwd">비밀번호 찾기</a> &nbsp; &nbsp;
			<a href="javascript:void(0);" onclick="fn_regForm()">회원가입</a>
		</div>
	</section>
	
	<form name="regForm" id="regForm" method="POST">
		<input type="hidden" name="userId" value="${user.userId}" />
		<input type="hidden" name="userPwd" value="${user.userPwd}" />
		<input type="hidden" name="userName" value="${user.userName}" />
		<input type="hidden" name="userEmail" value="${user.userEmail}" />
	</form>

<!-- BODY코드 -->
<%@ include file="/WEB-INF/views/include/footer.jsp" %>
</body>
</html>