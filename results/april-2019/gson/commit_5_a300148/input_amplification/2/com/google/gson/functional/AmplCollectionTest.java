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

    public void testSetSerialization_literalMutationNumber32397_add32878() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber32397__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber32397__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber32397__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber32397_add32878__17 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber32397_add32878__17);
        boolean o_testSetSerialization_literalMutationNumber32397__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber32397_add32878__17);
    }
}

