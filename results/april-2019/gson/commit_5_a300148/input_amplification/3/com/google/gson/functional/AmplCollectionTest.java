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

    public void testSetSerialization_literalMutationNumber167029_add167531() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber167029__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber167029__5 = set.add(new AmplCollectionTest.Entry(3));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        boolean o_testSetSerialization_literalMutationNumber167029_add167531__14 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber167029_add167531__14);
        boolean o_testSetSerialization_literalMutationNumber167029__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber167029__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":3}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber167029_add167531__14);
    }

    public void testSetSerialization_literalMutationNumber299501_add299979() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber299501_add299979__3 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber299501_add299979__3);
        boolean o_testSetSerialization_literalMutationNumber299501__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber299501__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber299501__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber299501__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber299501_add299979__3);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2},{\"value\":0}]", json);
    }

    public void testSetSerialization_add299517_literalMutationString299889() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add299517__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add299517__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add299517__9 = json.contains("1");
        boolean o_testSetSerialization_add299517__10 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_add299517__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber167026_add167543_literalMutationString169017() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber167026_add167543__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber167026__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber167026__5 = set.add(new AmplCollectionTest.Entry(4));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":4},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationNumber167026__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber167026__11 = json.contains("O");
        TestCase.assertEquals("[{\"value\":1},{\"value\":4},{\"value\":1}]", json);
    }

    public void testSetSerialization_add167040_literalMutationString167438() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add167040__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add167040__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add167040__9 = json.contains("1");
        boolean o_testSetSerialization_add167040__10 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_add167040__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add167038null167689() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add167038__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add167038__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add167038null167689__11 = gson.toJson(null);
        TestCase.assertEquals("null", o_testSetSerialization_add167038null167689__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add167038__10 = json.contains("1");
        boolean o_testSetSerialization_add167038__11 = json.contains("2");
        TestCase.assertEquals("null", o_testSetSerialization_add167038null167689__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_add299516_literalMutationString299906() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add299516__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add299516__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add299516__9 = json.contains("1");
        boolean o_testSetSerialization_add299516__10 = json.contains("");
        boolean o_testSetSerialization_add299516__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString299507_literalMutationString299659() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString299507__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString299507__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString299507__9 = json.contains("");
        boolean o_testSetSerialization_literalMutationString299507__10 = json.contains("");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
    }

    public void testSetSerialization_literalMutationString299508_add299964_add303315() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString299508_add299964__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString299508__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString299508__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString299508__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testSetSerialization_literalMutationString299508_add299964_add303315__20 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299508_add299964_add303315__20);
        boolean o_testSetSerialization_literalMutationString299508__10 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299508_add299964_add303315__20);
    }

    public void testSetSerialization_literalMutationNumber299500_literalMutationString299726() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber299500__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_literalMutationNumber299500__6 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber299500__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber299500__11 = json.contains("");
        TestCase.assertEquals("[{\"value\":2},{\"value\":0}]", json);
    }

    public void testSetSerialization_literalMutationNumber167021_literalMutationNumber167145_add170501() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber167021__3 = set.add(new AmplCollectionTest.Entry(2));
        boolean o_testSetSerialization_literalMutationNumber167021__6 = set.add(new AmplCollectionTest.Entry(1));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationNumber167021_literalMutationNumber167145_add170501__15 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber167021_literalMutationNumber167145_add170501__15);
        boolean o_testSetSerialization_literalMutationNumber167021__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber167021__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":2},{\"value\":1}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber167021_literalMutationNumber167145_add170501__15);
    }

    public void testSetSerialization_literalMutationString299511() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString299511__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__3);
        boolean o_testSetSerialization_literalMutationString299511__5 = set.add(new AmplCollectionTest.Entry(2));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__5);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_literalMutationString299511__9 = json.contains("1");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__9);
        boolean o_testSetSerialization_literalMutationString299511__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testSetSerialization_literalMutationString299511__10);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__3);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__5);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        TestCase.assertTrue(o_testSetSerialization_literalMutationString299511__9);
    }

    public void testSetSerialization_add167039_literalMutationNumber167464() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add167039__3 = set.add(new AmplCollectionTest.Entry(0));
        boolean o_testSetSerialization_add167039__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
        boolean o_testSetSerialization_add167039__9 = json.contains("1");
        boolean o_testSetSerialization_add167039__10 = json.contains("1");
        boolean o_testSetSerialization_add167039__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":0},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationNumber299504_add300000() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationNumber299504__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationNumber299504_add300000__7 = set.add(new AmplCollectionTest.Entry(0));
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber299504_add300000__7);
        boolean o_testSetSerialization_literalMutationNumber299504__5 = set.add(new AmplCollectionTest.Entry(0));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0},{\"value\":0}]", json);
        boolean o_testSetSerialization_literalMutationNumber299504__10 = json.contains("1");
        boolean o_testSetSerialization_literalMutationNumber299504__11 = json.contains("2");
        TestCase.assertTrue(o_testSetSerialization_literalMutationNumber299504_add300000__7);
        TestCase.assertEquals("[{\"value\":1},{\"value\":0},{\"value\":0}]", json);
    }

    public void testSetSerialization_add299516_add300035() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_add299516__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_add299516__5 = set.add(new AmplCollectionTest.Entry(2));
        String o_testSetSerialization_add299516_add300035__11 = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add299516_add300035__11);
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
        boolean o_testSetSerialization_add299516__9 = json.contains("1");
        boolean o_testSetSerialization_add299516__10 = json.contains("1");
        boolean o_testSetSerialization_add299516__11 = json.contains("2");
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", o_testSetSerialization_add299516_add300035__11);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2}]", json);
    }

    public void testSetSerialization_literalMutationString167035_add167563() throws Exception {
        Set<AmplCollectionTest.Entry> set = new HashSet<AmplCollectionTest.Entry>();
        boolean o_testSetSerialization_literalMutationString167035_add167563__3 = set.add(new AmplCollectionTest.Entry(1));
        TestCase.assertTrue(o_testSetSerialization_literalMutationString167035_add167563__3);
        boolean o_testSetSerialization_literalMutationString167035__3 = set.add(new AmplCollectionTest.Entry(1));
        boolean o_testSetSerialization_literalMutationString167035__5 = set.add(new AmplCollectionTest.Entry(2));
        String json = gson.toJson(set);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
        boolean o_testSetSerialization_literalMutationString167035__9 = json.contains("1");
        boolean o_testSetSerialization_literalMutationString167035__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertTrue(o_testSetSerialization_literalMutationString167035_add167563__3);
        TestCase.assertEquals("[{\"value\":1},{\"value\":2},{\"value\":1}]", json);
    }
}

