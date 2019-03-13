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
package org.apache.camel.component.infinispan;


import InfinispanOperation.QUERY;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.sampledomain.User;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.Test;


public class InfinispanRemoteQueryProducerIT extends CamelTestSupport {
    private static final InfinispanQueryBuilder NO_RESULT_QUERY_BUILDER = new InfinispanQueryBuilder() {
        @Override
        public Query build(QueryFactory queryFactory) {
            return queryFactory.from(User.class).having("name").like("%abc%").toBuilder().build();
        }
    };

    private static final InfinispanQueryBuilder WITH_RESULT_QUERY_BUILDER = new InfinispanQueryBuilder() {
        @Override
        public Query build(QueryFactory queryFactory) {
            return queryFactory.from(User.class).having("name").like("%A").toBuilder().build();
        }
    };

    private RemoteCacheManager manager;

    @Test
    public void producerQueryOperationWithoutQueryBuilder() throws Exception {
        Exchange request = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(InfinispanConstants.OPERATION, QUERY);
            }
        });
        assertNull(request.getException());
        List<User> queryResult = request.getIn().getBody(List.class);
        assertNull(queryResult);
    }

    @Test
    public void producerQueryWithoutResult() throws Exception {
        producerQueryWithoutResult("direct:start", InfinispanRemoteQueryProducerIT.NO_RESULT_QUERY_BUILDER);
    }

    @Test
    public void producerQueryWithoutResultAndQueryBuilderFromConfig() throws Exception {
        producerQueryWithoutResult("direct:noQueryResults", null);
    }

    @Test
    public void producerQueryWithResult() throws Exception {
        producerQueryWithResult("direct:start", InfinispanRemoteQueryProducerIT.WITH_RESULT_QUERY_BUILDER);
    }

    @Test
    public void producerQueryWithResultAndQueryBuilderFromConfig() throws Exception {
        producerQueryWithResult("direct:queryWithResults", null);
    }
}

