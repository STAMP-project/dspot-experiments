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
package org.apache.nifi.controller.repository;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static java.util.UUID.randomUUID;


public class TestStandardFlowFileRecord {
    @Test
    public void testAttributeCopiedOnModification() {
        final FlowFileRecord original = new StandardFlowFileRecord.Builder().addAttribute("uuid", randomUUID().toString()).addAttribute("abc", "xyz").build();
        final FlowFileRecord addAttribute = new StandardFlowFileRecord.Builder().fromFlowFile(original).addAttribute("hello", "good-bye").build();
        final Map<String, String> addAttributeMapCopy = new java.util.HashMap(addAttribute.getAttributes());
        Assert.assertEquals("good-bye", addAttribute.getAttributes().get("hello"));
        Assert.assertEquals("xyz", addAttribute.getAttributes().get("abc"));
        Assert.assertEquals("xyz", original.getAttributes().get("abc"));
        Assert.assertFalse(original.getAttributes().containsKey("hello"));
        final FlowFileRecord removeAttribute = new StandardFlowFileRecord.Builder().fromFlowFile(addAttribute).removeAttributes("hello").build();
        Assert.assertEquals(original.getAttributes(), removeAttribute.getAttributes());
        Assert.assertEquals(addAttributeMapCopy, addAttribute.getAttributes());
    }
}

