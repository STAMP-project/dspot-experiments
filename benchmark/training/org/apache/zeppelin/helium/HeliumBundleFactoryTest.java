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


import com.github.eirslett.maven.plugins.frontend.lib.InstallationException;
import com.github.eirslett.maven.plugins.frontend.lib.TaskRunnerException;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static HeliumType.VISUALIZATION;


public class HeliumBundleFactoryTest {
    private HeliumBundleFactory hbf;

    private File nodeInstallationDir;

    private String zeppelinHomePath;

    @Test
    public void testInstallNpm() throws InstallationException {
        Assert.assertTrue(new File(nodeInstallationDir, "/node/npm").isFile());
        Assert.assertTrue(new File(nodeInstallationDir, "/node/node").isFile());
        Assert.assertTrue(new File(nodeInstallationDir, "/node/yarn/dist/bin/yarn").isFile());
    }

    @Test
    public void downloadPackage() throws TaskRunnerException {
        HeliumPackage pkg = new HeliumPackage(VISUALIZATION, "lodash", "lodash", "lodash@3.9.3", "", null, "license", "icon");
        hbf.install(pkg);
        System.out.println(new File(nodeInstallationDir, "/node_modules/lodash"));
        Assert.assertTrue(new File(nodeInstallationDir, "/node_modules/lodash").isDirectory());
    }

    @Test
    public void bundlePackage() throws TaskRunnerException, IOException {
        HeliumPackage pkg = new HeliumPackage(VISUALIZATION, "zeppelin-bubblechart", "zeppelin-bubblechart", "zeppelin-bubblechart@0.0.3", "", null, "license", "icon");
        File bundle = hbf.buildPackage(pkg, true, true);
        Assert.assertTrue(bundle.isFile());
        long lastModified = bundle.lastModified();
        // buildBundle again and check if it served from cache
        bundle = hbf.buildPackage(pkg, false, true);
        Assert.assertEquals(lastModified, bundle.lastModified());
    }

    @Test
    public void bundleLocalPackage() throws TaskRunnerException, IOException {
        URL res = Resources.getResource("helium/webpack.config.js");
        String resDir = new File(res.getFile()).getParent();
        String localPkg = resDir + "/../../../src/test/resources/helium/vis1";
        HeliumPackage pkg = new HeliumPackage(VISUALIZATION, "vis1", "vis1", localPkg, "", null, "license", "fa fa-coffee");
        File bundle = hbf.buildPackage(pkg, true, true);
        Assert.assertTrue(bundle.isFile());
    }

    @Test
    public void switchVersion() throws TaskRunnerException, IOException {
        URL res = Resources.getResource("helium/webpack.config.js");
        String resDir = new File(res.getFile()).getParent();
        HeliumPackage pkgV1 = new HeliumPackage(VISUALIZATION, "zeppelin-bubblechart", "zeppelin-bubblechart", "zeppelin-bubblechart@0.0.3", "", null, "license", "icon");
        HeliumPackage pkgV2 = new HeliumPackage(VISUALIZATION, "zeppelin-bubblechart", "zeppelin-bubblechart", "zeppelin-bubblechart@0.0.1", "", null, "license", "icon");
        List<HeliumPackage> pkgsV1 = new LinkedList<>();
        pkgsV1.add(pkgV1);
        List<HeliumPackage> pkgsV2 = new LinkedList<>();
        pkgsV2.add(pkgV2);
        File bundle1 = hbf.buildPackage(pkgV1, true, true);
        File bundle2 = hbf.buildPackage(pkgV2, true, true);
        Assert.assertNotSame(bundle1.lastModified(), bundle2.lastModified());
    }
}

