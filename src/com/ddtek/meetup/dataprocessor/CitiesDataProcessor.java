package com.ddtek.meetup.dataprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ddtek.common.dataprocessor.DataProcessor;
import com.ddtek.common.executor.Request;
import com.ddtek.common.executor.Request.Verb;
import com.ddtek.common.executor.Response;
import com.ddtek.common.ip.InvocationContext;
import com.ddtek.common.ip.StatementContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class CitiesDataProcessor extends DataProcessor {

	/**
	 * This method is to build appropriate HTTP request that is to be invoked
	 * to get the required data from the table Cities 
	 * 

	 * @return A HTTP Request that is to be executed
	 * @throws Exception
	 */
	@Override
	public Request buildRequest(StatementContext ctx, InvocationContext invCtx) throws Exception {
		String url;
		Map<String, Object> equalsConditions = invCtx.getEqlConditionsInUse();
		Map<String, String> queryParams = new HashMap<String, String> ();
		Map<String, String> customProperties = ctx.getSessionContext().getIPCustomProperties();

		String baseURL =  ctx.getSessionContext().getBaseURL();
		url = baseURL + "/2/cities?page=200" + "&key=" + customProperties.get("KEY");


		if(equalsConditions.containsKey("ZIP"))
        {
            url = url + "&query=" + equalsConditions.get("ZIP").toString();
        }


        if(equalsConditions.containsKey("COUNTRY") && equalsConditions.containsKey("STATE"))
        {
            url = url + "&country=" + equalsConditions.get("COUNTRY").toString();
            url = url + "&state=" + equalsConditions.get("STATE").toString();
        }

        url = url.replaceAll(" ", "%20");


		return new Request(url, null, null, Verb.GET, queryParams);
	}

	@Override
	public ArrayList<HashMap<String,String>> parseJSONResponse(StatementContext statmentCtx, Response response, InvocationContext invCtx) throws Exception {
		
		Map<String, Object> equalsConditions = invCtx.getEqlConditionsInUse();
		return processAll(statmentCtx, response,  invCtx);

	}
	
	/**
	 * Process the JSON response and return the
	 * results as List of Map.
	 * The status of the response is to be set
	 * based on the results.
	 * There are three options for status
	 * 
	 * <p><tt>RestIP.DAM_SUCCESS</tt> - Fetched all the results.
	 * <br><tt>RestIP.DAM_SUCCESS_WITH_RESULT_PENDING</tt> - More rows to be fetched.
	 * In this case offset may be required to be set in statement context.
	 * <br><tt>RestIP.DAM_FAILURE</tt> - In case of some failure.
	 * 
	 *  
	 * @param statmentCtx
	 * @param response
	 * @param invCtx
	 * @return a list of Map containing the rows with column name as key and column value as value.
	 * @throws Exception
	 */
	private ArrayList<HashMap<String, String>> processAll(
			StatementContext statmentCtx, Response response,
			InvocationContext invCtx) throws Exception {

		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

		try {
			String json = response.getBody();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readValue(json, JsonNode.class);

			JsonNode resultsNode = rootNode.get("results");

			for(int i = 0; i< resultsNode.size(); i++)
			{
				HashMap<String, String> row = new HashMap<>();
				JsonNode rowJSON = resultsNode.get(i);
				Iterator<String> it = rowJSON.getFieldNames();

				while(it.hasNext())
				{
					String columnName = it.next();
					row.put(columnName, rowJSON.get(columnName).getValueAsText());
				}

				list.add(row);
			}


		} catch (Exception e) {
			throw new Exception("Error parsing response " + e.getMessage());
		}
		return list;
	}
	

}
