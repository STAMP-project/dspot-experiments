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
package org.apache.camel.component.elsql;


import java.util.Iterator;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class ElSqlProducerStreamListTest extends CamelTestSupport {
    private EmbeddedDatabase db;

    @Test
    public void testReturnAnIterator() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "testmsg");
        mock.assertIsSatisfied();
        assertThat(resultBodyAt(mock, 0), CoreMatchers.instanceOf(Iterator.class));
    }

    @Test
    public void testSplit() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);
        template.sendBody("direct:withSplit", "testmsg");
        mock.assertIsSatisfied();
        assertThat(resultBodyAt(mock, 0), CoreMatchers.instanceOf(Map.class));
        assertThat(resultBodyAt(mock, 1), CoreMatchers.instanceOf(Map.class));
        assertThat(resultBodyAt(mock, 2), CoreMatchers.instanceOf(Map.class));
    }

    @Test
    public void testSplitWithModel() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);
        template.sendBody("direct:withSplitModel", "testmsg");
        mock.assertIsSatisfied();
        assertThat(resultBodyAt(mock, 0), CoreMatchers.instanceOf(Project.class));
        assertThat(resultBodyAt(mock, 1), CoreMatchers.instanceOf(Project.class));
        assertThat(resultBodyAt(mock, 2), CoreMatchers.instanceOf(Project.class));
    }
}

