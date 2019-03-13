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


import SqlConstants.SQL_UPDATE_COUNT;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class ElSqlProducerBatchTest extends CamelTestSupport {
    private EmbeddedDatabase db;

    @Test
    public void testBatchMode() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).header(SQL_UPDATE_COUNT).isEqualTo(1);
        Map<String, Object> batchParams = new HashMap<>();
        batchParams.put("id", "4");
        batchParams.put("license", "GNU");
        batchParams.put("project", "Batch");
        template.sendBody("direct:batch", batchParams);
        mock.assertIsSatisfied();
    }

    @Test
    public void testNonBatchMode() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).header(SQL_UPDATE_COUNT).isEqualTo(1);
        mock.message(0).header("id").isEqualTo("4");
        mock.message(0).header("license").isEqualTo("GNU");
        mock.message(0).header("project").isEqualTo("nonBatch");
        Map<String, Object> headers = new HashMap<>();
        headers.put("id", "4");
        headers.put("license", "GNU");
        headers.put("project", "nonBatch");
        template.sendBodyAndHeaders("direct:nonBatch", "", headers);
        mock.assertIsSatisfied();
    }
}

