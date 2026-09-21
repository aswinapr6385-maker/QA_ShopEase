package com.qa.shopease.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qa.shopease.base.BaseTest;
import com.qa.shopease.utils.ExcelUtils;

public class login_Test extends BaseTest {

	@Test(dataProvider = "loginData")
	public void login(
	        String testCaseId,
	        String scenario,
	        String username,
	        String password,
	        String expectedResult) {

	    lp.do_login(username, password);

	    if (expectedResult.equalsIgnoreCase("SUCCESS")) {

	        assertThat(lp.getWelcomeMessage())
	                .isVisible();

	        assertThat(lp.getWelcomeMessage())
	                .hasText("Welcome back, Tester!");

	    } else if (expectedResult.equalsIgnoreCase("FAILURE")) {

	        assertThat(lp.getInvalidLoginMessage())
	                .isVisible();

	        assertThat(lp.getInvalidLoginMessage())
	                .hasText("Invalid email or password.");
	    }
	}

	@DataProvider(name = "loginData")
	public Object[][] getLoginData() throws IOException {

	    String filePath =
	            "src/test/resources/LoginData/LoginData.xlsx";

	    ExcelUtils excel =
	            new ExcelUtils(filePath, "Sheet1");

	    int rowCount = excel.getRowCount();
	    int columnCount = excel.getColumnCount();

	    Object[][] data =
	            new Object[rowCount - 1][columnCount];

	    for (int i = 1; i < rowCount; i++) {

	        for (int j = 0; j < columnCount; j++) {

	            data[i - 1][j] =
	                    excel.getCellData(i, j);
	        }
	    }

	    excel.closeWorkbook();

	    return data;
	}
}