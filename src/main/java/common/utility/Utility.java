package common.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.openqa.selenium.os.CommandLine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



/**
 * @author sabarinath.s
 *
 */
public class Utility {




    public static String GetCurrentDate(String format) {

        DateFormat dateFormat = new SimpleDateFormat(format);
        //get current date time with Date()
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        return (dateFormat.format(date)).toString();

    }
    
    public static Properties readFromPropertiesFile(String fileName) throws IOException{
        //InputStream resourceAsStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("/SellerE2ETests/src/test/java/resources/"+fileName);
        InputStream resourceAsStream = Utility.class.getClassLoader().getResourceAsStream(fileName);
    	Properties prop = new Properties();
    	prop.load(resourceAsStream);
    	return prop;
    }
    
    public static String prettyPrintJson(String json){
    	
    	JsonParser parser = new JsonParser();
    	JsonElement parse = parser.parse(json);
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	return gson.toJson(parse);
    }
    
    public static String prettyPrintJson(Reader json){
    	
    	JsonParser parser = new JsonParser();
    	JsonElement parse = parser.parse(json);
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	return gson.toJson(parse);
    }
    
    
    public static String prettyPrintJson(InputStream is){
    	
    	return prettyPrintJson( new InputStreamReader(is));
    }
    
    public static String prettyPrintJson(Object obj){
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	return gson.toJson(obj);
    }

    public static void main(String [] a) {
        System.out.println(CommandLine.find("chromedriver"));
    }
}
