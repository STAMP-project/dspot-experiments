/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.datasource;


import java.util.Collections;
import org.ethereum.datasource.inmem.HashMapDB;
import org.ethereum.db.prune.Pruner;
import org.ethereum.db.prune.Segment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Anton Nashatyrev on 27.07.2016.
 */
public class JournalPruneTest {
    class StringJDS extends JournalSource<byte[]> {
        final HashMapDB<byte[]> mapDB;

        final Source<byte[], byte[]> db;

        public StringJDS() {
            this(new HashMapDB<byte[]>());
        }

        private StringJDS(HashMapDB<byte[]> mapDB) {
            this(mapDB, mapDB);
        }

        private StringJDS(HashMapDB<byte[]> mapDB, Source<byte[], byte[]> db) {
            super(db);
            this.db = db;
            this.mapDB = mapDB;
        }

        public synchronized void put(String key) {
            super.put(key.getBytes(), key.getBytes());
        }

        public synchronized void delete(String key) {
            super.delete(key.getBytes());
        }

        public String get(String key) {
            return new String(super.get(key.getBytes()));
        }
    }

    @Test
    public void simpleTest() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        putKeys(jds, "a1", "a2");
        jds.put("a3");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a2");
        jds.delete("a3");
        pruner.feed(commitUpdates(hashInt(2)));
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(3)));
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(1, hashInt(1), hashInt(0)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1", "a2", "a3");
        segment = new Segment(1, hashInt(1), hashInt(0));
        segment.startTracking().addMain(2, hashInt(2), hashInt(1)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1", "a2");
        segment = new Segment(2, hashInt(2), hashInt(1));
        segment.startTracking().addMain(3, hashInt(3), hashInt(2)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1");
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void forkTest1() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        putKeys(jds, "a1", "a2", "a3");
        pruner.feed(commitUpdates(hashInt(0)));
        jds.put("a4");
        jds.put("a1");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a5");
        jds.delete("a3");
        jds.put("a2");
        jds.put("a1");
        pruner.feed(commitUpdates(hashInt(2)));
        pruner.feed(commitUpdates(hashInt(3)));// complete segment

        checkDb(jds, "a1", "a2", "a3", "a4", "a5");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(0, hashInt(0), hashInt(0)).commit();
        pruner.prune(segment);
        segment = new Segment(0, hashInt(0), hashInt((-1)));
        segment.startTracking().addMain(1, hashInt(1), hashInt(0)).addItem(1, hashInt(2), hashInt(0)).addMain(2, hashInt(3), hashInt(1)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1", "a3", "a4");
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void forkTest2() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        putKeys(jds, "a1", "a2", "a3");
        jds.delete("a1");
        jds.delete("a3");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a4");
        pruner.feed(commitUpdates(hashInt(2)));
        pruner.feed(commitUpdates(hashInt(3)));
        jds.put("a1");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(4)));
        jds.put("a4");
        pruner.feed(commitUpdates(hashInt(5)));
        pruner.feed(commitUpdates(hashInt(6)));
        pruner.feed(commitUpdates(hashInt(7)));
        jds.put("a3");
        pruner.feed(commitUpdates(hashInt(8)));
        checkDb(jds, "a1", "a2", "a3", "a4");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(1, hashInt(1), hashInt(0)).addItem(1, hashInt(2), hashInt(0)).addMain(2, hashInt(3), hashInt(1)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1", "a2", "a3", "a4");
        segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(1, hashInt(6), hashInt(0)).addItem(1, hashInt(4), hashInt(0)).addItem(1, hashInt(5), hashInt(0)).addMain(2, hashInt(7), hashInt(6)).commit();
        pruner.prune(segment);
        checkDb(jds, "a2", "a3");
        segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(1, hashInt(8), hashInt(0)).commit();
        pruner.prune(segment);
        checkDb(jds, "a2", "a3");
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void forkTest3() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        putKeys(jds, "a1");
        jds.put("a2");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a1");
        jds.put("a2");
        jds.put("a3");
        pruner.feed(commitUpdates(hashInt(2)));
        jds.put("a1");
        jds.put("a2");
        jds.put("a3");
        pruner.feed(commitUpdates(hashInt(3)));
        pruner.feed(commitUpdates(hashInt(4)));
        checkDb(jds, "a1", "a2", "a3");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(1, hashInt(1), hashInt(0)).addItem(1, hashInt(2), hashInt(0)).addItem(1, hashInt(3), hashInt(0)).addMain(2, hashInt(4), hashInt(1)).commit();
        pruner.prune(segment);
        checkDb(jds, "a1", "a2");
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void twoStepTest1() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        pruner.withSecondStep(Collections.emptyList(), 100);
        putKeys(jds, "a1", "a2", "a3");
        pruner.feed(commitUpdates(hashInt(0)));
        jds.put("a4");
        jds.put("a1");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a5");
        jds.delete("a3");
        jds.put("a1");
        pruner.feed(commitUpdates(hashInt(2)));
        pruner.feed(commitUpdates(hashInt(3)));// complete segment

        checkDb(jds, "a1", "a2", "a3", "a4", "a5");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(0, hashInt(0), hashInt(0)).commit();
        pruner.prune(segment);
        segment = new Segment(0, hashInt(0), hashInt((-1)));
        segment.startTracking().addMain(1, hashInt(1), hashInt(0)).addItem(1, hashInt(2), hashInt(0)).addMain(2, hashInt(3), hashInt(1)).commit();
        pruner.prune(segment);
        pruner.persist(hashInt(0));
        checkDb(jds, "a1", "a2", "a3", "a4");
        pruner.persist(hashInt(1));
        checkDb(jds, "a1", "a3", "a4");
        pruner.persist(hashInt(3));
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void twoStepTest2() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        pruner.withSecondStep(Collections.emptyList(), 100);
        putKeys(jds, "a1", "a2", "a3");
        pruner.feed(commitUpdates(hashInt(0)));
        jds.put("a4");
        jds.delete("a2");
        jds.delete("a1");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a2");
        jds.delete("a3");
        pruner.feed(commitUpdates(hashInt(2)));
        jds.put("a5");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(3)));
        jds.put("a5");
        jds.put("a6");
        jds.delete("a4");
        pruner.feed(commitUpdates(hashInt(31)));
        pruner.feed(commitUpdates(hashInt(4)));
        checkDb(jds, "a1", "a2", "a3", "a4", "a5", "a6");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(0, hashInt(0), hashInt(0)).addMain(1, hashInt(1), hashInt(0)).addMain(2, hashInt(2), hashInt(1)).commit();
        pruner.prune(segment);
        pruner.persist(hashInt(0));
        checkDb(jds, "a1", "a2", "a3", "a4", "a5", "a6");
        pruner.persist(hashInt(1));
        checkDb(jds, "a2", "a3", "a4", "a5", "a6");
        pruner.persist(hashInt(2));
        checkDb(jds, "a2", "a4", "a5", "a6");
        segment = new Segment(2, hashInt(2), hashInt(1));
        segment.startTracking().addMain(3, hashInt(3), hashInt(2)).addItem(3, hashInt(31), hashInt(2)).addMain(4, hashInt(4), hashInt(3)).commit();
        pruner.prune(segment);
        pruner.persist(hashInt(3));
        checkDb(jds, "a4", "a5");
        pruner.persist(hashInt(4));
        Assert.assertEquals(0, getStorage().size());
    }

    @Test
    public void twoStepTest3() {
        JournalPruneTest.StringJDS jds = new JournalPruneTest.StringJDS();
        Pruner pruner = new Pruner(getJournal(), jds.db);
        pruner.init();
        pruner.withSecondStep(Collections.emptyList(), 100);
        putKeys(jds, "a1", "a2", "a3");
        pruner.feed(commitUpdates(hashInt(0)));
        jds.put("a4");
        jds.delete("a2");
        jds.delete("a1");
        pruner.feed(commitUpdates(hashInt(1)));
        jds.put("a2");
        jds.delete("a3");
        pruner.feed(commitUpdates(hashInt(2)));
        jds.put("a5");
        jds.put("a1");
        jds.delete("a2");
        pruner.feed(commitUpdates(hashInt(3)));
        jds.put("a5");
        jds.put("a6");
        jds.delete("a4");
        pruner.feed(commitUpdates(hashInt(31)));
        pruner.feed(commitUpdates(hashInt(4)));
        checkDb(jds, "a1", "a2", "a3", "a4", "a5", "a6");
        Segment segment = new Segment(0, hashInt(0), hashInt(0));
        segment.startTracking().addMain(0, hashInt(0), hashInt(0)).addMain(1, hashInt(1), hashInt(0)).addMain(2, hashInt(2), hashInt(1)).commit();
        pruner.prune(segment);
        pruner.persist(hashInt(0));
        checkDb(jds, "a1", "a2", "a3", "a4", "a5", "a6");
        pruner.persist(hashInt(1));
        checkDb(jds, "a1", "a2", "a3", "a4", "a5", "a6");
        pruner.persist(hashInt(2));
        checkDb(jds, "a1", "a2", "a4", "a5", "a6");
        segment = new Segment(2, hashInt(2), hashInt(1));
        segment.startTracking().addMain(3, hashInt(3), hashInt(2)).addItem(3, hashInt(31), hashInt(2)).addMain(4, hashInt(4), hashInt(3)).commit();
        pruner.prune(segment);
        pruner.persist(hashInt(3));
        checkDb(jds, "a1", "a4", "a5");
        pruner.persist(hashInt(4));
        Assert.assertEquals(0, getStorage().size());
    }
}

