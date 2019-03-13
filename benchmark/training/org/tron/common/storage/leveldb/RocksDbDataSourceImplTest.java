package org.tron.common.storage.leveldb;


import com.google.common.collect.Sets;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.FileUtil;
import org.tron.common.utils.PropUtil;
import org.tron.core.config.args.Args;


@Slf4j
public class RocksDbDataSourceImplTest {
    private static final String dbPath = "output-Rocks-test";

    private static RocksDbDataSourceImpl dataSourceTest;

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
        RocksDbDataSourceImplTest.dataSourceTest.resetDb();
        String key1 = "2c0937534dd1b3832d05d865e8e6f2bf23218300b33a992740d45ccab7d4f519";
        byte[] key = key1.getBytes();
        RocksDbDataSourceImplTest.dataSourceTest.initDB();
        String value1 = "50000";
        byte[] value = value1.getBytes();
        RocksDbDataSourceImplTest.dataSourceTest.putData(key, value);
        Assert.assertNotNull(RocksDbDataSourceImplTest.dataSourceTest.getData(key));
        Assert.assertEquals(1, RocksDbDataSourceImplTest.dataSourceTest.allKeys().size());
        Assert.assertEquals("50000", ByteArray.toStr(RocksDbDataSourceImplTest.dataSourceTest.getData(key1.getBytes())));
        RocksDbDataSourceImplTest.dataSourceTest.closeDB();
    }

    @Test
    public void testReset() {
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_reset");
        dataSource.resetDb();
        Assert.assertEquals(0, dataSource.allKeys().size());
        dataSource.closeDB();
    }

    @Test
    public void testupdateByBatchInner() {
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_updateByBatch");
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
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_delete");
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
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_find_key");
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
        RocksDbDataSourceImplTest.dataSourceTest.initDB();
        // normal close
        RocksDbDataSourceImplTest.dataSourceTest.closeDB();
        // closing already closed db.
        RocksDbDataSourceImplTest.dataSourceTest.closeDB();
        // closing again to make sure the lock is free. If not test will hang.
        RocksDbDataSourceImplTest.dataSourceTest.closeDB();
        Assert.assertFalse("Database is still alive after closing.", RocksDbDataSourceImplTest.dataSourceTest.isAlive());
    }

    @Test
    public void allKeysTest() {
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_allKeysTest_key");
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
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_seek_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void getValuesNext() {
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_getValuesNext_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        Set<byte[]> seekKeyLimitNext = dataSource.getValuesNext("0000000300".getBytes(), 2);
        HashSet<String> hashSet = Sets.newHashSet(ByteArray.toStr(value3), ByteArray.toStr(value4));
        seekKeyLimitNext.forEach(( value) -> Assert.assertTrue("getValuesNext", hashSet.contains(ByteArray.toStr(value))));
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void getValuesPrev() {
        RocksDbDataSourceImpl dataSource = new RocksDbDataSourceImpl(Args.getInstance().getOutputDirectory(), "test_getValuesPrev_key");
        dataSource.initDB();
        dataSource.resetDb();
        putSomeKeyValue(dataSource);
        Set<byte[]> seekKeyLimitNext = dataSource.getValuesPrev("0000000300".getBytes(), 2);
        HashSet<String> hashSet = Sets.newHashSet(ByteArray.toStr(value1), ByteArray.toStr(value2));
        seekKeyLimitNext.forEach(( value) -> {
            Assert.assertTrue("getValuesPrev1", hashSet.contains(ByteArray.toStr(value)));
        });
        seekKeyLimitNext = dataSource.getValuesPrev("0000000100".getBytes(), 2);
        Assert.assertEquals("getValuesPrev2", 0, seekKeyLimitNext.size());
        dataSource.resetDb();
        dataSource.closeDB();
    }

    @Test
    public void testCheckOrInitEngine() {
        String dir = (Args.getInstance().getOutputDirectory()) + (Args.getInstance().getStorage().getDbDirectory());
        String enginePath = (((dir + (File.separator)) + "test_engine") + (File.separator)) + "engine.properties";
        FileUtil.createDirIfNotExists(((dir + (File.separator)) + "test_engine"));
        FileUtil.createFileIfNotExists(enginePath);
        boolean b = PropUtil.writeProperty(enginePath, "ENGINE", "ROCKSDB");
        Assert.assertEquals(PropUtil.readProperty(enginePath, "ENGINE"), "ROCKSDB");
        RocksDbDataSourceImpl dataSource;
        dataSource = new RocksDbDataSourceImpl(dir, "test_engine");
        dataSource.initDB();
        Assert.assertNotNull(dataSource.getDatabase());
        dataSource.closeDB();
        dataSource = null;
        System.gc();
        b = PropUtil.writeProperty(enginePath, "ENGINE", "LEVELDB");
        Assert.assertEquals(PropUtil.readProperty(enginePath, "ENGINE"), "LEVELDB");
        dataSource = new RocksDbDataSourceImpl(dir, "test_engine");
        try {
            dataSource.initDB();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Failed to"));
        }
        Assert.assertNull(dataSource.getDatabase());
        PropUtil.writeProperty(enginePath, "ENGINE", "ROCKSDB");
    }
}

