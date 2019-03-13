/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.store;


import ConsumeQueueExt.CqExtUnit;
import ConsumeQueueExt.CqExtUnit.MIN_EXT_UNIT_SIZE;
import java.io.File;
import org.apache.rocketmq.common.UtilAll;
import org.junit.Test;

import static ConsumeQueueExt.END_BLANK_DATA_LENGTH;


public class ConsumeQueueExtTest {
    private static final String topic = "abc";

    private static final int queueId = 0;

    private static final String storePath = ("." + (File.separator)) + "unit_test_store";

    private static final int bitMapLength = 64;

    private static final int unitSizeWithBitMap = (CqExtUnit.MIN_EXT_UNIT_SIZE) + ((ConsumeQueueExtTest.bitMapLength) / (Byte.SIZE));

    private static final int cqExtFileSize = 10 * (ConsumeQueueExtTest.unitSizeWithBitMap);

    private static final int unitCount = 20;

    @Test
    public void testPut() {
        ConsumeQueueExt consumeQueueExt = genExt();
        try {
            putSth(consumeQueueExt, true, false, ConsumeQueueExtTest.unitCount);
        } finally {
            consumeQueueExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }

    @Test
    public void testGet() {
        ConsumeQueueExt consumeQueueExt = genExt();
        putSth(consumeQueueExt, false, false, ConsumeQueueExtTest.unitCount);
        try {
            // from start.
            long addr = consumeQueueExt.decorate(0);
            ConsumeQueueExt.CqExtUnit unit = new ConsumeQueueExt.CqExtUnit();
            while (true) {
                boolean ret = consumeQueueExt.get(addr, unit);
                if (!ret) {
                    break;
                }
                assertThat(unit.getSize()).isGreaterThanOrEqualTo(MIN_EXT_UNIT_SIZE);
                addr += unit.getSize();
            } 
        } finally {
            consumeQueueExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }

    @Test
    public void testGet_invalidAddress() {
        ConsumeQueueExt consumeQueueExt = genExt();
        putSth(consumeQueueExt, false, true, ConsumeQueueExtTest.unitCount);
        try {
            ConsumeQueueExt.CqExtUnit unit = consumeQueueExt.get(0);
            assertThat(unit).isNull();
            long addr = ((ConsumeQueueExtTest.cqExtFileSize) / (ConsumeQueueExtTest.unitSizeWithBitMap)) * (ConsumeQueueExtTest.unitSizeWithBitMap);
            addr += ConsumeQueueExtTest.unitSizeWithBitMap;
            unit = consumeQueueExt.get(addr);
            assertThat(unit).isNull();
        } finally {
            consumeQueueExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }

    @Test
    public void testRecovery() {
        ConsumeQueueExt putCqExt = genExt();
        putSth(putCqExt, false, true, ConsumeQueueExtTest.unitCount);
        ConsumeQueueExt loadCqExt = genExt();
        loadCqExt.load();
        loadCqExt.recover();
        try {
            assertThat(loadCqExt.getMinAddress()).isEqualTo(Long.MIN_VALUE);
            // same unit size.
            int countPerFile = ((ConsumeQueueExtTest.cqExtFileSize) - (END_BLANK_DATA_LENGTH)) / (ConsumeQueueExtTest.unitSizeWithBitMap);
            int lastFileUnitCount = (ConsumeQueueExtTest.unitCount) % countPerFile;
            int fileCount = ((ConsumeQueueExtTest.unitCount) / countPerFile) + 1;
            if (lastFileUnitCount == 0) {
                fileCount -= 1;
            }
            if (lastFileUnitCount == 0) {
                assertThat(((loadCqExt.unDecorate(loadCqExt.getMaxAddress())) % (ConsumeQueueExtTest.cqExtFileSize))).isEqualTo(0);
            } else {
                assertThat(loadCqExt.unDecorate(loadCqExt.getMaxAddress())).isEqualTo(((lastFileUnitCount * (ConsumeQueueExtTest.unitSizeWithBitMap)) + ((fileCount - 1) * (ConsumeQueueExtTest.cqExtFileSize))));
            }
        } finally {
            putCqExt.destroy();
            loadCqExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }

    @Test
    public void testTruncateByMinOffset() {
        ConsumeQueueExt consumeQueueExt = genExt();
        putSth(consumeQueueExt, false, true, ((ConsumeQueueExtTest.unitCount) * 2));
        try {
            // truncate first one file.
            long address = consumeQueueExt.decorate(((long) ((ConsumeQueueExtTest.cqExtFileSize) * 1.5)));
            long expectMinAddress = consumeQueueExt.decorate(ConsumeQueueExtTest.cqExtFileSize);
            consumeQueueExt.truncateByMinAddress(address);
            long minAddress = consumeQueueExt.getMinAddress();
            assertThat(expectMinAddress).isEqualTo(minAddress);
        } finally {
            consumeQueueExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }

    @Test
    public void testTruncateByMaxOffset() {
        ConsumeQueueExt consumeQueueExt = genExt();
        putSth(consumeQueueExt, false, true, ((ConsumeQueueExtTest.unitCount) * 2));
        try {
            // truncate, only first 3 files exist.
            long address = consumeQueueExt.decorate((((ConsumeQueueExtTest.cqExtFileSize) * 2) + (ConsumeQueueExtTest.unitSizeWithBitMap)));
            long expectMaxAddress = address + (ConsumeQueueExtTest.unitSizeWithBitMap);
            consumeQueueExt.truncateByMaxAddress(address);
            long maxAddress = consumeQueueExt.getMaxAddress();
            assertThat(expectMaxAddress).isEqualTo(maxAddress);
        } finally {
            consumeQueueExt.destroy();
            UtilAll.deleteFile(new File(ConsumeQueueExtTest.storePath));
        }
    }
}

