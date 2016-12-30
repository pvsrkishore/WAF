package common.DataUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.beust.jcommander.internal.Lists;
import common.configurations.ContextManager;
import common.configurations.datamodels.Environment;

public class CSVUtil {


	/**
	 * parse a CSV file and return the data in the CSV file as CSVData
	 * 
	 * @param fileName
	 * @return CSVData
	 * @throws Exception
	 */
	public static CSVData readDataFromCSV(String fileName) throws Exception{

		InputStream resourceAsStream = CSVUtil.class.getClassLoader().getResourceAsStream(fileName);
		CSVData data = readDataFromCSV(resourceAsStream, null);
		data.setFileName(fileName);
		return data;
	}
	
	public static CSVData readDataFromCSV(File file) throws Exception{

		CSVData data = readDataFromCSV(new FileInputStream(file), null);
		data.setFileName(file.getName());
		return data;
	}
	
	public static CSVData readDataFromCSV(String fileName, List<CSVRowFilter> filterList) throws Exception{

		InputStream resourceAsStream = CSVUtil.class.getClassLoader().getResourceAsStream(fileName);
		CSVData data = readDataFromCSV(resourceAsStream, filterList);
		data.setFileName(fileName);
		return data;
	}
	
	public static CSVData readDataFromCSV(File file, List<CSVRowFilter> filterList) throws Exception{

		CSVData data = readDataFromCSV(new FileInputStream(file), filterList);
		data.setFileName(file.getName());
		return data;
	}
	
	
	 
	public static CSVData readDataFromCSV(InputStream is, List<CSVRowFilter> filterList) throws Exception{

		CSVData data = new CSVData();
		CSVParser parser = new CSVParser(new InputStreamReader(is), CSVFormat.EXCEL.withHeader());
		List<List<String>> values = new ArrayList<List<String>>();
		Map<String, Integer> headerMap = parser.getHeaderMap();
		data.setHeader( Lists.newArrayList(headerMap.keySet()));
		List<CSVRecord> records = parser.getRecords();
		for(CSVRecord rec : records){
			List<String> colValues = new ArrayList<String>();
			for(int i = 0; i< rec.size(); i++){
				colValues.add(rec.get(i));
			}
			values.add(colValues);
		}
		parser.close();
		data.setValues(values);
		data.setValues(DataUtility.filterData(filterList, data));
		return data;
	}

	public static Iterator<Object[]> getTestData(String fileName, LinkedHashMap<String, Class<?>> classMap, List<CSVRowFilter> filter) throws Exception{

		Environment env = ContextManager.getGlobalContext().getEnvironment();
		String newFileName = "./"+env.getName()+"/"+fileName;
		return DataUtility.convertCSVToObjet(readDataFromCSV(newFileName, filter), classMap);
	}

	public static class CSVRowFilter{

		private String fieldName = null;
		private String fieldValue = null;
		private FilterType filterType = null;

		public CSVRowFilter(String name, String value, FilterType type){
			fieldName = name;
			fieldValue = value;
			filterType = type;
		}
		public String getFieldValue() {
			return fieldValue;
		}
		public void setFieldValue(String fieldValue) {
			this.fieldValue = fieldValue;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public FilterType getFilterType() {
			return filterType;
		}

		public void setFilterType(FilterType filterType) {
			this.filterType = filterType;
		}
		public static enum FilterType{
			EQUAL_TO,
			GREATER_THAN,
			LESS_THAN;
		}

	}
	
	public static void main(String [] a){
		List<String> test = new ArrayList<String>();
		test.add("female");
		test.add("male");
		
		List<String> test3 = new ArrayList<String>();
		test3.add("female");
		test3.add("male");
		
		List<List<String>> test1 = new ArrayList<List<String>>();
		test1.add(test);
		test1.add(test3);
		
		List<List<String>> list = test1.stream().filter( t -> t.get(0).equals("male")).collect(Collectors.toList());
		System.out.println(list.size());
		
	}
}