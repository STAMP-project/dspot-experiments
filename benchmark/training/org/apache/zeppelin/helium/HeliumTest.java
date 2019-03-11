/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.helium;


import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

import static HeliumType.APPLICATION;


public class HeliumTest {
    private File tmpDir;

    private File localRegistryPath;

    @Test
    public void testSaveLoadConf() throws TaskRunnerException, IOException, URISyntaxException {
        // given
        File heliumConf = new File(tmpDir, "helium.conf");
        Helium helium = new Helium(heliumConf.getAbsolutePath(), localRegistryPath.getAbsolutePath(), null, null, null, null);
        Assert.assertFalse(heliumConf.exists());
        // when
        helium.saveConfig();
        // then
        Assert.assertTrue(heliumConf.exists());
        // then load without exception
        Helium heliumRestored = new Helium(heliumConf.getAbsolutePath(), localRegistryPath.getAbsolutePath(), null, null, null, null);
    }

    @Test
    public void testRestoreRegistryInstances() throws TaskRunnerException, IOException, URISyntaxException {
        File heliumConf = new File(tmpDir, "helium.conf");
        Helium helium = new Helium(heliumConf.getAbsolutePath(), localRegistryPath.getAbsolutePath(), null, null, null, null);
        HeliumTestRegistry registry1 = new HeliumTestRegistry("r1", "r1");
        HeliumTestRegistry registry2 = new HeliumTestRegistry("r2", "r2");
        helium.addRegistry(registry1);
        helium.addRegistry(registry2);
        // when
        registry1.add(new HeliumPackage(APPLICATION, "name1", "desc1", "artifact1", "className1", new String[][]{  }, "", ""));
        registry2.add(new HeliumPackage(APPLICATION, "name2", "desc2", "artifact2", "className2", new String[][]{  }, "", ""));
        // then
        Assert.assertEquals(2, helium.getAllPackageInfo().size());
    }

    @Test
    public void testRefresh() throws TaskRunnerException, IOException, URISyntaxException {
        File heliumConf = new File(tmpDir, "helium.conf");
        Helium helium = new Helium(heliumConf.getAbsolutePath(), localRegistryPath.getAbsolutePath(), null, null, null, null);
        HeliumTestRegistry registry1 = new HeliumTestRegistry("r1", "r1");
        helium.addRegistry(registry1);
        // when
        registry1.add(new HeliumPackage(APPLICATION, "name1", "desc1", "artifact1", "className1", new String[][]{  }, "", ""));
        // then
        Assert.assertEquals(1, helium.getAllPackageInfo().size());
        // when
        registry1.add(new HeliumPackage(APPLICATION, "name2", "desc2", "artifact2", "className2", new String[][]{  }, "", ""));
        // then
        Assert.assertEquals(2, helium.getAllPackageInfo(true, null).size());
    }
}

