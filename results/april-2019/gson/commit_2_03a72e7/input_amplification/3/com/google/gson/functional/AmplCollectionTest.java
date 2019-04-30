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

    public void testSetSerialization_literalMutationNumber3_literalMutationString425() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber3__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber3__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber3__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber3__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
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

    public void testSetSerialization_add164693_add165149() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add164693__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add164693__5 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_add164693__7 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add164693_add165149__15 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164693_add165149__15);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add164693__11 = json.contains("1");
        boolean o_testSetSerialization_add164693__12 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164693_add165149__15);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString164688_literalMutationString164926() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString164688__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString164688__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString164688__9 = json.contains("e");
        boolean o_testSetSerialization_literalMutationString164688__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber164685() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber164685__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__3);
        boolean o_testSetSerialization_literalMutationNumber164685__5 = set.add(new AmplCollectionTest.Entry(3));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber164685__10 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__10);
        boolean o_testSetSerialization_literalMutationNumber164685__11 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber164685__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164685__10);
    }

    public void testSetSerialization_literalMutationString14_literalMutationNumber229_add3907() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString14__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString14__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationString14_literalMutationNumber229_add3907__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString14_literalMutationNumber229_add3907__14);
        boolean o_testSetSerialization_literalMutationString14__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString14__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString14_literalMutationNumber229_add3907__14);
    }

    public void testSetSerialization_literalMutationString164688_literalMutationNumber164920() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString164688__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString164688__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationString164688__9 = json.contains("Y");
        boolean o_testSetSerialization_literalMutationString164688__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationNumber164684_literalMutationNumber165047() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber164684__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber164684__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber164684__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber164684__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationString164691_add165166() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString164691__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString164691__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString164691_add165166__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString164691_add165166__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString164691__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString164691__10 = json.contains("R");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString164691_add165166__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString164687_literalMutationString164962() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString164687__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString164687__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString164687__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString164687__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }
}

