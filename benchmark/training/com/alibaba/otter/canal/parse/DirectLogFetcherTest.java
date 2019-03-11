package com.alibaba.otter.canal.parse;


import com.alibaba.otter.canal.parse.driver.mysql.MysqlConnector;
import com.alibaba.otter.canal.parse.inbound.mysql.dbsync.DirectLogFetcher;
import com.taobao.tddl.dbsync.binlog.LogContext;
import com.taobao.tddl.dbsync.binlog.LogDecoder;
import com.taobao.tddl.dbsync.binlog.LogEvent;
import com.taobao.tddl.dbsync.binlog.event.DeleteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.FormatDescriptionLogEvent;
import com.taobao.tddl.dbsync.binlog.event.QueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsQueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.UpdateRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.WriteRowsLogEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DirectLogFetcherTest {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String binlogFileName = "mysql-bin.000001";

    protected Charset charset = Charset.forName("utf-8");

    private int binlogChecksum;

    @Test
    public void testSimple() {
        DirectLogFetcher fetcher = new DirectLogFetcher();
        try {
            MysqlConnector connector = new MysqlConnector(new InetSocketAddress("127.0.0.1", 3306), "root", "hello");
            connector.connect();
            updateSettings(connector);
            loadBinlogChecksum(connector);
            sendRegisterSlave(connector, 3);
            sendBinlogDump(connector, "mysql-bin.000001", 4L, 3);
            fetcher.start(connector.getChannel());
            LogDecoder decoder = new LogDecoder(LogEvent.UNKNOWN_EVENT, LogEvent.ENUM_END_EVENT);
            LogContext context = new LogContext();
            context.setFormatDescription(new FormatDescriptionLogEvent(4, binlogChecksum));
            while (fetcher.fetch()) {
                LogEvent event = null;
                event = decoder.decode(fetcher, context);
                if (event == null) {
                    throw new RuntimeException("parse failed");
                }
                int eventType = event.getHeader().getType();
                switch (eventType) {
                    case LogEvent.ROTATE_EVENT :
                        // binlogFileName = ((RotateLogEvent)
                        // event).getFilename();
                        System.out.println(getFilename());
                        break;
                    case LogEvent.WRITE_ROWS_EVENT_V1 :
                    case LogEvent.WRITE_ROWS_EVENT :
                        parseRowsEvent(((WriteRowsLogEvent) (event)));
                        break;
                    case LogEvent.UPDATE_ROWS_EVENT_V1 :
                    case LogEvent.PARTIAL_UPDATE_ROWS_EVENT :
                    case LogEvent.UPDATE_ROWS_EVENT :
                        parseRowsEvent(((UpdateRowsLogEvent) (event)));
                        break;
                    case LogEvent.DELETE_ROWS_EVENT_V1 :
                    case LogEvent.DELETE_ROWS_EVENT :
                        parseRowsEvent(((DeleteRowsLogEvent) (event)));
                        break;
                    case LogEvent.QUERY_EVENT :
                        parseQueryEvent(((QueryLogEvent) (event)));
                        break;
                    case LogEvent.ROWS_QUERY_LOG_EVENT :
                        parseRowsQueryEvent(((RowsQueryLogEvent) (event)));
                        break;
                    case LogEvent.ANNOTATE_ROWS_EVENT :
                        break;
                    case LogEvent.XID_EVENT :
                        break;
                    default :
                        break;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            try {
                fetcher.close();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
    }
}

