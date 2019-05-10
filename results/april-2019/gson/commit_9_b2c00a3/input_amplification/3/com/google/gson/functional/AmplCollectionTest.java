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

    public void testSetSerialization_literalMutationNumber2_add527() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber2__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber2__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber2__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber2_add527__17 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber2_add527__17);
        boolean o_testSetSerialization_literalMutationNumber2__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber2_add527__17);
    }

    public void testSetSerialization_literalMutationNumber25644_add26084() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber25644_add26084__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber25644_add26084__3);
        boolean o_testSetSerialization_literalMutationNumber25644__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber25644__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationNumber25644__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber25644__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber25644_add26084__3);
        TestCase.assertEquals("[{\"value\":0},{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_add25662_literalMutationString25980() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add25662__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add25662__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add25662__9 = json.contains("1");
        boolean o_testSetSerialization_add25662__10 = json.contains("2");
        boolean o_testSetSerialization_add25662__11 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString11_literalMutationNumber303() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString11__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString11__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationString11__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString11__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
    }

    public void testSetSerialization_literalMutationString14_literalMutationNumber252() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString14__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString14__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationString14__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString14__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("[{\"value\":1},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationString25656_add26117() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString25656__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString25656__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString25656_add26117__13 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString25656_add26117__13);
        boolean o_testSetSerialization_literalMutationString25656__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString25656__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString25656_add26117__13);
    }

    public void testSetSerialization_add17_literalMutationString407() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add17__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add17__5 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_add17__7 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add17__11 = json.contains("");
        boolean o_testSetSerialization_add17__12 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_add25660_add26169() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add25660__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add25660__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add25660_add26169__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add25660_add26169__11);
        String o_testSetSerialization_add25660_add26169__12 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add25660_add26169__12);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_add25660__10 = json.contains("1");
        boolean o_testSetSerialization_add25660__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add25660_add26169__11);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", o_testSetSerialization_add25660_add26169__12);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }
}

