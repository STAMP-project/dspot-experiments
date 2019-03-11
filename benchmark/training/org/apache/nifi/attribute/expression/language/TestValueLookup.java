/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.attribute.expression.language;


import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.registry.VariableRegistry;
import org.junit.Assert;
import org.junit.Test;


public class TestValueLookup {
    @Test
    @SuppressWarnings("unchecked")
    public void testCreateCustomVariableRegistry() {
        final VariableRegistry variableRegistry = VariableRegistry.ENVIRONMENT_SYSTEM_REGISTRY;
        final ValueLookup initialLookup = new ValueLookup(variableRegistry, null);
        Assert.assertTrue(initialLookup.containsKey("PATH"));
        Assert.assertFalse(initialLookup.containsKey("fake.property.3"));
        Assert.assertFalse(initialLookup.containsKey("fake"));
        final Map<String, String> otherAttrs = new HashMap<>();
        otherAttrs.put("fake", "test");
        otherAttrs.put("fake.property.3", "test me out 3, test me out 4");
        final ValueLookup newLookup = new ValueLookup(variableRegistry, null, otherAttrs);
        Assert.assertTrue(newLookup.containsKey("PATH"));
        Assert.assertTrue(newLookup.containsKey("fake.property.3"));
        Assert.assertEquals("test me out 3, test me out 4", newLookup.get("fake.property.3"));
        Assert.assertEquals("test", newLookup.get("fake"));
        Assert.assertFalse(newLookup.containsKey("filename"));
        final FlowFile fakeFile = createFlowFile();
        final ValueLookup ffLookup = new ValueLookup(variableRegistry, fakeFile, otherAttrs);
        Assert.assertTrue(ffLookup.containsKey("filename"));
        Assert.assertEquals("test", ffLookup.get("fake"));
        Assert.assertEquals("1", ffLookup.get("flowFileId"));
        Assert.assertEquals("50", ffLookup.get("fileSize"));
        Assert.assertEquals("1000", ffLookup.get("entryDate"));
        Assert.assertEquals("10000", ffLookup.get("lineageStartDate"));
        Assert.assertEquals("fakefile.txt", ffLookup.get("filename"));
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("fake", "the real deal");
        final ValueLookup overriddenLookup = new ValueLookup(variableRegistry, fakeFile, overrides, otherAttrs);
        Assert.assertTrue(overriddenLookup.containsKey("filename"));
        Assert.assertEquals("the real deal", overriddenLookup.get("fake"));
        Assert.assertEquals("1", overriddenLookup.get("flowFileId"));
        Assert.assertEquals("50", overriddenLookup.get("fileSize"));
        Assert.assertEquals("1000", overriddenLookup.get("entryDate"));
        Assert.assertEquals("10000", overriddenLookup.get("lineageStartDate"));
        Assert.assertEquals("fakefile.txt", overriddenLookup.get("filename"));
        Assert.assertEquals("original", overriddenLookup.get("override me"));
        final Map<String, String> newOverrides = new HashMap<>();
        newOverrides.put("fake", "the real deal");
        newOverrides.put("override me", "done you are now overridden");
        final ValueLookup newOverriddenLookup = new ValueLookup(variableRegistry, fakeFile, newOverrides, otherAttrs);
        Assert.assertTrue(newOverriddenLookup.containsKey("filename"));
        Assert.assertEquals("the real deal", newOverriddenLookup.get("fake"));
        Assert.assertEquals("1", newOverriddenLookup.get("flowFileId"));
        Assert.assertEquals("50", newOverriddenLookup.get("fileSize"));
        Assert.assertEquals("1000", newOverriddenLookup.get("entryDate"));
        Assert.assertEquals("10000", newOverriddenLookup.get("lineageStartDate"));
        Assert.assertEquals("fakefile.txt", newOverriddenLookup.get("filename"));
        Assert.assertEquals("done you are now overridden", newOverriddenLookup.get("override me"));
    }
}

