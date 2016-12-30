package common.datasource.hbase.models;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "CellSet")
public class CellSet {
	
	private List<Row> rowList;

	@XmlElement(name = "Row", required = true)
	public List<Row> getRowList() {
		return rowList;
	}

	public void setRowList(List<Row> rowLis) {
		this.rowList = rowLis;
	}
}
