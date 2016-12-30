package common.DataUtilities;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;


@Data
public class DBConfigMap{
	
	public Map<String, DBConfig> dbConfigMap = new HashMap<String, DBConfig>();
	
	@Data
	public static class DBConfig{
		private String host;
		private int port;
		private String user;
		private String password;
		private String sshUser;
		private String userName;
		private String sshHost;
		private String dbName;
	}
}
