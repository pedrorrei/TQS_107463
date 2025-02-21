package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TqsStack<T> {  

    private List<T> elements;
    private int maxSize;  

    public TqsStack() {
        this.elements = new ArrayList<>();
        this.maxSize = -1;  
    }

    public TqsStack(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Stack size must be greater than 0");
        }
        this.elements = new ArrayList<>();
        this.maxSize = maxSize;
    }

    public void push(T x) {
        if (maxSize > 0 && elements.size() >= maxSize) {
            throw new IllegalStateException("Stack is full");
        }
        elements.add(x);
    }

    public T pop() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return elements.remove(elements.size() - 1);
    }

    public T peek() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return elements.get(elements.size() - 1);
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public T popTopN(int n) {
        if (n <= 0 || n > elements.size()) {
            throw new NoSuchElementException("Not enough elements in the stack");
        }
        
        T pop = null;
        for (int i = 0; i < n; i++) {
            pop = elements.remove(elements.size() - 1);
        }
        return pop;
    }
}
