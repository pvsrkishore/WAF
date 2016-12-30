package common.helpers;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataBaseHelper {
    static Logger logger = Logger.getLogger(DataBaseHelper.class);

    Connection connection = null;

    public void createConnection(String hostname, String database, String username, String password)
            throws ClassNotFoundException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DriverManager.getConnection(hostname + database, username,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet fetchData(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

    public ArrayList<String> fetchDataList(String hostname, String database, String username, String password, String query) {
        ArrayList<String> responseList = new ArrayList<String>();
        try {
            createConnection(hostname, database, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ResultSet set = fetchData(query);
            while (set.next()) {
                responseList.add(set.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseList;
    }


    public void insertOrUpdateInDB(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void cleanTables(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "delete from " + tableName;
        statement.executeUpdate(query);
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String buildSqlQueryWithEqualsOperator(final LinkedHashMap<String, Object> map) {

        final Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        final StringBuilder sb = new StringBuilder(map.size() * 8);
        while (it.hasNext()) {
            final Map.Entry<String, Object> entry = it.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if ((key != null) && (value != null)) {
                sb.append(key);
                sb.append("='");

                sb.append(value.toString());
                sb.append("'");

                if (it.hasNext()) {
                    sb.append(" and ");
                }
            } else {
                assert false : String.format("Null key in query map: %s", map.entrySet());
            }
        }
        return sb.toString();
    }

    public static String buildSqlQueryWithLikeOperator(final LinkedHashMap<String, Object> map) {

        final Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        final StringBuilder sb = new StringBuilder(map.size() * 8);
        while (it.hasNext()) {
            final Map.Entry<String, Object> entry = it.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if ((key != null) && (value != null)) {
                sb.append(key);
                sb.append(" like '%");

                sb.append(value.toString());
                sb.append("%'");
                if (it.hasNext()) {
                    sb.append(" and ");
                }
            } else {
                assert false : String.format("Null key in query map: %s", map.entrySet());
            }
        }
        return sb.toString();
    }

    public static String buildSqlQueryWithLikeNEqualsOperator(final LinkedHashMap<String, Object> equalsOperatorMap, final LinkedHashMap<String, Object> likeOperatorMap) {
        String query1 = buildSqlQueryWithEqualsOperator(equalsOperatorMap);
        String query2 = buildSqlQueryWithLikeOperator(likeOperatorMap);
        String finalQuery = "";
        if ((query1 != null && query1.length() > 0) || (query2 != null && query2.length() > 0)) {
            finalQuery = ((query1 != null && query1.length() > 0) && (query2 != null && query2.length() > 0)) ? (query1 + " and " + query2) : ((query1 != null && query1.length() > 0) ? query1 : query2);
        } else {
            assert false : String.format("NUll query response for ::: ", equalsOperatorMap.entrySet() + " and " + likeOperatorMap.entrySet());
        }
        return finalQuery;
    }

    public static String buildSqlQueryWithCommaOperator(final ArrayList<String> list) {

        final Iterator<String> it = list.iterator();
        final StringBuilder sb = new StringBuilder(list.size() * 8);
        sb.append("(\"");
        while (it.hasNext()) {
            final String value = it.next();
            if ((value != null)) {

                sb.append(value);
                if (it.hasNext()) {
                    sb.append(" , ");
                }
            } else {
                assert false : String.format("Null entry in query List: %s", list.toArray());
            }

        }
        sb.append("\")");
        return sb.toString();
    }
}
