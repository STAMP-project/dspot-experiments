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
package org.apache.camel.component.validator;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class FileValidatorRouteTest extends ContextTestSupport {
    protected MockEndpoint validEndpoint;

    protected MockEndpoint finallyEndpoint;

    protected MockEndpoint invalidEndpoint;

    @Test
    public void testValidMessage() throws Exception {
        validEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/validator", "<mail xmlns='http://foo.com/bar'><subject>Hey</subject><body>Hello world!</body></mail>", FILE_NAME, "valid.xml");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
        // should be able to delete the file
        oneExchangeDone.matchesMockWaitTime();
        Assert.assertTrue("Should be able to delete the file", FileUtil.deleteFile(new File("target/data/validator/valid.xml")));
    }

    @Test
    public void testInvalidMessage() throws Exception {
        invalidEndpoint.expectedMessageCount(1);
        finallyEndpoint.expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/validator", "<mail xmlns='http://foo.com/bar'><body>Hello world!</body></mail>", FILE_NAME, "invalid.xml");
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint, finallyEndpoint);
        // should be able to delete the file
        oneExchangeDone.matchesMockWaitTime();
        Assert.assertTrue("Should be able to delete the file", FileUtil.deleteFile(new File("target/data/validator/invalid.xml")));
    }
}

