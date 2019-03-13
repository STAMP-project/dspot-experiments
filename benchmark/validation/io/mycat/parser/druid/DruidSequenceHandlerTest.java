package io.mycat.parser.druid;


import io.mycat.config.model.SystemConfig;
import io.mycat.route.parser.druid.DruidSequenceHandler;
import junit.framework.Assert;
import org.junit.Test;


/**
 * ??MYCAT SEQ ???
 */
public class DruidSequenceHandlerTest {
    @Test
    public void test() {
        DruidSequenceHandler handler = new DruidSequenceHandler(SystemConfig.SEQUENCEHANDLER_LOCALFILE);
        String sql = "select next value for mycatseq_xxxx".toUpperCase();
        String tableName = handler.getTableName(sql);
        Assert.assertEquals(tableName, "XXXX");
        sql = " insert into test(id,sid)values(next value for MYCATSEQ_TEST,1)".toUpperCase();
        tableName = handler.getTableName(sql);
        Assert.assertEquals(tableName, "TEST");
        sql = " insert into test(id,sid)values(next value for MYCATSEQ_TEST       ,1)".toUpperCase();
        tableName = handler.getTableName(sql);
        Assert.assertEquals(tableName, "TEST");
        sql = " insert into test(id)values(next value for MYCATSEQ_TEST  )".toUpperCase();
        tableName = handler.getTableName(sql);
        Assert.assertEquals(tableName, "TEST");
    }

    @Test
    public void test2() {
        DruidSequenceHandler handler = new DruidSequenceHandler(SystemConfig.SEQUENCEHANDLER_LOCALFILE);
        String sql = "/* APPLICATIONNAME=DBEAVER 3.3.2 - MAIN CONNECTION */ SELECT NEXT VALUE FOR MYCATSEQ_XXXX".toUpperCase();
        String tableName = handler.getTableName(sql);
        Assert.assertEquals(tableName, "XXXX");
    }
}

