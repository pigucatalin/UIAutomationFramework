package pages.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.utils.properties.PROPERTY;


public class DriverManager {
	
	private static WebDriver driver = null;
	static {
		System.setProperty("webdriver.chrome.driver", PROPERTY.CHROME_DRIVER_PATH);
	}
	
	public static WebDriver getDriver(){
		if(null == driver){
			driver = new ChromeDriver();
		}
		return driver;
	}
	
	public static void quitDriver(){
		try{
			driver.quit();}
		catch (Exception e){}
		driver = null;
	}
	
	
}
