package common.datasource.clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.rest.model.ScannerModel;
import org.apache.hadoop.hbase.rest.model.TableListModel;
import org.apache.hadoop.hbase.rest.model.TableSchemaModel;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import common.datasource.hbase.models.CellSet;
import common.helpers.JerseyRestConsumer;

/**
 * @author sabarinath.s
 * Date: 10-Apr-2015	
 * Time: 7:04:01 pm 
 */


public class HBaseRestClient extends JerseyRestConsumer {

	public HBaseRestClient(String endPoint) throws Exception {
		super(MediaType.TEXT_XML_TYPE, MediaType.TEXT_XML_TYPE, endPoint);
		startRestService(new URI(endPoint).getHost());
	}

	public static final Logger logger = Logger.getLogger(HBaseRestClient.class);

	public final static String STATUS_CHECK = "status/cluster";

	private void startRestService(String sshendPoint) throws Exception {

		try {
			if (!isServiceUP()) {
				logger.info("starting hbase rest service");
				JSch jsch = new JSch();
				jsch.addIdentity(System.getProperty("user.home")
						+ "/.ssh/id_rsa");
				Session session = jsch.getSession(
						System.getProperty("user.name"), sshendPoint, 22);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();

				ChannelExec channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand("hbase rest start");
				channel.connect();

			}
		} catch (Exception e) {
			logger.info("unable to start the service", e);
			throw new Exception("unable to start the service", e);

		}
	}

	private void stopRestService(String sshendPoint) throws Exception {

		try {
			if (isServiceUP()) {
				logger.info("stoping hbase rest service");
				JSch jsch = new JSch();
				jsch.addIdentity(System.getProperty("user.home")
						+ "/.ssh/id_rsa");
				Session session = jsch.getSession(
						System.getProperty("user.name"), sshendPoint, 22);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();

				ChannelExec channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand("hbase rest stop");
				channel.connect();

			}
		} catch (Exception e) {
			logger.info("unable to start the service", e);
			throw new Exception("unable to start the service", e);

		}
	}

	private Boolean isServiceUP() throws Exception {
		ClientResponse response;
		try {
			ClientRequest request = buildRequest(
					constructURI(STATUS_CHECK, null), Method.GET, null, null);
			response = (ClientResponse) executeMethod(request,
					ClientResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hbase rest service is down");
			return false;
		}

		if (response.getStatus() == 200) {
			logger.info("hbase rest service is up");
			return true;
		} else
			return false;

	}

	public void createTable(TableSchemaModel model) throws Exception {

		this.setRequestMediaType(MediaType.TEXT_HTML_TYPE);
		ClientRequest request = buildRequest(
				constructURI(model.getName() + "/schema", null), Method.PUT,
				null, model, MediaType.APPLICATION_JSON_TYPE,
				MediaType.APPLICATION_JSON_TYPE);
		executeMethod(request, String.class);
	}

	public TableListModel getTables() throws Exception {

		ClientRequest request = buildRequest(constructURI(null, null),
				Method.GET, null, null);
		ClientResponse response = (ClientResponse) executeMethod(request,
				ClientResponse.class);
		TableListModel entity = response.getEntity(TableListModel.class);
		return entity;

	}

	public TableSchemaModel getTableSchema(String tableName) throws Exception {

		ClientRequest request = buildRequest(
				constructURI(tableName + "/schema", null), Method.GET, null,
				null);
		String response = (String) executeMethod(request, String.class);
		// ClientResponse response = (ClientResponse)executeMethod(request,
		// ClientResponse.class);

		response = response.trim().replaceFirst("^([\\W]+)<", "<");
		JAXBContext context = JAXBContext.newInstance(TableSchemaModel.class);
		Unmarshaller unMar = context.createUnmarshaller();
		TableSchemaModel entity = (TableSchemaModel) unMar
				.unmarshal(new StringBufferInputStream(response));
		// Gson gsmainon = new Gson();
		// TableSchemaModel entity = response.getEntity(TableSchemaModel.class);
		// TableSchemaModel entity = gson.fromJson(response,
		// TableSchemaModel.class);
		return entity;
	}

	public CellSet getDataUsingKey(String tableName, String key)
			throws Exception {

		logger.info("querying hbase table" + tableName + " for key" + key);
		ClientRequest request = buildRequest(
				constructURI(tableName + key, null), Method.GET, null, null);
		ClientResponse response = (ClientResponse) executeMethod(request,
				ClientResponse.class);
		return (CellSet) convertStreamToObject(response.getEntityInputStream(),
				CellSet.class);
	}

	private String createScanner(String tableName, Filter filter,
			List<byte[]> columns, byte[] startRow, byte[] endRow,
			Long startTime, Long endTime) throws Exception {

		logger.info("creating scanner with filter - " + filter);
		ScannerModel scanner = new ScannerModel();
		scanner.setBatch(1000);
		if (columns != null && columns.size() > 0)
			scanner.setColumns(columns);
		if (startRow != null)
			scanner.setStartRow(startRow);
		if (endRow != null)
			scanner.setEndRow(endRow);
		if (startTime != null && startTime.longValue() > 0)
			scanner.setStartTime(startTime);
		if (endTime != null && endTime.longValue() > 0)
			scanner.setEndTime(endTime);
		scanner.setMaxVersions(1);
		if (filter != null)
			scanner.setFilter(ScannerModel.stringifyFilter(filter));

		JAXBContext context = JAXBContext.newInstance(ScannerModel.class);
		Marshaller createMarshaller = context.createMarshaller();
		createMarshaller.setProperty(Marshaller.JAXB_ENCODING, "ASCII");

		Writer w = new StringWriter();

		createMarshaller.marshal(scanner, w);

		System.out.println(w.toString());
		ClientRequest request = buildRequest(
				constructURI(tableName + "/scanner", null), Method.PUT, null,
				w.toString());

		ClientResponse response = (ClientResponse) executeMethod(request,
				ClientResponse.class);

		if (response.getStatus() != 201)
			throw new Exception(
					"Scanner creation call failed with response code -"
							+ response.getStatus());
		else
			return response.getHeaders().get("Location").get(0);
	}

	public static Object convertStreamToObject(InputStream is, Class<?> clazz)
			throws JAXBException, IOException, SAXException,
			ParserConfigurationException {

		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller unMarshall = context.createUnmarshaller();
		return unMarshall.unmarshal(is);
	}

}
