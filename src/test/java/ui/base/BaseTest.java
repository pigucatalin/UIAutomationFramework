package ui.base;

import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.utils.CustomTestListener;

@Listeners({CustomTestListener.class})
public class BaseTest {
	
	public SoftAssert softAssert;

	
	@BeforeClass
	public void beforeClass() {

	}
	
	@BeforeMethod
	public void beforeMeth(){
		softAssert = new SoftAssert();
	}
	
	@AfterMethod
	public void afterMeth() {
		softAssert = null;
	}
	
	
	@AfterClass
	public void afterClass() {

	}
	
	
	
	
}
