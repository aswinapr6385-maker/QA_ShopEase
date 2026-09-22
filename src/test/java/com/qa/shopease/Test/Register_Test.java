package com.qa.shopease.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.logging.Logger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qa.shopease.base.BaseTest;
import com.qa.shopease.utils.ExcelUtils;

public class Register_Test extends BaseTest {

    private static final Logger logger =
            Logger.getLogger(Register_Test.class.getName());

    @Test(dataProvider = "RegisterData")
    public void doregisters(
            String testCaseID,
            String scenario,
            String firstName,
            String lastName,
            String email,
            String phone,
            String password,
            String repeatPassword,
            String terms,
            String expectedResult) {

        logger.info("======================================");
        logger.info("Test Case: " + testCaseID);
        logger.info("Scenario: " + scenario);
        logger.info("Expected Result: " + expectedResult);
        logger.info("======================================");

        rp.doregister(
                firstName,
                lastName,
                email,
                password,
                repeatPassword,
                phone
        );

        if (expectedResult.equalsIgnoreCase("SUCCESS")) {

            assertThat(rp.getsucessMessage())
                    .isVisible();

            assertThat(rp.getsucessMessage())
                    .hasText(
                        "Account created successfully! Please login."
                    );

            logger.info("Registration successful");

        } else {

            logger.info(
                "Negative/edge scenario executed: "
                + expectedResult
            );
        }
    }
    @DataProvider(name = "RegisterData")
    public Object[][] registerData() throws Exception {

        ExcelUtils excel = new ExcelUtils(
                "src/test/resources/RegisterData/Register_TestData.xlsx",
                "Sheet1"
        );

        int rows = excel.getRowCount();
        int columns = excel.getColumnCount();

        Object[][] data = new Object[rows - 1][columns];

        for (int i = 1; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                data[i - 1][j] = excel.getCellData(i, j);
            }
        }

        excel.closeWorkbook();

        return data;
    }
}