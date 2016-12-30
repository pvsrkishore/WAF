package common.datasource.hbase.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import org.apache.hadoop.hbase.util.Base64;


public class Cell {
	
	private String column;
	private String timestamp;
	private String value;
	
	@XmlAttribute
	public String getColumn(){
		return column;
	}
	
	public void setColumn(String val){
		column= new String(Base64.decode(val));
	}
	
	@XmlAttribute
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@XmlValue
	public String getValue() {
		return value;
	}
	public void setValue(String val) {
		this.value = new String(Base64.decode(val));
	}
	
	

}
