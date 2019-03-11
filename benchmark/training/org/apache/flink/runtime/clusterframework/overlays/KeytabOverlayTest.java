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
package org.apache.flink.runtime.clusterframework.overlays;


import KeytabOverlay.Builder;
import SecurityOptions.KERBEROS_LOGIN_KEYTAB;
import java.io.File;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.clusterframework.ContainerSpecification;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class KeytabOverlayTest extends ContainerOverlayTestBase {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testConfigure() throws Exception {
        File keytab = tempFolder.newFile();
        KeytabOverlay overlay = new KeytabOverlay(keytab);
        ContainerSpecification spec = new ContainerSpecification();
        overlay.configure(spec);
        Assert.assertEquals(KeytabOverlay.TARGET_PATH.getPath(), spec.getDynamicConfiguration().getString(KERBEROS_LOGIN_KEYTAB));
        ContainerOverlayTestBase.checkArtifact(spec, KeytabOverlay.TARGET_PATH);
    }

    @Test
    public void testNoConf() throws Exception {
        KeytabOverlay overlay = new KeytabOverlay(((Path) (null)));
        ContainerSpecification containerSpecification = new ContainerSpecification();
        overlay.configure(containerSpecification);
    }

    @Test
    public void testBuilderFromEnvironment() throws Exception {
        final Configuration conf = new Configuration();
        File keytab = tempFolder.newFile();
        conf.setString(KERBEROS_LOGIN_KEYTAB, keytab.getAbsolutePath());
        KeytabOverlay.Builder builder = KeytabOverlay.newBuilder().fromEnvironment(conf);
        Assert.assertEquals(builder.keytabPath, keytab);
    }
}

