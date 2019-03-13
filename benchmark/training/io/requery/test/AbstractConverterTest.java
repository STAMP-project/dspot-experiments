package io.requery.test;


import io.requery.Converter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by mluchi on 05/04/2017.
 */
public abstract class AbstractConverterTest<T extends Converter<FROM, TO>, FROM, TO> {
    @Test
    public void testConvertToPersisted() {
        Map<FROM, TO> testCases = getTestCases();
        for (FROM from : testCases.keySet()) {
            TO expectedConvertedValue = testCases.get(from);
            TO convertedValue = convertToPersisted(from);
            assertEquals(expectedConvertedValue, convertedValue);
        }
    }

    @Test
    public void testConvertToMapped() {
        Map<TO, FROM> testCases = new HashMap<>();
        for (Map.Entry<FROM, TO> entry : getTestCases().entrySet()) {
            testCases.put(entry.getValue(), entry.getKey());
        }
        Assert.assertTrue("Test cases map does not have unique values!", ((testCases.size()) == (getTestCases().size())));
        for (TO from : testCases.keySet()) {
            FROM expectedConvertedValue = testCases.get(from);
            FROM convertedValue = convertToMapped(getType(), from);
            assertEquals(expectedConvertedValue, convertedValue);
        }
    }
}

