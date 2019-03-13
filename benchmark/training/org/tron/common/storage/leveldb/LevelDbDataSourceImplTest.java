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
package org.tron.common.storage.leveldb;


import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.utils.ByteArray;
import org.tron.core.config.args.Args;


@Slf4j
public class LevelDbDataSourceImplTest {
    private static final String dbPath = "output-levelDb-test";

    private static LevelDbDataSourceImpl dataSourceTest;

    private byte[] value1 = "10000".getBytes();

    private byte[] value2 = "20000".getBytes();

    private byte[] value3 = "30000".getBytes();

    private byte[] value4 = "40000".getBytes();

    private byte[] value5 = "50000".getBytes();

    private byte[] value6 = "60000".getBytes();

    private byte[] key1 = "00000001aa".getBytes();

    private byte[] key2 = "00000002aa".getBytes();

    private byte[] key3 = "00000003aa".getBytes();

    private byte[] key4 = "00000004aa".getBytes();

    private byte[] key5 = "00000005aa".getBytes();

    private byte[] key6 = "00000006aa".getBytes();

    @Test
    public void testPutGet() {
        LevelDbDataSourceImplTest.dataSourceTest.resetDb();
        String key1 = "2c0937534dd1b3832d05d865e8e6f2bf23218300b33a992740d45ccab7d4f519";
        byte[] key = key1.getBytes();
        LevelDbDataSourceImplTest.dataSourceTest.initDB();
        String value1 = "50000";
        byte[] value = value1.getBytes();
        LevelDbDataSourceImplTest.dataSourceTest.putData(key, value);
        Assert.assertNotNull(LevelDbDataSourceImplTest.dataSourceTest.getData(key));
        Assert.assertEquals(1, LevelDbDataSourceImplTest.dataSourceTest.allKeys().size());
        Assert.assertEquals("50000", ByteArray.toStr(LevelDbDataSourceImplTest.dataSourceTest.getData(key1.getBytes())));
        LevelDbDataSourceImplTest.dataSourceTest.closeDB();
    }

    @Test
    public void testReset() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_reset");
        dataSource.resetDb();
        Assert.assertEquals(0, dataSource.allKeys().size());
        dataSource.closeDB();
    }

    @Test
    public void testupdateByBatchInner() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_updateByBatch");
        dataSource.initDB();
        dataSource.resetDb();
        String key1 = "431cd8c8d5abe5cb5944b0889b32482d85772fbb98987b10fbb7f17110757350";
        String value1 = "50000";
        String key2 = "431cd8c8d5abe5cb5944b0889b32482d85772fbb98987b10fbb7f17110757351";
        String value2 = "10000";
        Map<byte[], byte[]> rows = new HashMap<>();
        rows.put(key1.getBytes(), value1.getBytes());
        rows.put(key2.getBytes(), value2.getBytes());
        dataSource.updateByBatch(rows);
        Assert.assertEquals("50000", ByteArray.toStr(dataSource.getData(key1.getBytes())));
        Assert.assertEquals("10000", ByteArray.toStr(dataSource.getData(key2.getBytes())));
        Assert.assertEquals(2, dataSource.allKeys().size());
        dataSource.closeDB();
    }

    @Test
    public void testdeleteData() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_delete");
        dataSource.initDB();
        String key1 = "431cd8c8d5abe5cb5944b0889b32482d85772fbb98987b10fbb7f17110757350";
        byte[] key = key1.getBytes();
        dataSource.deleteData(key);
        byte[] value = dataSource.getData(key);
        String s = ByteArray.toStr(value);
        Assert.assertNull(s);
        dataSource.closeDB();
    }

    @Test
    public void testallKeys() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_find_key");
        dataSource.initDB();
        dataSource.resetDb();
        String key1 = "431cd8c8d5abe5cb5944b0889b32482d85772fbb98987b10fbb7f17110757321";
        byte[] key = key1.getBytes();
        String value1 = "50000";
        byte[] value = value1.getBytes();
        dataSource.putData(key, value);
        String key3 = "431cd8c8d5abe5cb5944b0889b32482d85772fbb98987b10fbb7f17110757091";
        byte[] key2 = key3.getBytes();
        String value3 = "30000";
        byte[] value2 = value3.getBytes();
        dataSource.putData(key2, value2);
        Assert.assertEquals(2, dataSource.allKeys().size());
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test(timeout = 1000)
    public void testLockReleased() {
        LevelDbDataSourceImplTest.dataSourceTest.initDB();
        // normal close
        LevelDbDataSourceImplTest.dataSourceTest.closeDB();
        // closing already closed db.
        LevelDbDataSourceImplTest.dataSourceTest.closeDB();
        // closing again to make sure the lock is free. If not test will hang.
        LevelDbDataSourceImplTest.dataSourceTest.closeDB();
        Assert.assertFalse("Database is still alive after closing.", LevelDbDataSourceImplTest.dataSourceTest.isAlive());
    }

    @Test
    public void allKeysTest() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_allKeysTest_key");
        dataSource.initDB();
        dataSource.resetDb();
        byte[] key = "0000000987b10fbb7f17110757321".getBytes();
        byte[] value = "50000".getBytes();
        byte[] key2 = "000000431cd8c8d5a".getBytes();
        byte[] value2 = "30000".getBytes();
        dataSource.putData(key, value);
        dataSource.putData(key2, value2);
        dataSource.allKeys().forEach(( keyOne) -> {
            logger.info(ByteArray.toStr(keyOne));
        });
        Assert.assertEquals(2, dataSource.allKeys().size());
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void seekTest() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_seek_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void getValuesNext() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_getValuesNext_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        Set<byte[]> seekKeyLimitNext = dataSource.getValuesNext("0000000300".getBytes(), 2);
        HashSet<String> hashSet = Sets.newHashSet(ByteArray.toStr(value3), ByteArray.toStr(value4));
        seekKeyLimitNext.forEach(( valeu) -> {
            Assert.assertTrue("getValuesNext", hashSet.contains(ByteArray.toStr(valeu)));
        });
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void getValuesPrev() {
        LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_getValuesPrev_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        Set<byte[]> seekKeyLimitNext = dataSource.getValuesPrev("0000000300".getBytes(), 2);
        HashSet<String> hashSet = Sets.newHashSet(ByteArray.toStr(value1), ByteArray.toStr(value2));
        seekKeyLimitNext.forEach(( valeu) -> {
            Assert.assertTrue("getValuesPrev1", hashSet.contains(ByteArray.toStr(valeu)));
        });
        seekKeyLimitNext = dataSource.getValuesPrev("0000000100".getBytes(), 2);
        Assert.assertEquals("getValuesPrev2", 0, seekKeyLimitNext.size());
        dataSource.resetDb();
        dataSource.closeDB();
    }
}

