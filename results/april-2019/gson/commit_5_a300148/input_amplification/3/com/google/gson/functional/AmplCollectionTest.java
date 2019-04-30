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

    public void testSetSerialization_literalMutationString298675_literalMutationNumber298811_literalMutationNumber299974() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298675__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationString298675__5 = set.add(new AmplCollectionTest.Entry(1));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString298675__9 = json.contains("/");
        boolean o_testSetSerialization_literalMutationString298675__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationString298674_add299141() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298674__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298674__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString298674_add299141__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString298674_add299141__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString298674__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString298674__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_literalMutationString298674_add299141__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add166744_add167273() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add166744__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add166744__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add166744__7 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add166744__7);
        String o_testSetSerialization_add166744_add167273__14 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add166744_add167273__14);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add166744__10 = json.contains("1");
        boolean o_testSetSerialization_add166744__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add166744__7);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add166744_add167273__14);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString166736_add167228() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString166736__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString166736__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString166736_add167228__13 = json.contains("");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString166736_add167228__13);
        boolean o_testSetSerialization_literalMutationString166736__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString166736__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString166736_add167228__13);
    }

    public void testSetSerialization_literalMutationString298677_add299147() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298677__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298677__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString298677_add299147__13 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298677_add299147__13);
        boolean o_testSetSerialization_literalMutationString298677__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString298677__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298677_add299147__13);
    }

    public void testSetSerialization_literalMutationString298675_add299118() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298675__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298675__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString298675__9 = json.contains("/");
        boolean o_testSetSerialization_literalMutationString298675_add299118__16 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298675_add299118__16);
        boolean o_testSetSerialization_literalMutationString298675__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298675_add299118__16);
    }

    public void testSetSerialization_literalMutationNumber298665_add299122() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber298665__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber298665__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber298665_add299122__14 = json.contains("1");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber298665_add299122__14);
        boolean o_testSetSerialization_literalMutationNumber298665__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber298665__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber298665_add299122__14);
    }

    public void testSetSerialization_literalMutationNumber298665_literalMutationString298832() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber298665__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber298665__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber298665__10 = json.contains("");
        boolean o_testSetSerialization_literalMutationNumber298665__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_add166746_add167258() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add166746_add167258__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_add166746_add167258__3);
        boolean o_testSetSerialization_add166746__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add166746__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add166746__9 = json.contains("1");
        boolean o_testSetSerialization_add166746__10 = json.contains("2");
        boolean o_testSetSerialization_add166746__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add166746_add167258__3);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationString298677_literalMutationString298915() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298677__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298677__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString298677__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString298677__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString298673_add299130() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298673__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298673_add299130__7 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298673_add299130__7);
        boolean o_testSetSerialization_literalMutationString298673__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString298673__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString298673__10 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString298673_add299130__7);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString298674_add299141null303217() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString298674__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString298674__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_literalMutationString298674_add299141__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_literalMutationString298674_add299141__11);
        String json = gson.toJson(null);
        TestCase.assertEquals("null", json);
        boolean o_testSetSerialization_literalMutationString298674__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString298674__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_literalMutationString298674_add299141__11);
        TestCase.assertEquals("null", json);
    }
}

