/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jsonvalidator;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.FileUtil;
import org.junit.Test;


public class FileValidatorRouteTest extends CamelTestSupport {
    protected MockEndpoint validEndpoint;

    protected MockEndpoint finallyEndpoint;

    protected MockEndpoint invalidEndpoint;

    @Test
    public void testValidMessage() throws Exception {
        validEndpoint.expectedMessageCount(1);
        invalidEndpoint.expectedMessageCount(0);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/validator", "{ \"name\": \"Joe Doe\", \"id\": 1, \"price\": 12.5 }", FILE_NAME, "valid.json");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
        assertTrue("Should be able to delete the file", FileUtil.deleteFile(new File("target/validator/valid.json")));
    }

    @Test
    public void testInvalidMessage() throws Exception {
        validEndpoint.expectedMessageCount(0);
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/validator", "{ \"name\": \"Joe Doe\", \"id\": \"AA1\", \"price\": 12.5 }", FILE_NAME, "invalid.json");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
        // should be able to delete the file
        assertTrue("Should be able to delete the file", FileUtil.deleteFile(new File("target/validator/invalid.json")));
    }
}

