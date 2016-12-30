package common.DataUtilities;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class PojoParser {

	
	public static Object parseJsonString(InputStream is, Class<?> clzz ){

		Gson gson = new Gson();
		return gson.fromJson(new InputStreamReader(is), clzz);	
	}
	
	public static Object parseJsonString(String fileName, Class<?> clzz){
		
		InputStream is = PojoParser.class.getClassLoader().getResourceAsStream(fileName);
		return parseJsonString(is, clzz);
	}
	
}
