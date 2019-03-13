package com.bumptech.glide.load;


import android.app.Application;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.tests.KeyTester;
import com.bumptech.glide.tests.Util;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class MultiTransformationTest {
    @Rule
    public final KeyTester keyTester = new KeyTester();

    @Mock
    private Transformation<Object> first;

    @Mock
    private Transformation<Object> second;

    @Mock
    private Resource<Object> initial;

    @Mock
    private Resource<Object> firstTransformed;

    @Mock
    private Resource<Object> secondTransformed;

    private Application context;

    @Test
    public void testAppliesTransformationsInOrder() {
        final int width = 584;
        final int height = 768;
        MultiTransformation<Object> transformation = new MultiTransformation(first, second);
        Mockito.when(first.transform(Util.anyContext(), ArgumentMatchers.eq(initial), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height))).thenReturn(firstTransformed);
        Mockito.when(second.transform(Util.anyContext(), ArgumentMatchers.eq(firstTransformed), ArgumentMatchers.eq(width), ArgumentMatchers.eq(height))).thenReturn(secondTransformed);
        Assert.assertEquals(secondTransformed, transformation.transform(context, initial, width, height));
    }

    @Test
    public void testInitialResourceIsNotRecycled() {
        Mockito.when(first.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(firstTransformed);
        MultiTransformation<Object> transformation = new MultiTransformation(first);
        transformation.transform(context, initial, 123, 456);
        Mockito.verify(initial, Mockito.never()).recycle();
    }

    @Test
    public void testInitialResourceIsNotRecycledEvenIfReturnedByMultipleTransformations() {
        Mockito.when(first.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(initial);
        Mockito.when(second.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(initial);
        MultiTransformation<Object> transformation = new MultiTransformation(first, second);
        transformation.transform(context, initial, 1111, 2222);
        Mockito.verify(initial, Mockito.never()).recycle();
    }

    @Test
    public void testInitialResourceIsNotRecycledIfReturnedByOneTransformationButNotByALaterTransformation() {
        Mockito.when(first.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(initial);
        Mockito.when(second.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(Util.mockResource());
        MultiTransformation<Object> transformation = new MultiTransformation(first, second);
        transformation.transform(context, initial, 1, 2);
        Mockito.verify(initial, Mockito.never()).recycle();
    }

    @Test
    public void testFinalResourceIsNotRecycled() {
        Mockito.when(first.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(firstTransformed);
        MultiTransformation<Object> transformation = new MultiTransformation(first);
        transformation.transform(context, Util.mockResource(), 111, 222);
        Mockito.verify(firstTransformed, Mockito.never()).recycle();
    }

    @Test
    public void testIntermediateResourcesAreRecycled() {
        Mockito.when(first.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(firstTransformed);
        Mockito.when(second.transform(Util.anyContext(), Util.anyResource(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(secondTransformed);
        MultiTransformation<Object> transformation = new MultiTransformation(first, second);
        transformation.transform(context, Util.mockResource(), 233, 454);
        Mockito.verify(firstTransformed).recycle();
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        keyTester.addEquivalenceGroup(new MultiTransformation(first), new MultiTransformation(first)).addEquivalenceGroup(new MultiTransformation(second)).addEquivalenceGroup(new MultiTransformation(first, second)).addEquivalenceGroup(new MultiTransformation(second, first)).addRegressionTest(new MultiTransformation(first), "a7937b64b8caa58f03721bb6bacf5c78cb235febe0e70b1b84cd99541461a08e").addRegressionTest(new MultiTransformation(first, second), "da83f63e1a473003712c18f5afc5a79044221943d1083c7c5a7ac7236d85e8d2").test();
    }
}

