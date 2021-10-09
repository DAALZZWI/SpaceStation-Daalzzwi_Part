<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/views/include/head.jsp" %>

<script type="text/javascript" src="${path}/resources/js/user/regForm.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/include/navigation.jsp" %>
  <div class="mask d-flex align-items-center h-100 gradient-custom-3">
    <div class="container h-100">
      <div class="row d-flex justify-content-center align-items-center h-100">
        <div class="col-12 col-md-9 col-lg-7 col-xl-6">
          <div class="card" style="border-radius: 15px;">
            <div class="card-body p-5">
              <h2 class="text-uppercase text-center mb-5">우주정거장에 오신걸 환영합니다.</h2>
              <form id="signinform" name="signinform" method="post">
				<c:choose>
				<c:when test="${userId == null or userId == '' }">
                <div class="form-outline mb-4">
                	<label class="form-label" for="form3Example1cg">아이디</label>
                  	<input type="text" id="userId" name="userId" class="form-control form-control-lg" />
                	<span id="idCheck"></span>
                </div>
                </c:when>
                <c:otherwise>
                <input type="hidden" id="userId" name="userId" value="${userId}" class="form-control form-control-lg" /> 
                </c:otherwise>
                </c:choose>

				<c:choose>
				<c:when test="${userPwd == null or userId == '' }">
                <div class="form-outline mb-4">
                  <label class="form-label" for="form3Example3cg">비밀번호</label>
                  <input type="password" id="userPwd" name="userPwd" class="form-control form-control-lg" />
                  <span id="pwdCheck"></span>
                </div>
                </c:when>
                <c:otherwise>
                   <input type="hidden" id="userPwd" name="userPwd" value="${userPwd}" class="form-control form-control-lg" />               
                </c:otherwise>
                </c:choose>

				<c:choose>
				<c:when test="${userPwd == null or userId == '' }">
                <div class="form-outline mb-4">
                  <label class="form-label" for="form3Example3cg">비밀번호 확인</label>
                  <input type="password" id="userPwdr" name="userPwdr" class="form-control form-control-lg" />
                  <span id="pwdrCheck"></span>
                </div>
                   </c:when>
                   <c:otherwise>
                     <input type="hidden" id="userPwdr" name="userPwdr" value="${userPwd}"  class="form-control form-control-lg" />
                   </c:otherwise>
                </c:choose>
   
   				<c:choose>
				<c:when test="${userName == null or userId == '' }">             
                <div class="form-outline mb-4">
               	  <label class="form-label" for="form3Example4cg">닉네임</label>
                  <input type="text" id="userName" name="userName" class="form-control form-control-lg" />
                  <span id="nameCheck"></span>
                </div>
                 </c:when>
                 <c:otherwise>
                 <input type="hidden" id="userName" name="userName" value="${userName}" class="form-control form-control-lg" />
                 </c:otherwise>
                </c:choose>
                
                
                <div class="form-outline mb-4">
                  <label class="form-label" for="form3Example4cg">전화번호</label>
                  
                  <input type="text" id="userCell" name="userCell" class="form-control form-control-lg" />
                  <p></p>
                  <div style="display:flex;">
                  <span id="cellCheck" style="width:90%;"></span>
	                  <button type="button" class="btn btn-primary btn-sm col-auto" data-bs-toggle="modal" data-bs-target="#phoneCheckModal">
							전화번호 인증하기
					  </button>
                  </div>
                </div>
                
                
				<c:choose>
				<c:when test="${userEmail == null or userEmail == '' }">
                <div class="form-outline mb-4">
                  <label class="form-label" for="form3Example4cdg">이메일</label>                
                  <input type="text" id="userEmail" name="userEmail" class="form-control form-control-lg" />
                  <span id="emailCheck"></span>
                </div>
                </c:when>
                <c:otherwise>
                <input type="hidden" id="userEmail" name="userEmail" value="${userEmail}" class="form-control form-control-lg" />
                </c:otherwise>
                </c:choose>
                
                <div class="form-check d-flex justify-content-center mb-3">
                  <input class="form-check-input me-2" type="checkbox" id="agree" value="" id="form2Example3cg" />
	              <label class="agree" for="form2Example3g">약관내용에 동의합니다.</label>
                </div>	

				<div class="d-flex justify-content-around">
					<button class="btn btn-primary btn-lg btn-block" type="button" data-bs-toggle="collapse" data-bs-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
					    약관 내용 보기
					</button>
				</div>
				<div class="collapse" id="collapseExample" style="padding: 0px 25%; text-align:center;">
				  <div class="card card-body" style="margin:10px;">
				    $$$$약관정보$$$$
				  </div>
               	</div>
 
				<br/>	
					
                <div class="d-flex justify-content-around">
                  <button type="button" class="btn btn-primary  btn-block btn-lg gradient-custom-4" id="btnReg" name="btnReg">회원가입</button>
                </div>

                <p class="text-center text-muted mt-5 mb-0">이미 계정이 있으신가요? <a href="/user/login" class="fw-bold text-body"><u>로그인</u></a></p>

              </form>

            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

	<!-- Modal -->
<div class="modal fade" id="phoneCheckModal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered ">
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title" id="exampleModalLongTitle">전화번호 인증</h2>
      </div>
		<div class="card-content">
			<div class="card-body">
				<form id="modelform" name="modelform" class="modelform">
					<div class="row">
			 				<div class="form-outline mb-4 ">
			                  <label class="form-label" for="form3Example4cg">인증번호 입력</label>
			                  <input type="text" id="identifyCell" name="identifyCell" class="form-control form-control-lg" />
			                  <p></p>
			                  <div class="d-grid gap-2 d-md-flex justify-content-md-end">
				                  <button type="button" class="btn btn-outline-primary btn-sm  btn-block btn-lg gradient-custom-4 col-auto me-auto" id="btnIdentifyCell" name="btnIdentifyCell">인증번호 발송</button>
				                  <button type="button" class="btn btn-outline-primary btn-sm  btn-block btn-lg gradient-custom-4 col-auto" id="btnIdentifyCellConfirm" name="btnIdentifyCellConfirm">확인</button>
				                  <span id="identifycellCheck"></span>
			                  </div>
			                </div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
	<!-- End Modal -->
	
<%@ include file="/WEB-INF/views/include/footer.jsp" %>
</body>
</html>