package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

class TqsStackTest {

    private static final Logger log = Logger.getLogger(TqsStackTest.class.getName());
    private TqsStack<Integer> stack;

    @BeforeEach
    void setup() {
        stack = new TqsStack<>();
    }

    @Test
    void testStackIsEmptyOnConstruction() {
        log.info("Testing if stack is empty");
        assertTrue(stack.isEmpty());
    }

    @Test
    void testStackSizeZeroOnConstruction() {
        log.info("Testing if stack size is 0");
        assertEquals(0, stack.size());
    }

    @Test
    void testPushesMakeStackNonEmptyAndSizeCorrect() {
        log.info("Testing stack after multiple pushes.");
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertFalse(stack.isEmpty());
        assertEquals(3, stack.size());
    }

    @Test
    void testPushThenPopReturnsSameValue() {
        log.info("Testing push then pop.");
        stack.push(42);
        assertEquals(42, stack.pop());
    }

    @Test
    void testPushThenPeekReturnsSameValueWithoutChangingSize() {
        log.info("Testing push then peek.");
        stack.push(99);
        assertEquals(99, stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void testPopAllMakesStackEmpty() {
        log.info("Testing popping all elements.");
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.pop();
        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void testPopFromEmptyStackThrowsException() {
        log.info("Testing pop from empty stack.");
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    void testPeekIntoEmptyStackThrowsException() {
        log.info("Testing peek into empty stack.");
        assertThrows(NoSuchElementException.class, stack::peek);
    }

    @Test
    void testPushToFullBoundedStackThrowsException() {
        log.info("Testing push to full bounded stack.");
        TqsStack<Integer> boundedStack = new TqsStack<>(3); // Stack com tamanho 3
        boundedStack.push(1);
        boundedStack.push(2);
        boundedStack.push(3);
        assertThrows(IllegalStateException.class, () -> boundedStack.push(4));
    }

    @Test
    void testPopTopN() {
        log.info("Testing popTopN method.");
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);
        stack.push(50);

        assertEquals(30, stack.popTopN(3));  
        assertEquals(2, stack.size());       
    }

    @Test
    void testPopTopNThrowsExceptionWhenNIsTooLarge() {
        log.info("Testing popTopN throws exception for large n.");
        stack.push(5);
        stack.push(10);

        assertThrows(NoSuchElementException.class, () -> stack.popTopN(3)); 
    }

    @Test
    void testPopTopNThrowsExceptionOnEmptyStack() {
        log.info("Testing popTopN on empty stack.");
        assertThrows(NoSuchElementException.class, () -> stack.popTopN(1));
    }
}
