package com.baeldung.charstack;


import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class CharStackUnitTest {
    @Test
    public void whenCharStackIsCreated_thenItHasSize0() {
        CharStack charStack = new CharStack();
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenEmptyCharStack_whenElementIsPushed_thenStackSizeisIncreased() {
        CharStack charStack = new CharStack();
        charStack.push('A');
        Assert.assertEquals(1, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {
        CharStack charStack = new CharStack();
        charStack.push('A');
        char element = charStack.pop();
        Assert.assertEquals('A', element);
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
        CharStack charStack = new CharStack();
        charStack.push('A');
        char element = charStack.peek();
        Assert.assertEquals('A', element);
        Assert.assertEquals(1, charStack.size());
    }
}

