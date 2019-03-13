/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.config;


import com.hazelcast.core.HazelcastException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * These tests manipulate system properties, therefore they must be run in serial mode.
 *
 * @see YamlConfigWithSystemPropertyTest
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class XMLConfigWithSystemPropertyTest {
    @Test
    public void testConfigurationWithFile() throws Exception {
        URL url = getClass().getClassLoader().getResource("hazelcast-default.xml");
        String decodedURL = URLDecoder.decode(url.getFile(), "UTF-8");
        System.setProperty("hazelcast.config", decodedURL);
        Config config = new XmlConfigBuilder().build();
        URL file = new URL("file:");
        URL encodedURL = new URL(file, decodedURL);
        Assert.assertEquals(encodedURL, config.getConfigurationUrl());
    }

    @Test(expected = HazelcastException.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadingThroughSystemProperty_nonExistingFile() throws Exception {
        File file = File.createTempFile("foo", ".xml");
        file.delete();
        System.setProperty("hazelcast.config", file.getAbsolutePath());
        new XmlConfigBuilder();
    }

    @Test
    public void loadingThroughSystemProperty_existingFile() throws Exception {
        String xml = (((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "    <group>\n") + "        <name>foobar</name>\n") + "        <password>dev-pass</password>\n") + "    </group>") + (XMLConfigBuilderTest.HAZELCAST_END_TAG);
        File file = File.createTempFile("foo", ".xml");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println(xml);
        writer.close();
        System.setProperty("hazelcast.config", file.getAbsolutePath());
        XmlConfigBuilder configBuilder = new XmlConfigBuilder();
        Config config = configBuilder.build();
        Assert.assertEquals("foobar", config.getGroupConfig().getName());
    }

    @Test(expected = HazelcastException.class)
    public void loadingThroughSystemProperty_nonExistingClasspathResource() {
        System.setProperty("hazelcast.config", "classpath:idontexist.xml");
        new XmlConfigBuilder();
    }

    @Test
    public void loadingThroughSystemProperty_existingClasspathResource() {
        System.setProperty("hazelcast.config", "classpath:test-hazelcast.xml");
        XmlConfigBuilder configBuilder = new XmlConfigBuilder();
        Config config = configBuilder.build();
        Assert.assertEquals("foobar", config.getGroupConfig().getName());
    }
}

