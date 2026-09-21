package com.qa.shopease.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qa.shopease.base.BaseTest;
import com.qa.shopease.utils.ExcelUtils;

public class login_Test extends BaseTest {

    private static final Logger logger = Logger.getLogger(login_Test.class.getName());
    @Test(dataProvider = "loginData")
    public void login(
            String testCaseId,
            String scenario,
            String username,
            String password,
            String expectedResult) {

        logger.info("Starting test: " + testCaseId);
        logger.info("Scenario: " + scenario);

        try {

            lp.do_login(username, password);

            if (expectedResult.equalsIgnoreCase("SUCCESS")) {

                logger.info("Validating successful login");

                assertThat(lp.getWelcomeMessage())
                        .isVisible();

                assertThat(lp.getWelcomeMessage())
                        .hasText("Welcome back, Tester!");

                logger.info("PASS: " + testCaseId
                        + " - Successful login validation completed");

            } else if (expectedResult.equalsIgnoreCase("FAILURE")) {

                logger.info("Validating invalid login message");

                assertThat(lp.getInvalidLoginMessage())
                        .isVisible();

                assertThat(lp.getInvalidLoginMessage())
                        .hasText("Invalid email or password.");

                logger.info("PASS: " + testCaseId
                        + " - Invalid login validation completed");
            }

            logger.info("Test completed successfully: " + testCaseId);

        } catch (AssertionError e) {

            logger.severe(
                    "FAIL: " + testCaseId
                    + " | Scenario: " + scenario
                    + " | Reason: " + e.getMessage()
            );

            throw e;

        } catch (Exception e) {

            logger.severe(
                    "ERROR: " + testCaseId
                    + " | Scenario: " + scenario
                    + " | Exception: " + e.getMessage()
            );

            throw e;
        }
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() throws IOException {

        logger.info("Reading login test data from Excel");

        String filePath =
                "src/test/resources/LoginData/LoginData.xlsx";

        ExcelUtils excel =
                new ExcelUtils(filePath, "Sheet1");

        int rowCount = excel.getRowCount();
        int columnCount = excel.getColumnCount();

        logger.info("Excel rows: " + rowCount);
        logger.info("Excel columns: " + columnCount);

        Object[][] data =
                new Object[rowCount - 1][columnCount];

        for (int i = 1; i < rowCount; i++) {

            for (int j = 0; j < columnCount; j++) {

                data[i - 1][j] =
                        excel.getCellData(i, j);
            }
        }

        excel.closeWorkbook();

        logger.info("Login test data loaded successfully");

        return data;
    }
}