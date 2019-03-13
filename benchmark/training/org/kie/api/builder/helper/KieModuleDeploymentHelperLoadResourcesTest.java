/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.api.builder.helper;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.builder.helper.KieModuleDeploymentHelperImpl.KJarResource;


public class KieModuleDeploymentHelperLoadResourcesTest {
    @Test
    public void testInternalLoadResources() throws Exception {
        List<KJarResource> resources = null;
        // local
        String path = "/builder/simple_query_test.drl";
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(path, false);
        Assert.assertEquals(path, 1, resources.size());
        String content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        path = "/builder/test/";
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(path, true);
        Assert.assertEquals(path, 2, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        path = "/builder/";
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(path, true);
        Assert.assertEquals(path, 1, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        // classpath
        path = "META-INF/WorkDefinitions.conf";
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(path, false);
        Assert.assertEquals(path, 1, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        path = "META-INF/plexus/";
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(path, true);
        Assert.assertEquals(path, 3, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        // file
        content = "test file created by " + (this.getClass().getSimpleName());
        final String baseTempPath = System.getProperty("java.io.tmpdir");
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".tst");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(tempFile.getAbsolutePath(), false);
        Assert.assertEquals(path, 1, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
        File tempDir = new File(((baseTempPath + "/") + (UUID.randomUUID().toString())));
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFile = new File(((((tempDir.getAbsolutePath()) + "/") + (UUID.randomUUID().toString())) + ".tst"));
        fos = new FileOutputStream(tempFile);
        fos.write(content.getBytes());
        fos.close();
        resources = KieModuleDeploymentHelperImpl.internalLoadResources(tempDir.getAbsolutePath(), true);
        Assert.assertEquals(path, 1, resources.size());
        content = resources.get(0).content;
        Assert.assertTrue(((content != null) && ((content.length()) > 10)));
    }
}

