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
package org.apache.ambari.server.resources;


import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.io.File;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.state.stack.OsFamily;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TestResources extends TestCase {
    private static ResourceManager resMan;

    private static final String RESOURCE_FILE_NAME = "resources.ext";

    private static final String RESOURCE_FILE_CONTENT = "CONTENT";

    Injector injector;

    private TemporaryFolder tempFolder = new TemporaryFolder();

    private File resourceFile;

    private class ResourceModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Properties.class).toInstance(buildTestProperties());
            bind(Configuration.class).toConstructor(getConfigurationConstructor());
            bind(OsFamily.class).toInstance(createNiceMock(OsFamily.class));
            requestStaticInjection(TestResources.class);
        }
    }

    @Test
    public void testGetResource() throws Exception {
        File resFile = TestResources.resMan.getResource(resourceFile.getName());
        TestCase.assertTrue(resFile.exists());
        String resContent = FileUtils.readFileToString(resFile);
        TestCase.assertEquals(resContent, TestResources.RESOURCE_FILE_CONTENT);
    }
}

