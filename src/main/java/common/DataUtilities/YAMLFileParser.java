package common.DataUtilities;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


/**
 * @author sabarinath.s
 * Date: 13-Jul-2015	
 * Time: 7:24:01 pm 
 */

public class YAMLFileParser {

	public static String getJsonString(String fileName){
		
		InputStream resourceAsStream = YAMLFileParser.class.getResourceAsStream(fileName);
		Yaml yaml = new Yaml();
		return yaml.load(resourceAsStream).toString();
	}
	
	public static Object readYAMLAsJavaPojo(String fileName, Class<?> c){
		
		InputStream resourceAsStream = YAMLFileParser.class.getClassLoader().getResourceAsStream(fileName);
		Constructor constructor = new Constructor(c);
		Yaml yaml = new Yaml(constructor);
		return yaml.load(resourceAsStream);
	}
		
	
}
