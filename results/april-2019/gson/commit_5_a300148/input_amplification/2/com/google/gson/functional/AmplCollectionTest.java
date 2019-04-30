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

    public void testSetSerialization_literalMutationNumber36694_add37162() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36694_add37162__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36694_add37162__3);
        boolean o_testSetSerialization_literalMutationNumber36694__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber36694__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber36694__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber36694__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36694_add37162__3);
        TestCase.assertEquals("[{\"value\":0},{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_add78679_add79201() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add78679_add79201__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_add78679_add79201__3);
        boolean o_testSetSerialization_add78679__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add78679__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add78679__9 = json.contains("1");
        boolean o_testSetSerialization_add78679__10 = json.contains("1");
        boolean o_testSetSerialization_add78679__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add78679_add79201__3);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_add78680_add79197() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add78680__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add78680__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add78680_add79197__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add78680_add79197__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add78680__9 = json.contains("1");
        boolean o_testSetSerialization_add78680__10 = json.contains("2");
        boolean o_testSetSerialization_add78680__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add78680_add79197__11);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationString78673_literalMutationString78903() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString78673__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString78673__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString78673__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString78673__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationString36707_add37176() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString36707__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString36707__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString36707__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString36707_add37176__16 = json.contains("G");
        TestCase.assertFalse(o_testSetSerialization_literalMutationString36707_add37176__16);
        boolean o_testSetSerialization_literalMutationString36707__10 = json.contains("G");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationString36707_add37176__16);
    }

    public void testSetSerialization_literalMutationNumber78664_literalMutationString78936() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber78664__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber78664__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber78664__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber78664__11 = json.contains("t");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber78669_literalMutationNumber78798() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber78669__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber78669__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":3},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber78669__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber78669__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":3},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString36705_add37202() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString36705_add37202__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString36705_add37202__3);
        boolean o_testSetSerialization_literalMutationString36705__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString36705__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString36705__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString36705__10 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString36705_add37202__3);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":1}]", json);
    }
}

