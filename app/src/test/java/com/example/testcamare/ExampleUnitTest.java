package com.example.testcamare;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    public static void main(String[] args) {
        TestB testB = new TestB();
        testB.setName("王二小");
        testB.setAge(18);
        System.out.println("TestB的姓名：" + testB.getName());

        TestA testA = new TestA();
        testA.setTestB(testB);
        testB = null;
        testA.clostB();

//        System.out.println(">>>>TestB的姓名>>>：" + testB.getName());

    }
}
