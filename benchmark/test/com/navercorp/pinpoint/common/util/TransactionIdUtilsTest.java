/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.util;


import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import static TransactionIdUtils.TRANSACTION_ID_DELIMITER;


/**
 *
 *
 * @author emeroad
 */
public class TransactionIdUtilsTest {
    public static final String AGENT_ID = "test";

    @Test
    public void testParseTransactionId() {
        TransactionId transactionId = TransactionIdUtils.parseTransactionId((((((TransactionIdUtilsTest.AGENT_ID) + (TRANSACTION_ID_DELIMITER)) + "1") + (TRANSACTION_ID_DELIMITER)) + "2"));
        Assert.assertEquals(transactionId.getAgentId(), "test");
        Assert.assertEquals(transactionId.getAgentStartTime(), 1L);
        Assert.assertEquals(transactionId.getTransactionSequence(), 2L);
    }

    @Test
    public void testParseTransactionId2() {
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(((((((TransactionIdUtilsTest.AGENT_ID) + (TRANSACTION_ID_DELIMITER)) + "1") + (TRANSACTION_ID_DELIMITER)) + "2") + (TRANSACTION_ID_DELIMITER)));
        Assert.assertEquals(transactionId.getAgentId(), TransactionIdUtilsTest.AGENT_ID);
        Assert.assertEquals(transactionId.getAgentStartTime(), 1L);
        Assert.assertEquals(transactionId.getTransactionSequence(), 2L);
    }

    @Test(expected = Exception.class)
    public void testParseTransactionId_RpcHeaderDuplicateAdd_BugReproduce() {
        // #27 http://yobi.navercorp.com/pinpoint/pinpoint/issue/27
        String id1 = ((((TransactionIdUtilsTest.AGENT_ID) + (TRANSACTION_ID_DELIMITER)) + "1") + (TRANSACTION_ID_DELIMITER)) + "2";
        String id2 = ((((TransactionIdUtilsTest.AGENT_ID) + (TRANSACTION_ID_DELIMITER)) + "1") + (TRANSACTION_ID_DELIMITER)) + "3";
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(((id1 + ", ") + id2));
        Assert.assertEquals(transactionId.getAgentId(), "test");
        Assert.assertEquals(transactionId.getAgentStartTime(), 1L);
        Assert.assertEquals(transactionId.getTransactionSequence(), 2L);
    }

    @Test
    public void testParseTransactionIdByte1() {
        long time = System.currentTimeMillis();
        byte[] bytes = TransactionIdUtils.formatBytes(TransactionIdUtilsTest.AGENT_ID, time, 2);
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(bytes);
        Assert.assertEquals(transactionId.getAgentId(), TransactionIdUtilsTest.AGENT_ID);
        Assert.assertEquals(transactionId.getAgentStartTime(), time);
        Assert.assertEquals(transactionId.getTransactionSequence(), 2L);
    }

    @Test
    public void testParseTransactionIdByte2() {
        long time = Long.MAX_VALUE;
        byte[] bytes = TransactionIdUtils.formatBytes(TransactionIdUtilsTest.AGENT_ID, time, Long.MAX_VALUE);
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(bytes);
        Assert.assertEquals(transactionId.getAgentId(), TransactionIdUtilsTest.AGENT_ID);
        Assert.assertEquals(transactionId.getAgentStartTime(), Long.MAX_VALUE);
        Assert.assertEquals(transactionId.getTransactionSequence(), Long.MAX_VALUE);
    }

    @Test
    public void testParseTransactionIdByte3() {
        long time = Long.MIN_VALUE;
        byte[] bytes = TransactionIdUtils.formatBytes(TransactionIdUtilsTest.AGENT_ID, time, Long.MIN_VALUE);
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(bytes);
        Assert.assertEquals(transactionId.getAgentId(), TransactionIdUtilsTest.AGENT_ID);
        Assert.assertEquals(transactionId.getAgentStartTime(), Long.MIN_VALUE);
        Assert.assertEquals(transactionId.getTransactionSequence(), Long.MIN_VALUE);
    }

    @Test
    public void testParseTransactionIdByte_AgentIdisNull() {
        long time = System.currentTimeMillis();
        byte[] bytes = TransactionIdUtils.formatBytes(null, time, 1);
        TransactionId transactionId = TransactionIdUtils.parseTransactionId(bytes);
        Assert.assertEquals(transactionId.getAgentId(), null);
        Assert.assertEquals(transactionId.getAgentStartTime(), time);
        Assert.assertEquals(transactionId.getTransactionSequence(), 1L);
    }

    @Test
    public void testParseTransactionIdByte_compatibility1() {
        long time = System.currentTimeMillis();
        ByteBuffer byteBuffer1 = ByteBuffer.wrap(TransactionIdUtils.formatBytes(TransactionIdUtilsTest.AGENT_ID, time, 2));
        ByteBuffer byteBuffer2 = TransactionIdUtilsTest.writeTransactionId_for_compatibility(TransactionIdUtilsTest.AGENT_ID, time, 2);
        Assert.assertEquals(byteBuffer1, byteBuffer2);
    }

    @Test
    public void testParseTransactionIdByte_compatibility2() {
        long time = System.currentTimeMillis();
        ByteBuffer byteBuffer1 = ByteBuffer.wrap(TransactionIdUtils.formatBytes(null, time, 2));
        ByteBuffer byteBuffer2 = TransactionIdUtilsTest.writeTransactionId_for_compatibility(null, time, 2);
        Assert.assertEquals(byteBuffer1, byteBuffer2);
    }
}

