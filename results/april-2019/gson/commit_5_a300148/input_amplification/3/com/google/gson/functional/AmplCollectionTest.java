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

    public void testSetSerialization_literalMutationString180312_add180836_add182640() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString180312__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString180312__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString180312_add180836__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString180312_add180836__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString180312_add180836_add182640__16 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString180312_add180836_add182640__16);
        boolean o_testSetSerialization_literalMutationString180312__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString180312__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString180312_add180836__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString180312_add180836_add182640__16);
    }

    public void testSetSerialization_add180315_add180754() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add180315__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add180315__5 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_add180315_add180754__11 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_add180315_add180754__11);
        boolean o_testSetSerialization_add180315__7 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":2},{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add180315__11 = json.contains("1");
        boolean o_testSetSerialization_add180315__12 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add180315_add180754__11);
        TestCase.assertEquals("[{\"value\":2},{\"value\":2},{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber180302_add180858() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180302__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber180302__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber180302__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180302_add180858__17 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180302_add180858__17);
        boolean o_testSetSerialization_literalMutationNumber180302__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180302_add180858__17);
    }

    public void testSetSerialization_literalMutationNumber180302_add180857_add182456() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180302__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber180302__6 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationNumber180302_add180857_add182456__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", o_testSetSerialization_literalMutationNumber180302_add180857_add182456__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber180302_add180857__14 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180302__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180302__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", o_testSetSerialization_literalMutationNumber180302_add180857_add182456__12);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }
}

