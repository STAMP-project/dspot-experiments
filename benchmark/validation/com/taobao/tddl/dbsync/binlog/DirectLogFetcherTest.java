package com.taobao.tddl.dbsync.binlog;


import com.taobao.tddl.dbsync.binlog.event.DeleteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.QueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsQueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.UpdateRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.WriteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.XidLogEvent;
import com.taobao.tddl.dbsync.binlog.event.mariadb.AnnotateRowsEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;

import static LogEvent.ANNOTATE_ROWS_EVENT;
import static LogEvent.DELETE_ROWS_EVENT;
import static LogEvent.DELETE_ROWS_EVENT_V1;
import static LogEvent.ENUM_END_EVENT;
import static LogEvent.MARIA_SLAVE_CAPABILITY_MINE;
import static LogEvent.PARTIAL_UPDATE_ROWS_EVENT;
import static LogEvent.QUERY_EVENT;
import static LogEvent.ROTATE_EVENT;
import static LogEvent.ROWS_QUERY_LOG_EVENT;
import static LogEvent.UNKNOWN_EVENT;
import static LogEvent.UPDATE_ROWS_EVENT;
import static LogEvent.UPDATE_ROWS_EVENT_V1;
import static LogEvent.WRITE_ROWS_EVENT;
import static LogEvent.WRITE_ROWS_EVENT_V1;
import static LogEvent.XID_EVENT;


public class DirectLogFetcherTest extends BaseLogFetcherTest {
    @Test
    public void testSimple() {
        DirectLogFetcher fecther = new DirectLogFetcher();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", "root", "hello");
            Statement statement = connection.createStatement();
            statement.execute("SET @master_binlog_checksum='@@global.binlog_checksum'");
            statement.execute((("SET @mariadb_slave_capability='" + (MARIA_SLAVE_CAPABILITY_MINE)) + "'"));
            fecther.open(connection, "mysql-bin.000007", 89797036L, 2);
            LogDecoder decoder = new LogDecoder(UNKNOWN_EVENT, ENUM_END_EVENT);
            LogContext context = new LogContext();
            while (fecther.fetch()) {
                LogEvent event = decoder.decode(fecther, context);
                int eventType = event.getHeader().getType();
                switch (eventType) {
                    case ROTATE_EVENT :
                        binlogFileName = getFilename();
                        break;
                    case WRITE_ROWS_EVENT_V1 :
                    case WRITE_ROWS_EVENT :
                        parseRowsEvent(((WriteRowsLogEvent) (event)));
                        break;
                    case UPDATE_ROWS_EVENT_V1 :
                    case PARTIAL_UPDATE_ROWS_EVENT :
                    case UPDATE_ROWS_EVENT :
                        parseRowsEvent(((UpdateRowsLogEvent) (event)));
                        break;
                    case DELETE_ROWS_EVENT_V1 :
                    case DELETE_ROWS_EVENT :
                        parseRowsEvent(((DeleteRowsLogEvent) (event)));
                        break;
                    case QUERY_EVENT :
                        parseQueryEvent(((QueryLogEvent) (event)));
                        break;
                    case ROWS_QUERY_LOG_EVENT :
                        parseRowsQueryEvent(((RowsQueryLogEvent) (event)));
                        break;
                    case ANNOTATE_ROWS_EVENT :
                        parseAnnotateRowsEvent(((AnnotateRowsEvent) (event)));
                        break;
                    case XID_EVENT :
                        parseXidEvent(((XidLogEvent) (event)));
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
                fecther.close();
            } catch (IOException e) {
                Assert.fail(e.getMessage());
            }
        }
    }
}

