/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.config;


import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConfigPropertyPathTest extends TestCase {
    private Config config = null;

    final String homeDir = System.getProperty("user.home");

    // test getting config from light-4j-config-dir
    public void testGetConfig() throws Exception {
        config.clear();
        Map<String, Object> configMap = config.getJsonMapConfig("test");
        Assert.assertEquals("default dir", configMap.get("value"));
    }

    // test getting config from absolute path "/homeDir/src"
    public void testGetConfigFromAbsPath() {
        config.clear();
        Map<String, Object> configMap = config.getJsonMapConfig("test", ((homeDir) + "/dir1"));
        Assert.assertEquals("externalized dir1", configMap.get("value"));
    }

    // test getting config from relative path "src"
    public void testGetConfigFromRelPath() {
        config.clear();
        Map<String, Object> configMap = config.getJsonMapConfig("test", "dir1");
        Assert.assertEquals("externalized dir1", configMap.get("value"));
    }

    // test getting config from absolute path "/homeDir/src"
    public void testGetObjectConfigFromAbsPath() {
        config.clear();
        TestConfig configObject = ((TestConfig) (config.getJsonObjectConfig("test", TestConfig.class, ((homeDir) + "/dir1"))));
        Assert.assertEquals("externalized dir1", configObject.getValue());
    }

    // test getting config from relative path "src"
    public void testGetObjectConfigFromRelPath() {
        config.clear();
        TestConfig configObject = ((TestConfig) (config.getJsonObjectConfig("test", TestConfig.class, "dir1")));
        Assert.assertEquals("externalized dir1", configObject.getValue());
    }

    // test getting config when the config dir is a list
    public void testGetMapConfigFromMultiPath() throws Exception {
        config.clear();
        setExternalizedConfigDir(((((((homeDir) + ":") + (homeDir)) + "/dir1:") + (homeDir)) + "/dir2"));
        Map<String, Object> configMap = config.getJsonMapConfig("test");
        Assert.assertEquals("externalized dir2", configMap.get("value"));
    }
}

