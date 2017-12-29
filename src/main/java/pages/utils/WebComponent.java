package pages.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.MoveToOffsetAction;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.utils.properties.PROPERTY;

import java.util.ArrayList;
import java.util.List;

public class WebComponent {

	protected WebDriver driver = DriverManager.getDriver();
	protected WebDriverWait wait;


	protected static Logger log = LoggerFactory.getLogger(WebComponent.class);


	public WebComponent() {
		Long timeout = Long.valueOf(PROPERTY.LONG_TIMEOUT);
		this.wait = new WebDriverWait(this.driver, timeout);
	}
	




	public void openHomePage() {
		log.info("Opening homepage");
		driver.get(PROPERTY.UI_BASE_URL);
		waitForAngular();
		waitForLoading();
	}
	
	public String getSelectedContext() {
		String raw = ((JavascriptExecutor)driver).executeScript("return localStorage.getItem('ngStorage-scopeName');").toString() + "";
		String context = raw.replaceAll("\"", "");
		return context;
	}



	public void waitForLoading(){
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".fa-spinner")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#content>.loadingIndicator")));
	}


	protected void waitForAngular(){
		wait.until(AdditionalConditions.angularHasFinishedProcessing());
	}

	/**
	 * waits for element to be present in page
	 */
	protected void waitPresent(By by){
		log.info("waiting for - " + by + " to be present");
		waitForLoading();
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	public void waitSeconds(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * waits for element to become visible
	 */
	protected void waitVisible(By by){
		log.info("waiting for - "+by+" to be visible");
		waitForLoading();
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	protected boolean isVisible(By by){
		log.info("Check ifVisible for - "+by+" to be visible");

		WebElement element = null;
		try { element = wait.until(ExpectedConditions.visibilityOfElementLocated(by)); } catch (Exception e) {}
		return element!=null;
	}


	/**
	 * Clicks element identified By by
	 */
	protected void click(By by){
		waitVisible(by);
		log.info("clicking - "+by);
		driver.findElement(by).click();
	}

	/**
	 * clears input of content and fills it with content
	 */
	protected void fillInput(By by, String content){

		if(content == null){
			log.info("content is null so nothing placed in input" + by);
			return;
		}

		waitVisible(by);
		WebElement element = driver.findElement(by);
		element.clear();

		log.info("typing in- " + by + " - " + content);
		element.sendKeys(content);
	}

	/**
	 * fill input of non clearable
	 */
	protected void fillInputNonClearable(By by, String content){

		if(content == null){
			log.info("content is null so nothing placed in input" + by);
			return;
		}

		waitVisible(by);
		WebElement element = driver.findElement(by);

		log.info("typing in- " + by + " - " + content);
		element.sendKeys(content);
	}

	/**
	 * hovers over element identified BY by
	 */
	protected void hover(By by){
		waitVisible(by);

		MoveToOffsetAction action_forms = new MoveToOffsetAction(
				((HasInputDevices) driver).getMouse(),
				(Locatable) driver.findElement(by), 10, 5);
		log.info("hovering - "+by);
		action_forms.perform();
	}

	/**
	 * returns visible text in element identified By by
	 */
	protected String getText(By by){
		waitVisible(by);
		String text = driver.findElement(by).getText().trim();
		log.info("retrieving text for - " + by + " - " + text);
		return text;
	}

	/**
	 * returns visible text in element identified By by
	 */
	protected ArrayList<String> getTexts(By by){
		waitPresent(by);
		log.info("retrieving textS for - " + by);


		ArrayList<String> toReturn = new ArrayList<String>();

		List<WebElement> elements = driver.findElements(by);
		for (WebElement element : elements) {
			String text = element.getText().trim();
			toReturn.add(text);
			log.info("got text for - " + by + " - " + text);
		}

		return toReturn;
	}


	/**
	 * returns value of the attribute with the specified name for the element located by by
	 */
	protected String getAttributeValue(By by, String attributeName){
		waitPresent(by);
		String text = driver.findElement(by).getAttribute(attributeName).trim();
		log.info("rerieving attribute "+attributeName+" value for - " + by + " -> " + text);
		return text;
	}

	/**
	 * returns true is radio button or checkbox is checked
	 */
	protected boolean isChecked(By by){
		waitVisible(by);
		boolean isChecked = driver.findElement(by).isSelected();
		log.info("is checked - " + by + " = " + isChecked);
		return isChecked;
	}

	/**
	 * Makes sure checkbox/radioButton is checked
	 */
	protected void check(By by){
		log.info("ticking - " + by);
		if(isChecked(by)){
			return;
		}
		driver.findElement(by).click();
	}


	/**
	 * Makes sure checkbox/radioButton is checked
	 */
	protected void handleCheckbox(By by, Boolean isChecked){

		if(isChecked == null){
			return;
		}

		if(isChecked) check(by);
		if(!isChecked) uncheckCheckbox(by);
	}

	/**
	 * Makes sure checkbox/radioButton is checked
	 */
	protected void handleCheckbox(WebElement element, Boolean isChecked){
		if(isChecked == null){
			return;
		}

		if(isChecked){
			if(element.isSelected()){
				return;
			}
			else {
				element.click();
			}
		}
		if(!isChecked){
			if(element.isSelected()){
				element.click();
			}
			else {
				return;
			}
		}
	}

	/**
	 * Makes sure checkbox is unchecked
	 */
	protected void uncheckCheckbox(By by){
		log.info("unticking - " + by);
		if(!isChecked(by)){
			return;
		}
		driver.findElement(by).click();
	}

	/**
	 * Selects option with index
	 * @return String
	 */
	protected void selectOption(By by, int index){
		waitVisible(by);
		log.info("selecting option - " + index + " in select " + by);
		Select selectBox = new Select(driver.findElement(by));
		selectBox.selectByIndex(index);
	}

	/**
	 * Returns the selected option of the SelectBox
	 * @return String
	 */
	protected String getSelectedOption(By by){
		waitVisible(by);
		Select selectBox = new Select(driver.findElement(by));
		String option = selectBox.getFirstSelectedOption().getText();
		log.info("selected option = " + option + " in select " + by);
		return option;
	}

	/**
	 * Selects an option by value
	 * @param value
	 */
	protected void selectByValue(By by, String value){
		if(value == null){
			return;
		}
		waitVisible(by);
		log.info("selecting option - " + value + " in select " + by);
		Select selectBox = new Select(driver.findElement(by));
		selectBox.selectByValue(value);
	}

	/**
	 * Selects an option by value
	 * @param visibleText
	 */
	protected void selectByVisibleText(By by, String visibleText){
		if(visibleText == null){
			return;
		}
		waitVisible(by);
		log.info("selecting option - "+visibleText + " in select "+by);
		Select selectBox = new Select(driver.findElement(by));
		selectBox.selectByVisibleText(visibleText);
	}

	/**
	 * Returns all the available options
	 * @return text
	 */
	@SuppressWarnings("null")
	protected ArrayList<String> getOptions(By by){
		waitVisible(by);
		Select selectBox = new Select(driver.findElement(by));
		List<WebElement> elements = selectBox.getOptions();
		ArrayList<String> elementsText = null;
		for (int i=0;i<elements.size();i++)
			elementsText.add(elements.get(i).getText().trim());
		log.info("options available in select - " + by + " are " + elementsText.toString());
		return elementsText;
	}


	protected void executeJavascript(String theJavascript){
		((JavascriptExecutor)driver).executeScript(theJavascript);
	}


	protected boolean isPresent(By by){
		try{
			waitPresent(by);
			driver.findElement(by);
		}catch (Exception e){
			return false;
		}
		return true;
	}

	
	public void refresh() {
		log.info("REFRESH");
		driver.navigate().refresh();
	}

	public void goBack() {
		log.info("BACK");
		driver.navigate().back();
	}

	protected String getCurentWindowHandle(){
		return driver.getWindowHandle();
	}

	protected void switchToWindowWithTitle(String title) {
		String currentWindow = driver.getWindowHandle();
		log.info("swiotching to window with title {}", title);
		for (String windowHandle : driver.getWindowHandles()) {
			driver.switchTo().window(windowHandle);
			if(driver.getTitle().equalsIgnoreCase(title)){
				return;
			}
		}
	}

	/**
	 * Accepts the alert
	 */
	protected void acceptAlert(){
		log.info("oking alert");
		driver.switchTo().alert().accept();
	}

	/**
	 * Cancels the alert
	 */
	protected void cancelAlert(){
		log.info("canceling alert");
		driver.switchTo().alert().dismiss();
	}
	
	public Boolean isPageLoading(){
		Object isLoading = ((JavascriptExecutor)driver).executeScript("return $('.fa.fa-spin').is(':visible')");
		
		return (Boolean) isLoading;
		
	}
	
	
	
	
}
