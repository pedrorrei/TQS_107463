package com.example;

import java.util.NoSuchElementException;
// import java.util.ArrayList;
import java.util.Stack;


public class TqsStack<T> {
    
    

    private Stack<T> stack = new Stack<>();
    private final int maxSize = 5;

    public TqsStack() {
        
    }

    public boolean isEmpty(){
        return stack.isEmpty();
    }

    public int size(){
        return stack.size();
    }

    public void push(T i){
        if (stack.size() >= maxSize) {
            throw new IllegalStateException();
        }

        stack.add(i);   
    }

    public T pop() throws Exception{
        if(stack.isEmpty()){
            throw new NoSuchElementException();
        }
        return stack.remove(stack.size()-1);
    }

    public T peek() throws Exception{
        if(stack.isEmpty()){
            throw new NoSuchElementException();
        }
        return stack.get(stack.size()-1);
    }

}
