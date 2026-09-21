package com.qa.shopease.base;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;

public class Playwright_factory {

    private static final Logger logger =
            Logger.getLogger(Playwright_factory.class.getName());

    Playwright pl;
    Browser browser;
    static Page page;
    BrowserContext bc;

    public Page init_browser(Properties prop) {

        String browsername = prop.getProperty("browser").trim();

        logger.info("Initializing Playwright");
        logger.info("Browser selected: " + browsername);

        pl = Playwright.create();

        switch (browsername.toLowerCase()) {

        case "chrome":
            logger.info("Launching Chrome browser");
            browser = pl.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false));
            break;

        case "chromium":
            logger.info("Launching Chromium browser");
            browser = pl.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setChannel("chrome")
                            .setHeadless(false));
            break;

        case "webkit":
            logger.info("Launching WebKit browser");
            browser = pl.webkit().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false));
            break;

        case "firefox":
            logger.info("Launching Firefox browser");
            browser = pl.firefox().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false));
            break;

        default:
            logger.severe("Invalid browser: " + browsername);
            throw new IllegalArgumentException(
                    "Invalid browser: " + browsername);
        }

        logger.info("Creating browser context");
        bc = browser.newContext();

        logger.info("Creating new page");
        page = bc.newPage();

        String url = prop.getProperty("URL");

        logger.info("Navigating to URL: " + url);

        page.navigate(
                url,
                new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(60000)
        );

        logger.info("Page loaded successfully");
        logger.info("Current URL: " + page.url());

        return page;
    }

    public static String takeScreenshot(String testName, int attempt) {

        try {

            logger.info("Taking screenshot for test: " + testName
                    + ", attempt: " + attempt);

            String screenshotFolder =
                    "./test-output/screenshots/";

            Path folderPath =
                    Paths.get(screenshotFolder);

            if (!folderPath.toFile().exists()) {

                logger.info("Screenshot folder does not exist. Creating folder.");

                folderPath.toFile().mkdirs();
            }

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
                    screenshotFolder + fileName;

            if (page == null) {

                logger.warning(
                        "Page is null. Screenshot cannot be taken."
                );

                return null;
            }

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(
                                    Paths.get(screenshotPath)
                            )
                            .setFullPage(true)
            );

            logger.info(
                    "Screenshot saved successfully: "
                    + screenshotPath
            );

            return screenshotPath;

        } catch (Exception e) {

            logger.severe(
                    "Failed to take screenshot: "
                    + e.getMessage()
            );

            return null;
        }
    }
}