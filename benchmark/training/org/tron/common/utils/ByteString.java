package org.tron.common.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;


public class ByteString {
    @Test
    public void test() {
        List<com.google.protobuf.ByteString> list1 = new ArrayList<>();
        List<com.google.protobuf.ByteString> list2 = new ArrayList<>();
        com.google.protobuf.ByteString one = com.google.protobuf.ByteString.copyFromUtf8("1111");
        com.google.protobuf.ByteString one2 = com.google.protobuf.ByteString.copyFromUtf8("1111");
        com.google.protobuf.ByteString two = com.google.protobuf.ByteString.copyFromUtf8("2222");
        com.google.protobuf[] array = new com.google.protobuf.ByteString[]{ one, two };
        com.google.protobuf[] array2 = new com.google.protobuf.ByteString[]{ two, one2 };
        list1.addAll(Arrays.asList(array));
        list2.addAll(Arrays.asList(array2));
        Assert.assertEquals(true, CollectionUtils.isEqualCollection(list1, list2));
        list2.clear();
        list2.add(one2);
        Assert.assertEquals(false, CollectionUtils.isEqualCollection(list1, list2));
        list1.clear();
        list2.clear();
        list1.add(one);
        list2.addAll(Arrays.asList(array2));
        Assert.assertEquals(false, CollectionUtils.isEqualCollection(list1, list2));
    }
}

