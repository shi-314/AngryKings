package com.angrykings.utils;

public class ServerJSONBuilder {
	
	private String json;
	
	public ServerJSONBuilder create(int action){
		json =  "{\"action\":" + action;
		return this;
	}
	
	public ServerJSONBuilder option(String key, String value){
		json +=  ",\"" + key + "\":\"" + value + "\"";
		return this;
	}
	
	public String build(){
		return json + "}";
	}

}
