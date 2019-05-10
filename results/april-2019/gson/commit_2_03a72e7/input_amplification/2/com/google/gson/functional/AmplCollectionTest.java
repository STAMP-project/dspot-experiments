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

    public void testSetSerialization_add19_literalMutationNumber378() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add19__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add19__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_add19__9 = json.contains("1");
        boolean o_testSetSerialization_add19__10 = json.contains("1");
        boolean o_testSetSerialization_add19__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationNumber32074_literalMutationString32264() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber32074__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber32074__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber32074__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber32074__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString12_literalMutationNumber198() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString12__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString12__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString12__9 = json.contains("d");
        boolean o_testSetSerialization_literalMutationString12__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationNumber32078_add32547() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber32078__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber32078__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber32078_add32547__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber32078_add32547__14);
        boolean o_testSetSerialization_literalMutationNumber32078__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber32078__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber32078_add32547__14);
    }

    public void testSetSerialization_literalMutationString10() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString10__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__3);
        boolean o_testSetSerialization_literalMutationString10__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString10__9 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__9);
        boolean o_testSetSerialization_literalMutationString10__10 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__10);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString10__9);
    }

    public void testSetSerialization_add18_remove595() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add18__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add18__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add18__10 = json.contains("1");
        boolean o_testSetSerialization_add18__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }
}

