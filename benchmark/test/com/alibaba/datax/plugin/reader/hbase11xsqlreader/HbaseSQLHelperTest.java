package com.alibaba.datax.plugin.reader.hbase11xsqlreader;


import com.alibaba.datax.common.util.Configuration;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by shf on 16/7/20.
 */
public class HbaseSQLHelperTest {
    private String jsonStr = "{\n" + ((((("        \"hbaseConfig\": {\n" + "            \"hbase.zookeeper.quorum\": \"hb-proxy-pub-xxx-001.hbase.rds.aliyuncs.com,hb-proxy-pub-xxx-002.hbase.rds.aliyuncs.com,hb-proxy-pub-xxx-003.hbase.rds.aliyuncs.com\"\n") + "        },\n") + "        \"table\": \"TABLE1\",\n") + "        \"column\": []\n") + "    }");

    @Test
    public void testParseConfig() {
        Configuration config = Configuration.from(jsonStr);
        HbaseSQLReaderConfig readerConfig = HbaseSQLHelper.parseConfig(config);
        System.out.println(((("tablenae = " + (readerConfig.getTableName())) + ",zk = ") + (readerConfig.getZkUrl())));
        Assert.assertEquals("TABLE1", readerConfig.getTableName());
        Assert.assertEquals("hb-proxy-pub-xxx-001.hbase.rds.aliyuncs.com,hb-proxy-pub-xxx-002.hbase.rds.aliyuncs.com,hb-proxy-pub-xxx-003.hbase.rds.aliyuncs.com:2181", readerConfig.getZkUrl());
    }

    @Test
    public void testSplit() {
        Configuration config = Configuration.from(jsonStr);
        HbaseSQLReaderConfig readerConfig = HbaseSQLHelper.parseConfig(config);
        List<Configuration> splits = HbaseSQLHelper.split(readerConfig);
        System.out.println(("split size = " + (splits.size())));
    }
}

