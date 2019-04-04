package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.common.TestTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplCollectionTest {
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    static class HasArrayListField {
        ArrayList<Long> longs = new ArrayList<Long>();
    }

    @SuppressWarnings("rawtypes")
    private static int[] toIntArray(Collection collection) {
        int[] ints = new int[collection.size()];
        int i = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ++i) {
            Object obj = iterator.next();
            if (obj instanceof Integer) {
                ints[i] = ((Integer) (obj)).intValue();
            }else
                if (obj instanceof Long) {
                    ints[i] = ((Long) (obj)).intValue();
                }

        }
        return ints;
    }

    private static class ObjectWithWildcardCollection {
        private final Collection<? extends TestTypes.BagOfPrimitives> collection;

        public ObjectWithWildcardCollection(Collection<? extends TestTypes.BagOfPrimitives> collection) {
            this.collection = collection;
        }

        public Collection<? extends TestTypes.BagOfPrimitives> getCollection() {
            return collection;
        }
    }

    private static class Entry {
        int value;

        Entry(int value) {
            this.value = value;
        }
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationString180879_literalMutationString181320() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString180879__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString180879__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString180879__9 = json.contains("/LJ[::wS_d+Ga4");
        boolean o_testSetSerialization_literalMutationString180879__10 = json.contains("2");
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180893_failAssert742_add181367_add183410() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180893_failAssert742_add181367__5 = set.add(new AmplCollectionTest.Entry(1));
            boolean o_testSetSerializationnull180893_failAssert742_add181367__7 = set.add(new AmplCollectionTest.Entry(1));
            boolean o_testSetSerializationnull180893_failAssert742_add181367__9 = set.add(new AmplCollectionTest.Entry(2));
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":1},{\"value\":1},{\"value\":2}]", json);
            boolean o_testSetSerializationnull180893_failAssert742_add181367__13 = json.contains("1");
            json.contains(null);
            json.contains(null);
            org.junit.Assert.fail("testSetSerializationnull180893 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180893_failAssert742_add181387_add182972() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180893_failAssert742_add181387__5 = set.add(new AmplCollectionTest.Entry(1));
            boolean o_testSetSerializationnull180893_failAssert742_add181387_add182972__9 = set.add(new AmplCollectionTest.Entry(2));
            Assert.assertTrue(o_testSetSerializationnull180893_failAssert742_add181387_add182972__9);
            boolean o_testSetSerializationnull180893_failAssert742_add181387__7 = set.add(new AmplCollectionTest.Entry(2));
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":2},{\"value\":2},{\"value\":1}]", json);
            boolean o_testSetSerializationnull180893_failAssert742_add181387__11 = json.contains("1");
            boolean o_testSetSerializationnull180893_failAssert742_add181387__12 = json.contains("1");
            json.contains(null);
            org.junit.Assert.fail("testSetSerializationnull180893 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180893_failAssert742_add181373_add184366() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180893_failAssert742_add181373__5 = set.add(new AmplCollectionTest.Entry(1));
            boolean o_testSetSerializationnull180893_failAssert742_add181373__7 = set.add(new AmplCollectionTest.Entry(2));
            boolean o_testSetSerializationnull180893_failAssert742_add181373__9 = set.add(new AmplCollectionTest.Entry(2));
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
            boolean o_testSetSerializationnull180893_failAssert742_add181373_add184366__19 = json.contains("1");
            Assert.assertTrue(o_testSetSerializationnull180893_failAssert742_add181373_add184366__19);
            boolean o_testSetSerializationnull180893_failAssert742_add181373__13 = json.contains("1");
            json.contains(null);
            org.junit.Assert.fail("testSetSerializationnull180893 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull320707_failAssert1471_literalMutationString320962() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__5 = set.add(new AmplCollectionTest.Entry(1));
            Assert.assertTrue(o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__5);
            boolean o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__7 = set.add(new AmplCollectionTest.Entry(2));
            Assert.assertTrue(o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__7);
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
            boolean o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__11 = json.contains("");
            Assert.assertTrue(o_testSetSerializationnull320707_failAssert1471_literalMutationString320962__11);
            json.contains(null);
            org.junit.Assert.fail("testSetSerializationnull320707 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180892_failAssert741_add181403() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180892_failAssert741_add181403__5 = set.add(new AmplCollectionTest.Entry(1));
            Assert.assertTrue(o_testSetSerializationnull180892_failAssert741_add181403__5);
            boolean o_testSetSerializationnull180892_failAssert741_add181403__7 = set.add(new AmplCollectionTest.Entry(2));
            Assert.assertTrue(o_testSetSerializationnull180892_failAssert741_add181403__7);
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
            json.contains(null);
            json.contains("2");
            org.junit.Assert.fail("testSetSerializationnull180892 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationString180878() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString180878__3 = set.add(new AmplCollectionTest.Entry(1));
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__3);
        boolean o_testSetSerialization_literalMutationString180878__5 = set.add(new AmplCollectionTest.Entry(2));
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__5);
        String json = gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString180878__9 = json.contains("");
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__9);
        boolean o_testSetSerialization_literalMutationString180878__10 = json.contains("2");
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__10);
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__3);
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__5);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        Assert.assertTrue(o_testSetSerialization_literalMutationString180878__9);
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationString180883_literalMutationNumber181054() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString180883__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationString180883__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationString180883__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString180883__10 = json.contains("K");
        Assert.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationString320693_add321177() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString320693__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString320693__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString320693_add321177__11 = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_literalMutationString320693_add321177__11);
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString320693__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString320693__10 = json.contains("2");
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_literalMutationString320693_add321177__11);
        Assert.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180893_failAssert742_literalMutationNumber181111_literalMutationNumber183997() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180893_failAssert742_literalMutationNumber181111__5 = set.add(new AmplCollectionTest.Entry(2));
            boolean o_testSetSerializationnull180893_failAssert742_literalMutationNumber181111__7 = set.add(new AmplCollectionTest.Entry(4));
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":2},{\"value\":4}]", json);
            boolean o_testSetSerializationnull180893_failAssert742_literalMutationNumber181111__12 = json.contains("1");
            json.contains(null);
            org.junit.Assert.fail("testSetSerializationnull180893 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerialization_add180887() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add180887__3 = set.add(new AmplCollectionTest.Entry(1));
        Assert.assertTrue(o_testSetSerialization_add180887__3);
        boolean o_testSetSerialization_add180887__5 = set.add(new AmplCollectionTest.Entry(2));
        Assert.assertTrue(o_testSetSerialization_add180887__5);
        String json = gson.toJson(set);
        Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add180887__9 = json.contains("1");
        Assert.assertTrue(o_testSetSerialization_add180887__9);
        boolean o_testSetSerialization_add180887__10 = json.contains("1");
        Assert.assertTrue(o_testSetSerialization_add180887__10);
        boolean o_testSetSerialization_add180887__11 = json.contains("2");
        Assert.assertTrue(o_testSetSerialization_add180887__11);
        Assert.assertTrue(o_testSetSerialization_add180887__3);
        Assert.assertTrue(o_testSetSerialization_add180887__5);
        Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        Assert.assertTrue(o_testSetSerialization_add180887__9);
        Assert.assertTrue(o_testSetSerialization_add180887__10);
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationString180878_literalMutationNumber181033() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString180878__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString180878__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":4},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString180878__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString180878__10 = json.contains("2");
        Assert.assertEquals("[{\"value\":4},{\"value\":1}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerializationnull180892_failAssert741_add181390() throws Exception {
        try {
            Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
            boolean o_testSetSerializationnull180892_failAssert741_add181390__5 = set.add(new AmplCollectionTest.Entry(1));
            Assert.assertTrue(o_testSetSerializationnull180892_failAssert741_add181390__5);
            boolean o_testSetSerializationnull180892_failAssert741_add181390__7 = set.add(new AmplCollectionTest.Entry(2));
            Assert.assertTrue(o_testSetSerializationnull180892_failAssert741_add181390__7);
            String json = this.gson.toJson(set);
            Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
            json.contains(null);
            json.contains(null);
            json.contains("2");
            org.junit.Assert.fail("testSetSerializationnull180892 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationNumber180869_literalMutationNumber181087() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180869__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber180869__6 = set.add(new AmplCollectionTest.Entry(4));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":4},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber180869__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180869__11 = json.contains("2");
        Assert.assertEquals("[{\"value\":4},{\"value\":2}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerialization_literalMutationNumber180869_literalMutationNumber181041() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180869__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber180869__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber180869__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180869__11 = json.contains("2");
        Assert.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }

    @Test(timeout = 10000)
    public void testSetSerialization_add320701_add321225() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add320701__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add320701__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = this.gson.toJson(set);
        Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add320701__9 = json.contains("1");
        boolean o_testSetSerialization_add320701_add321225__16 = json.contains("1");
        Assert.assertTrue(o_testSetSerialization_add320701_add321225__16);
        boolean o_testSetSerialization_add320701__10 = json.contains("1");
        boolean o_testSetSerialization_add320701__11 = json.contains("2");
        Assert.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        Assert.assertTrue(o_testSetSerialization_add320701_add321225__16);
    }
}

