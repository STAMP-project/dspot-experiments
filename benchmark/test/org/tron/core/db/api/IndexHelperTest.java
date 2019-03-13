package org.tron.core.db.api;


import Transaction.raw;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.protos.Protocol.Transaction;


@Slf4j
public class IndexHelperTest {
    private static Manager dbManager;

    private static IndexHelper indexHelper;

    private static TronApplicationContext context;

    private static String dbPath = "output_IndexHelper_test";

    private static Application AppT;

    static {
        Args.setParam(new String[]{ "-d", IndexHelperTest.dbPath, "-w" }, "config-test-index.conf");
        Args.getInstance().setSolidityNode(true);
        IndexHelperTest.context = new TronApplicationContext(DefaultConfig.class);
        IndexHelperTest.AppT = ApplicationFactory.create(IndexHelperTest.context);
    }

    @Test
    public void addAndRemoveTransaction() {
        TransactionCapsule transactionCapsule = new TransactionCapsule(Transaction.newBuilder().setRawData(raw.newBuilder().setData(ByteString.copyFrom("i am trans".getBytes())).build()).build());
        IndexHelperTest.dbManager.getTransactionStore().put(transactionCapsule.getTransactionId().getBytes(), transactionCapsule);
        IndexHelperTest.indexHelper.add(transactionCapsule.getInstance());
        int size = getIndexSizeOfTransaction();
        Assert.assertEquals("account index add", 1, size);
        IndexHelperTest.indexHelper.remove(transactionCapsule.getInstance());
        size = getIndexSizeOfTransaction();
        Assert.assertEquals("account index remove", 0, size);
    }
}

