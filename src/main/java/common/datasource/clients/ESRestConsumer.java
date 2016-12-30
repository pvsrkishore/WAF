package common.datasource.clients;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.scenario.Settings;
import common.configurations.ContextManager;
import common.helpers.JerseyRestConsumer;

public class ESRestConsumer extends JerseyRestConsumer {

	public static final Integer DEFAULT_LIMIT = 1000;
	private static FileOutputStream out;

	public ESRestConsumer() {

		super(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE,
				ContextManager.getGlobalContext().getValueAsString(
						"es_endPoint"));

	}
	
	public static final String CREATE = "/create";

	public ESRestConsumer(String baseUrl) {

		super(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE,
				baseUrl);

	}

	public JsonArray searchDocsByPhrase(String indexName, String type,
			String fieldName, String valuePhrase, Integer limit)
			throws Exception {

		MatchQueryBuilder matchPhraseQuery = QueryBuilders.matchPhraseQuery(
				fieldName, valuePhrase);
		return searchDocs(indexName, type, limit, matchPhraseQuery);

	}

	public JsonArray searchDocsByPhrase(String indexName, String type,
			Map<String, String> fieldValuePair, Integer limit) throws Exception {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		for (Entry<String, String> entry : fieldValuePair.entrySet()) {
			boolQuery.must(QueryBuilders.matchPhraseQuery(entry.getKey(),
					entry.getValue()));
		}
		return searchDocs(indexName, type, limit, boolQuery);

	}

	public JsonArray searchDocsMultiValueMatch(String indexName, String type,
			Map<String, List<String>> fieldValuePair, Integer limit)
			throws Exception {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		for (Entry<String, List<String>> entry : fieldValuePair.entrySet()) {
			if (entry.getValue().size() > 1) {
				BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
				for (String s : entry.getValue()) {
					boolQuery1.must(QueryBuilders.matchPhraseQuery(
							entry.getKey(), s));
				}
				boolQuery.must(boolQuery1);
			} else {
				boolQuery.must(QueryBuilders.matchPhraseQuery(entry.getKey(),
						entry.getValue().get(0)));
			}
		}
		return searchDocs(indexName, type, limit, boolQuery);

	}

	public JsonArray searchDocs(String indexName, String type, Integer limit)
			throws Exception {

		return searchDocs(indexName, type, limit, QueryBuilders.matchAllQuery());

	}

	public JsonArray searchDocs(String indexName, String type, Integer limit,
			QueryBuilder query) throws Exception {

		String queryString = "{\"query\":" + query.toString() + "}";
		JsonArray output = new JsonArray();
		Integer tempIndex = 0;
		int totalDocs;
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

		do {
			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put(
					"size",
					(limit != null && limit > DEFAULT_LIMIT) ? DEFAULT_LIMIT
							.toString() : limit.toString());
			queryParams.put("from", tempIndex.toString());

			ClientRequest request = buildRequest(
					constructURI(indexName + "/" + type + "/" + "_search",
							queryParams), Method.POST, null, queryString);
			String response = (String) executeMethod(request, String.class);

			JsonElement parse = parser.parse(response);
			JsonObject asJsonObject = parse.getAsJsonObject();
			JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("hits");

			JsonElement totalHits = asJsonObject2.get("total");
			totalDocs = totalHits.getAsInt();

			output.addAll(asJsonObject2.getAsJsonArray("hits"));

			tempIndex = tempIndex + (DEFAULT_LIMIT);

		} while ((limit != null ? limit : totalDocs) / tempIndex > 1);

		return output;

	}

	public Map<String, Settings> getIndexSettings(String indexName)
			throws Exception {

		ClientRequest request = buildRequest(
				constructURI(indexName + "/_settings", null), Method.GET, null,
				null);
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Map<String, Settings>> t = new TypeReference<Map<String, Settings>>() {
		};
		String response = (String) executeMethod(request, String.class);
		Map<String, Settings> settings = mapper.convertValue(response, t);
		return settings;

	}

}
