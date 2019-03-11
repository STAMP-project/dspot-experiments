package org.apache.zookeeper.test;


import ZooDefs.OpCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.zookeeper.server.persistence.FileTxnLog;
import org.apache.zookeeper.server.util.LogChopper;
import org.apache.zookeeper.txn.DeleteTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.junit.Assert;
import org.junit.Test;


public class LogChopperTest extends ClientBase {
    @Test
    public void testChopper() throws IOException {
        long clientId = 17;
        int cxid = 77;
        long zxid = 1000;
        long time = 1;
        int type = OpCode.delete;
        DeleteTxn txn = new DeleteTxn("/foo");
        File tmpDir = ClientBase.createTmpDir();
        FileTxnLog txnLog = new FileTxnLog(tmpDir);
        for (int i = 0; i < 100; i++) {
            TxnHeader hdr = new TxnHeader(clientId, cxid, (++zxid), (++time), type);
            txnLog.append(hdr, txn);
        }
        // append a txn with gap
        TxnHeader hdr = new TxnHeader(clientId, cxid, (zxid + 10), (++time), type);
        txnLog.append(hdr, txn);
        txnLog.commit();
        // now find the log we just created.
        final File logFile = new File(tmpDir, ("log." + (Integer.toHexString(1001))));
        Pair<Long, Long> firstLast = getFirstLastZxid(logFile);
        Assert.assertEquals(1001, ((long) (firstLast.getFirst())));
        Assert.assertEquals(1110, ((long) (firstLast.getSecond())));
        File choppedFile = new File(tmpDir, "chopped_failed");
        Assert.assertFalse(LogChopper.chop(new FileInputStream(logFile), new FileOutputStream(choppedFile), 1107));
        choppedFile = new File(tmpDir, "chopped");
        Assert.assertTrue(LogChopper.chop(new FileInputStream(logFile), new FileOutputStream(choppedFile), 1017));
        firstLast = getFirstLastZxid(choppedFile);
        Assert.assertEquals(1001, ((long) (firstLast.getFirst())));
        Assert.assertEquals(1017, ((long) (firstLast.getSecond())));
    }
}

