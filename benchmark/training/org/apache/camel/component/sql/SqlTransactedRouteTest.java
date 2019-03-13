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


import SqlConstants.SQL_QUERY;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class SqlTransactedRouteTest extends CamelTestSupport {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbc;

    private String startEndpoint = "direct:start";

    private String sqlEndpoint = "sql:overriddenByTheHeader?dataSource=#testdb";

    @Test
    public void testCommit() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start").routeId("commit").transacted("required").to(sqlEndpoint).process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setHeader(SQL_QUERY, "insert into customer values('cust2','muellerc')");
                    }
                }).to(sqlEndpoint);
            }
        });
        Exchange exchange = template.send(startEndpoint, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(SQL_QUERY, "insert into customer values('cust1','cmueller')");
            }
        });
        assertFalse(exchange.isFailed());
        long count = jdbc.queryForObject("select count(*) from customer", Long.class);
        assertEquals(2, count);
        Map<String, Object> map = jdbc.queryForMap("select * from customer where id = 'cust1'");
        assertEquals(2, map.size());
        assertEquals("cust1", map.get("ID"));
        assertEquals("cmueller", map.get("NAME"));
        map = jdbc.queryForMap("select * from customer where id = 'cust2'");
        assertEquals(2, map.size());
        assertEquals("cust2", map.get("ID"));
        assertEquals("muellerc", map.get("NAME"));
    }

    @Test
    public void testRollbackAfterExceptionInSecondStatement() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start").routeId("rollback").transacted("required").to(sqlEndpoint).process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        // primary key violation
                        exchange.getIn().setHeader(SQL_QUERY, "insert into customer values('cust1','muellerc')");
                    }
                }).to(sqlEndpoint);
            }
        });
        Exchange exchange = template.send(startEndpoint, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(SQL_QUERY, "insert into customer values('cust1','cmueller')");
            }
        });
        assertTrue(exchange.isFailed());
        long count = jdbc.queryForObject("select count(*) from customer", Long.class);
        assertEquals(0, count);
    }

    @Test
    public void testRollbackAfterAnException() throws Exception {
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start").routeId("rollback2").transacted("required").to(sqlEndpoint).process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        throw new Exception("forced Exception");
                    }
                });
            }
        });
        Exchange exchange = template.send(startEndpoint, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(SQL_QUERY, "insert into customer values('cust1','cmueller')");
            }
        });
        assertTrue(exchange.isFailed());
        long count = jdbc.queryForObject("select count(*) from customer", Long.class);
        assertEquals(0, count);
    }
}

