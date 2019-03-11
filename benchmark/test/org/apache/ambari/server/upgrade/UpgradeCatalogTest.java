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


import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.InMemoryDefaultTestModule;
import org.junit.Test;


public class UpgradeCatalogTest {
    private Injector injector;

    private AmbariMetaInfo metaInfo;

    private final String DESIRED_STACK_VERSION = "{\"stackName\":\"HDP\"," + "\"stackVersion\":\"1.2.0\"}";

    private static class UpgradeCatalog201 extends AbstractUpgradeCatalog {
        @Inject
        public UpgradeCatalog201(Injector injector) {
            super(injector);
        }

        @Override
        public void executeDDLUpdates() throws SQLException, AmbariException {
            // do nothing: only for path testing
        }

        @Override
        public void executePreDMLUpdates() throws SQLException, AmbariException {
        }

        @Override
        public void executeDMLUpdates() throws SQLException, AmbariException {
        }

        @Override
        public String getTargetVersion() {
            return "2.0.1";
        }
    }

    private static class UpgradeHelperModuleTest extends InMemoryDefaultTestModule {
        @Override
        protected void configure() {
            super.configure();
            // Add binding to each newly created catalog
            Multibinder<UpgradeCatalog> catalogBinder = Multibinder.newSetBinder(binder(), UpgradeCatalog.class);
            catalogBinder.addBinding().to(UpgradeCatalogTest.UpgradeCatalog201.class);
            catalogBinder.addBinding().to(UpgradeCatalog251.class);
            catalogBinder.addBinding().to(UpgradeCatalog252.class);
            catalogBinder.addBinding().to(UpgradeCatalog270.class);
        }
    }

    @Test
    public void testUpgradePath() throws Exception {
        SchemaUpgradeHelper schemaUpgradeHelper = injector.getInstance(SchemaUpgradeHelper.class);
        Set<UpgradeCatalog> upgradeCatalogSet = schemaUpgradeHelper.getAllUpgradeCatalogs();
        Assert.assertNotNull(upgradeCatalogSet);
        Assert.assertEquals(4, upgradeCatalogSet.size());
        List<UpgradeCatalog> upgradeCatalogs = schemaUpgradeHelper.getUpgradePath(null, "2.5.1");
        Assert.assertNotNull(upgradeCatalogs);
        Assert.assertEquals(2, upgradeCatalogs.size());
        Assert.assertEquals("2.0.1", upgradeCatalogs.get(0).getTargetVersion());
        Assert.assertEquals("2.5.1", upgradeCatalogs.get(1).getTargetVersion());
    }
}

