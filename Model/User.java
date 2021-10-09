
package com.icia.web.model;

import java.io.Serializable;

public class User implements Serializable
{
	private static final long serialVersionUID = 8638989512396268543L;
	
	private String userId;    // 사용자 아이디
	private String userPwd;   // 비밀번호
	private String userName;  // 사용자 명
	private String userEmail; // 사용자 이메일 
	private String userCell;  // 사용자 전화번호
	private String status;    // 상태 ("Y":사용, "N":정지)
	private String regDate;   // 가입일
	private int point;		  // 포인트
	
	public User()
	{
		userId = "";
		userPwd = "";
		userName = "";
		userEmail = "";
		userCell = "";
		status = "";
		regDate = "";
		point= 0;
	}

	public String getUserCell() {
		return userCell;
	}

	public void setUserCell(String userCell) {
		this.userCell = userCell;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserPwd()
	{
		return userPwd;
	}

	public void setUserPwd(String userPwd)
	{
		this.userPwd = userPwd;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserEmail()
	{
		return userEmail;
	}

	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getRegDate()
	{
		return regDate;
	}

	public void setRegDate(String regDate)
	{
		this.regDate = regDate;
	}
}
