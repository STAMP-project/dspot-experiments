package com.baeldung.java9.language;


import org.junit.Assert;
import org.junit.Test;


public class TryWithResourcesUnitTest {
    static int closeCount = 0;

    static class MyAutoCloseable implements AutoCloseable {
        final TryWithResourcesUnitTest.MyAutoCloseable.FinalWrapper finalWrapper = new TryWithResourcesUnitTest.MyAutoCloseable.FinalWrapper();

        public void close() {
            (TryWithResourcesUnitTest.closeCount)++;
        }

        static class FinalWrapper {
            public final AutoCloseable finalCloseable = new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    (TryWithResourcesUnitTest.closeCount)++;
                }
            };
        }
    }

    @Test
    public void tryWithResourcesTest() {
        TryWithResourcesUnitTest.MyAutoCloseable mac = new TryWithResourcesUnitTest.MyAutoCloseable();
        try  {
            Assert.assertEquals("Expected and Actual does not match", 0, TryWithResourcesUnitTest.closeCount);
        }
        try  {
            Assert.assertEquals("Expected and Actual does not match", 1, TryWithResourcesUnitTest.closeCount);
        } catch (Exception ex) {
        }
        try  {
            Assert.assertEquals("Expected and Actual does not match", 2, TryWithResourcesUnitTest.closeCount);
        } catch (Exception ex) {
        }
        try  {
            Assert.assertEquals("Expected and Actual does not match", 3, TryWithResourcesUnitTest.closeCount);
        } catch (Exception ex) {
        }
        try {
            throw new TryWithResourcesUnitTest.CloseableException();
        } catch (TryWithResourcesUnitTest.CloseableException ex) {
            try  {
                Assert.assertEquals("Expected and Actual does not match", 4, TryWithResourcesUnitTest.closeCount);
            }
        }
        Assert.assertEquals("Expected and Actual does not match", 5, TryWithResourcesUnitTest.closeCount);
    }

    static class CloseableException extends Exception implements AutoCloseable {
        @Override
        public void close() {
            (TryWithResourcesUnitTest.closeCount)++;
        }
    }
}

