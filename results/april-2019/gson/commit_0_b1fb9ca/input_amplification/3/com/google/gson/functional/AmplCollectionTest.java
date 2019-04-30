package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.common.TestTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import junit.framework.TestCase;


public class AmplCollectionTest extends TestCase {
    private Gson gson;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
            } else {
                if (obj instanceof Long) {
                    ints[i] = ((Long) (obj)).intValue();
                }
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

    public void testSetSerialization_add20() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add20__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_add20__3);
        boolean o_testSetSerialization_add20__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_add20__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add20__9 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add20__9);
        boolean o_testSetSerialization_add20__10 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add20__10);
        boolean o_testSetSerialization_add20__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add20__11);
        TestCase.assertTrue(o_testSetSerialization_add20__3);
        TestCase.assertTrue(o_testSetSerialization_add20__5);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        TestCase.assertTrue(o_testSetSerialization_add20__9);
        TestCase.assertTrue(o_testSetSerialization_add20__10);
    }

    public void testSetSerialization_literalMutationNumber2_add478_add3607() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber2__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber2__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber2_add478__14 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber2__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber2_add478_add3607__20 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber2_add478_add3607__20);
        boolean o_testSetSerialization_literalMutationNumber2__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber2_add478_add3607__20);
    }

    public void testSetSerialization_literalMutationString168255() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString168255__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString168255__3);
        boolean o_testSetSerialization_literalMutationString168255__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString168255__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString168255__9 = json.contains("z");
        TestCase.assertFalse(o_testSetSerialization_literalMutationString168255__9);
        boolean o_testSetSerialization_literalMutationString168255__10 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString168255__10);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString168255__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString168255__5);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationString168255__9);
    }

    public void testSetSerialization_add17_literalMutationString134() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add17__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add17__5 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_add17__7 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
        boolean o_testSetSerialization_add17__11 = json.contains("1");
        boolean o_testSetSerialization_add17__12 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber168248_literalMutationNumber168478() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber168248__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber168248__5 = set.add(new AmplCollectionTest.Entry(1));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationNumber168248__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber168248__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationNumber168251_literalMutationNumber168514() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber168251__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber168251__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber168251__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber168251__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationNumber168251_add168755() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber168251__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber168251__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber168251_add168755__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168251_add168755__14);
        boolean o_testSetSerialization_literalMutationNumber168251__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber168251__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168251_add168755__14);
    }

    public void testSetSerialization_literalMutationNumber168252() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber168252__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__3);
        boolean o_testSetSerialization_literalMutationNumber168252__5 = set.add(new AmplCollectionTest.Entry(3));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber168252__10 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__10);
        boolean o_testSetSerialization_literalMutationNumber168252__11 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber168252__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber168252__10);
    }

    public void testSetSerialization_literalMutationNumber3_literalMutationNumber366() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber3__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber3__6 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":4},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber3__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber3__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":4},{\"value\":0}]", json);
    }

    public void testSetSerialization_add168261null168833() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add168261__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add168261__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add168261null168833__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add168261null168833__11);
        String json = gson.toJson(null);
        TestCase.assertEquals("null", json);
        boolean o_testSetSerialization_add168261__10 = json.contains("1");
        boolean o_testSetSerialization_add168261__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add168261null168833__11);
        TestCase.assertEquals("null", json);
    }
}

