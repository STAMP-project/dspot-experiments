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
package org.apache.ambari.server.state;


import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.validation.Validator;
import org.apache.ambari.server.stack.StackManager;
import org.junit.Test;
import org.xml.sax.SAXException;


public class ServicePropertiesTest {
    /**
     * This unit test checks that all config xmls for all services pass validation.
     * They should match xsd schema configuration-schema.xsd.
     * Test checks real (production) configs like hdfs-site.xml. The reason why
     * this test exists is to make sure that anybody who adds new properties to stack
     * configs, explicitly defines whether they should be added/modified/deleted
     * during ambari upgrade and/or stack upgrade.
     *
     * @throws SAXException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void validatePropertySchemaOfServiceXMLs() throws IOException, URISyntaxException, SAXException {
        Validator validator = StackManager.getPropertySchemaValidator();
        // TODO: make sure that test does not depend on mvn/junit working directory
        StackManager.validateAllPropertyXmlsInFolderRecursively(getDirectoryFromMainResources("common-services"), validator);
        StackManager.validateAllPropertyXmlsInFolderRecursively(getDirectoryFromMainResources("stacks"), validator);
    }
}

