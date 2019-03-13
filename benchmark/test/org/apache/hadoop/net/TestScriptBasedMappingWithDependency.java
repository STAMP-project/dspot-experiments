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
package org.apache.hadoop.net;


import ScriptBasedMapping.SCRIPT_ARG_COUNT_KEY;
import ScriptBasedMapping.SCRIPT_FILENAME_KEY;
import ScriptBasedMappingWithDependency.DEPENDENCY_SCRIPT_FILENAME_KEY;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;

import static ScriptBasedMapping.MIN_ALLOWABLE_ARGS;


public class TestScriptBasedMappingWithDependency {
    public TestScriptBasedMappingWithDependency() {
    }

    @Test
    public void testNoArgsMeansNoResult() {
        Configuration conf = new Configuration();
        conf.setInt(SCRIPT_ARG_COUNT_KEY, ((MIN_ALLOWABLE_ARGS) - 1));
        conf.set(SCRIPT_FILENAME_KEY, "any-filename-1");
        conf.set(DEPENDENCY_SCRIPT_FILENAME_KEY, "any-filename-2");
        conf.setInt(SCRIPT_ARG_COUNT_KEY, 10);
        ScriptBasedMappingWithDependency mapping = createMapping(conf);
        List<String> names = new ArrayList<String>();
        names.add("some.machine.name");
        names.add("other.machine.name");
        List<String> result = mapping.resolve(names);
        Assert.assertNull("Expected an empty list for resolve", result);
        result = mapping.getDependency("some.machine.name");
        Assert.assertNull("Expected an empty list for getDependency", result);
    }

    @Test
    public void testNoFilenameMeansSingleSwitch() throws Throwable {
        Configuration conf = new Configuration();
        ScriptBasedMapping mapping = createMapping(conf);
        Assert.assertTrue("Expected to be single switch", mapping.isSingleSwitch());
        Assert.assertTrue("Expected to be single switch", AbstractDNSToSwitchMapping.isMappingSingleSwitch(mapping));
    }

    @Test
    public void testFilenameMeansMultiSwitch() throws Throwable {
        Configuration conf = new Configuration();
        conf.set(SCRIPT_FILENAME_KEY, "any-filename");
        ScriptBasedMapping mapping = createMapping(conf);
        Assert.assertFalse("Expected to be multi switch", mapping.isSingleSwitch());
        mapping.setConf(new Configuration());
        Assert.assertTrue("Expected to be single switch", mapping.isSingleSwitch());
    }

    @Test
    public void testNullConfig() throws Throwable {
        ScriptBasedMapping mapping = createMapping(null);
        Assert.assertTrue("Expected to be single switch", mapping.isSingleSwitch());
    }
}

