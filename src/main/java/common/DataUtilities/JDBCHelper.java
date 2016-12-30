package common.DataUtilities;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.jcraft.jsch.JSchException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import common.DataUtilities.DBConfigMap.DBConfig;

/**
 * @author sabarinath.s Date: 30-Jun-2015 Time: 11:40:50 pm
 */

public class JDBCHelper {

	public static MysqlDataSource getConnectionDataSource(DBConfig config)
			throws JSchException {

		MysqlDataSource d = new MysqlDataSource();
		d.setUser(config.getUser());
		if (config.getPassword() != null)
			d.setPassword(config.getPassword());
		d.setServerName(config.getHost());
		d.setDatabaseName(config.getDbName());
		return d;
	}

	public static <T> T fetchData(MysqlDataSource d, String query,
			Class<?> clazz) throws SQLException {

		QueryRunner r = new QueryRunner();
		return (T) r.query(d.getConnection(), query, new BeanHandler(clazz));
	}

	public static void main(String[] a) throws JSchException {

		DBConfigMap load = (DBConfigMap) YAMLFileParser.readYAMLAsJavaPojo(
				"DBConfig.yml", DBConfigMap.class);
		System.out.println(load.getDbConfigMap().get("SPMAPI").getHost());
		/*
		 * TypeDescription definition = new TypeDescription(DBConfig.class,
		 * Tag.MAP); definition.putMapPropertyType("dbConfigMap", String.class,
		 * DBConfig.class); constructor.addTypeDescription(definition );
		 */
		getConnectionDataSource(load.getDbConfigMap().get("SPMAPI"));

	}

	public static DBConfigMap getDBConfigsFromYAMLFile() {
		return (DBConfigMap) YAMLFileParser.readYAMLAsJavaPojo("DBConfig.yml",
				DBConfigMap.class);
	}

}
