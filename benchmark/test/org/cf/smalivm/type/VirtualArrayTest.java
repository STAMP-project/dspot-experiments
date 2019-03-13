package org.cf.smalivm.type;


import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cf.smalivm.dex.CommonTypes;
import org.junit.Assert;
import org.junit.Test;


public class VirtualArrayTest {
    private static final String TEST_DIRECTORY = "resources/test/smali";

    private static ClassManager classManager;

    private VirtualArray virtualArray;

    @Test
    public void twoDimensionalStringArrayHasExpectedImmediateAncestors() {
        String arrayType = "[[" + (CommonTypes.STRING);
        virtualArray = ((VirtualArray) (VirtualArrayTest.classManager.getVirtualType(arrayType)));
        String[] expectedImmediateAncestorNames = new String[]{ "[[Ljava/lang/Object;", "[[Ljava/lang/CharSequence;", "[[Ljava/lang/Comparable;", "[[Ljava/io/Serializable;" };
        Set<VirtualType> expectedImmediateAncestors = Arrays.stream(expectedImmediateAncestorNames).map(( a) -> VirtualArrayTest.classManager.getVirtualType(a)).collect(Collectors.toSet());
        Set<? extends VirtualType> ancestors = virtualArray.getImmediateAncestors();
        Assert.assertEquals(expectedImmediateAncestors, ancestors);
    }

    @Test
    public void oneDimensionalStringArrayHasExpectedImmediateAncestors() {
        String arrayType = "[" + (CommonTypes.STRING);
        virtualArray = ((VirtualArray) (VirtualArrayTest.classManager.getVirtualType(arrayType)));
        String[] expectedImmediateAncestorNames = new String[]{ "[Ljava/lang/Object;", "[Ljava/lang/CharSequence;", "[Ljava/lang/Comparable;", "[Ljava/io/Serializable;" };
        Set<VirtualType> expectedImmediateAncestors = Arrays.stream(expectedImmediateAncestorNames).map(( a) -> VirtualArrayTest.classManager.getVirtualType(a)).collect(Collectors.toSet());
        Set<? extends VirtualType> ancestors = virtualArray.getImmediateAncestors();
        Assert.assertEquals(expectedImmediateAncestors, ancestors);
    }

    @Test
    public void threeDimensionalStringArrayHasExpectedAncestors() {
        String arrayType = "[[[" + (CommonTypes.STRING);
        virtualArray = ((VirtualArray) (VirtualArrayTest.classManager.getVirtualType(arrayType)));
        String[] expectedAncestorNames = new String[]{ "[[[Ljava/lang/Object;", "[[[Ljava/lang/CharSequence;", "[[[Ljava/lang/Comparable;", "[[[Ljava/io/Serializable;", "[[Ljava/lang/Object;", "[Ljava/lang/Object;", "Ljava/lang/Object;" };
        Set<VirtualType> expectedAncestors = Arrays.stream(expectedAncestorNames).map(( a) -> VirtualArrayTest.classManager.getVirtualType(a)).collect(Collectors.toSet());
        Set<? extends VirtualType> ancestors = virtualArray.getAncestors();
        Assert.assertEquals(expectedAncestors, ancestors);
    }

    @Test
    public void threeDimensionalIntArrayHasExpectedAncestors() {
        String arrayType = "[[[" + (CommonTypes.INTEGER);
        virtualArray = ((VirtualArray) (VirtualArrayTest.classManager.getVirtualType(arrayType)));
        String[] expectedAncestorNames = new String[]{ "[[Ljava/lang/Object;", "[Ljava/lang/Object;", "Ljava/lang/Object;" };
        Set<VirtualType> expectedAncestors = Arrays.stream(expectedAncestorNames).map(( a) -> VirtualArrayTest.classManager.getVirtualType(a)).collect(Collectors.toSet());
        Set<? extends VirtualType> ancestors = virtualArray.getAncestors();
        Assert.assertEquals(expectedAncestors, ancestors);
    }
}

