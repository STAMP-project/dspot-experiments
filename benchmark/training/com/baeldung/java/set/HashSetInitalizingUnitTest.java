package com.baeldung.java.set;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class HashSetInitalizingUnitTest {
    @Test
    public void whenUsingJava_usingArraysStaticMethod_thenCorrectSize() {
        Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void whenUsingJava_usingAnonymousClass_thenCorrectSize() {
        Set<String> set = new HashSet<String>() {
            {
                add("a");
                add("b");
                add("c");
            }
        };
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void whenUsingJava_creatingSingletonSet_thenCorrectSize() {
        Set<String> set = Collections.singleton("a");
        Assert.assertEquals(1, set.size());
    }

    @Test
    public void whenUsingJava_usingCustomStaticUtilMethod_thenCorrectSize() {
        Set<String> set = HashSetInitalizingUnitTest.newHashSet("a", "b", "c");
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void whenUsingJava8_usingCollectOnStream_thenCorrectSize() {
        Set<String> set = Stream.of("a", "b", "c").collect(Collectors.toCollection(HashSet::new));
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void whenUsingGoogleGuava_createMutableSet_thenCorrectSize() {
        Set<String> set = Sets.newHashSet("a", "b", "c");
        Assert.assertEquals(3, set.size());
    }

    @Test
    public void whenUsingGoogleGuava_createImmutableSet_thenCorrectSize() {
        Set<String> set = ImmutableSet.of("a", "b", "c");
        Assert.assertEquals(3, set.size());
    }
}

