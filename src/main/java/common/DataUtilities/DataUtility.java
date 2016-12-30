package common.DataUtilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ClassUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.DataUtilities.CSVUtil.CSVRowFilter;

public class DataUtility {

	private static List<String>[] objectFieldsMapping(List<String> header,
			List<String> values, String className, String delimiter) {

		List<String> objectFieldslist = new ArrayList<String>();
		List<String> objectValueslist = new ArrayList<String>();
		List list[] = { objectFieldslist, objectValueslist };

		for (int i = 0; i < header.size(); i++) {
			String s = header.get(i);
			if (StringUtils.containsIgnoreCase(header.get(i), className)) {
				if (s.indexOf(delimiter) == -1)
					continue;
				objectFieldslist.add(s.substring(s.indexOf(delimiter)
						+ delimiter.length(), s.length()));
				objectValueslist.add(values.get(i));
			}
		}
		return list;
	}

	public static JSONObject convertCSVToObject(List<String> header,
			List<String> values, Entry<String, Class<?>> classEntry)
			throws Exception {

		List<String>[] objectFieldsMapping = objectFieldsMapping(header,
				values, classEntry.getKey(), ".");
		return convertToJsonObject(objectFieldsMapping[0],
				objectFieldsMapping[1], classEntry);
	}

	public static JSONObject convertToJsonObject(List<String> feildsMap,
			List<String> valuesMap, Entry<String, Class<?>> classEntry)
			throws Exception {

		Class<?> clzz = classEntry.getValue();
		JSONObject jo = new JSONObject();

		for (int i = 0; i < feildsMap.size(); i++) {
			boolean recursion = false;
			String fieldName = feildsMap.get(i);
			if (fieldName.contains(".")) {
				fieldName = fieldName.substring(0, fieldName.indexOf("."));
				recursion = true;
			}

			if (jo.get(fieldName) != null)
				continue;

			Type returnType = null;
			Class<?> retrunClassType = null;

			try {
				Method method = clzz.getMethod("get"
						+ StringUtils.capitalize(fieldName));
				returnType = method.getGenericReturnType();
				retrunClassType = method.getReturnType();
			} catch (NoSuchMethodException ex) {
				try {
					Method method = clzz.getMethod("is"
							+ StringUtils.capitalize(fieldName));
					returnType = method.getReturnType();
					retrunClassType = method.getReturnType();

				} catch (NoSuchMethodException ex1) {
					try {
						Field f = clzz.getField(StringUtils
								.uncapitalize(fieldName));
						returnType = f.getGenericType();
						retrunClassType = f.getType();
					} catch (NoSuchFieldException e) {
						throw new Exception(
								"unable to read the field return type. Skipping the field"
										+ clzz.getName() + "-" + fieldName);
					}
				}
			}

			if (returnType != null && retrunClassType.isArray()) {
				Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
				classMap.put(fieldName, retrunClassType.getComponentType());
				jo.put(fieldName,
						constructArray(feildsMap.subList(i, feildsMap.size()),
								valuesMap.subList(i, feildsMap.size()),
								classMap.entrySet().iterator().next()));
				continue;
			}

			if (returnType != null
					&& (retrunClassType.isAssignableFrom(List.class))) {
				ParameterizedType t = (ParameterizedType) returnType;
				Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
				classMap.put(fieldName,
						(Class<?>) t.getActualTypeArguments()[0]);
				jo.put(fieldName,
						constructArray(feildsMap.subList(i, feildsMap.size()),
								valuesMap.subList(i, feildsMap.size()),
								classMap.entrySet().iterator().next()));
				continue;
			}

			if (recursion) {
				Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
				classMap.put(fieldName, retrunClassType);
				JSONObject jsonObject = convertCSVToObject(
						feildsMap.subList(i, feildsMap.size()),
						valuesMap.subList(i, feildsMap.size()), classMap
								.entrySet().iterator().next());
				jo.put(fieldName, jsonObject);
			} else {
				if (!"".equals(valuesMap.get(i)))
					jo.put(fieldName, valuesMap.get(i));
			}
		}
		return jo;
	}

	private static JSONArray constructArray(List<String> header,
			List<String> values, Entry<String, Class<?>> classEntry)
			throws Exception {

		JSONArray ja = new JSONArray();

		if (ClassUtils.isPrimitiveOrWrapper(classEntry.getValue())
				|| classEntry.getValue().isAssignableFrom(String.class)
				|| classEntry.getValue().isEnum()) {
			List<String>[] objectFieldsMapping = objectFieldsMapping(header,
					values, classEntry.getKey(), ".");
			for (String vals : objectFieldsMapping[1]) {
				ja.add(vals);
			}
		} else {
			List<String>[] objectFieldsMapping = objectFieldsMapping(header,
					values, classEntry.getKey(), "0.");
			List<String> feildsMap = objectFieldsMapping[0];
			List<String> valuesMap = objectFieldsMapping[1];
			int i = 1;
			while (feildsMap.size() > 0 && valuesMap.size() > 0) {
				Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
				classMap.put(classEntry.getValue().getSimpleName(),
						classEntry.getValue());
				ja.add(convertToJsonObject(feildsMap, valuesMap, classMap
						.entrySet().iterator().next()));
				objectFieldsMapping = objectFieldsMapping(header, values,
						classEntry.getKey(), i + ".");
				feildsMap = objectFieldsMapping[0];
				valuesMap = objectFieldsMapping[1];
				i++;
			}
		}
		return ja;
	}

	public static Iterator<Object[]> convertCSVToObjet(CSVData data,
			Map<String, Class<?>> classMap) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(
				DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		// mapper.configure(DeserializationConfig.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
		// true);

		List<List<String>> valuesList = data.getValues();

		List<Object[]> outputData = new ArrayList<Object[]>();

		for (List<String> valueList : valuesList) {
			List<Object> dataObject = new ArrayList<Object>();
			mapStringObjects(data.getHeader(), valueList, dataObject);
			if (classMap != null) {
				for (Entry<String, Class<?>> classEntry : classMap.entrySet()) {
					JSONObject convertCSVToObject = convertCSVToObject(
							data.getHeader(), valueList, classEntry);
					// Gson gson = new GsonBuilder().create();
					dataObject.add(mapper.readValue(
							convertCSVToObject.toJSONString(),
							classEntry.getValue()));

					// dataObject.add(gson.fromJson(convertCSVToObject.toJSONString(),
					// classEntry.getValue()));
				}
			}
			outputData.add(dataObject.toArray());
		}
		return outputData.iterator();
	}

	public static List<List<String>> filterData(int pos, String value,
			CSVRowFilter.FilterType filterType, List<List<String>> valueList) {

		switch (filterType) {
		case EQUAL_TO: {
			return valueList.stream()
					.filter(t -> t.get(pos).equalsIgnoreCase(value))
					.collect(Collectors.toList());
		}
		default:
			return valueList;
		}
	}

	public static List<List<String>> filterData(List<CSVRowFilter> filters,
			CSVData data) {

		List<List<String>> valuesList = data.getValues();
		if (filters != null) {
			for (CSVRowFilter filter : filters) {

				int filterIndex = data.getHeader().indexOf(
						filter.getFieldName());
				if (filterIndex != -1)
					try {
						valuesList = filterData(filterIndex,
								filter.getFieldValue(), filter.getFilterType(),
								valuesList);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
		return valuesList;
	}

	public static void mapStringObjects(List<String> header,
			List<String> values, List<Object> outputData) {
		;
		for (int i = 0; i < header.size(); i++) {
			if (header.get(i).indexOf(".") == -1) {
				outputData.add(values.get(i));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String s[]) throws Exception {

		// returnType.desiredAssertionStatus();
		CSVData data = new CSVData();
		List<String> header = new ArrayList<String>();
		header.add("listing.id");
		header.add("listing.productList.0.id");
		header.add("listing.productList.0.productId");
		header.add("listing.quantity");
		header.add("listing.mrp");

		List<List<String>> values = new ArrayList<List<String>>();

		List<String> val = new ArrayList<String>();

		val.add("listId11");
		val.add("Id12312");
		val.add("ProdId12312");
		val.add("1");
		val.add("399");

		List<String> val1 = Lists.newArrayList(val);

		values.add(val);
		values.add(val1);

		data.setHeader(header);
		data.setValues(values);

		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		classMap.put("listing", Listing.class);
		convertCSVToObjet(data, classMap);

	}

}

class Listing {

	Listing() {

	}

	private String id;
	private int quantity;
	private double mrp;
	private List<Product> productList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getMrp() {
		return mrp;
	}

	public void setMrp(double mrp) {
		this.mrp = mrp;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> product) {
		this.productList = product;
	}
}

class Product {

	private String id;
	private String productId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}