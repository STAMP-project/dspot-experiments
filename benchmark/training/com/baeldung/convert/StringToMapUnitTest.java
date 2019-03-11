package com.baeldung.convert;


import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class StringToMapUnitTest {
    @Test
    public void givenString_WhenUsingStream_ThenResultingStringIsCorrect() {
        Map<String, String> wordsByKey = StringToMap.convertWithStream("1=one,2=two,3=three,4=four");
        Assert.assertEquals(4, wordsByKey.size());
        Assert.assertEquals("one", wordsByKey.get("1"));
    }
}

