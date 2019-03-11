/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.kahadb.disk.index;


import LongMarshaller.INSTANCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.store.kahadb.disk.util.SequenceSet;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ListIndexTest extends IndexTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ListIndexTest.class);

    private NumberFormat nf;

    @Test(timeout = 60000)
    public void testSize() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> listIndex = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        int count = 30;
        tx = pf.tx();
        doInsert(count);
        tx.commit();
        TestCase.assertEquals("correct size", count, listIndex.size());
        tx = pf.tx();
        Iterator<Map.Entry<String, Long>> iterator = index.iterator(tx);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            TestCase.assertEquals("correct size", (--count), listIndex.size());
        } 
        tx.commit();
        count = 30;
        tx = pf.tx();
        doInsert(count);
        tx.commit();
        TestCase.assertEquals("correct size", count, listIndex.size());
        tx = pf.tx();
        listIndex.clear(tx);
        TestCase.assertEquals("correct size", 0, listIndex.size());
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testPut() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> listIndex = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        int count = 30;
        tx = pf.tx();
        doInsert(count);
        tx.commit();
        TestCase.assertEquals("correct size", count, listIndex.size());
        tx = pf.tx();
        Long value = listIndex.get(tx, key(10));
        TestCase.assertNotNull(value);
        listIndex.put(tx, key(10), Long.valueOf(1024));
        tx.commit();
        tx = pf.tx();
        value = listIndex.get(tx, key(10));
        TestCase.assertEquals(1024L, value.longValue());
        TestCase.assertTrue(((listIndex.size()) == 30));
        tx.commit();
        tx = pf.tx();
        value = listIndex.put(tx, key(31), Long.valueOf(2048));
        TestCase.assertNull(value);
        TestCase.assertTrue(((listIndex.size()) == 31));
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testAddFirst() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> listIndex = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        tx = pf.tx();
        // put is add last
        doInsert(10);
        listIndex.addFirst(tx, key(10), ((long) (10)));
        listIndex.addFirst(tx, key(11), ((long) (11)));
        tx.commit();
        tx = pf.tx();
        int counter = 11;
        Iterator<Map.Entry<String, Long>> iterator = index.iterator(tx);
        TestCase.assertEquals(key(counter), iterator.next().getKey());
        counter--;
        TestCase.assertEquals(key(counter), iterator.next().getKey());
        counter--;
        int count = 0;
        while ((iterator.hasNext()) && (count < counter)) {
            Map.Entry<String, Long> entry = iterator.next();
            TestCase.assertEquals(key(count), entry.getKey());
            TestCase.assertEquals(count, ((long) (entry.getValue())));
            count++;
        } 
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testPruning() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> index = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        long pageCount = index.getPageFile().getPageCount();
        TestCase.assertEquals(1, pageCount);
        long freePageCount = index.getPageFile().getFreePageCount();
        TestCase.assertEquals("No free pages", 0, freePageCount);
        tx = pf.tx();
        doInsert(20);
        tx.commit();
        pageCount = index.getPageFile().getPageCount();
        ListIndexTest.LOG.info(("page count: " + pageCount));
        TestCase.assertTrue("used some pages", (pageCount > 1));
        tx = pf.tx();
        // Remove the data.
        doRemove(20);
        tx.commit();
        freePageCount = index.getPageFile().getFreePageCount();
        ListIndexTest.LOG.info(("FreePage count: " + freePageCount));
        TestCase.assertTrue(("Some free pages " + freePageCount), (freePageCount > 0));
        ListIndexTest.LOG.info("add some more to use up free list");
        tx = pf.tx();
        doInsert(20);
        tx.commit();
        freePageCount = index.getPageFile().getFreePageCount();
        ListIndexTest.LOG.info(("FreePage count: " + freePageCount));
        TestCase.assertEquals(("no free pages " + freePageCount), 0, freePageCount);
        TestCase.assertEquals("Page count is static", pageCount, index.getPageFile().getPageCount());
        this.index.unload(tx);
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testIterationAddFirst() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> index = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        tx = pf.tx();
        final int entryCount = 200;
        // Insert in reverse order..
        doInsertReverse(entryCount);
        this.index.unload(tx);
        tx.commit();
        this.index.load(tx);
        tx.commit();
        int counter = 0;
        for (Iterator<Map.Entry<String, Long>> i = index.iterator(tx); i.hasNext();) {
            Map.Entry<String, Long> entry = i.next();
            TestCase.assertEquals(key(counter), entry.getKey());
            TestCase.assertEquals(counter, ((long) (entry.getValue())));
            counter++;
        }
        TestCase.assertEquals("We iterated over all entries", entryCount, counter);
        tx = pf.tx();
        // Remove the data.
        doRemove(entryCount);
        tx.commit();
        this.index.unload(tx);
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testIteration() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> index = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        // Insert in reverse order..
        final int entryCount = 200;
        doInsert(entryCount);
        this.index.unload(tx);
        tx.commit();
        this.index.load(tx);
        tx.commit();
        int counter = 0;
        for (Iterator<Map.Entry<String, Long>> i = index.iterator(tx); i.hasNext();) {
            Map.Entry<String, Long> entry = i.next();
            TestCase.assertEquals(key(counter), entry.getKey());
            TestCase.assertEquals(counter, ((long) (entry.getValue())));
            counter++;
        }
        TestCase.assertEquals("We iterated over all entries", entryCount, counter);
        this.index.unload(tx);
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testRandomRemove() throws Exception {
        createPageFileAndIndex((4 * 1024));
        ListIndex<String, Long> index = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        final int count = 4000;
        doInsert(count);
        Random rand = new Random(System.currentTimeMillis());
        int i = 0;
        int prev = 0;
        while (!(index.isEmpty(tx))) {
            prev = i;
            i = rand.nextInt(count);
            try {
                index.remove(tx, key(i));
            } catch (Exception e) {
                e.printStackTrace();
                TestCase.fail(((((("unexpected exception on " + i) + ", prev: ") + prev) + ", ex: ") + e));
            }
        } 
    }

    @Test(timeout = 60000)
    public void testRemovePattern() throws Exception {
        createPageFileAndIndex(100);
        ListIndex<String, Long> index = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        final int count = 4000;
        doInsert(count);
        index.remove(tx, key(3697));
        index.remove(tx, key(1566));
    }

    @Test(timeout = 60000)
    public void testLargeAppendRemoveTimed() throws Exception {
        createPageFileAndIndex((1024 * 4));
        ListIndex<String, Long> listIndex = ((ListIndex<String, Long>) (this.index));
        this.index.load(tx);
        tx.commit();
        final int COUNT = 50000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            listIndex.add(tx, key(i), ((long) (i)));
            tx.commit();
        }
        ListIndexTest.LOG.info((((("Time to add " + COUNT) + ": ") + ((System.currentTimeMillis()) - start)) + " mills"));
        ListIndexTest.LOG.info(("Page count: " + (listIndex.getPageFile().getPageCount())));
        start = System.currentTimeMillis();
        tx = pf.tx();
        int removeCount = 0;
        Iterator<Map.Entry<String, Long>> iterator = index.iterator(tx);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            removeCount++;
        } 
        tx.commit();
        TestCase.assertEquals("Removed all", COUNT, removeCount);
        ListIndexTest.LOG.info((((("Time to remove " + COUNT) + ": ") + ((System.currentTimeMillis()) - start)) + " mills"));
        ListIndexTest.LOG.info(("Page count: " + (listIndex.getPageFile().getPageCount())));
        ListIndexTest.LOG.info(("Page free count: " + (listIndex.getPageFile().getFreePageCount())));
    }

    @Test(timeout = 60000)
    public void testLargeValueOverflow() throws Exception {
        pf = new PageFile(getDirectory(), getClass().getName());
        pf.setPageSize((4 * 1024));
        pf.setEnablePageCaching(false);
        pf.setWriteBatchSize(1);
        pf.load();
        tx = pf.tx();
        long id = tx.allocate().getPageId();
        ListIndex<Long, String> test = new ListIndex<Long, String>(pf, id);
        test.setKeyMarshaller(INSTANCE);
        test.setValueMarshaller(StringMarshaller.INSTANCE);
        test.load(tx);
        tx.commit();
        final long NUM_ADDITIONS = 32L;
        LinkedList<Long> expected = new LinkedList<Long>();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; ++i) {
            final int stringSize = getMessageSize(1, 4096);
            String val = new String(new byte[stringSize]);
            expected.add(Long.valueOf(stringSize));
            test.add(tx, i, val);
        }
        tx.commit();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; i++) {
            String s = test.get(tx, i);
            TestCase.assertEquals("string length did not match expected", expected.get(((int) (i))), Long.valueOf(s.length()));
        }
        tx.commit();
        expected.clear();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; ++i) {
            final int stringSize = getMessageSize(1, 4096);
            String val = new String(new byte[stringSize]);
            expected.add(Long.valueOf(stringSize));
            test.addFirst(tx, (i + NUM_ADDITIONS), val);
        }
        tx.commit();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; i++) {
            String s = test.get(tx, (i + NUM_ADDITIONS));
            TestCase.assertEquals("string length did not match expected", expected.get(((int) (i))), Long.valueOf(s.length()));
        }
        tx.commit();
        expected.clear();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; ++i) {
            final int stringSize = getMessageSize(1, 4096);
            String val = new String(new byte[stringSize]);
            expected.add(Long.valueOf(stringSize));
            test.put(tx, i, val);
        }
        tx.commit();
        tx = pf.tx();
        for (long i = 0; i < NUM_ADDITIONS; i++) {
            String s = test.get(tx, i);
            TestCase.assertEquals("string length did not match expected", expected.get(((int) (i))), Long.valueOf(s.length()));
        }
        tx.commit();
    }

    @Test(timeout = 60000)
    public void testListIndexConsistencyOverTime() throws Exception {
        final int NUM_ITERATIONS = 100;
        pf = new PageFile(getDirectory(), getClass().getName());
        pf.setPageSize((4 * 1024));
        pf.setEnablePageCaching(false);
        pf.setWriteBatchSize(1);
        pf.load();
        tx = pf.tx();
        long id = tx.allocate().getPageId();
        ListIndex<String, SequenceSet> test = new ListIndex<String, SequenceSet>(pf, id);
        test.setKeyMarshaller(StringMarshaller.INSTANCE);
        test.setValueMarshaller(SequenceSet.Marshaller.INSTANCE);
        test.load(tx);
        tx.commit();
        int expectedListEntries = 0;
        int nextSequenceId = 0;
        ListIndexTest.LOG.info((("Loading up the ListIndex with " + NUM_ITERATIONS) + " entires and sparsely populating the sequences."));
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            test.add(tx, String.valueOf((expectedListEntries++)), new SequenceSet());
            for (int j = 0; j < expectedListEntries; j++) {
                SequenceSet sequenceSet = test.get(tx, String.valueOf(j));
                int startSequenceId = nextSequenceId;
                for (int ix = 0; ix < NUM_ITERATIONS; ix++) {
                    sequenceSet.add((nextSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
                sequenceSet = test.get(tx, String.valueOf(j));
                for (int ix = 0; ix < (NUM_ITERATIONS - 1); ix++) {
                    sequenceSet.remove((startSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
            }
        }
        ListIndexTest.LOG.info("Checking if Index has the expected number of entries.");
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.get(tx, String.valueOf(i)));
        }
        ListIndexTest.LOG.info("Index has the expected number of entries.");
        TestCase.assertEquals(expectedListEntries, test.size());
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            ListIndexTest.LOG.debug(((("Size of ListIndex before removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.remove(tx, String.valueOf(i)));
            ListIndexTest.LOG.debug(((("Size of ListIndex after removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertEquals((expectedListEntries - (i + 1)), test.size());
        }
    }

    @Test(timeout = 60000)
    public void testListLargeDataAddWithReverseRemove() throws Exception {
        final int NUM_ITERATIONS = 100;
        pf = new PageFile(getDirectory(), getClass().getName());
        pf.setPageSize((4 * 1024));
        pf.setEnablePageCaching(false);
        pf.setWriteBatchSize(1);
        pf.load();
        tx = pf.tx();
        long id = tx.allocate().getPageId();
        ListIndex<String, SequenceSet> test = new ListIndex<String, SequenceSet>(pf, id);
        test.setKeyMarshaller(StringMarshaller.INSTANCE);
        test.setValueMarshaller(SequenceSet.Marshaller.INSTANCE);
        test.load(tx);
        tx.commit();
        int expectedListEntries = 0;
        int nextSequenceId = 0;
        ListIndexTest.LOG.info((("Loading up the ListIndex with " + NUM_ITERATIONS) + " entries and sparsely populating the sequences."));
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            test.add(tx, String.valueOf((expectedListEntries++)), new SequenceSet());
            for (int j = 0; j < expectedListEntries; j++) {
                SequenceSet sequenceSet = test.get(tx, String.valueOf(j));
                int startSequenceId = nextSequenceId;
                for (int ix = 0; ix < NUM_ITERATIONS; ix++) {
                    sequenceSet.add((nextSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
                sequenceSet = test.get(tx, String.valueOf(j));
                for (int ix = 0; ix < (NUM_ITERATIONS - 1); ix++) {
                    sequenceSet.remove((startSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
            }
        }
        ListIndexTest.LOG.info("Checking if Index has the expected number of entries.");
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.get(tx, String.valueOf(i)));
        }
        ListIndexTest.LOG.info("Index has the expected number of entries.");
        TestCase.assertEquals(expectedListEntries, test.size());
        for (int i = NUM_ITERATIONS - 1; i >= 0; --i) {
            ListIndexTest.LOG.debug(((("Size of ListIndex before removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.remove(tx, String.valueOf(i)));
            ListIndexTest.LOG.debug(((("Size of ListIndex after removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertEquals((--expectedListEntries), test.size());
        }
    }

    @Test(timeout = 60000)
    public void testListLargeDataAddAndNonSequentialRemove() throws Exception {
        final int NUM_ITERATIONS = 100;
        pf = new PageFile(getDirectory(), getClass().getName());
        pf.setPageSize((4 * 1024));
        pf.setEnablePageCaching(false);
        pf.setWriteBatchSize(1);
        pf.load();
        tx = pf.tx();
        long id = tx.allocate().getPageId();
        ListIndex<String, SequenceSet> test = new ListIndex<String, SequenceSet>(pf, id);
        test.setKeyMarshaller(StringMarshaller.INSTANCE);
        test.setValueMarshaller(SequenceSet.Marshaller.INSTANCE);
        test.load(tx);
        tx.commit();
        int expectedListEntries = 0;
        int nextSequenceId = 0;
        ListIndexTest.LOG.info((("Loading up the ListIndex with " + NUM_ITERATIONS) + " entires and sparsely populating the sequences."));
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            test.add(tx, String.valueOf((expectedListEntries++)), new SequenceSet());
            for (int j = 0; j < expectedListEntries; j++) {
                SequenceSet sequenceSet = test.get(tx, String.valueOf(j));
                int startSequenceId = nextSequenceId;
                for (int ix = 0; ix < NUM_ITERATIONS; ix++) {
                    sequenceSet.add((nextSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
                sequenceSet = test.get(tx, String.valueOf(j));
                for (int ix = 0; ix < (NUM_ITERATIONS - 1); ix++) {
                    sequenceSet.remove((startSequenceId++));
                    test.put(tx, String.valueOf(j), sequenceSet);
                }
            }
        }
        ListIndexTest.LOG.info("Checking if Index has the expected number of entries.");
        for (int i = 0; i < NUM_ITERATIONS; ++i) {
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.get(tx, String.valueOf(i)));
        }
        ListIndexTest.LOG.info("Index has the expected number of entries.");
        TestCase.assertEquals(expectedListEntries, test.size());
        for (int i = 0; i < NUM_ITERATIONS; i += 2) {
            ListIndexTest.LOG.debug(((("Size of ListIndex before removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.remove(tx, String.valueOf(i)));
            ListIndexTest.LOG.debug(((("Size of ListIndex after removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertEquals((--expectedListEntries), test.size());
        }
        for (int i = NUM_ITERATIONS - 1; i > 0; i -= 2) {
            ListIndexTest.LOG.debug(((("Size of ListIndex before removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertTrue((("List should contain Key[" + i) + "]"), test.containsKey(tx, String.valueOf(i)));
            TestCase.assertNotNull((("List contents of Key[" + i) + "] should not be null"), test.remove(tx, String.valueOf(i)));
            ListIndexTest.LOG.debug(((("Size of ListIndex after removal of entry [" + i) + "] is: ") + (test.size())));
            TestCase.assertEquals((--expectedListEntries), test.size());
        }
        TestCase.assertEquals(0, test.size());
    }

    static class HashSetStringMarshaller extends VariableMarshaller<HashSet<String>> {
        static final ListIndexTest.HashSetStringMarshaller INSTANCE = new ListIndexTest.HashSetStringMarshaller();

        @Override
        public void writePayload(HashSet<String> object, DataOutput dataOut) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(baos);
            oout.writeObject(object);
            oout.flush();
            oout.close();
            byte[] data = baos.toByteArray();
            dataOut.writeInt(data.length);
            dataOut.write(data);
        }

        @Override
        @SuppressWarnings("unchecked")
        public HashSet<String> readPayload(DataInput dataIn) throws IOException {
            int dataLen = dataIn.readInt();
            byte[] data = new byte[dataLen];
            dataIn.readFully(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream oin = new ObjectInputStream(bais);
            try {
                return ((HashSet<String>) (oin.readObject()));
            } catch (ClassNotFoundException cfe) {
                IOException ioe = new IOException(("Failed to read HashSet<String>: " + cfe));
                ioe.initCause(cfe);
                throw ioe;
            }
        }
    }
}

