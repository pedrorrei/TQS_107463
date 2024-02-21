package com.example;

import static org.junit.jupiter.api.Assertions.*;

// import static org.junit.jupiter.api.Assertions.*;

// import static org.junit.Assert.*;
// import static org.slf4j.LoggerFactory.getLogger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    TqsStack stack; 


    @BeforeEach
    void setup() {
        stack = new TqsStack();
    }


    @Test
    void emptyTest() {
        System.out.println("Test a)");
        // log.debug("Testing empty method in {}", mySut.getName());

        // exercise
        boolean result = stack.isEmpty();

        // verify
        assertTrue(result);
    }

    @Test
    void size0Test() {
        System.out.println("Test b)");
        // log.debug("Testing size method in {}", mySut.getName());

        // exercise
        int result = stack.size();

        // verify
        assertTrue(result == 0);
    }

    @Test
    void pushTest() {
        System.out.println("Test c)");
        // log.debug("Testing push method in {}", mySut.getName());

        // exercise
        int n = 5;
        for (int i = 0; i < n; i++) {
            stack.push(i);
        }

        // verify
        assertTrue(stack.size() == n);
    }

    @Test
    void pushpopTest() throws Exception {
        System.out.println("Test d)");
        stack.push(1);
        int val = (int) stack.pop();
        assertTrue(val == 1);
    }

    @Test
    void pushPeekTest() throws Exception {
        System.out.println("Test e)");
        stack.push(5);
        int tamanho = stack.size();
        int val = (int) stack.peek();
        assertTrue(val== 5 && tamanho == stack.size());
    }


    @Test
    void nPopsTest() {
        System.out.println("Test f)");
        int n = 5;
        for (int i=0; i < n ; i++){
            stack.push(i);
        }


        for (int j= 0; j<n; j++){
            try {
                int temp = (int) stack.pop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        assertTrue(stack.isEmpty() && stack.size() == 0);

    }

    @Test
    void popEmptyTest() {

        System.out.println("Test g)");
        assertThrows(java.util.NoSuchElementException.class, () -> {
            stack.pop();
        });
    }


    @Test
    void peekEmptyTest() {  
        System.out.println("Test h)");
        assertThrows(java.util.NoSuchElementException.class, () -> {
            stack.peek();
        });

    }

    @Test
    void pushIllegalTest() {
        System.out.println("Test i)");
        assertThrows(IllegalStateException.class, () -> {
            for (int i = 0; i <= 5 ; i++  ){
                stack.push(i);
            }
        });
    }     

    @AfterEach
    void teardown() {
        System.out.println("Tá feito, dalééé");
    }

    

}
