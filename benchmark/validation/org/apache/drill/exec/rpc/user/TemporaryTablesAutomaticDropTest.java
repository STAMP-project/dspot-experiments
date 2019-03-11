/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.rpc.user;


import StoragePluginTestUtils.DFS_PLUGIN_NAME;
import java.io.File;
import java.util.UUID;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.store.StoragePluginRegistry;
import org.apache.drill.exec.util.StoragePluginTestUtils;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.DirTestWatcher;
import org.junit.Assert;
import org.junit.Test;


public class TemporaryTablesAutomaticDropTest extends BaseTestQuery {
    private static final UUID SESSION_UUID = UUID.randomUUID();

    @Test
    public void testAutomaticDropWhenClientIsClosed() throws Exception {
        final File sessionTemporaryLocation = createAndCheckSessionTemporaryLocation("client_closed", ExecTest.dirTestWatcher.getDfsTestTmpDir());
        BaseTestQuery.updateClient("new_client");
        Assert.assertFalse("Session temporary location should be absent", sessionTemporaryLocation.exists());
    }

    @Test
    public void testAutomaticDropWhenDrillbitIsClosed() throws Exception {
        final File sessionTemporaryLocation = createAndCheckSessionTemporaryLocation("drillbit_closed", ExecTest.dirTestWatcher.getDfsTestTmpDir());
        BaseTestQuery.bits[0].close();
        Assert.assertFalse("Session temporary location should be absent", sessionTemporaryLocation.exists());
    }

    @Test
    public void testAutomaticDropOfSeveralSessionTemporaryLocations() throws Exception {
        final File firstSessionTemporaryLocation = createAndCheckSessionTemporaryLocation("first_location", ExecTest.dirTestWatcher.getDfsTestTmpDir());
        final StoragePluginRegistry pluginRegistry = BaseTestQuery.getDrillbitContext().getStorage();
        final File tempDir = DirTestWatcher.createTempDir(ExecTest.dirTestWatcher.getDir());
        try {
            StoragePluginTestUtils.updateSchemaLocation(DFS_PLUGIN_NAME, pluginRegistry, tempDir);
            final File secondSessionTemporaryLocation = createAndCheckSessionTemporaryLocation("second_location", tempDir);
            BaseTestQuery.updateClient("new_client");
            Assert.assertFalse("First session temporary location should be absent", firstSessionTemporaryLocation.exists());
            Assert.assertFalse("Second session temporary location should be absent", secondSessionTemporaryLocation.exists());
        } finally {
            StoragePluginTestUtils.updateSchemaLocation(DFS_PLUGIN_NAME, pluginRegistry, ExecTest.dirTestWatcher.getDfsTestTmpDir());
        }
    }
}

