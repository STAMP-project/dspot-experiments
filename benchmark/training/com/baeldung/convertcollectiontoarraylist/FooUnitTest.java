package com.baeldung.convertcollectiontoarraylist;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author chris
 */
public class FooUnitTest {
    private static Collection<Foo> srcCollection = new HashSet<>();

    public FooUnitTest() {
    }

    /**
     * Section 3. Using the ArrayList Constructor
     */
    @Test
    public void whenUsingConstructor_thenVerifyShallowCopy() {
        ArrayList<Foo> newList = new ArrayList(FooUnitTest.srcCollection);
        verifyShallowCopy(FooUnitTest.srcCollection, newList);
    }

    /**
     * Section 4. Using the Streams API
     */
    @Test
    public void whenUsingStream_thenVerifyShallowCopy() {
        ArrayList<Foo> newList = FooUnitTest.srcCollection.stream().collect(Collectors.toCollection(ArrayList::new));
        verifyShallowCopy(FooUnitTest.srcCollection, newList);
    }

    /**
     * Section 5. Deep Copy
     */
    @Test
    public void whenUsingDeepCopy_thenVerifyDeepCopy() {
        ArrayList<Foo> newList = FooUnitTest.srcCollection.stream().map(( foo) -> foo.deepCopy()).collect(Collectors.toCollection(ArrayList::new));
        verifyDeepCopy(FooUnitTest.srcCollection, newList);
    }

    /**
     * Section 6. Controlling the List Order
     */
    @Test
    public void whenUsingSortedStream_thenVerifySortOrder() {
        ArrayList<Foo> newList = FooUnitTest.srcCollection.stream().sorted(Comparator.comparing(Foo::getName)).collect(Collectors.toCollection(ArrayList::new));
        Assert.assertTrue("ArrayList is not sorted by name", FooUnitTest.isSorted(newList));
    }
}

