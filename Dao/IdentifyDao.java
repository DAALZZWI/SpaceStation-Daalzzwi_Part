package com.icia.web.dao;

import org.springframework.stereotype.Repository;
import com.icia.web.model.IdentifyCell;

@Repository("identifyDao")
public interface IdentifyDao 
{
	public IdentifyCell cellSelect(String cell);
	
	public int cellInsert(IdentifyCell ic);
	
	public int cellDelete(String cell);
	public int cellUpdate(IdentifyCell ic);
}
