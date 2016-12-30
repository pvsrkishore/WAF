package common.datasource.hbase.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.apache.hadoop.hbase.util.Base64;


public class Row {

	
	private String key = null;
	private List<Cell> cellList;

	@XmlAttribute
	public String getKey() {
		return key;
	}

	public void setKey(String val) {
		this.key = new String(Base64.decode(val));
	}

	@XmlElement(name = "Cell", required=true)
	public List<Cell> getCellList() {
		return cellList;
	}

	public void setCellList(List<Cell> cell) {
		this.cellList = cell;
	}
	
}
