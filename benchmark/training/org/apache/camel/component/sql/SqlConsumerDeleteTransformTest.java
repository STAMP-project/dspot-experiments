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
package org.apache.camel.component.sql;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


/**
 *
 */
public class SqlConsumerDeleteTransformTest extends CamelTestSupport {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    @Test
    public void testConsume() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("The project is Camel", "The project is AMQ", "The project is Linux");
        assertMockEndpointsSatisfied();
        // some servers may be a bit slow for this
        for (int i = 0; i < 5; i++) {
            // give it a little time to delete
            Thread.sleep(200);
            int rows = jdbcTemplate.queryForObject("select count(*) from projects", Integer.class);
            if (rows == 0) {
                break;
            }
        }
        assertEquals("Should have deleted all 3 rows", new Integer(0), jdbcTemplate.queryForObject("select count(*) from projects", Integer.class));
    }
}

