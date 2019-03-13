package com.blankj.utilcode.util;


import android.support.v4.util.LongSparseArray;
import android.support.v4.util.SimpleArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import java.util.HashMap;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/12/24
 *     desc  : test ObjectUtils
 * </pre>
 */
public class ObjectUtilsTest extends BaseTest {
    @Test
    public void isEmpty() {
        StringBuilder sb = new StringBuilder("");
        StringBuilder sb1 = new StringBuilder(" ");
        String string = "";
        String string1 = " ";
        int[][] arr = new int[][]{  };
        LinkedList<Integer> list = new LinkedList<>();
        HashMap<String, Integer> map = new HashMap<>();
        SimpleArrayMap<String, Integer> sam = new SimpleArrayMap();
        SparseArray<String> sa = new SparseArray();
        SparseBooleanArray sba = new SparseBooleanArray();
        SparseIntArray sia = new SparseIntArray();
        SparseLongArray sla = new SparseLongArray();
        LongSparseArray<String> lsa = new LongSparseArray();
        android.util.LongSparseArray<String> lsaV4 = new android.util.LongSparseArray();
        Assert.assertTrue(ObjectUtils.isEmpty(sb));
        Assert.assertFalse(ObjectUtils.isEmpty(sb1));
        Assert.assertTrue(ObjectUtils.isEmpty(string));
        Assert.assertFalse(ObjectUtils.isEmpty(string1));
        Assert.assertTrue(ObjectUtils.isEmpty(arr));
        Assert.assertTrue(ObjectUtils.isEmpty(list));
        Assert.assertTrue(ObjectUtils.isEmpty(map));
        Assert.assertTrue(ObjectUtils.isEmpty(sam));
        Assert.assertTrue(ObjectUtils.isEmpty(sa));
        Assert.assertTrue(ObjectUtils.isEmpty(sba));
        Assert.assertTrue(ObjectUtils.isEmpty(sia));
        Assert.assertTrue(ObjectUtils.isEmpty(sla));
        Assert.assertTrue(ObjectUtils.isEmpty(lsa));
        Assert.assertTrue(ObjectUtils.isEmpty(lsaV4));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sb))));
        Assert.assertFalse((!(ObjectUtils.isNotEmpty(sb1))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(string))));
        Assert.assertFalse((!(ObjectUtils.isNotEmpty(string1))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(arr))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(list))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(map))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sam))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sa))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sba))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sia))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(sla))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(lsa))));
        Assert.assertTrue((!(ObjectUtils.isNotEmpty(lsaV4))));
    }

    @Test
    public void equals() {
        Assert.assertTrue(ObjectUtils.equals(1, 1));
        Assert.assertTrue(ObjectUtils.equals("str", "str"));
        Assert.assertTrue(ObjectUtils.equals(null, null));
        Assert.assertFalse(ObjectUtils.equals(null, 1));
        Assert.assertFalse(ObjectUtils.equals(null, ""));
    }
}

