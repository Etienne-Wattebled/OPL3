package com.durieudufaux.demoproject;


public class A {
    public A() {
    }

    public int methodAFoo() {
        java.lang.System.out.println("content of methodAFoo v1");
        return 10;
    }

    public int methodABar(com.durieudufaux.demoproject.B b) {
        java.lang.System.out.println("content of methodABar v1");
        return b.methodBJam();
    }
}

