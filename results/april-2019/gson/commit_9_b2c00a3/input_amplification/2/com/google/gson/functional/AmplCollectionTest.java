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

    public void testSetSerialization_literalMutationNumber8404_add8883() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber8404__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber8404__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber8404__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber8404_add8883__17 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber8404_add8883__17);
        boolean o_testSetSerialization_literalMutationNumber8404__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber8404_add8883__17);
    }

    public void testSetSerialization_literalMutationNumber6_literalMutationNumber293() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber6__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber6__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber6__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber6__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationString11_literalMutationString248() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString11__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString11__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString11__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString11__10 = json.contains("r");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add19_literalMutationNumber364() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add19__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_add19__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_add19__9 = json.contains("1");
        boolean o_testSetSerialization_add19__10 = json.contains("1");
        boolean o_testSetSerialization_add19__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }
}

