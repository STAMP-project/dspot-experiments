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

    public void testSetSerialization_add18_literalMutationNumber162() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add18__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add18__5 = set.add(new AmplCollectionTest.Entry(0));
        String o_testSetSerialization_add18_literalMutationNumber162__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", o_testSetSerialization_add18_literalMutationNumber162__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_add18__10 = json.contains("1");
        boolean o_testSetSerialization_add18__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", o_testSetSerialization_add18_literalMutationNumber162__12);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationNumber3_literalMutationNumber436() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber3__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber3__6 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber3__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber3__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":3}]", json);
    }

    public void testSetSerialization_literalMutationString15_literalMutationString390() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString15__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString15__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString15__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString15__10 = json.contains("/");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber36441() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36441__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36441__3);
        boolean o_testSetSerialization_literalMutationNumber36441__6 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36441__6);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber36441__10 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36441__10);
        boolean o_testSetSerialization_literalMutationNumber36441__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36441__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36441__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber36441__6);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36441__10);
    }

    public void testSetSerialization_add36456_add36965() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add36456__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add36456__5 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_add36456__7 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
        boolean o_testSetSerialization_add36456_add36965__17 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add36456_add36965__17);
        boolean o_testSetSerialization_add36456__11 = json.contains("1");
        boolean o_testSetSerialization_add36456__12 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_add36456_add36965__17);
    }

    public void testSetSerialization_literalMutationNumber2_literalMutationNumber353() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber2__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber2__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber2__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber2__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString12_literalMutationString346() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString12__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString12__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString12__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString12__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString13_add508() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString13__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString13__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString13__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString13_add508__16 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13_add508__16);
        boolean o_testSetSerialization_literalMutationString13__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString13_add508__16);
    }

    public void testSetSerialization_literalMutationNumber1_literalMutationNumber309() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber1__3 = set.add(new AmplCollectionTest.Entry(3));
        boolean o_testSetSerialization_literalMutationNumber1__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber1__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber1__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":3}]", json);
    }

    public void testSetSerialization_literalMutationNumber9_literalMutationNumber400() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber9__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber9__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber9__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber9__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationNumber36443_add37015() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36443__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber36443__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber36443_add37015__14 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36443_add37015__14);
        boolean o_testSetSerialization_literalMutationNumber36443__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber36443__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber36443_add37015__14);
    }

    public void testSetSerialization_literalMutationNumber36440_literalMutationNumber36821() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber36440__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber36440__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber36440__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber36440__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }
}

