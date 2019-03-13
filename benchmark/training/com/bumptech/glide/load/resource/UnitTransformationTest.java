package com.bumptech.glide.load.resource;


import android.app.Application;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class UnitTransformationTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    private Application app;

    @Test
    public void testReturnsGivenResource() {
        Resource<Object> resource = Util.mockResource();
        UnitTransformation<Object> transformation = UnitTransformation.get();
        Assert.assertEquals(resource, transformation.transform(app, resource, 10, 10));
    }

    @Test
    public void testEqualsHashCodeDigest() throws NoSuchAlgorithmException {
        @SuppressWarnings("unchecked")
        Transformation<Object> other = Mockito.mock(Transformation.class);
        Mockito.doAnswer(new Util.WriteDigest("other")).when(other).updateDiskCacheKey(ArgumentMatchers.any(MessageDigest.class));
        keyTester.addEquivalenceGroup(UnitTransformation.get(), UnitTransformation.get()).addEquivalenceGroup(other).addEmptyDigestRegressionTest(UnitTransformation.get()).test();
    }
}

