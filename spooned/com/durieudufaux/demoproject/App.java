package com.durieudufaux.demoproject;


public class App {
    public static void main(java.lang.String[] args) {
        com.durieudufaux.demoproject.A a = new com.durieudufaux.demoproject.A();
        com.durieudufaux.demoproject.B b = new com.durieudufaux.demoproject.B(a);
        b.methodBTuna();
        a.methodABar(b);
    }
}

