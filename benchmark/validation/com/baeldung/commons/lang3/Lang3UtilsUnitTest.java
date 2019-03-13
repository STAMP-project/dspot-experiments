package com.baeldung.commons.lang3;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.arch.Processor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.commons.lang3.event.EventUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Assert;
import org.junit.Test;


public class Lang3UtilsUnitTest {
    @Test
    public void test_to_Boolean_fromString() {
        Assert.assertFalse(BooleanUtils.toBoolean("off"));
        Assert.assertTrue(BooleanUtils.toBoolean("true"));
        Assert.assertTrue(BooleanUtils.toBoolean("tRue"));
        Assert.assertFalse(BooleanUtils.toBoolean("no"));
        Assert.assertFalse(BooleanUtils.isTrue(Boolean.FALSE));
        Assert.assertFalse(BooleanUtils.isTrue(null));
    }

    @Test
    public void testGetUserHome() {
        final File dir = SystemUtils.getUserHome();
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.exists());
    }

    @Test
    public void testGetJavaHome() {
        final File dir = SystemUtils.getJavaHome();
        Assert.assertNotNull(dir);
        Assert.assertTrue(dir.exists());
    }

    @Test
    public void testProcessorArchType() {
        Processor processor = ArchUtils.getProcessor("x86");
        Assert.assertTrue(processor.is32Bit());
        Assert.assertFalse(processor.is64Bit());
    }

    @Test
    public void testProcessorArchType64Bit() {
        Processor processor = ArchUtils.getProcessor("x86_64");
        Assert.assertFalse(processor.is32Bit());
        Assert.assertTrue(processor.is64Bit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConcurrentRuntimeExceptionCauseError() {
        new ConcurrentRuntimeException("An error", new Error());
    }

    @Test
    public void testConstantFuture_Integer() throws Exception {
        Future<Integer> test = ConcurrentUtils.constantFuture(5);
        Assert.assertTrue(test.isDone());
        Assert.assertSame(5, test.get());
        Assert.assertFalse(test.isCancelled());
    }

    @Test
    public void testFieldUtilsGetAllFields() {
        final Field[] fieldsNumber = Number.class.getDeclaredFields();
        Assert.assertArrayEquals(fieldsNumber, FieldUtils.getAllFields(Number.class));
    }

    @Test
    public void test_getInstance_String_Locale() {
        final FastDateFormat format1 = FastDateFormat.getInstance("MM/DD/yyyy", Locale.US);
        final FastDateFormat format3 = FastDateFormat.getInstance("MM/DD/yyyy", Locale.GERMANY);
        Assert.assertNotSame(format1, format3);
    }

    @Test
    public void testAddEventListenerThrowsException() {
        final Lang3UtilsUnitTest.ExceptionEventSource src = new Lang3UtilsUnitTest.ExceptionEventSource();
        try {
            EventUtils.addEventListener(src, PropertyChangeListener.class, (PropertyChangeEvent e) -> {
                /* Change event */
            });
            Assert.fail("Add method should have thrown an exception, so method should fail.");
        } catch (final RuntimeException e) {
        }
    }

    @Test
    public void ConcurrentExceptionSample() throws ConcurrentException {
        final Error err = new AssertionError("Test");
        try {
            ConcurrentUtils.handleCause(new ExecutionException(err));
            Assert.fail("Error not thrown!");
        } catch (final Error e) {
            Assert.assertEquals("Wrong error", err, e);
        }
    }

    public static class ExceptionEventSource {
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testLazyInitializer() throws Exception {
        SampleLazyInitializer sampleLazyInitializer = new SampleLazyInitializer();
        SampleObject sampleObjectOne = sampleLazyInitializer.get();
        SampleObject sampleObjectTwo = sampleLazyInitializer.get();
        Assert.assertEquals(sampleObjectOne, sampleObjectTwo);
    }

    @Test
    public void testBuildDefaults() {
        BasicThreadFactory.Builder builder = new BasicThreadFactory.Builder();
        BasicThreadFactory factory = builder.build();
        Assert.assertNull("No naming pattern set Yet", factory.getNamingPattern());
        BasicThreadFactory factory2 = builder.namingPattern("sampleNamingPattern").daemon(true).priority(Thread.MIN_PRIORITY).build();
        Assert.assertNotNull("Got a naming pattern", factory2.getNamingPattern());
        Assert.assertEquals("sampleNamingPattern", factory2.getNamingPattern());
    }
}

