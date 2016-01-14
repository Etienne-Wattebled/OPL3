package com.durieudufaux.demoproject;


public class B {
    private com.durieudufaux.demoproject.A a;

    public B(com.durieudufaux.demoproject.A a) {
        this.a = a;
    }

    public int methodBTuna() {
        java.lang.System.out.println("content of methodBTuna v1");
        return a.methodAFoo();
    }

    public int methodBJam() {
        java.lang.System.out.println("content of methodBJam v1");
        return 1;
    }
}

