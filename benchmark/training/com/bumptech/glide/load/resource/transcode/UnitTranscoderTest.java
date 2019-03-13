package com.bumptech.glide.load.resource.transcode;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class UnitTranscoderTest {
    @Test
    public void testReturnsTheGivenResource() {
        Resource<Object> resource = Util.mockResource();
        ResourceTranscoder<Object, Object> unitTranscoder = UnitTranscoder.get();
        Assert.assertEquals(resource, unitTranscoder.transcode(resource, new Options()));
    }
}

