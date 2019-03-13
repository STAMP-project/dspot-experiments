/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.CDatabaseManager;
import com.google.security.zynamics.binnavi.Database.Interfaces.IDatabase;
import com.google.security.zynamics.binnavi.disassembly.MockDatabaseManagerListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class DatabaseManagerTest {
    @Test
    public void testLifecycle() {
        final MockDatabaseManagerListener listener = new MockDatabaseManagerListener();
        final DatabaseManager manager = new DatabaseManager(CDatabaseManager.instance());
        manager.addListener(listener);
        manager.addDatabase("Description", "Driver", "Host", "Name", "User", "Password", "identity", true, false);
        final IDatabase internalDatabase = CDatabaseManager.instance().iterator().next();
        Assert.assertEquals("Description", internalDatabase.getConfiguration().getDescription());
        Assert.assertEquals("Driver", internalDatabase.getConfiguration().getDriver());
        Assert.assertEquals("User", internalDatabase.getConfiguration().getUser());
        Assert.assertEquals("Password", internalDatabase.getConfiguration().getPassword());
        Assert.assertTrue(internalDatabase.getConfiguration().isSavePassword());
        Assert.assertFalse(internalDatabase.getConfiguration().isAutoConnect());
        Assert.assertEquals(1, manager.getDatabases().size());
        Assert.assertEquals("addedDatabase;", listener.events);
        Assert.assertEquals("Database Manager ['Description']", manager.toString());
        final Database database = manager.getDatabases().get(0);
        manager.removeDatabase(database);
        Assert.assertEquals(0, manager.getDatabases().size());
        Assert.assertEquals("addedDatabase;removedDatabase;", listener.events);
        manager.removeListener(listener);
    }
}

