package com.quickium.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.quickium.core.Quickium;
import com.quickium.pages.TemplatePage;

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