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
package org.apache.ambari.server.stack;


import category.KerberosTest;
import com.networknt.schema.JsonSchema;
import java.io.File;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * KerberosDescriptorTest tests the stack- and service-level descriptors for certain stacks
 * and services
 */
@Category({ KerberosTest.class })
public class KerberosDescriptorTest {
    private static Logger LOG = LoggerFactory.getLogger(KerberosDescriptorTest.class);

    private static final Pattern PATTERN_KERBEROS_DESCRIPTOR_FILENAME = Pattern.compile("^kerberos(?:_preconfigure)?\\.json$");

    private static File stacksDirectory;

    private static File commonServicesDirectory;

    @Test
    public void testCommonServiceDescriptor() throws Exception {
        JsonSchema schema = getJsonSchemaFromPath("kerberos_descriptor_schema.json");
        Assert.assertTrue(visitFile(schema, KerberosDescriptorTest.commonServicesDirectory, true));
    }

    @Test
    public void testStackServiceDescriptor() throws Exception {
        JsonSchema schema = getJsonSchemaFromPath("kerberos_descriptor_schema.json");
        Assert.assertTrue(visitFile(schema, KerberosDescriptorTest.stacksDirectory, true));
    }
}

