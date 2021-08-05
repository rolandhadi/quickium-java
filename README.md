# quickium-java
Java Selenium/Appium quick page object model framework. It supports local and cloud test automation execution.


# setup page
```
public class TemplatePage {
	private long start_time;
	private Quickium q = null;

	public TemplatePage(Quickium q) {
		this.q = q;
		PageFactory.initElements(new AppiumFieldDecorator(this.q.mdriver), this);
	}

	By Button_ok = By.xpath("//*[contains(text(), 'OK')]");
  
	@AndroidFindBy(xpath = "//*[@resource-id='com.quickium.app:id/progressBar']")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeButton[@name='progressBar']")
	public MobileElement Progressbar_loading;

	private void start_timer() {
		this.start_time = System.nanoTime();
	}

	private void end_timer(String method_name) {
		long endTime = System.nanoTime();
		long duration = (endTime - this.start_time) / 1000000;
		this.q.log("info", "END: " + method_name + ": " + duration + "ms", true);
	}

	public void template() {
		try {
			this.q.log("info", "START: " + Thread.currentThread().getStackTrace()[1].getMethodName().trim(), true);
			start_timer();
			// Code Start Here

			// Code End Here
			end_timer(Thread.currentThread().getStackTrace()[1].getMethodName().trim());
		} catch (Exception e) {
			this.q.capture_screen_base64("fail");
			this.q.log("fail", e.getMessage(), true);
			this.q.log("fail", ExceptionUtils.getStackTrace(e), true);
			Assert.fail();
		}
	}

}
```

# setup test
```
public class TemplateTest {
	private Quickium q = null;
	private TemplatePage template_page = null;

	@Before
	public void setUp() throws Exception {
		q = new Quickium("mobile");
		q.test_data.put("test_case_passed", false);
		q.test_data.put("test_case_name", this.getClass().getSimpleName().trim());
		template_page = new TemplatePage(q);
	}

	@After
	public void tearDown() throws Exception {
		q.terminate();
	}

	@Test
	public void test() {
		try {
			// START CODE HERE

			template_page.template();

			// END CODE HERE
			q.test_data.put("test_case_passed", true);
		} catch (Exception e) {
			this.q.log("fail", e.getMessage());
		}
	}
}
```
