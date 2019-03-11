package org.tron.core.db.backup;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.rocksdb.RocksDB;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.utils.PropUtil;
import org.tron.core.db.Manager;
import org.tron.core.db.ManagerForTest;
import org.tron.core.db2.core.RevokingDBWithCachingNewValue;


@Slf4j
public class BackupDbUtilTest {
    static {
        RocksDB.loadLibrary();
    }

    public TronApplicationContext context;

    public BackupDbUtil dbBackupUtil;

    public Manager dbManager;

    public ManagerForTest mng_test;

    public String dbPath = "output-BackupDbUtilTest";

    String prop_path;

    String bak1_path;

    String bak2_path;

    int frequency;

    @Test
    public void testDoBackup() {
        PropUtil.writeProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE(), String.valueOf("11"));
        mng_test.pushNTestBlock(50);
        List<RevokingDBWithCachingNewValue> alist = getDbs();
        Assert.assertTrue(((dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 50));
        Assert.assertTrue("22".equals(PropUtil.readProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE())));
        mng_test.pushNTestBlock(50);
        Assert.assertTrue(((dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 100));
        Assert.assertTrue("11".equals(PropUtil.readProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE())));
        mng_test.pushNTestBlock(50);
        Assert.assertTrue(((dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 150));
        Assert.assertTrue("22".equals(PropUtil.readProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE())));
        PropUtil.writeProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE(), String.valueOf("1"));
        mng_test.pushNTestBlock(50);
        Assert.assertTrue(((dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 200));
        Assert.assertTrue("11".equals(PropUtil.readProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE())));
        PropUtil.writeProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE(), String.valueOf("2"));
        mng_test.pushNTestBlock(50);
        Assert.assertTrue(((dbManager.getDynamicPropertiesStore().getLatestBlockHeaderNumber()) == 250));
        Assert.assertTrue("22".equals(PropUtil.readProperty(prop_path, BackupDbUtil.getDB_BACKUP_STATE())));
    }
}

