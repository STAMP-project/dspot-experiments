package com.baeldung.string;


import org.junit.Assert;
import org.junit.Test;


public class ReplaceCharInStringUnitTest {
    private ReplaceCharacterInString characterInString = new ReplaceCharacterInString();

    @Test
    public void whenReplaceCharAtIndexUsingSubstring_thenSuccess() {
        Assert.assertEquals("abcme", characterInString.replaceCharSubstring("abcde", 'm', 3));
    }

    @Test
    public void whenReplaceCharAtIndexUsingCharArray_thenSuccess() {
        Assert.assertEquals("abcme", characterInString.replaceCharUsingCharArray("abcde", 'm', 3));
    }

    @Test
    public void whenReplaceCharAtIndexUsingStringBuilder_thenSuccess() {
        Assert.assertEquals("abcme", characterInString.replaceCharStringBuilder("abcde", 'm', 3));
    }

    @Test
    public void whenReplaceCharAtIndexUsingStringBuffer_thenSuccess() {
        Assert.assertEquals("abcme", characterInString.replaceCharStringBuffer("abcde", 'm', 3));
    }
}

