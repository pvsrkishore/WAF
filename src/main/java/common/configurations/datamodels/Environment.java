package common.configurations.datamodels;

public enum Environment {

	QA("qa"), STAGE("stage"), PROD("prod");
	
	private Environment(String name){
		this.name = name;
	}
	
	private String name;
	
	public String getName(){
		return name;
	}
}
