package com.taobao.tddl.dbsync.binlog;


import com.taobao.tddl.dbsync.binlog.event.DeleteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.QueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.RowsQueryLogEvent;
import com.taobao.tddl.dbsync.binlog.event.UpdateRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.WriteRowsLogEvent;
import com.taobao.tddl.dbsync.binlog.event.XidLogEvent;
import com.taobao.tddl.dbsync.binlog.event.mariadb.AnnotateRowsEvent;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static LogEvent.ANNOTATE_ROWS_EVENT;
import static LogEvent.DELETE_ROWS_EVENT;
import static LogEvent.DELETE_ROWS_EVENT_V1;
import static LogEvent.ENUM_END_EVENT;
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


public class FileLogFetcherTest extends BaseLogFetcherTest {
    private String directory;

    @Test
    public void testSimple() {
        FileLogFetcher fetcher = new FileLogFetcher((1024 * 16));
        try {
            LogDecoder decoder = new LogDecoder(UNKNOWN_EVENT, ENUM_END_EVENT);
            LogContext context = new LogContext();
            File current = new File(directory, "mysql-bin.000001");
            fetcher.open(current, 2051L);
            context.setLogPosition(new LogPosition(current.getName()));
            while (fetcher.fetch()) {
                LogEvent event = null;
                event = decoder.decode(fetcher, context);
                if (event != null) {
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
            } 
        } catch (Exception e) {
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

