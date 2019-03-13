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


import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ValidatorBeanCallTest extends ContextTestSupport {
    protected MockEndpoint validEndpoint;

    protected MockEndpoint invalidEndpoint;

    @Test
    public void testCallBean() throws Exception {
        validEndpoint.expectedMessageCount(1);
        invalidEndpoint.expectedMessageCount(0);
        template.sendBody("direct:rootPath", ("<report xmlns='http://foo.com/report' xmlns:rb='http://foo.com/report-base'><author><rb:name>Knuth</rb:name></author><content><rb:chapter><rb:subject></rb:subject>" + "<rb:abstract></rb:abstract><rb:body></rb:body></rb:chapter></content></report>"));
        MockEndpoint.assertIsSatisfied(validEndpoint, invalidEndpoint);
    }

    public static class MyValidatorBean {
        public InputStream loadFile() throws Exception {
            return Files.newInputStream(Paths.get("src/test/resources/report.xsd"));
        }
    }
}

