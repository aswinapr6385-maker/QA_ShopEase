package com.qa.shopease.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.shopease.base.BaseTest;

public class login_page extends BaseTest{
	Page page;
	public String lgnbtn = "//a[normalize-space()='Login']";
	public String email = "//input[@placeholder='you@example.com']";
	public String password = "//input[@placeholder='••••••••']";
	public String signup = "//button[normalize-space()='Sign In']";
	public String alert = "//div[@role='alert']";
	public String invalidLoginAlert = "(//div[@role='alert'])[1]";

	public Locator getInvalidLoginMessage() {
	    return page.locator(invalidLoginAlert);
	}
	public login_page(Page page) {
	    this.page = page;
	}
	public String pageurl() {
		String url = page.url();
		System.out.println("url is "+ url);
		return url;

	}
	
	public void do_login(String username,String pass) {
		page.click(lgnbtn);
		page.fill(email,username );
		page.fill(password, pass);
		page.click(signup);
	}

    public Locator getWelcomeMessage() {
        return page.locator(alert);
    }
		
		
		

	}
	
	


