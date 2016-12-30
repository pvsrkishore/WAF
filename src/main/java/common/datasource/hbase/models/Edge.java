package common.datasource.hbase.models;

import java.util.List;

public class Edge {

	private List<String> toId;
	private int toId_size;

	public int getToId_size() {
		return toId_size;
	}
	public void setToId_size(int toId_size) {
		this.toId_size = toId_size;
	}
	public List<String> getToId() {
		return toId;
	}
	public void setToId(List<String> toId) {
		this.toId = toId;
	} 

}
