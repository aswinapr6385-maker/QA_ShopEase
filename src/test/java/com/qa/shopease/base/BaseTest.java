package com.qa.shopease.base;

import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Page;
import com.qa.shopease.page.Register_Page;
import com.qa.shopease.page.login_page;

public class BaseTest {
	Playwright_factory pf;
	Config_properties cp;
	Properties prop;
	public Page page;
	protected login_page lp;
	protected Register_Page rp;
	
	@BeforeMethod
	
	
	public void setup() throws IOException {
		pf =new Playwright_factory();
		cp =new Config_properties();
		prop = cp.int_prop();
		page = pf.init_browser(prop);
		lp = new login_page(page);
		rp = new Register_Page(page);
		

	}
	@AfterMethod
	public void teardown() {
		page.context().browser().close();
		
	}
	public Page getPage() {
		// TODO Auto-generated method stub
		return page;
	}

}
