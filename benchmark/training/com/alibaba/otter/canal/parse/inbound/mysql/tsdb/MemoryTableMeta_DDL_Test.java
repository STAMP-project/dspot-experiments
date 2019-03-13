package com.alibaba.otter.canal.parse.inbound.mysql.tsdb;


import com.alibaba.otter.canal.parse.inbound.TableMeta;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author agapple 2017?8?1? ??7:15:54
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/tsdb/mysql-tsdb.xml" })
public class MemoryTableMeta_DDL_Test {
    @Test
    public void test1() throws Throwable {
        MemoryTableMeta memoryTableMeta = new MemoryTableMeta();
        URL url = Thread.currentThread().getContextClassLoader().getResource("dummy.txt");
        File dummyFile = new File(url.getFile());
        File create = new File(((dummyFile.getParent()) + "/ddl"), "ddl_test1.sql");
        String sql = StringUtils.join(IOUtils.readLines(new FileInputStream(create)), "\n");
        memoryTableMeta.apply(null, "test", sql, null);
        TableMeta meta = memoryTableMeta.find("yushitai_test", "card_record");
        System.out.println(meta);
        Assert.assertNotNull(meta.getFieldMetaByName("customization_id"));
        meta = memoryTableMeta.find("yushitai_test", "_card_record_gho");
        Assert.assertNull(meta);
    }

    @Test
    public void test2() throws Throwable {
        MemoryTableMeta memoryTableMeta = new MemoryTableMeta();
        URL url = Thread.currentThread().getContextClassLoader().getResource("dummy.txt");
        File dummyFile = new File(url.getFile());
        File create = new File(((dummyFile.getParent()) + "/ddl"), "ddl_test2.sql");
        String sql = StringUtils.join(IOUtils.readLines(new FileInputStream(create)), "\n");
        memoryTableMeta.apply(null, "test", sql, null);
        TableMeta meta = memoryTableMeta.find("yushitai_test", "card_record");
        System.out.println(meta);
        Assert.assertEquals(meta.getFieldMetaByName("id").isKey(), true);
        Assert.assertEquals(meta.getFieldMetaByName("name").isUnique(), true);
    }
}

