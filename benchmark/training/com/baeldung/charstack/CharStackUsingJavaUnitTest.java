package com.baeldung.charstack;


import java.util.Stack;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class CharStackUsingJavaUnitTest {
    @Test
    public void whenCharStackIsCreated_thenItHasSize0() {
        Stack<Character> charStack = new Stack<>();
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenEmptyCharStack_whenElementIsPushed_thenStackSizeisIncreased() {
        Stack<Character> charStack = new Stack<>();
        charStack.push('A');
        Assert.assertEquals(1, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {
        Stack<Character> charStack = new Stack<>();
        charStack.push('A');
        char element = charStack.pop();
        Assert.assertEquals('A', element);
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
        Stack<Character> charStack = new Stack<>();
        charStack.push('A');
        char element = charStack.peek();
        Assert.assertEquals('A', element);
        Assert.assertEquals(1, charStack.size());
    }
}

