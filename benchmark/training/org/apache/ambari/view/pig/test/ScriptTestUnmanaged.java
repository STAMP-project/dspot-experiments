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
package org.apache.ambari.view.pig.test;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.view.pig.BasePigTest;
import org.apache.ambari.view.pig.resources.scripts.ScriptService;
import org.apache.ambari.view.pig.utils.ServiceFormattedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests without HDFS and predefined properties
 */
public class ScriptTestUnmanaged extends BasePigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ScriptService scriptService;

    private File pigStorageFile;

    private File baseDir;

    @Test
    public void createScriptAutoCreateNoDefaultFS() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("dataworker.storagePath", pigStorageFile.toString());
        properties.put("scripts.dir", "/tmp/.pigscripts");
        expect(context.getProperties()).andReturn(properties).anyTimes();
        expect(context.getUsername()).andReturn("ambari-qa").anyTimes();
        replay(handler, context);
        scriptService = BasePigTest.getService(ScriptService.class, handler, context);
        thrown.expect(ServiceFormattedException.class);
        doCreateScript("Test", null);
    }
}

