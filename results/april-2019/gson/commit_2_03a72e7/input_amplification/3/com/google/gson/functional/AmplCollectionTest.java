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

    public void testSetSerialization_add18_remove591() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add18__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add18__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add18__10 = json.contains("1");
        boolean o_testSetSerialization_add18__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
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

    public void testSetSerialization_literalMutationNumber164348_add164862() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber164348__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber164348__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber164348__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber164348_add164862__17 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber164348_add164862__17);
        boolean o_testSetSerialization_literalMutationNumber164348__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber164348_add164862__17);
    }

    public void testSetSerialization_add164359_literalMutationString164539() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add164359__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add164359__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add164359__9 = json.contains("s");
        boolean o_testSetSerialization_add164359__10 = json.contains("1");
        boolean o_testSetSerialization_add164359__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber7_add507() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber7__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber7__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber7_add507__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber7_add507__14);
        boolean o_testSetSerialization_literalMutationNumber7__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber7__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber7_add507__14);
    }

    public void testSetSerialization_literalMutationNumber4_add522() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber4__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber4__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber4_add522__14 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber4_add522__14);
        boolean o_testSetSerialization_literalMutationNumber4__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber4__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber4_add522__14);
    }

    public void testSetSerialization_literalMutationNumber164347_add164826() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber164347__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber164347__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationNumber164347_add164826__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164347_add164826__14);
        boolean o_testSetSerialization_literalMutationNumber164347__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber164347__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":1}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164347_add164826__14);
    }

    public void testSetSerialization_literalMutationNumber164346_literalMutationString164625_add168146() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber164346__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber164346_literalMutationString164625_add168146__7 = set.add(new AmplCollectionTest.Entry(4));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164346_literalMutationString164625_add168146__7);
        boolean o_testSetSerialization_literalMutationNumber164346__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber164346__10 = json.contains("n");
        boolean o_testSetSerialization_literalMutationNumber164346__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber164346_literalMutationString164625_add168146__7);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationString11_literalMutationString238() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString11__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString11__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString11__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString11__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString11_add513() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString11__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString11__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString11__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString11_add513__16 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString11_add513__16);
        boolean o_testSetSerialization_literalMutationString11__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString11_add513__16);
    }

    public void testSetSerialization_add164358_add164797() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add164358__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add164358__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add164358_add164797__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164358_add164797__11);
        String o_testSetSerialization_add164358_add164797__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164358_add164797__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add164358__10 = json.contains("1");
        boolean o_testSetSerialization_add164358__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164358_add164797__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add164358_add164797__12);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }
}

