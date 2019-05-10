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

    public void testSetSerialization_literalMutationNumber6_add513_literalMutationNumber1118() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber6__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber6__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":4},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber6_add513__14 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber6__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber6__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":4},{\"value\":0}]", json);
    }

    public void testSetSerialization_add20_add550() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add20__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add20__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add20__9 = json.contains("1");
        boolean o_testSetSerialization_add20_add550__16 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_add20_add550__16);
        boolean o_testSetSerialization_add20__10 = json.contains("2");
        boolean o_testSetSerialization_add20__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_add20_add550__16);
    }

    public void testSetSerialization_literalMutationNumber5_literalMutationNumber183() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber5__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber5__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber5__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber5__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_add95244_remove95841() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add95244__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add95244__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add95244__10 = json.contains("1");
        boolean o_testSetSerialization_add95244__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber5_literalMutationNumber183_add2157() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber5__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber5__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber5_literalMutationNumber183_add2157__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber5_literalMutationNumber183_add2157__14);
        boolean o_testSetSerialization_literalMutationNumber5__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber5__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber5_literalMutationNumber183_add2157__14);
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

    public void testSetSerialization_literalMutationString95240_literalMutationString95481() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString95240__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString95240__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString95240__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString95240__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add95245_add95795() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add95245__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add95245__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add95245_add95795__13 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_add95245_add95795__13);
        boolean o_testSetSerialization_add95245__9 = json.contains("1");
        boolean o_testSetSerialization_add95245__10 = json.contains("1");
        boolean o_testSetSerialization_add95245__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_add95245_add95795__13);
    }

    public void testSetSerialization_literalMutationNumber6_add513() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber6__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber6__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber6_add513__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber6_add513__14);
        boolean o_testSetSerialization_literalMutationNumber6__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber6__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber6_add513__14);
    }

    public void testSetSerialization_literalMutationNumber95227_literalMutationNumber95349() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber95227__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber95227__6 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber95227__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber95227__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }
}

