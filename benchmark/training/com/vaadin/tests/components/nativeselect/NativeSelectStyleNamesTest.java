package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class NativeSelectStyleNamesTest extends SingleBrowserTest {
    @Test
    public void correctStyleNames() {
        openTestURL();
        Set<String> expected = Stream.of("v-select", "v-widget").collect(Collectors.toSet());
        Assert.assertEquals(expected, $(NativeSelectElement.class).first().getClassNames());
    }
}

