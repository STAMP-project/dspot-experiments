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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit tests for {@link AntPathMatcherGenericFileFilter}.
 */
public class AntPathMatcherGenericFileFilterTest extends ContextTestSupport {
    @Test
    public void testInclude() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result1");
        mock.expectedBodiesReceivedInAnyOrder("Hello World");
        template.sendBodyAndHeader("file://target/data/files/ant-path-1/x/y/z", "Hello World", FILE_NAME, "report.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-1/x/y/z", "Hello World 2", FILE_NAME, "b.TXT");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExclude() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result2");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 2", "Hello World 3", "Hello World 4");
        template.sendBodyAndHeader("file://target/data/files/ant-path-2/x/y/z", "Hello World 1", FILE_NAME, "report.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-2/x/y/z", "Hello World 2", FILE_NAME, "report.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-2/x/y/z", "Hello World 3", FILE_NAME, "b.BAK");
        template.sendBodyAndHeader("file://target/data/files/ant-path-2/x/y/z", "Hello World 4", FILE_NAME, "b.TXT");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIncludesAndExcludes() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result3");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 2", "Hello World 4");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 1", FILE_NAME, "a.pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 2", FILE_NAME, "m.pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 3", FILE_NAME, "b.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 4", FILE_NAME, "m.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 5", FILE_NAME, "b.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 6", FILE_NAME, "m.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 7", FILE_NAME, "ay.PDF");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 8", FILE_NAME, "my.Pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 9", FILE_NAME, "by.TXT");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 10", FILE_NAME, "my.TxT");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 11", FILE_NAME, "by.BAK");
        template.sendBodyAndHeader("file://target/data/files/ant-path-3/x/y/z", "Hello World 12", FILE_NAME, "my.BaK");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIncludesAndExcludesAndFilter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result4");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 3");
        template.sendBodyAndHeader("file://target/data/files/ant-path-4/x/y/z", "Hello World 1", FILE_NAME, "a.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-4/x/y/z", "Hello World 2", FILE_NAME, "b.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-4/x/y/z", "Hello World 3", FILE_NAME, "c.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-4/x/y/z", "Hello World 4", FILE_NAME, "Cy.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIncludeAndAntFilterNotCaseSensitive() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result5");
        mock.expectedBodiesReceivedInAnyOrder("Hello World");
        template.sendBodyAndHeader("file://target/data/files/ant-path-5/x/y/z", "Hello World", FILE_NAME, "report.TXT");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExcludeAndAntFilterNotCaseSensitive() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result6");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 2", "Hello World 4");
        template.sendBodyAndHeader("file://target/data/files/ant-path-6/x/y/z", "Hello World 1", FILE_NAME, "report.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-6/x/y/z", "Hello World 2", FILE_NAME, "report.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-6/x/y/z", "Hello World 3", FILE_NAME, "b.BAK");
        template.sendBodyAndHeader("file://target/data/files/ant-path-6/x/y/z", "Hello World 4", FILE_NAME, "b.TXT");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIncludesAndExcludesAndAntFilterNotCaseSensitive() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result7");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 2", "Hello World 4", "Hello World 8", "Hello World 10");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 1", FILE_NAME, "a.pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 2", FILE_NAME, "m.pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 3", FILE_NAME, "b.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 4", FILE_NAME, "m.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 5", FILE_NAME, "b.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 6", FILE_NAME, "m.bak");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 7", FILE_NAME, "ay.PDF");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 8", FILE_NAME, "my.Pdf");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 9", FILE_NAME, "by.TXT");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 10", FILE_NAME, "my.TxT");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 11", FILE_NAME, "By.BAK");
        template.sendBodyAndHeader("file://target/data/files/ant-path-7/x/y/z", "Hello World 12", FILE_NAME, "My.BaK");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIncludesAndExcludesAndFilterAndAntFilterNotCaseSensitive() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result8");
        mock.expectedBodiesReceivedInAnyOrder("Hello World 3", "Hello World 4");
        template.sendBodyAndHeader("file://target/data/files/ant-path-8/x/y/z", "Hello World 1", FILE_NAME, "a.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-8/x/y/z", "Hello World 2", FILE_NAME, "b.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-8/x/y/z", "Hello World 3", FILE_NAME, "c.txt");
        template.sendBodyAndHeader("file://target/data/files/ant-path-8/x/y/z", "Hello World 4", FILE_NAME, "Cy.txt");
        assertMockEndpointsSatisfied();
    }
}

