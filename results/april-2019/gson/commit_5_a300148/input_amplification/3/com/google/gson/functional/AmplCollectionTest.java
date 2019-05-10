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

    public void testSetSerialization_literalMutationNumber97310_add97864_literalMutationNumber98414() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber97310__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber97310__5 = set.add(new AmplCollectionTest.Entry(-1));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":-1}]", json);
        boolean o_testSetSerialization_literalMutationNumber97310_add97864__14 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber97310__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber97310__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":-1}]", json);
    }

    public void testSetSerialization_literalMutationNumber180496() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180496__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__3);
        boolean o_testSetSerialization_literalMutationNumber180496__5 = set.add(new AmplCollectionTest.Entry(4));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber180496__10 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__10);
        boolean o_testSetSerialization_literalMutationNumber180496__11 = json.contains("2");
        TestCase.assertFalse(o_testSetSerialization_literalMutationNumber180496__11);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber180496__10);
    }

    public void testSetSerialization_literalMutationString97315_literalMutationNumber97658() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString97315__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString97315__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationString97315__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString97315__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationNumber97312_literalMutationNumber97561() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber97312__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber97312__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber97312__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber97312__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":3}]", json);
    }

    public void testSetSerialization_literalMutationNumber180499_literalMutationNumber180749() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber180499__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber180499__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
        boolean o_testSetSerialization_literalMutationNumber180499__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber180499__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4}]", json);
    }

    public void testSetSerialization_literalMutationString97315_add97859_literalMutationString98407() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString97315__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString97315__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString97315_add97859__13 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString97315__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString97315__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }
}

