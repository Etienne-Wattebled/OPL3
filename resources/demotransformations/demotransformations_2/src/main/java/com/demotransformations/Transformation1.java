package com.demotransformations;

import java.util.List;
import java.lang.String;

public class Transformation1 {
	public void method1(int b) {
		System.out.println(b);
	}
	public int method2() {
		return 2;
	}
	public int method3(int a) {
		return method4(a);
	}
	public static int method4(int a) {
		return a;
	}
	public void method5(List<String> list) {
	}
}