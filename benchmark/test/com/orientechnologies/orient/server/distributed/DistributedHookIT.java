/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.server.distributed;


import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.hook.ORecordHookAbstract;
import com.orientechnologies.orient.core.record.ORecord;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;

import static DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;


/**
 * Tests the behavior of hooks in distributed configuration.
 */
public class DistributedHookIT extends AbstractServerClusterTest {
    private static final int SERVERS = 2;

    private final AtomicLong beforeCreate = new AtomicLong();

    private final AtomicLong afterCreate = new AtomicLong();

    private final AtomicLong beforeRead = new AtomicLong();

    private final AtomicLong afterRead = new AtomicLong();

    private final AtomicLong beforeUpdate = new AtomicLong();

    private final AtomicLong afterUpdate = new AtomicLong();

    private final AtomicLong beforeDelete = new AtomicLong();

    private final AtomicLong afterDelete = new AtomicLong();

    public class TestHookSourceNode extends ORecordHookAbstract {
        @Override
        public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
            return SOURCE_NODE;
        }

        @Override
        public RESULT onRecordBeforeCreate(ORecord iRecord) {
            beforeCreate.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordBeforeCreate");
            return super.onRecordBeforeCreate(iRecord);
        }

        @Override
        public void onRecordAfterCreate(ORecord iRecord) {
            afterCreate.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordAfterCreate");
        }

        @Override
        public RESULT onRecordBeforeRead(ORecord iRecord) {
            beforeRead.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordBeforeRead");
            return super.onRecordBeforeRead(iRecord);
        }

        @Override
        public void onRecordAfterRead(ORecord iRecord) {
            afterRead.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordAfterRead");
        }

        @Override
        public RESULT onRecordBeforeUpdate(ORecord iRecord) {
            beforeUpdate.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordBeforeUpdate");
            return super.onRecordBeforeUpdate(iRecord);
        }

        @Override
        public void onRecordAfterUpdate(ORecord iRecord) {
            afterUpdate.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordAfterUpdate");
        }

        @Override
        public RESULT onRecordBeforeDelete(ORecord iRecord) {
            beforeDelete.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordBeforeDelete");
            return super.onRecordBeforeDelete(iRecord);
        }

        @Override
        public void onRecordAfterDelete(ORecord iRecord) {
            afterDelete.incrementAndGet();
            OLogManager.instance().info(this, "TestHookSourceNode onRecordAfterDelete");
        }
    }

    @Test
    public void test() throws Exception {
        init(DistributedHookIT.SERVERS);
        prepare(false);
        execute();
    }
}

