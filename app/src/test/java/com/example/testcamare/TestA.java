package com.example.testcamare;

public class TestA {
    private TestB testB;


    public void setTestB(TestB testB) {
        this.testB = testB;
    }

    public void clostB() {
        System.out.println("》》》》》"+testB.getName());
        testB = null;
        System.out.println("--------"+testB.getName());
    }
}
