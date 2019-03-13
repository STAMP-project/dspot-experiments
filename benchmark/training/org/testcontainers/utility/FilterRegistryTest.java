package org.testcontainers.utility;


import FilterRegistry.ACKNOWLEDGMENT;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.utility.ResourceReaper.FilterRegistry;


public class FilterRegistryTest {
    private static final List<Map.Entry<String, String>> FILTERS = Arrays.asList(new AbstractMap.SimpleEntry<>("key1!", "value2?"), new AbstractMap.SimpleEntry<>("key2#", "value2%"));

    private static final String URL_ENCODED_FILTERS = "key1%21=value2%3F&key2%23=value2%25";

    private static final byte[] ACKNOWLEDGEMENT = ACKNOWLEDGMENT.getBytes();

    private static final byte[] NO_ACKNOWLEDGEMENT = "".getBytes();

    private static final String NEW_LINE = "\n";

    @Test
    public void registerReturnsTrueIfAcknowledgementIsReadFromInputStream() throws IOException {
        FilterRegistry registry = new FilterRegistry(FilterRegistryTest.inputStream(FilterRegistryTest.ACKNOWLEDGEMENT), FilterRegistryTest.anyOutputStream());
        boolean successful = registry.register(FilterRegistryTest.FILTERS);
        Assert.assertTrue(successful);
    }

    @Test
    public void registerReturnsFalseIfNoAcknowledgementIsReadFromInputStream() throws IOException {
        FilterRegistry registry = new FilterRegistry(FilterRegistryTest.inputStream(FilterRegistryTest.NO_ACKNOWLEDGEMENT), FilterRegistryTest.anyOutputStream());
        boolean successful = registry.register(FilterRegistryTest.FILTERS);
        Assert.assertFalse(successful);
    }

    @Test
    public void registerWritesUrlEncodedFiltersAndNewlineToOutputStream() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FilterRegistry registry = new FilterRegistry(FilterRegistryTest.anyInputStream(), outputStream);
        registry.register(FilterRegistryTest.FILTERS);
        Assert.assertEquals(((FilterRegistryTest.URL_ENCODED_FILTERS) + (FilterRegistryTest.NEW_LINE)), new String(outputStream.toByteArray()));
    }
}

