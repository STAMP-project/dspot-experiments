package com.alibaba.otter.canal.parse.inbound.mysql;


import CanalEntry.Entry;
import CanalEntry.EntryType;
import com.alibaba.otter.canal.filter.aviater.AviaterRegexFilter;
import com.alibaba.otter.canal.parse.helper.TimeoutChecker;
import com.alibaba.otter.canal.parse.inbound.mysql.rds.RdsBinlogEventParserProxy;
import com.alibaba.otter.canal.parse.index.AbstractLogPositionManager;
import com.alibaba.otter.canal.parse.stub.AbstractCanalEventSinkTest;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.position.EntryPosition;
import com.alibaba.otter.canal.protocol.position.LogPosition;
import com.alibaba.otter.canal.sink.exception.CanalSinkException;
import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author chengjin.lyf on 2018/7/21 ??5:24
 * @since 1.0.25
 */
public class RdsBinlogEventParserProxyTest {
    private static final String DETECTING_SQL = "insert into retl.xdual values(1,now()) on duplicate key update x=now()";

    private static final String MYSQL_ADDRESS = "";

    private static final String USERNAME = "";

    private static final String PASSWORD = "";

    public static final String DBNAME = "";

    public static final String TBNAME = "";

    public static final String DDL = "";

    @Test
    public void test_timestamp() throws InterruptedException {
        final TimeoutChecker timeoutChecker = new TimeoutChecker((3000 * 1000));
        final AtomicLong entryCount = new AtomicLong(0);
        final EntryPosition entryPosition = new EntryPosition();
        final RdsBinlogEventParserProxy controller = new RdsBinlogEventParserProxy();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, ((-24) * 4));
        final EntryPosition defaultPosition = buildPosition(null, null, calendar.getTimeInMillis());
        controller.setSlaveId(3344L);
        controller.setDetectingEnable(false);
        controller.setDetectingSQL(RdsBinlogEventParserProxyTest.DETECTING_SQL);
        controller.setMasterInfo(buildAuthentication());
        controller.setMasterPosition(defaultPosition);
        controller.setInstanceId("");
        controller.setAccesskey("");
        controller.setSecretkey("");
        controller.setDirectory("/tmp/binlog");
        controller.setEventBlackFilter(new AviaterRegexFilter("mysql\\.*"));
        controller.setFilterTableError(true);
        controller.setBatchFileSize(4);
        controller.setEventSink(new AbstractCanalEventSinkTest<List<CanalEntry.Entry>>() {
            @Override
            public boolean sink(List<CanalEntry.Entry> entrys, InetSocketAddress remoteAddress, String destination) throws CanalSinkException {
                for (CanalEntry.Entry entry : entrys) {
                    if ((entry.getEntryType()) != (EntryType.HEARTBEAT)) {
                        entryCount.incrementAndGet();
                        String logfilename = entry.getHeader().getLogfileName();
                        long logfileoffset = entry.getHeader().getLogfileOffset();
                        long executeTime = entry.getHeader().getExecuteTime();
                        entryPosition.setJournalName(logfilename);
                        entryPosition.setPosition(logfileoffset);
                        entryPosition.setTimestamp(executeTime);
                        break;
                    }
                }
                return true;
            }
        });
        controller.setLogPositionManager(new AbstractLogPositionManager() {
            private LogPosition logPosition;

            public void persistLogPosition(String destination, LogPosition logPosition) {
                this.logPosition = logPosition;
            }

            public LogPosition getLatestIndexBy(String destination) {
                return logPosition;
            }
        });
        controller.start();
        timeoutChecker.waitForIdle();
        if (controller.isStart()) {
            controller.stop();
        }
        // check
        Assert.assertTrue(((entryCount.get()) > 0));
        // ???????????position??
        Assert.assertEquals(entryPosition.getJournalName(), "mysql-bin.000001");
        Assert.assertTrue(((entryPosition.getPosition()) <= 6163L));
        Assert.assertTrue(((entryPosition.getTimestamp()) <= (defaultPosition.getTimestamp())));
    }
}

