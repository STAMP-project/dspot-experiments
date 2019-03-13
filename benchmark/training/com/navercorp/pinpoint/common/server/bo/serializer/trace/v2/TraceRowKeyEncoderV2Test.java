package com.navercorp.pinpoint.common.server.bo.serializer.trace.v2;


import com.navercorp.pinpoint.common.server.bo.serializer.RowKeyDecoder;
import com.navercorp.pinpoint.common.server.bo.serializer.RowKeyEncoder;
import com.navercorp.pinpoint.common.util.TransactionId;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class TraceRowKeyEncoderV2Test {
    private RowKeyDistributorByHashPrefix distributorByHashPrefix = newDistributorByHashPrefix();

    private RowKeyEncoder<TransactionId> traceRowKeyEncoder = new TraceRowKeyEncoderV2(distributorByHashPrefix);

    private RowKeyDecoder<TransactionId> traceRowKeyDecoder = new TraceRowKeyDecoderV2();

    @Test
    public void encodeRowKey() throws Exception {
        TransactionId spanTransactionId = new TransactionId("traceAgentId", System.currentTimeMillis(), RandomUtils.nextLong(0, 10000));
        byte[] rowKey = traceRowKeyEncoder.encodeRowKey(spanTransactionId);
        TransactionId transactionId = traceRowKeyDecoder.decodeRowKey(rowKey);
        Assert.assertEquals(transactionId, spanTransactionId);
    }
}

