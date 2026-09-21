package com.qa.shopease.base;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    // Number of retries after the initial execution
    public static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < MAX_RETRY_COUNT) {

            retryCount++;

            // Store retry information for the Extent listener
            result.setAttribute("retryCount", retryCount);
            result.setAttribute("willRetry", true);

            System.out.println(
                    "Retrying test: "
                    + result.getMethod().getMethodName()
                    + " | Retry "
                    + retryCount
                    + " of "
                    + MAX_RETRY_COUNT
            );

            return true;
        }

        // No more retries available
        result.setAttribute("retryCount", retryCount);
        result.setAttribute("willRetry", false);

        return false;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}