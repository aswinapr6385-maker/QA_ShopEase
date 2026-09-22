package com.qa.shopease.listners;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;
import com.qa.shopease.base.BaseTest;
import com.qa.shopease.base.RetryAnalyzer;

public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static final Logger logger =
            Logger.getLogger(ExtentReportListener.class.getName());

    private static ExtentReports extent;

    private static final Map<String, ExtentTest> testMap =
            new ConcurrentHashMap<>();

    private static final Map<String, Integer> attemptMap =
            new ConcurrentHashMap<>();

    private static String screenshotPath;

    // ============================================================
    // START ENTIRE SUITE
    // ============================================================

    @Override
    public void onStart(ISuite suite) {

        logger.info("==============================================");
        logger.info("Starting ShopEase Regression Suite");
        logger.info("Suite Name: " + suite.getName());
        logger.info("==============================================");

        String outputFolder = "test-output";

        new File(outputFolder).mkdirs();

        screenshotPath = outputFolder + "/screenshots";

        new File(screenshotPath).mkdirs();

        String reportPath =
                outputFolder + "/TestExecutionReport.html";

        logger.info("Report folder: " + outputFolder);
        logger.info("Report path: " + reportPath);
        logger.info("Screenshot path: " + screenshotPath);

        ExtentSparkReporter sparkReporter =
                new ExtentSparkReporter(reportPath);

        sparkReporter.config().setDocumentTitle(
                "ShopEase - Test Execution Report");

        sparkReporter.config().setReportName(
                "ShopEase Automation Test Execution");

        extent = new ExtentReports();

        extent.attachReporter(sparkReporter);

        extent.setSystemInfo(
                "Project",
                "ShopEase");

        extent.setSystemInfo(
                "Framework",
                "Playwright + Java + TestNG");

        extent.setSystemInfo(
                "Environment",
                "QA");

        logger.info("ExtentReport initialized successfully");
    }

    // ============================================================
    // TEST START
    // ============================================================

    @Override
    public void onTestStart(ITestResult result) {

        String testKey = getTestKey(result);

        String displayName = getDisplayName(result);

        int attempt =
                attemptMap.getOrDefault(testKey, 0) + 1;

        attemptMap.put(testKey, attempt);

        logger.info("----------------------------------------------");
        logger.info("TEST STARTED");
        logger.info("Test: " + displayName);
        logger.info("Test Key: " + testKey);
        logger.info("Attempt: " + attempt);
        logger.info("----------------------------------------------");

        ExtentTest test =
                testMap.get(testKey);

        if (test == null) {

            test = extent.createTest(displayName);

            testMap.put(testKey, test);

            logger.info(
                    "Created new ExtentTest: " + displayName);

            test.log(
                    Status.INFO,
                    "Initial execution started.");

        } else {

            logger.info(
                    "Retry execution started for: "
                    + displayName);

            test.log(
                    Status.WARNING,
                    "Retry "
                            + (attempt - 1)
                            + "/"
                            + RetryAnalyzer.MAX_RETRY_COUNT
                            + " started.");
        }

        test.log(
                Status.INFO,
                "Execution Attempt: " + attempt);

        addTestParameters(test, result);
    }

    // ============================================================
    // TEST SUCCESS
    // ============================================================

    @Override
    public void onTestSuccess(ITestResult result) {

        String testKey =
                getTestKey(result);

        ExtentTest test =
                testMap.get(testKey);

        int attempt =
                attemptMap.getOrDefault(testKey, 1);

        String displayName =
                getDisplayName(result);

        logger.info("----------------------------------------------");
        logger.info("TEST PASSED");
        logger.info("Test: " + displayName);
        logger.info("Attempt: " + attempt);
        logger.info("----------------------------------------------");

        if (test != null) {

            test.log(
                    Status.PASS,
                    "Test Passed on Attempt " + attempt);

            takeScreenshot(
                    result,
                    test,
                    "PASS",
                    attempt);

        } else {

            logger.warning(
                    "ExtentTest instance is NULL for passed test: "
                    + displayName);
        }

        attemptMap.remove(testKey);
        testMap.remove(testKey);
    }

    // ============================================================
    // TEST FAILURE
    // ============================================================

    @Override
    public void onTestFailure(ITestResult result) {

        String testKey =
                getTestKey(result);

        String displayName =
                getDisplayName(result);

        ExtentTest test =
                testMap.get(testKey);

        int attempt =
                attemptMap.getOrDefault(testKey, 1);

        logger.severe("----------------------------------------------");
        logger.severe("TEST FAILED");
        logger.severe("Test: " + displayName);
        logger.severe("Attempt: " + attempt);

        if (result.getThrowable() != null) {

            logger.severe(
                    "Failure Reason: "
                    + result.getThrowable().getMessage());
        }

        logger.severe("----------------------------------------------");

        if (test == null) {

            logger.warning(
                    "ExtentTest was NULL. Creating new ExtentTest for failed test.");

            test = extent.createTest(displayName);

            testMap.put(testKey, test);

            test.log(
                    Status.INFO,
                    "Test started without an existing ExtentTest instance.");
        }

        takeScreenshot(
                result,
                test,
                "FAIL",
                attempt);

        if (attempt <= RetryAnalyzer.MAX_RETRY_COUNT) {

            logger.warning(
                    "Attempt "
                    + attempt
                    + " FAILED. Retry "
                    + attempt
                    + "/"
                    + RetryAnalyzer.MAX_RETRY_COUNT
                    + " will be executed.");

            test.log(
                    Status.WARNING,
                    "Attempt "
                    + attempt
                    + " FAILED. Retry "
                    + attempt
                    + "/"
                    + RetryAnalyzer.MAX_RETRY_COUNT
                    + " will be executed.");

            if (result.getThrowable() != null) {

                test.log(
                        Status.WARNING,
                        result.getThrowable().toString());
            }

        } else {

            logger.severe(
                    "FINAL FAILURE - Maximum retries exhausted for: "
                    + displayName);

            test.log(
                    Status.FAIL,
                    "FINAL FAILURE - Maximum retries exhausted.");

            if (result.getThrowable() != null) {

                test.fail(result.getThrowable());
            }

            attemptMap.remove(testKey);
            testMap.remove(testKey);
        }
    }

    // ============================================================
    // TEST SKIPPED
    // ============================================================

    @Override
    public void onTestSkipped(ITestResult result) {

        String testKey =
                getTestKey(result);

        String displayName =
                getDisplayName(result);

        logger.warning("----------------------------------------------");
        logger.warning("TEST SKIPPED");
        logger.warning("Test: " + displayName);
        logger.warning("----------------------------------------------");

        ExtentTest test =
                testMap.get(testKey);

        if (test != null) {

            test.log(
                    Status.SKIP,
                    "Test Skipped.");

            if (result.getThrowable() != null) {

                test.skip(result.getThrowable());
            }

        } else {

            logger.warning(
                    "ExtentTest instance is NULL for skipped test: "
                    + displayName);
        }

        attemptMap.remove(testKey);
        testMap.remove(testKey);
    }

    // ============================================================
    // TEST PARAMETERS
    // ============================================================

    private void addTestParameters(
            ExtentTest test,
            ITestResult result) {

        Object[] parameters =
                result.getParameters();

        if (parameters != null
                && parameters.length > 0) {

            StringBuilder data =
                    new StringBuilder();

            for (Object parameter : parameters) {

                if (data.length() > 0) {
                    data.append(" | ");
                }

                data.append(
                        String.valueOf(parameter));
            }

            logger.info(
                    "Test Parameters: " + data);

            test.log(
                    Status.INFO,
                    "Test Data: " + data);
        }
    }

    // ============================================================
    // SCREENSHOT
    // ============================================================

    private void takeScreenshot(
            ITestResult result,
            ExtentTest test,
            String status,
            int attempt) {

        try {

            String displayName =
                    getDisplayName(result);

            logger.info(
                    "Taking " + status
                    + " screenshot for: "
                    + displayName);

            Page page =
                    getPage(result);

            if (page == null) {

                logger.warning(
                        "Page is NULL. Screenshot cannot be captured for: "
                        + displayName);

                if (test != null) {

                    test.warning(
                            "Screenshot not captured because Page is null.");
                }

                return;
            }

            String safeName =
                    displayName.replaceAll(
                            "[^a-zA-Z0-9._-]",
                            "_");

            if (safeName.length() > 100) {

                safeName =
                        safeName.substring(0, 100);
            }

            String fileName =
                    safeName
                    + "_"
                    + status
                    + "_attempt"
                    + attempt
                    + ".png";

            String filePath =
                    screenshotPath
                    + File.separator
                    + fileName;

            logger.info(
                    "Screenshot file path: "
                    + filePath);

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(
                                    Paths.get(filePath))
                            .setFullPage(true));

            logger.info(
                    "Screenshot saved successfully: "
                    + fileName);

            test.addScreenCaptureFromPath(
                    "screenshots/" + fileName);

            test.log(
                    Status.INFO,
                    "Screenshot captured: "
                    + fileName);

        } catch (Exception e) {

            logger.severe(
                    "Failed to capture screenshot: "
                    + e.getMessage());

            if (test != null) {

                test.warning(
                        "Unable to capture screenshot: "
                        + e.getMessage());
            }
        }
    }

    // ============================================================
    // GET PAGE
    // ============================================================

    private Page getPage(ITestResult result) {

        try {

            Object instance =
                    result.getInstance();

            if (instance instanceof BaseTest) {

                BaseTest baseTest =
                        (BaseTest) instance;

                Page page =
                        baseTest.getPage();

                if (page != null) {

                    logger.info(
                            "Playwright Page retrieved successfully");
                } else {

                    logger.warning(
                            "Playwright Page returned NULL");
                }

                return page;
            }

        } catch (Exception e) {

            logger.severe(
                    "Unable to get Playwright Page: "
                    + e.getMessage());
        }

        return null;
    }

    // ============================================================
    // TEST KEY
    // ============================================================

    private String getTestKey(ITestResult result) {

        String methodName =
                result.getMethod()
                        .getQualifiedName();

        Object[] parameters =
                result.getParameters();

        return methodName
                + Arrays.deepToString(parameters);
    }

    // ============================================================
    // DISPLAY NAME
    // ============================================================

    private String getDisplayName(ITestResult result) {

        String methodName =
                result.getMethod()
                        .getMethodName();

        Object[] parameters =
                result.getParameters();

        if (parameters == null
                || parameters.length == 0) {

            return methodName;
        }

        StringBuilder name =
                new StringBuilder(methodName);

        name.append(" [");

        for (int i = 0;
             i < parameters.length;
             i++) {

            if (i > 0) {
                name.append(", ");
            }

            name.append(
                    String.valueOf(
                            parameters[i]));
        }

        name.append("]");

        return name.toString();
    }

    // ============================================================
    // FINISH ENTIRE SUITE
    // ============================================================

    @Override
    public void onFinish(ISuite suite) {

        logger.info("==============================================");
        logger.info("Suite execution finished");
        logger.info("Suite Name: " + suite.getName());

        if (extent != null) {

            logger.info(
                    "Flushing Extent Report...");

            extent.flush();

            logger.info(
                    "Extent Report flushed successfully");
            logger.info(
                    "Report location: test-output/TestExecutionReport.html");
        }

        testMap.clear();
        attemptMap.clear();

        logger.info("Extent test maps cleared");

        // ========================================================
        // AUTO-TRIGGER BUG ANALYZER IF SUITE HAD FAILURES
        // ========================================================

        boolean hasFailures =
                suite.getResults()
                        .values()
                        .stream()
                        .anyMatch(r ->
                                r.getTestContext()
                                        .getFailedTests()
                                        .size() > 0);

        logger.info(
                "Suite contains failures: " + hasFailures);

        if (hasFailures) {

            logger.warning(
                    "Failures detected in suite! Running Bug Analyzer...");

            try {

                String logFile =
                        "target/surefire-reports/testng-results.xml";

                File f =
                        new File(logFile);

                if (!f.exists()) {

                    logger.info(
                            "Primary TestNG result not found: "
                            + logFile);

                    logFile =
                            "test-output/testng-results.xml";

                    logger.info(
                            "Using fallback TestNG result: "
                            + logFile);
                }

                logger.info(
                        "Bug Analyzer input: "
                        + logFile);

                ProcessBuilder pb =
                        new ProcessBuilder(
                                "java",
                                "-jar",
                                "ci-tools/bug-analyzer.jar",
                                "--log",
                                logFile,
                                "--output",
                                "reports"
                        );

                pb.inheritIO();

                logger.info(
                        "Starting Bug Analyzer...");

                Process process =
                        pb.start();

                int exitCode =
                        process.waitFor();

                logger.info(
                        "Bug Analyzer completed with exit code: "
                        + exitCode);

                logger.info(
                        "Bug Analyzer report updated at: "
                        + "reports/latest-analysis.html");

            } catch (Exception e) {

                logger.severe(
                        "Could not run Bug Analyzer: "
                        + e.getMessage());
            }
        }

        logger.info("==============================================");
        logger.info("ShopEase Regression Suite completed");
        logger.info("==============================================");
    }
}