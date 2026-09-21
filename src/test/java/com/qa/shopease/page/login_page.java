package com.qa.shopease.page;

import java.util.logging.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.shopease.base.BaseTest;

public class login_page extends BaseTest {

    private static final Logger logger = Logger.getLogger(login_page.class.getName());

    Page page;

    public String lgnbtn = "//a[normalize-space()='Login']";
    public String email = "//input[@placeholder='you@example.com']";
    public String password = "//input[@placeholder='••••••••']";
    public String signup = "//button[normalize-space()='Sign In']";
    public String alert = "//div[@role='alert']";
    public String invalidLoginAlert = "(//div[@role='alert'])[1]";

    public login_page(Page page) {
        this.page = page;
        logger.info("Login page initialized");
    }

    public Locator getInvalidLoginMessage() {
        logger.info("Getting invalid login message");
        return page.locator(invalidLoginAlert);
    }

    public String pageurl() {
        String url = page.url();
        logger.info("Current page URL: " + url);
        return url;
    }

    public void do_login(String username, String pass) {

        logger.info("Starting login process");

        logger.info("Clicking Login button");
        page.click(lgnbtn);

        logger.info("Entering username");
        page.fill(email, username);

        logger.info("Entering password");
        page.fill(password, pass);

        logger.info("Clicking Sign In button");
        page.click(signup);

        logger.info("Login process completed");
    }

    public Locator getWelcomeMessage() {
        logger.info("Getting welcome message");
        return page.locator(alert);
    }
    
}