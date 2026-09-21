package com.qa.shopease.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import freemarker.core.ReturnInstruction.Return;
import com.microsoft.playwright.options.WaitUntilState;

public class Playwright_factory {
	Playwright pl;
	Browser browser;
	static Page page;
	BrowserContext bc;
	public Page init_browser(Properties prop) {
		String browsername = prop.getProperty("browser").trim();
		
		
		pl=Playwright.create();
		switch (browsername) {
		case "chrome": 
			browser =pl.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "chromium": 
			browser =pl.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
			break;
		case "webkit": 
			browser =pl.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;
		case "firefox": 
			browser =pl.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
			break;	
		
			
		default:
			System.out.println("invalid browser............");
		}
		bc = browser.newContext();
		page = bc.newPage();
		page.navigate(
			    prop.getProperty("url"),
			    new Page.NavigateOptions()
			        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
			        .setTimeout(60000)
			);
		return page;
		

	}
	public static String takeScreenshot(
            String testName,
            int attempt) {

        try {

            String screenshotFolder =
                    "./test-output/screenshots/";


            // Create folder if it doesn't exist
            Path folderPath =
                    Paths.get(screenshotFolder);

            if (!folderPath.toFile().exists()) {

                folderPath.toFile().mkdirs();
            }


            // Clean test name
            String cleanTestName =
                    testName.replaceAll(
                            "[^a-zA-Z0-9_-]",
                            "_"
                    );


            String fileName =
                    cleanTestName
                    + "_attempt_"
                    + attempt
                    + ".png";


            String screenshotPath =
                    screenshotFolder
                    + fileName;


            if (page == null) {

                System.out.println(
                        "Page is null. "
                        + "Screenshot cannot be taken."
                );

                return null;
            }


            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(
                                    Paths.get(
                                            screenshotPath
                                    )
                            )
                            .setFullPage(true)
            );


            System.out.println(
                    "Screenshot saved: "
                    + screenshotPath
            );


            return screenshotPath;


        } catch (Exception e) {

            System.out.println(
                    "Failed to take screenshot: "
                    + e.getMessage()
            );

            return null;
        }
    }
	
	


	

}
