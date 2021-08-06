# quickium-java
Java Selenium/Appium quick page object model framework. It supports local and cloud test automation execution.
Most basic appium and selenuim steering actions are provided out of the box to kick start any automation mobile and web projects.
Reports are auto generated powered by extentreports.


## quick setup
### update configurations \config\config.json

### For pCloudy Cloud Devices [appium]
```
	"pCloudy_url": "https://device.pcloudy.com/appiumcloud/wd/hub",
	"pCloudy_Username": "roland.hadi@email.com",
	"pCloudy_ApiKey": "123456789",
	"pCloudy_DurationInMinutes": 60,
	"pCloudy_ApplicationName": "my_adb_app.apk",
	"pCloudy_WildNet": false,
	"pCloudy_EnableVideo": false,
	"pCloudy_EnablePerformanceData": false,
	"pCloudy_EnableDeviceLogs": true,
```

### For Local Devices [appium]
```
	"local_url": "http://127.0.0.1:4723/wd/hub",
	"app": "/Users/rolandhadi/Downloads/my_adb_app.apk",
	"newCommandTimeout": 600,
	"launchTimeout": 90000,
	"android_appPackage": "com.app.package",
	"android_appActivity": "com.app.package.modules.login",
	"ios_bundleId": "com.app.package",
	"settings_allowInvisibleElement": false,
	"settings_ignoreUnimportantView": true,
	"settings_shouldUseCompactResponse": true,
	"unicodeKeyboard": false,
	"fullReset": true,
	"autoGrantPermissions": true,
	"driver_timeouts_seconds": 60,
```

### proxy config [if needed]
```
	"proxy": false,
	"proxy_host": "proxy.com.http",
	"proxy_port": 8080,
	"proxy_auth_user": "roland.hadi",
	"proxy_auth_password": "password",
	"proxy_auth_key": "123456789="
```

### load the libraries to eclipse/idea intellij
![image](https://user-images.githubusercontent.com/65001113/128493456-1262aa6b-eaff-4366-b435-f6cf300f4a8c.png)

### duplicate the \pages\com.quickium.pages to your project page
![image](https://user-images.githubusercontent.com/65001113/128493787-6cd6ccf6-bac3-4dfc-9aa9-689fd1efd3a7.png)

### define page objects
```
	// Define Web Objects
	By Web_Button_ok = By.xpath("//*[contains(text(), 'OK')]");
	
	// Define Mobile Objects
	@AndroidFindBy(xpath = "//*[@resource-id='button-ok']")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='button-ok']")
	public MobileElement Mobile_button_ok;
```
![image](https://user-images.githubusercontent.com/65001113/128494338-9af355b8-f210-4cc4-a5b1-8bdb286d30b1.png)

### steer objects from action calls
```
q.welement(Web_Button_ok).click();
```

### chain action calls
```
q.welement(Web_Button_ok).ifexist(10).click();
```

### perform assertions
```
q.welement(Web_Button_ok).assert_text_contains("Ok", false);
```

### capture screenshots
```
q.capture_screen_base64("screenshot_name");
```
![image](https://user-images.githubusercontent.com/65001113/128495024-d98494d7-caaf-45fd-96c4-c1f4f6337af9.png)

### duplicate the \test\com.quickium.tests to your project test
![image](https://user-images.githubusercontent.com/65001113/128495206-4058efdc-675e-441b-bfa0-dba4752ba752.png)

### import your project pages
```
import com.project.pages.*;
```

### update the variables
```
private Project project_page = null;
project_page = new Project(q);
```

### call your page object model action
```
project_page.actions_here();
```
![image](https://user-images.githubusercontent.com/65001113/128495770-fc7f8e58-de6d-4b80-863b-2689f44f17dc.png)

### update the TestRunner.java
```
JUnitCore.main("com.project.tests." + test_data.getString("test_name").replace(".java", "").trim());
```
![image](https://user-images.githubusercontent.com/65001113/128496058-78e4fdad-fe13-45eb-8c84-34ce163600e7.png)

## running test
```
javac \com\quickium\core\TestRunner.java
java com\quickium\core\TestRunner.java Project
```
