package com.quickium.core;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.net.Proxy.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpClient.Builder;
import org.openqa.selenium.remote.http.HttpClient.Factory;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.internal.OkHttpClient;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.appium.java_client.touch.WaitOptions;

@SuppressWarnings({ "unused", "deprecation" })
public class Quickium {

	public MobileDriver<MobileElement> mdriver = null;
	public WebDriver wdriver = null;
	private MobileElement melement = null;
	private WebElement welement = null;
	private int implicitly_wait_timeout_seconds = 120;
	private boolean element_found = false;
	private boolean if_element_found = false;
	private Reporter reporter = null;
	private HttpClientFactory proxy = null;
	private Util util = null;
	public JSONObject test_data;
	public String report_name = "";
	public String report_filename = "";
	private boolean warning_on_fail = false;
	private boolean is_mobile = true;

	public MobileDriver<MobileElement> get_available_driver(String platform, JSONArray devices, boolean is_cloud) {
		MobileDriver<MobileElement> next_available_driver = null;
		boolean next_available_driver_found = false;
		this.is_mobile = true;
		if (platform.equalsIgnoreCase("ios")) {

		} else {
			long startTime = System.currentTimeMillis();
			while (!next_available_driver_found && (System.currentTimeMillis() - startTime) < (60000 * 30)) {
				for (int i = 0; i < devices.length(); i++) {
					JSONObject device = (JSONObject) devices.get(i);
					String deviceFullName = device.getString("deviceFullName");
					String platformVersion = device.getString("platformVersion");
					String automationName = device.getString("automationName");
					String appiumUrl = device.getString("appiumUrl");
					String chromedriverExecutable = device.getString("chromedriverExecutable");
					this.test_data.put("deviceFullName", deviceFullName);
					this.test_data.put("platformVersion", platformVersion);
					this.test_data.put("automationName", automationName);
					this.reporter.console_log("Attempting to connect to " + deviceFullName + ":" + platformVersion + ":"
							+ automationName);
					try {
						next_available_driver = get_android_driver(deviceFullName, platformVersion, automationName,
								is_cloud, appiumUrl, chromedriverExecutable);
						Thread.sleep(10000);
						next_available_driver_found = true;
						break;
					} catch (Exception e) {
						this.reporter.console_log("Failed to connect to " + deviceFullName + ":" + platformVersion + ":"
								+ automationName);
						this.reporter.console_log(e.getMessage());
						next_available_driver = null;
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		return next_available_driver;
	}

	public WebDriver get_available_driver(String browser) {
		WebDriver next_available_driver = null;
		boolean next_available_driver_found = false;
		JSONObject configs = ReadExternalData.read_json_file("config" + File.separator + "config.json");
		this.is_mobile = false;
		if (browser.equalsIgnoreCase("ie")) {

		} else if (browser.equalsIgnoreCase("firefox")) {

		} else {
			System.setProperty("webdriver.chrome.driver", "resource/chromedriver90.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("headless");
			options.addArguments("window-size=1024x768");
			options.addArguments("--ignore-ssl-errors=yes");
			options.addArguments("--ignore-certificate-errors");
			options.addArguments("--start-maximized");
			options.addArguments("--disable-gpu");
			next_available_driver = new ChromeDriver(options);
			next_available_driver.manage().timeouts().implicitlyWait(configs.getInt("driver_timeouts_seconds"),
					TimeUnit.SECONDS);
		}
		return next_available_driver;
	}

	public MobileDriver<MobileElement> get_android_driver(String deviceFullName, String platformVersion,
			String automationName, boolean is_cloud, String appiumUrl, String chromedriverExecutable) throws Exception {
		long startTime = System.nanoTime();
		JSONObject configs = ReadExternalData.read_json_file("config" + File.separator + "config.json");
		MobileDriver<MobileElement> driver = null;
		DesiredCapabilities capabilities = new DesiredCapabilities();
		if (is_cloud == true) {
			capabilities.setCapability("pCloudy_Username", configs.getString("pCloudy_Username"));
			capabilities.setCapability("pCloudy_ApiKey", configs.getString("pCloudy_ApiKey"));
			capabilities.setCapability("pCloudy_DurationInMinutes", configs.getInt("pCloudy_DurationInMinutes"));
			capabilities.setCapability("newCommandTimeout", configs.getInt("newCommandTimeout"));
			capabilities.setCapability("launchTimeout", configs.getInt("launchTimeout"));

			capabilities.setCapability("pCloudy_DeviceFullName", deviceFullName);
			capabilities.setCapability("platformVersion", platformVersion);
			capabilities.setCapability("automationName", automationName);

			capabilities.setCapability("platformName", "android");
			capabilities.setCapability("pCloudy_ApplicationName", configs.getString("pCloudy_ApplicationName"));
			capabilities.setCapability("appPackage", configs.getString("android_appPackage"));
			capabilities.setCapability("appActivity", configs.getString("android_appActivity"));
			capabilities.setCapability("pCloudy_WildNet", configs.getBoolean("pCloudy_WildNet"));
			capabilities.setCapability("pCloudy_EnableVideo", configs.getBoolean("pCloudy_EnableVideo"));
			capabilities.setCapability("pCloudy_EnablePerformanceData",
					configs.getBoolean("pCloudy_EnablePerformanceData"));
			capabilities.setCapability("pCloudy_EnableDeviceLogs", configs.getBoolean("pCloudy_EnableDeviceLogs"));
			capabilities.setCapability("settings[allowInvisibleElements]",
					configs.getBoolean("settings_allowInvisibleElement"));
			capabilities.setCapability("settings[ignoreUnimportantViews]",
					configs.getBoolean("settings_ignoreUnimportantView"));
			capabilities.setCapability("settings[shouldUseCompactResponses]",
					configs.getBoolean("settings_shouldUseCompactResponse"));
			capabilities.setCapability("autoGrantPermissions", configs.getBoolean("autoGrantPermissions"));
			capabilities.setCapability("unicodeKeyboard", configs.getBoolean("unicodeKeyboard"));
			capabilities.setCapability("fullReset", configs.getBoolean("fullReset"));
			capabilities.setCapability("appium:chromeOptions", ImmutableMap.of("w3c", false));
			capabilities.setCapability("chromedriverExecutable", chromedriverExecutable);
			this.reporter.console_log(capabilities.toString());
			HttpClientFactory httpClientFactory = new HttpClientFactory();
			httpClientFactory.setProxyAuth();
			HttpClientFactory factory = httpClientFactory;
			String pCloudy_url = appiumUrl;
			this.reporter.console_log("Connecting to " + pCloudy_url);
			if (configs.getBoolean("proxy")) {
				driver = new AndroidDriver<MobileElement>(new URL(pCloudy_url), factory, capabilities);
			} else {
				driver = new AndroidDriver<MobileElement>(new URL(pCloudy_url), capabilities);
			}
			driver.manage().timeouts().implicitlyWait(configs.getInt("driver_timeouts_seconds"), TimeUnit.SECONDS);
		} else {
			String[] config = { appiumUrl, "4723", deviceFullName, platformVersion, automationName,
			configs.getString("app") };
			capabilities.setCapability("platformVersion", config[3]);
			capabilities.setCapability("platformName", "android");
			capabilities.setCapability("automationName", "uiautomator2");
			capabilities.setCapability("appPackage", configs.getString("android_appPackage"));
			capabilities.setCapability("appActivity", configs.getString("android_appActivity"));
			capabilities.setCapability("deviceName", config[2]);
			capabilities.setCapability("app", configs.getString("app"));
			capabilities.setCapability("settings[allowInvisibleElements]",
					configs.getBoolean("settings_allowInvisibleElement"));
			capabilities.setCapability("settings[ignoreUnimportantViews]",
					configs.getBoolean("settings_ignoreUnimportantView"));
			capabilities.setCapability("settings[shouldUseCompactResponses]",
					configs.getBoolean("settings_shouldUseCompactResponse"));
			capabilities.setCapability("autoGrantPermissions", configs.getBoolean("autoGrantPermissions"));
			capabilities.setCapability("unicodeKeyboard", configs.getBoolean("unicodeKeyboard"));
			capabilities.setCapability("fullReset", configs.getBoolean("fullReset"));
			capabilities.setCapability("appium:chromeOptions", ImmutableMap.of("w3c", false));
			capabilities.setCapability("chromedriverExecutable", chromedriverExecutable);
			capabilities.setCapability("unicodeKeyboard", false);
			capabilities.setCapability("resetKeyboard", false);
			this.reporter.console_log(capabilities.toString());
			String local_url = configs.getString("local_url");
			this.reporter.console_log("Connecting to " + local_url);
			driver = new AndroidDriver<MobileElement>(new URL(local_url), capabilities);
			driver.manage().timeouts().implicitlyWait(configs.getInt("driver_timeouts_seconds"), TimeUnit.SECONDS);
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		this.reporter.console_log("getDriver END" + ": " + duration + "ms", true);
		Thread.sleep(1000);
		return driver;
	}

	public Quickium(String plaftom) {
		this.reporter = new Reporter(report_name);
		this.proxy = new HttpClientFactory();
		this.util = new Util();
		String test_data_name = System.getProperty("current_test_data_name").replace(".json", "").trim();
		test_data = ReadExternalData
				.read_json_file(util.get_current_directory() + "test_data" + File.separator + test_data_name + ".json");
		this.report_name = test_data.getString("report_name").trim();
		this.report_filename = this.report_name + "_" + this.reporter.timestamp();
		reporter = new Reporter(this.report_filename);
		if (plaftom.equalsIgnoreCase("web")) {
			this.is_mobile = false;
			this.wdriver = get_available_driver(test_data.getString("browser"));
			if (this.wdriver == null) {
				reporter.log("info", "IdealAutomation Version: " + "1.0");
				reporter.log("fail", "BROWSER: " + this.test_data.getString("browser"));
				Assert.fail();
			}
		} else {
			this.is_mobile = true;
			this.mdriver = get_available_driver(test_data.getString("platform"),
					(JSONArray) test_data.get("device_list"), test_data.getBoolean("cloud"));
			if (this.mdriver == null) {
				reporter.log("info", "IdealAutomation Version: " + "1.0");
				reporter.log("fail",
						"DEVICE: " + this.test_data.getString("deviceFullName") + ":"
								+ this.test_data.getString("platformVersion") + ":"
								+ this.test_data.getString("automationName"));
				Assert.fail();
			}
		}
		reporter.log("info", "TEST DATA: " + test_data_name + ".json");
		reporter.log("info", "START: " + this.report_name);
	}

	public Quickium terminate() {
		if (this.is_mobile) {
			try {
				mdriver.closeApp();
			} catch (Exception e) {
				// Ignore
			}
			try {
				mdriver.quit();
			} catch (Exception e) {
				// Ignore
			}
		} else {
			try {
				wdriver.quit();
			} catch (Exception e) {
				// Ignore
			}
		}
		try {
			reporter.log("info", "END: " + this.report_name);
			reporter.end_test();
		} catch (Exception e) {
			// Ignore
		}
		return null;
	}

	public boolean set_warning_on_fail() {
		return this.warning_on_fail;
	}

	public void set_warning_on_fail(boolean status) {
		this.warning_on_fail = status;
	}

	public Quickium element(MobileElement element) {
		this.if_element_found = false;
		this.element_found = false;
		this.melement = element;
		return this;
	}

	public Quickium melement(By element) {
		this.if_element_found = false;
		this.element_found = false;
		this.melement = this.mdriver.findElement(element);
		return this;
	}

	public Quickium welement(By element) {
		this.if_element_found = false;
		this.element_found = false;
		this.welement = this.wdriver.findElement(element);
		return this;
	}

	public MobileElement melement() {
		return melement;
	}

	public WebElement welement() {
		return welement;
	}

	public boolean exists(int timeout_seconds) {
		boolean isElementPresent = false;
		if (this.is_mobile) {
			try {
				mdriver.manage().timeouts().implicitlyWait(timeout_seconds, TimeUnit.SECONDS);
				WebDriverWait wait = new WebDriverWait(mdriver, timeout_seconds);
				wait.until(ExpectedConditions.visibilityOf(melement));
				isElementPresent = melement.isDisplayed();
				mdriver.manage().timeouts().implicitlyWait(implicitly_wait_timeout_seconds, TimeUnit.SECONDS);
				this.reporter.console_log("Element Found: " + melement.toString());

			} catch (Exception e) {
				isElementPresent = false;
				this.reporter.console_log("Element NOT Found: " + melement.toString());
				mdriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
			}
		} else {
			try {
				wdriver.manage().timeouts().implicitlyWait(timeout_seconds, TimeUnit.SECONDS);
				WebDriverWait wait = new WebDriverWait(wdriver, timeout_seconds);
				wait.until(ExpectedConditions.visibilityOf(welement));
				isElementPresent = welement.isDisplayed();
				wdriver.manage().timeouts().implicitlyWait(implicitly_wait_timeout_seconds, TimeUnit.SECONDS);
				this.reporter.console_log("Element Found: " + welement.toString());

			} catch (Exception e) {
				isElementPresent = false;
				this.reporter.console_log("Element NOT Found: " + melement.toString());
				wdriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
			}
		}
		return isElementPresent;

	}

	public Quickium ifexist(int timeout_seconds) {
		this.if_element_found = true;
		element_found = exists(timeout_seconds);
		return this;
	}

	public Quickium click() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		if (if_element_found && !element_found) {
			return this;
		}
		if (this.is_mobile) {
			this.melement.click();
			reporter.log("info", "Element Clicked: " + melement.toString());
		} else {
			this.welement.click();
			reporter.log("info", "Element Clicked: " + welement.toString());
		}
		return this;
	}

	public Quickium js_click() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		if (if_element_found && !element_found) {
			return this;
		}
		if (this.is_mobile) {
			JavascriptExecutor executor = (JavascriptExecutor) mdriver;
			executor.executeScript("arguments[0].click();", melement);
			reporter.log("info", "Element Clicked: " + melement.toString());
		} else {
			JavascriptExecutor executor = (JavascriptExecutor) wdriver;
			executor.executeScript("arguments[0].click();", welement);
			reporter.log("info", "Element Clicked: " + welement.toString());
		}
		return this;
	}

	public Quickium send_keys(String string) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		if (if_element_found && !element_found) {
			return this;
		}
		if (this.is_mobile) {
			this.melement.sendKeys(string);
			reporter.log("info", "Element SendKeys: " + melement.toString() + ": " + string);
		} else {
			this.welement.sendKeys(string);
			reporter.log("info", "Element SendKeys: " + welement.toString() + ": " + string);
		}
		return this;
	}

	public Quickium select(String string) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		if (if_element_found && !element_found) {
			return this;
		}
		if (this.is_mobile) {
			this.melement.sendKeys(string);
			reporter.log("info", "Element Select: " + melement.toString() + ": " + string);
		} else {
			Select dropDown = new Select(this.welement);
			dropDown.selectByVisibleText(string);
			reporter.log("info", "Element Select: " + welement.toString() + ": " + string);
		}
		return this;
	}

	public Quickium select(int index) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		if (if_element_found && !element_found) {
			return this;
		}
		if (this.is_mobile) {
			this.melement.sendKeys(String.valueOf(index));
			reporter.log("info", "Element Select: " + melement.toString() + ": " + String.valueOf(index));
		} else {
			Select dropDown = new Select(this.welement);
			dropDown.selectByIndex(index);
			reporter.log("info", "Element Select: " + welement.toString() + ": " + String.valueOf(index));
		}
		return this;
	}

	public Quickium tap_then_send_keys(String string) {
		if (this.is_mobile) {
			this.melement.click();
			Actions action = new Actions(this.mdriver);
			action.sendKeys(string).perform();
			reporter.log("info", "Element Tap then SendKeys: " + melement.toString() + ": " + string);
		} else {
			this.welement.click();
			Actions action = new Actions(this.wdriver);
			action.sendKeys(string).perform();
			reporter.log("info", "Element Tap then SendKeys: " + welement.toString() + ": " + string);
		}
		return this;
	}

	public boolean vanished() {
		for (int i = 1; i <= 120; i++) {
			if (!this.exists(5)) {
				break;
			}
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (this.is_mobile) {
			this.reporter.console_log("Element Vanished: " + melement.toString());
		} else {
			this.reporter.console_log("Element Vanished: " + welement.toString());
		}
		return true;
	}

	public void log(String string, String string2, boolean b) {
		this.reporter.log(string, string2, b);
	}

	public void console_log(String log_text) {
		this.reporter.console_log(log_text, false);
	}

	public void capture_screen_base64(String type) {
		if (this.is_mobile) {
			this.util.capture_screen_base64(this.mdriver, this.reporter, type);
		} else {
			this.util.capture_screen_base64(this.wdriver, this.reporter, type);
		}
	}

	public void log(String string, String string2) {
		this.reporter.log(string, string2);

	}

	public void add_summary(String string) {
		String log_message = this.reporter.timestamp("yyyy-MMM-dd") + "," + this.reporter.timestamp("HH:mm:ss") + ","
				+ this.report_filename + "," + string;
		this.reporter.console_log(log_message);
		ReadExternalData.log_to_file(log_message + "\n");
	}

	public Quickium switch_native_context() {
		this.mdriver.context("NATIVE_APP");
		this.reporter.log("info", "CONTEXT: " + "NATIVE_APP");
		return this;
	}

	public Quickium switch_web_context() {
		String web_context = this.mdriver.getContextHandles().toArray()[1].toString();
		this.mdriver.context(web_context);
		this.reporter.log("info", "CONTEXT: " + web_context);
		return this;
	}

	@SuppressWarnings("rawtypes")
	public void swipe_screen_via_direction(String dir) {
		System.out.println("swipeScreen(): dir: '" + dir + "'");
		final int ANIMATION_TIME = 200; // ms
		final int PRESS_TIME = 200; // ms
		int edgeBorder = 10;
		Point pointStart, pointEnd;
		PointOption pointOptionStart, pointOptionEnd;
		// init screen variables
		Dimension dims = mdriver.manage().window().getSize();
		// init start point = center of screen
		pointStart = new Point(dims.width / 2, dims.height / 2);
		switch (dir.toUpperCase()) {
		case "DOWN": // center of footer
			pointEnd = new Point(dims.width / 2, dims.height - edgeBorder);
			break;
		case "UP": // center of header
			pointEnd = new Point(dims.width / 2, edgeBorder);
			break;
		case "LEFT": // center of left side
			pointEnd = new Point(edgeBorder, dims.height / 2);
			break;
		case "RIGHT": // center of right side
			pointEnd = new Point(dims.width - edgeBorder, dims.height / 2);
			break;
		default:
			throw new IllegalArgumentException("swipeScreen(): dir: '" + dir.toString() + "' NOT supported");
		}
		// execute swipe using TouchAction
		pointOptionStart = PointOption.point(pointStart.x, pointStart.y);
		pointOptionEnd = PointOption.point(pointEnd.x, pointEnd.y);
		System.out.println("swipe_screen_via_direction(): pointStart: {" + pointStart.x + "," + pointStart.y + "}");
		System.out.println("swipe_screen_via_direction(): pointEnd: {" + pointEnd.x + "," + pointEnd.y + "}");
		System.out.println("swipe_screen_via_direction(): screenSize: {" + dims.width + "," + dims.height + "}");
		try {
			new TouchAction(mdriver).press(pointOptionStart)
					// a bit more reliable when we add small wait
					.waitAction(WaitOptions.waitOptions(Duration.ofMillis(PRESS_TIME))).moveTo(pointOptionEnd).release()
					.perform();
		} catch (Exception e) {
			System.err.println("swipe_screen_via_direction(): TouchAction FAILED\n" + e.getMessage());
			return;
		}
		// always allow swipe action to complete
		try {
			Thread.sleep(ANIMATION_TIME);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public boolean is_displayed() {
		if (this.is_mobile) {
			return this.melement.isDisplayed();
		} else {
			return this.welement.isDisplayed();
		}
	}

	public boolean text_contains(String string) {
		if (this.is_mobile) {
			return this.melement.getText().toLowerCase().trim().contains(string.toLowerCase().trim());
		} else {
			return this.welement.getText().toLowerCase().trim().contains(string.toLowerCase().trim());
		}
	}

	public boolean page_contains(String string) {
		if (this.is_mobile) {
			return this.mdriver.getPageSource().toLowerCase().trim().contains(string.toLowerCase().trim());
		} else {
			return this.wdriver.getPageSource().toLowerCase().trim().contains(string.toLowerCase().trim());
		}
	}

	public Quickium assert_text_contains(String string, boolean expected_state) {
		if (this.is_mobile) {
			if (this.melement.getText().toLowerCase().trim().contains(string.trim().toLowerCase()) == expected_state) {
				this.log("pass", "ASSERTION: '" + string + "' is " + expected_state + " in element");
				capture_screen_base64("ASSERTION: '" + string + "' is " + expected_state + " in element");
			} else {
				this.log("fail", "ASSERTION: '" + string + "' is " + !expected_state + " in element");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + !expected_state + " in element");
				Assert.fail();
			}
		} else {
			if (this.welement.getText().toLowerCase().trim().contains(string.trim().toLowerCase()) == expected_state) {
				this.log("pass", "ASSERTION: '" + string + "' is " + expected_state + " in element");
				capture_screen_base64("ASSERTION: '" + string + "' is " + expected_state + " in element");
			} else {
				this.log("fail", "ASSERTION: '" + string + "' is " + !expected_state + " in element");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + !expected_state + " in element");
				Assert.fail();
			}
		}
		return this;
	}

	public Quickium assert_page_contains(String string, boolean expected_state) {
		if (this.is_mobile) {
			if (this.mdriver.getPageSource().toLowerCase().trim()
					.contains(string.trim().toLowerCase()) == expected_state) {
				this.log("pass", "ASSERTION: '" + string + "' is " + expected_state + " in page");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + expected_state + " in page");
			} else {
				this.log("fail", "ASSERTION: '" + string + "' is " + !expected_state + " in page");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + !expected_state + " in page");
				Assert.fail();
			}
		} else {
			if (this.wdriver.getPageSource().toLowerCase().trim()
					.contains(string.trim().toLowerCase()) == expected_state) {
				this.log("pass", "ASSERTION: '" + string + "' is " + expected_state + " in page");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + expected_state + " in page");
			} else {
				this.log("fail", "ASSERTION: '" + string + "' is " + !expected_state + " in page");
				this.capture_screen_base64("ASSERTION: '" + string + "' is " + !expected_state + " in page");
				Assert.fail();
			}
		}
		return this;
	}

}

class Util {

	public Util() {

	}

	public void capture_screen_base64(WebDriver driver, Reporter reporter, String type) {
		reporter.log(type, "", reporter.test.addBase64ScreenShot(capture_screen_file(driver)));
	}

	private String capture_screen_file(WebDriver driver) {
		try {
			Thread.sleep(100);
			return "data:image/jpg;base64, " + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			return "data:image/jpg;base64, ";
		}
	}

	public String capture_screen_file(MobileDriver<MobileElement> driver) {
		try {
			Thread.sleep(100);
			return "data:image/jpg;base64, " + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			return "data:image/jpg;base64, ";
		}
	}

	public void capture_screen_base64(MobileDriver<MobileElement> driver, Reporter reporter, String type) {
		reporter.log(type, "", reporter.test.addBase64ScreenShot(capture_screen_file(driver)));
	}

	public String toBase64(String file_path) {
		byte[] fileContent = null;
		try {
			fileContent = FileUtils.readFileToByteArray(new File(file_path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}

	public String get_current_directory() {
		String absolute = getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
		absolute = absolute.substring(0, absolute.length() - 1);
		absolute = absolute.substring(0, absolute.lastIndexOf("/") + 1);
		String current_path = absolute;
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") != -1) {
			current_path = current_path.replace("/", "\\\\");
			if (current_path.indexOf("file:\\\\") != -1) {
				current_path = current_path.replace("file:\\\\", "");
			}
		} else if (current_path.indexOf("file:") != -1) {
			current_path = current_path.replace("file:", "");
		}
		return current_path;
	}
}

class Reporter {
	private ExtentReports report;
	public ExtentTest test;
	private Util util = new Util();

	public Reporter(String report_name) {
		String current_directory_path = util.get_current_directory() + "results" + File.separator;
		File directory = new File(current_directory_path);
		if (!directory.exists()) {
			directory.mkdir();
		}
		this.report = new ExtentReports(current_directory_path + report_name + ".html", true);
		this.report.loadConfig(new File(util.get_current_directory() + "extent-config.xml"));
		this.test = this.report.startTest(report_name);
	}

	public Reporter() {

	}

	public void end_test() {
		this.report.endTest(this.test);
		this.report.flush();
	}

	public String timestamp() {
		return timestamp("yyyyMMddHHmmss");
	}

	public String timestamp(String format) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(format);
		Timestamp timestamp_value = new Timestamp(System.currentTimeMillis());
		return sdf1.format(timestamp_value);
	}

	public void console_log(String log_text) {
		console_log(log_text, false);
	}

	public void console_log(String log_text, boolean with_separator) {
		if (with_separator)
			System.out.println(
					"- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
		System.out.println(this.timestamp("yyyy.MM.dd.HH.mm.ss") + ": " + log_text);
		if (with_separator)
			System.out.println(
					"- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	}

	public void log(String type, String log, boolean with_separator) {
		LogStatus log_status = LogStatus.INFO;
		switch (type.toLowerCase().trim()) {
		case "pass":
			log_status = LogStatus.PASS;
			break;
		case "fail":
			log_status = LogStatus.FAIL;
			break;
		case "warning":
			log_status = LogStatus.WARNING;
			break;
		case "skip":
			log_status = LogStatus.SKIP;
			break;
		}
		this.test.log(log_status, log);
		if (with_separator)
			console_log(log, true);
		else
			console_log(log, false);
	}

	public void log(String type, String log) {
		log(type, log, false);
	}

	public void log(String type, String log, String addScreenCapture) {
		LogStatus log_status = LogStatus.INFO;
		switch (type.toLowerCase().trim()) {
		case "pass":
			log_status = LogStatus.PASS;
			break;
		case "fail":
			log_status = LogStatus.FAIL;
			break;
		}
		this.test.log(log_status, log, addScreenCapture);
		console_log(log, false);
	}

}

class ReadExternalData {

	public static void log_to_file(String data) {
		OutputStream os = null;
		Util util = new Util();
		try {
			// below true flag tells OutputStream to append
			os = new FileOutputStream(
					new File(util.get_current_directory() + "results" + File.separator + "execution.logs"), true);
			os.write(data.getBytes(), 0, data.length());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static JSONObject read_json_file(String json_path) {
		Util util = new Util();
		json_path = json_path.replace(util.get_current_directory(), "");
		JSONObject obj = new JSONObject(read_text_file(util.get_current_directory() + json_path));
		return obj;

	}

	public static String read_text_file(String text_path) {
		String data = "";
		try {
			File myObj = new File(text_path);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data = data + myReader.nextLine();
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return data;
	}
}

class HttpClientFactory implements Factory {

	private final ConnectionPool pool = new ConnectionPool();
	private final long connectionTimeout;
	private final long readTimeout;
	private String authUser;
	private String authPassword;
	private String proxyHost;
	private int proxyPort;

	public HttpClientFactory() {
		this(Duration.ofMinutes(2), Duration.ofHours(3));
	}

	public HttpClientFactory(Duration connectionTimeout, Duration readTimeout) {
		Objects.requireNonNull(connectionTimeout, "Connection timeout cannot be null");
		Objects.requireNonNull(readTimeout, "Read timeout cannot be null");

		this.connectionTimeout = connectionTimeout.toMillis();
		this.readTimeout = readTimeout.toMillis();
	}

	public void setProxyAuth() {
		JSONObject configs = ReadExternalData.read_json_file("config" + File.separator + "config.json");
		this.proxyHost = configs.getString("proxy_host");
		this.proxyPort = configs.getInt("proxy_port");
		this.authUser = configs.getString("proxy_auth_user");
		this.authPassword = configs.getString("proxy_auth_password");
	}

	public HttpClient createClient(URL url) {
		Objects.requireNonNull(authUser, "Auth User cannot be NULL");
		Objects.requireNonNull(authPassword, "Auth Password cannot be NULL");
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		okhttp3.Authenticator proxyAuthenticator = (route, response) -> {
			String credential = Credentials.basic(authUser, authPassword);
			return response.request().newBuilder().header("Proxy-Authorization", credential).build();
		};

		okhttp3.OkHttpClient.Builder client = new okhttp3.OkHttpClient.Builder().connectionPool(pool)
				.followRedirects(true).followSslRedirects(true).retryOnConnectionFailure(true)
				.readTimeout(readTimeout, MILLISECONDS).proxy(proxy).proxyAuthenticator(proxyAuthenticator)
				.connectTimeout(connectionTimeout, MILLISECONDS);

		String info = url.getUserInfo();
		if (!Strings.isNullOrEmpty(info)) {
			String[] parts = info.split(":", 2);
			String user = parts[0];
			String pass = parts.length > 1 ? parts[1] : null;
			String credentials = Credentials.basic(user, pass);
			client.authenticator((route, response) -> {
				if (response.request().header("Authorization") != null) {
					return null;
				}

				return response.request().newBuilder().header("Authorization", credentials).build();
			});
		}

		return new OkHttpClient(client.build(), url);
	}

	@Override
	public Builder builder() {
		return null;
	}

	@Override
	public void cleanupIdleClients() {
	}

}
