package com.orientechnologies.orient.core.sql;


import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexUnique;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OChainedIndexProxyFindBestIndexTest {
    @Test
    public void testTheOnlyChoice() throws Exception {
        final OIndexUnique expectedResult = mockUniqueIndex();
        final List<OIndex<?>> indexes = Arrays.<OIndex<?>>asList(expectedResult);
        final OIndex<?> result = OChainedIndexProxy.findBestIndex(indexes);
        Assert.assertSame(result, expectedResult);
    }

    @Test
    public void testDoNotUseIndexesWithNoNullValueSupport() throws Exception {
        final OIndexUnique expectedResult = mockUniqueIndex();
        final List<OIndex<?>> indexes = Arrays.<OIndex<?>>asList(mockUniqueCompositeHashIndex(), mockUniqueCompositeIndex(), expectedResult);
        final OIndex<?> result = OChainedIndexProxy.findBestIndex(indexes);
        Assert.assertSame(result, expectedResult);
    }

    @Test
    public void testDoNotUseCompositeHashIndex() throws Exception {
        final OIndexUnique expectedResult = mockUniqueIndex();
        final List<OIndex<?>> indexes = Arrays.<OIndex<?>>asList(mockUniqueCompositeHashIndexWithNullSupport(), expectedResult, mockUniqueCompositeHashIndexWithNullSupport());
        final OIndex<?> result = OChainedIndexProxy.findBestIndex(indexes);
        Assert.assertSame(result, expectedResult);
    }

    @Test
    public void testPriorityHashOverNonHash() throws Exception {
        final OIndexUnique expectedResult = mockUniqueHashIndex();
        final List<OIndex<?>> indexes = Arrays.<OIndex<?>>asList(mockUniqueIndex(), mockUniqueCompositeIndex(), expectedResult, mockUniqueIndex(), mockUniqueCompositeIndex());
        final OIndex<?> result = OChainedIndexProxy.findBestIndex(indexes);
        Assert.assertSame(result, expectedResult);
    }

    @Test
    public void testPriorityNonCompositeOverComposite() throws Exception {
        final OIndexUnique expectedResult = mockUniqueIndex();
        final List<OIndex<?>> indexes = Arrays.<OIndex<?>>asList(mockUniqueCompositeIndexWithNullSupport(), mockUniqueCompositeHashIndexWithNullSupport(), expectedResult, mockUniqueCompositeIndexWithNullSupport(), mockUniqueCompositeHashIndexWithNullSupport());
        final OIndex<?> result = OChainedIndexProxy.findBestIndex(indexes);
        Assert.assertSame(result, expectedResult);
    }
}

