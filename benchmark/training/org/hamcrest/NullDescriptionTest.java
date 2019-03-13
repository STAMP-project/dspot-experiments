package org.hamcrest;


import org.junit.Assert;
import org.junit.Test;


public final class NullDescriptionTest {
    private final Description.NullDescription nullDescription = new Description.NullDescription();

    @Test
    public void isUnchangedByAppendedText() {
        nullDescription.appendText("myText");
        Assert.assertEquals("", nullDescription.toString());
    }
}

