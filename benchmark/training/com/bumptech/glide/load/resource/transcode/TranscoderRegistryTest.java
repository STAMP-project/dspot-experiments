package com.bumptech.glide.load.resource.transcode;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class TranscoderRegistryTest {
    private TranscoderRegistry factories;

    @Test
    public void testReturnsUnitDecoderIfClassesAreIdentical() {
        Assert.assertEquals(UnitTranscoder.get(), factories.get(Object.class, Object.class));
    }

    @Test
    public void testCanRegisterAndRetrieveResourceTranscoder() {
        @SuppressWarnings("unchecked")
        ResourceTranscoder<File, String> transcoder = Mockito.mock(ResourceTranscoder.class);
        factories.register(File.class, String.class, transcoder);
        Assert.assertEquals(transcoder, factories.get(File.class, String.class));
    }

    @Test
    public void testDoesNotThrowIfRequestCanBeSatisfiedByUnitTranscoder() {
        // Assignable from.
        Assert.assertNotNull(factories.get(Integer.class, Number.class));
        // Equal to.
        Assert.assertNotNull(factories.get(Integer.class, Integer.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIfNoTranscoderRegistered() {
        factories.get(File.class, Integer.class);
    }
}

