package com.fasterxml.jackson.core;


import com.fasterxml.jackson.core.io.SerializedString;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for class {@link JsonpCharacterEscapes}.
 *
 * @unknown 2017-07-20
 * @see JsonpCharacterEscapes
 */
public class JsonpCharacterEscapesTest {
    @Test
    public void testGetEscapeSequenceOne() {
        JsonpCharacterEscapes jsonpCharacterEscapes = JsonpCharacterEscapes.instance();
        Assert.assertEquals(new SerializedString("\\u2028"), jsonpCharacterEscapes.getEscapeSequence(8232));
    }

    @Test
    public void testGetEscapeSequenceTwo() {
        JsonpCharacterEscapes jsonpCharacterEscapes = JsonpCharacterEscapes.instance();
        Assert.assertEquals(new SerializedString("\\u2029"), jsonpCharacterEscapes.getEscapeSequence(8233));
    }
}

