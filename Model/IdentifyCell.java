package com.icia.web.model;

import java.io.Serializable;

public class IdentifyCell implements Serializable
{
	private static final long serialVersionUID = -4657489476709059915L;
	
	private String cell;
	private int randomNumber;
	
	public IdentifyCell()
	{
		cell = "";
		randomNumber = 0;
	}

	public String getCell() 
	{
		return cell;
	}

	public void setCell(String cell) 
	{
		this.cell = cell;
	}

	public int getRandomNumber() 
	{
		return randomNumber;
	}

	public void setRandomNumber(int randomNumber) 
	{
		this.randomNumber = randomNumber;
	}
	
	
	
	
}
