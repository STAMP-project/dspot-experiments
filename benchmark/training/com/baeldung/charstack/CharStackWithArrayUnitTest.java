package com.baeldung.charstack;


import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class CharStackWithArrayUnitTest {
    @Test
    public void whenCharStackIsCreated_thenItHasSize0() {
        CharStackWithArray charStack = new CharStackWithArray();
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenEmptyCharStack_whenElementIsPushed_thenStackSizeisIncreased() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');
        Assert.assertEquals(1, charStack.size());
    }

    @Test
    public void givenEmptyCharStack_when5ElementIsPushed_thenStackSizeis() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');
        charStack.push('B');
        charStack.push('C');
        charStack.push('D');
        charStack.push('E');
        Assert.assertEquals(5, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPoppedFromStack_thenElementIsRemovedAndSizeChanges() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');
        char element = charStack.pop();
        Assert.assertEquals('A', element);
        Assert.assertEquals(0, charStack.size());
    }

    @Test
    public void givenCharStack_whenElementIsPeeked_thenElementIsNotRemovedAndSizeDoesNotChange() {
        CharStackWithArray charStack = new CharStackWithArray();
        charStack.push('A');
        char element = charStack.peek();
        Assert.assertEquals('A', element);
        Assert.assertEquals(1, charStack.size());
    }
}

