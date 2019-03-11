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
package org.apache.hadoop.yarn.service.client;


import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.service.conf.ExampleAppJson;
import org.apache.hadoop.yarn.service.utils.SliderFileSystem;
import org.junit.Test;


/**
 * Test for building / resolving components of type SERVICE.
 */
public class TestBuildExternalComponents {
    protected Configuration conf = new YarnConfiguration();

    private File basedir;

    // Test applications defining external components(SERVICE type)
    // can be resolved correctly
    @Test
    public void testExternalComponentBuild() throws Throwable {
        SliderFileSystem sfs = new SliderFileSystem(conf);
        Set<String> nameSet = new HashSet<>();
        nameSet.add("simple");
        nameSet.add("master");
        nameSet.add("worker");
        // app-1 has 3 components: simple, master, worker
        buildAndCheckComponents("app-1", ExampleAppJson.APP_JSON, sfs, nameSet);
        buildAndCheckComponents("external-0", ExampleAppJson.EXTERNAL_JSON_0, sfs, nameSet);
        nameSet.add("other");
        // external1 has 3 components: simple(SERVICE - app1), master and other
        buildAndCheckComponents("external-1", ExampleAppJson.EXTERNAL_JSON_1, sfs, nameSet);
        nameSet.add("another");
        // external2 has 2 components: ext(SERVICE - external1), another
        buildAndCheckComponents("external-2", ExampleAppJson.EXTERNAL_JSON_2, sfs, nameSet);
    }
}

