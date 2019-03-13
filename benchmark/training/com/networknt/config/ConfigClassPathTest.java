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


/**
 * Created by steve on 23/09/16.
 */
public class ConfigClassPathTest extends TestCase {
    private Config config = null;

    private final String homeDir = System.getProperty("user.home");

    public void testGetConfigFromClassPath() {
        config.clear();
        Map<String, Object> configMap = config.getJsonMapConfig("test");
        Assert.assertEquals("classpath", configMap.get("value"));
    }
}

