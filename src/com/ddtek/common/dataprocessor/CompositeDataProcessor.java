package com.ddtek.common.dataprocessor;

import java.util.HashMap;
import java.util.Map;
import com.ddtek.common.ip.StatementContext;

import oajava.sql.ip;
import com.ddtek.meetup.dataprocessor.CategoriesDataProcessor;
import com.ddtek.meetup.dataprocessor.CitiesDataProcessor;
import com.ddtek.meetup.dataprocessor.GroupsDataProcessor;


public class CompositeDataProcessor  {
	
	
	private static CompositeDataProcessor compositeRequestBuilder = new CompositeDataProcessor();

	public static CompositeDataProcessor getInstance(){
		return compositeRequestBuilder;
	}
	public DataProcessor getDataProcessor(StatementContext stmtCtx){
		String tableName = stmtCtx.getTableName();
		stmtCtx.trace(ip.UL_TM_F_TRACE, "getDataProcessor() called for table " + tableName);
		DataProcessor dataProcessor = reqBuilders.get(tableName);
		return dataProcessor;
	}
	
	Map<String, DataProcessor> reqBuilders = new HashMap<String, DataProcessor>();
	
	public CompositeDataProcessor() {
		
		reqBuilders.put("CATEGORIES", new CategoriesDataProcessor());
reqBuilders.put("CITIES", new CitiesDataProcessor());
reqBuilders.put("GROUPS", new GroupsDataProcessor());


	}
	


}
