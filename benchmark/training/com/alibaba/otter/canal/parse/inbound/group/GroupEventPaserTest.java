package com.alibaba.otter.canal.parse.inbound.group;


import com.alibaba.otter.canal.parse.inbound.mysql.MysqlEventParser;
import com.alibaba.otter.canal.sink.entry.group.GroupEventSink;
import org.junit.Test;


public class GroupEventPaserTest {
    private static final String DETECTING_SQL = "insert into retl.xdual values(1,now()) on duplicate key update x=now()";

    private static final String MYSQL_ADDRESS = "127.0.0.1";

    private static final String USERNAME = "xxxxx";

    private static final String PASSWORD = "xxxxx";

    @Test
    public void testMysqlWithMysql() {
        // MemoryEventStoreWithBuffer eventStore = new
        // MemoryEventStoreWithBuffer();
        // eventStore.setBufferSize(8196);
        GroupEventSink eventSink = new GroupEventSink(3);
        eventSink.setFilterTransactionEntry(false);
        eventSink.setEventStore(new DummyEventStore());
        eventSink.start();
        // ?????mysql
        MysqlEventParser mysqlEventPaser1 = buildEventParser(3344);
        mysqlEventPaser1.setEventSink(eventSink);
        // ?????mysql
        MysqlEventParser mysqlEventPaser2 = buildEventParser(3345);
        mysqlEventPaser2.setEventSink(eventSink);
        // ?????mysql
        MysqlEventParser mysqlEventPaser3 = buildEventParser(3346);
        mysqlEventPaser3.setEventSink(eventSink);
        // ??
        mysqlEventPaser1.start();
        mysqlEventPaser2.start();
        mysqlEventPaser3.start();
        try {
            Thread.sleep(((30 * 10) * 1000L));
        } catch (InterruptedException e) {
        }
        mysqlEventPaser1.stop();
        mysqlEventPaser2.stop();
        mysqlEventPaser3.stop();
    }
}

