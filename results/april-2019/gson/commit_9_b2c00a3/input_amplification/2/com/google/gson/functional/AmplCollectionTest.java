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

    public void testSetSerialization_add9740_literalMutationString10060() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add9740__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add9740__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add9740__9 = json.contains("1");
        boolean o_testSetSerialization_add9740__10 = json.contains("2");
        boolean o_testSetSerialization_add9740__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString9730_literalMutationNumber9875() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString9730__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationString9730__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString9730__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString9730__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_add9740_add10234() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add9740__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add9740__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add9740_add10234__13 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add9740_add10234__13);
        boolean o_testSetSerialization_add9740__9 = json.contains("1");
        boolean o_testSetSerialization_add9740__10 = json.contains("2");
        boolean o_testSetSerialization_add9740__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_add9740_add10234__13);
    }

    public void testSetSerialization_add9740_add10232() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add9740__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add9740_add10232__7 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_add9740_add10232__7);
        boolean o_testSetSerialization_add9740__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
        boolean o_testSetSerialization_add9740__9 = json.contains("1");
        boolean o_testSetSerialization_add9740__10 = json.contains("2");
        boolean o_testSetSerialization_add9740__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add9740_add10232__7);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber9727_add10167() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber9727__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber9727_add10167__7 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9727_add10167__7);
        boolean o_testSetSerialization_literalMutationNumber9727__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber9727__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber9727__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9727_add10167__7);
        TestCase.assertEquals("[{\"value\":0},{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationNumber9_add546() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber9__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber9_add546__7 = set.add(new AmplCollectionTest.Entry(3));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9_add546__7);
        boolean o_testSetSerialization_literalMutationNumber9__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":3},{\"value\":3},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationNumber9__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber9__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9_add546__7);
        TestCase.assertEquals("[{\"value\":3},{\"value\":3},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationNumber9728() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber9728__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__3);
        boolean o_testSetSerialization_literalMutationNumber9728__5 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber9728__10 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__10);
        boolean o_testSetSerialization_literalMutationNumber9728__11 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber9728__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber9728__10);
    }
}

