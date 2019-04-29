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

    public void testSetSerialization_literalMutationNumber36433() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36433__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36433__3);
        boolean o_testSetSerialization_literalMutationNumber36433__6 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36433__6);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber36433__10 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36433__10);
        boolean o_testSetSerialization_literalMutationNumber36433__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36433__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36433__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36433__6);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36433__10);
    }

    public void testSetSerialization_literalMutationNumber3() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber3__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber3__3);
        boolean o_testSetSerialization_literalMutationNumber3__6 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber3__6);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber3__10 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber3__10);
        boolean o_testSetSerialization_literalMutationNumber3__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber3__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber3__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber3__6);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber3__10);
    }

    public void testSetSerialization_literalMutationNumber36433_literalMutationString36777() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36433__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber36433__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber36433__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber36433__11 = json.contains("");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationString15_add509() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString15_add509__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString15_add509__3);
        boolean o_testSetSerialization_literalMutationString15__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString15__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString15__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString15__10 = json.contains("?");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString15_add509__3);
        TestCase.assertEquals("[{\"value\":1},{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add36448_add36866() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add36448__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add36448__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add36448_add36866__13 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add36448_add36866__13);
        boolean o_testSetSerialization_add36448__9 = json.contains("1");
        boolean o_testSetSerialization_add36448__10 = json.contains("1");
        boolean o_testSetSerialization_add36448__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_add36448_add36866__13);
    }

    public void testSetSerialization_add16_add490() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add16__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add16__5 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add16__7 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add16_add490__17 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add16_add490__17);
        boolean o_testSetSerialization_add16__11 = json.contains("1");
        boolean o_testSetSerialization_add16__12 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
        TestCase.assertTrue(o_testSetSerialization_add16_add490__17);
    }

    public void testSetSerialization_literalMutationString10_add556() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString10__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString10__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString10_add556__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString10_add556__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString10__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString10__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString10_add556__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber9_literalMutationNumber255() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber9__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber9__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":3},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber9__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber9__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":3},{\"value\":0}]", json);
    }

    public void testSetSerialization_add36448_literalMutationString36573() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add36448__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add36448__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add36448__9 = json.contains("");
        boolean o_testSetSerialization_add36448__10 = json.contains("1");
        boolean o_testSetSerialization_add36448__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }
}

