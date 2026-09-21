package com.qa.shopease.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config_properties {
	Properties prop;
	public Properties int_prop() throws IOException {
		FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
		prop = new Properties();
		prop.load(ip);
		return prop;

	}

}
