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
package org.apache.ambari.server.state.stack;


import category.StackUpgradeTest;
import com.google.inject.Injector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.annotations.Experimental;
import org.apache.ambari.annotations.ExperimentalFeature;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.stack.ModuleFileUnmarshaller;
import org.apache.ambari.server.stack.upgrade.ConfigUpgradePack;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeContextFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that for every upgrade pack found, that all referenced configuration
 * IDs exist in the {@code config-upgrade.xml} which will be used/created. Also
 * ensures that every XML file is valid against its XSD.
 */
@Ignore
@Category({ StackUpgradeTest.class })
@Experimental(feature = ExperimentalFeature.MULTI_SERVICE, comment = "This was a very useful test that no longer works since all of the XML files for " + ("upgrades are delivered in mpacks. This should be converted into a some realtime check " + "when an mpack is registered."))
public class ConfigUpgradeValidityTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigUpgradeValidityTest.class);

    private Injector injector;

    private AmbariMetaInfo ambariMetaInfo;

    private UpgradeContextFactory upgradeContextFactory;

    private int validatedConfigCount = 0;

    @Test
    @SuppressWarnings("unchecked")
    public void testValidateConfigUpgradePacks() throws Exception {
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return false;
            }

            @Override
            public boolean accept(File file) {
                // file has the folder named 'upgrades', ends with '.xml' and is NOT
                // 'config-upgrade.xml'
                if ((file.getAbsolutePath().contains("upgrades")) && (file.getAbsolutePath().endsWith("config-upgrade.xml"))) {
                    return true;
                }
                return false;
            }
        };
        List<File> files = new ArrayList<>();
        files.addAll(FileUtils.listFiles(new File("src/main/resources/stacks"), filter, FileFilterUtils.directoryFileFilter()));
        files.addAll(FileUtils.listFiles(new File("src/test/resources/stacks"), filter, FileFilterUtils.directoryFileFilter()));
        files.addAll(FileUtils.listFiles(new File("src/test/resources/stacks_with_upgrade_cycle"), filter, FileFilterUtils.directoryFileFilter()));
        ModuleFileUnmarshaller unmarshaller = new ModuleFileUnmarshaller();
        int filesTestedCount = 0;
        for (File file : files) {
            String fileContent = FileUtils.readFileToString(file, "UTF-8");
            // these things must be in upgrade packs for them to work anyway
            if ((fileContent.contains("<upgrade-config-changes")) && (fileContent.contains("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""))) {
                if (!(fileContent.contains("xsi:noNamespaceSchemaLocation=\"upgrade-config.xsd\""))) {
                    String msg = String.format("File %s appears to be a config upgrade pack, but does not define 'upgrade-config.xsd' as its schema", file.getAbsolutePath());
                    Assert.fail(msg);
                } else {
                    filesTestedCount++;
                    unmarshaller.unmarshal(ConfigUpgradePack.class, file, true);
                }
            }
        }
        Assert.assertTrue("This test didn't appear to do any work which could indicate that it failed to find files to validate", (filesTestedCount > 5));
    }
}

