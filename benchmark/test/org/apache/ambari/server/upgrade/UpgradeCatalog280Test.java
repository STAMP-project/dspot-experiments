/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.upgrade;


import DBAccessor.DBColumnInfo;
import com.google.inject.Injector;
import org.apache.ambari.server.orm.DBAccessor;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;


public class UpgradeCatalog280Test {
    private Injector injector;

    private DBAccessor dbAccessor;

    @Test
    public void testExecuteDDLUpdates() throws Exception {
        Capture<DBAccessor.DBColumnInfo> perBatchLimitColumn = newCapture(CaptureType.ALL);
        dbAccessor.addColumn(eq("requestschedule"), capture(perBatchLimitColumn));
        expectLastCall().once();
        Capture<DBAccessor.DBColumnInfo> autoPauseColumn = newCapture(CaptureType.ALL);
        dbAccessor.addColumn(eq("requestschedule"), capture(autoPauseColumn));
        expectLastCall().once();
        dbAccessor.dropColumn(eq(UpgradeCatalog280.HOST_COMPONENT_STATE_TABLE), eq(UpgradeCatalog280.LAST_LIVE_STATE_COLUMN));
        expectLastCall().once();
        Capture<DBAccessor.DBColumnInfo> upgradePackStackColumn = newCapture(CaptureType.ALL);
        dbAccessor.addColumn(eq("upgrade"), capture(upgradePackStackColumn));
        expectLastCall().once();
        final Capture<DBAccessor.DBColumnInfo> alterPropertyValueColumnCapture = newCapture(CaptureType.ALL);
        dbAccessor.alterColumn(eq(UpgradeCatalog280.AMBARI_CONFIGURATION_TABLE), capture(alterPropertyValueColumnCapture));
        expectLastCall().once();
        replay(dbAccessor, injector);
        UpgradeCatalog280 upgradeCatalog280 = new UpgradeCatalog280(injector);
        upgradeCatalog280.dbAccessor = dbAccessor;
        upgradeCatalog280.executeDDLUpdates();
        DBAccessor.DBColumnInfo perBatchLimitColumnInfo = perBatchLimitColumn.getValue();
        Assert.assertEquals("batch_toleration_limit_per_batch", perBatchLimitColumnInfo.getName());
        Assert.assertEquals(null, perBatchLimitColumnInfo.getDefaultValue());
        Assert.assertEquals(Short.class, perBatchLimitColumnInfo.getType());
        DBAccessor.DBColumnInfo autoPauseColumnInfo = autoPauseColumn.getValue();
        Assert.assertEquals("pause_after_first_batch", autoPauseColumnInfo.getName());
        Assert.assertEquals(null, autoPauseColumnInfo.getDefaultValue());
        Assert.assertEquals(Boolean.class, autoPauseColumnInfo.getType());
        DBAccessor.DBColumnInfo capturedUpgradeColumn = upgradePackStackColumn.getValue();
        Assert.assertEquals("upgrade_pack_stack_id", capturedUpgradeColumn.getName());
        Assert.assertEquals(String.class, capturedUpgradeColumn.getType());
        Assert.assertEquals(((Integer) (255)), capturedUpgradeColumn.getLength());
        final DBAccessor.DBColumnInfo alterPropertyValueColumn = alterPropertyValueColumnCapture.getValue();
        Assert.assertEquals(UpgradeCatalog280.AMBARI_CONFIGURATION_PROPERTY_VALUE_COLUMN, alterPropertyValueColumn.getName());
        Assert.assertEquals(String.class, alterPropertyValueColumn.getType());
        Assert.assertEquals(((Integer) (4000)), alterPropertyValueColumn.getLength());
        Assert.assertFalse(alterPropertyValueColumn.isNullable());
        verify(dbAccessor);
    }
}

