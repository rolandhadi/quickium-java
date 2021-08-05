package com.quickium.core;

import java.io.File;

import org.json.JSONObject;
import org.junit.runner.JUnitCore;

public class TestRunner {
	public static void main(String[] args) {
		JSONObject test_data = null;
		System.out.println("- - - - - - - - - - - v1.0 - - - - - - - - - - - ");
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - ");
		System.setProperty("current_execution_path", "");
		System.setProperty("current_execution_type", "");
		System.setProperty("current_test_data_name", "");
		if (args.length > 1) {
			System.setProperty("current_execution_path", args[1]);
			test_data = ReadExternalData.read_json_file(args[1] + File.separator + "test_data" + File.separator
					+ args[0].replace(".json", "").trim() + ".json");
			if (args.length > 2) {
				System.setProperty("current_execution_type", args[2]);
			}
		} else {
			test_data = ReadExternalData
					.read_json_file("test_data" + File.separator + args[0].replace(".json", "").trim() + ".json");
		}
		System.setProperty("current_test_data_name", args[0]);
		JUnitCore.main("com.quickium.tests." + test_data.getString("test_name").replace(".java", "").trim());
	}
}