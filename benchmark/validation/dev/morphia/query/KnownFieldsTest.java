package dev.morphia.query;


import dev.morphia.TestBase;
import org.junit.Assert;
import org.junit.Test;


public class KnownFieldsTest extends TestBase {
    @Test
    public void testKnownFields() {
        Assert.assertNotNull(getDs().find(KnownFieldsTest.A.class).retrieveKnownFields());
    }

    private static class A {
        private String foo;

        private String bar;
    }
}

