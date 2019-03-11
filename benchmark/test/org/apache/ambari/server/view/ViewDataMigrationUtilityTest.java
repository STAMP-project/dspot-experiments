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
package org.apache.ambari.server.view;


import junit.framework.Assert;
import org.apache.ambari.server.orm.entities.ViewInstanceEntity;
import org.apache.ambari.view.migration.ViewDataMigrationException;
import org.apache.ambari.view.migration.ViewDataMigrator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * ViewDataMigrationUtility Tests.
 */
public class ViewDataMigrationUtilityTest {
    private static String viewName = "MY_VIEW";

    private static String instanceName = "INSTANCE1";

    private static String version1 = "1.0.0";

    private static String version2 = "2.0.0";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ViewRegistry viewRegistry;

    @Test
    public void testMigrateDataSameVersions() throws Exception {
        ViewDataMigrationUtilityTest.TestViewDataMigrationUtility migrationUtility = new ViewDataMigrationUtilityTest.TestViewDataMigrationUtility(viewRegistry);
        ViewDataMigrationContextImpl context = ViewDataMigrationUtilityTest.getViewDataMigrationContext(42, 42);
        migrationUtility.setMigrationContext(context);
        ViewDataMigrator migrator = migrationUtility.getViewDataMigrator(getInstanceDefinition(ViewDataMigrationUtilityTest.viewName, ViewDataMigrationUtilityTest.version2, ViewDataMigrationUtilityTest.instanceName), context);
        Assert.assertTrue((migrator instanceof ViewDataMigrationUtility.CopyAllDataMigrator));
    }

    @Test
    public void testMigrateDataDifferentVersions() throws Exception {
        ViewDataMigrationUtilityTest.TestViewDataMigrationUtility migrationUtility = new ViewDataMigrationUtilityTest.TestViewDataMigrationUtility(viewRegistry);
        ViewDataMigrationContextImpl context = ViewDataMigrationUtilityTest.getViewDataMigrationContext(2, 1);
        migrationUtility.setMigrationContext(context);
        ViewDataMigrator migrator = createStrictMock(ViewDataMigrator.class);
        expect(migrator.beforeMigration()).andReturn(true);
        migrator.migrateEntity(anyObject(Class.class), anyObject(Class.class));
        expectLastCall();
        migrator.migrateInstanceData();
        expectLastCall();
        migrator.afterMigration();
        expectLastCall();
        replay(migrator);
        ViewInstanceEntity targetInstance = getInstanceDefinition(ViewDataMigrationUtilityTest.viewName, ViewDataMigrationUtilityTest.version2, ViewDataMigrationUtilityTest.instanceName, migrator);
        ViewInstanceEntity sourceInstance = getInstanceDefinition(ViewDataMigrationUtilityTest.viewName, ViewDataMigrationUtilityTest.version1, ViewDataMigrationUtilityTest.instanceName);
        migrationUtility.migrateData(targetInstance, sourceInstance, false);
        verify(migrator);
    }

    @Test
    public void testMigrateDataDifferentVersionsCancel() throws Exception {
        ViewDataMigrationUtilityTest.TestViewDataMigrationUtility migrationUtility = new ViewDataMigrationUtilityTest.TestViewDataMigrationUtility(viewRegistry);
        ViewDataMigrationContextImpl context = ViewDataMigrationUtilityTest.getViewDataMigrationContext(2, 1);
        migrationUtility.setMigrationContext(context);
        ViewDataMigrator migrator = createStrictMock(ViewDataMigrator.class);
        expect(migrator.beforeMigration()).andReturn(false);
        ViewInstanceEntity targetInstance = getInstanceDefinition(ViewDataMigrationUtilityTest.viewName, ViewDataMigrationUtilityTest.version2, ViewDataMigrationUtilityTest.instanceName, migrator);
        ViewInstanceEntity sourceInstance = getInstanceDefinition(ViewDataMigrationUtilityTest.viewName, ViewDataMigrationUtilityTest.version1, ViewDataMigrationUtilityTest.instanceName);
        thrown.expect(ViewDataMigrationException.class);
        migrationUtility.migrateData(targetInstance, sourceInstance, false);
    }

    private static class TestViewDataMigrationUtility extends ViewDataMigrationUtility {
        private ViewDataMigrationContextImpl migrationContext;

        public TestViewDataMigrationUtility(ViewRegistry viewRegistry) {
            super(viewRegistry);
        }

        @Override
        protected ViewDataMigrationContextImpl getViewDataMigrationContext(ViewInstanceEntity targetInstanceDefinition, ViewInstanceEntity sourceInstanceDefinition) {
            if ((migrationContext) == null) {
                return super.getViewDataMigrationContext(targetInstanceDefinition, sourceInstanceDefinition);
            }
            return migrationContext;
        }

        public ViewDataMigrationContextImpl getMigrationContext() {
            return migrationContext;
        }

        public void setMigrationContext(ViewDataMigrationContextImpl migrationContext) {
            this.migrationContext = migrationContext;
        }
    }
}

