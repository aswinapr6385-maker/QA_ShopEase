package com.qa.shopease.page;

import java.util.logging.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Register_Page {

    private static final Logger logger =
            Logger.getLogger(Register_Page.class.getName());

    Page page;

    public String registerbtn = "//a[normalize-space()='Register']";
    public String firstname = "//input[@placeholder='John']";
    public String Lastname = "//input[@placeholder='Doe']";
    public String emailadddress = "//input[@placeholder='you@example.com']";
    public String phonenumber = "//input[@placeholder='555-123-4567']";
    public String password = "//input[@placeholder='Minimum 6 characters']";
    public String repeatpass = "//input[@placeholder='Repeat your password']";
    public String terms = "//input[@id='terms']";
    public String createacc = "//button[normalize-space()='Create Account']";
    public String alert = "//div[@role='alert']";

    public Register_Page(Page page) {

        this.page = page;

        logger.info("Register page initialized");
    }

    public void doregister(
            String frstname,
            String Secondname,
            String email,
            String pass,
            String repass,
            String phnum) {

        logger.info("Starting registration process");

        logger.info("Clicking Register button");
        page.click(registerbtn);

        logger.info("Entering first name");
        page.fill(firstname, frstname);

        logger.info("Entering last name");
        page.fill(Lastname, Secondname);

        logger.info("Entering email address");
        page.fill(emailadddress, email);

        logger.info("Entering phone number");
        page.fill(phonenumber, phnum);

        logger.info("Entering password");
        page.fill(password, pass);

        logger.info("Entering repeat password");
        page.fill(repeatpass, repass);

        logger.info("Accepting terms and conditions");
        page.click(terms);

        logger.info("Clicking Create Account button");
        page.click(createacc);

        logger.info("Registration process completed");
    }

    public Locator getsucessMessage() {

        logger.info("Getting registration success message");

        return page.locator(alert);
    }
}