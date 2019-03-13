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


import InfinispanConstants.CACHE_ENTRY_JOINING;
import InfinispanConstants.CACHE_ENTRY_LEAVING;
import InfinispanConstants.CACHE_NAME;
import InfinispanConstants.EVENT_DATA;
import InfinispanConstants.EVENT_TYPE;
import InfinispanConstants.KEY;
import org.apache.camel.component.infinispan.util.UserUtils;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.sampledomain.User;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.Test;


public class InfinispanContinuousQueryIT extends CamelTestSupport {
    private static final InfinispanQueryBuilder CONTINUOUS_QUERY_BUILDER = new InfinispanQueryBuilder() {
        @Override
        public Query build(QueryFactory queryFactory) {
            return queryFactory.from(User.class).having("name").like("CQ%").build();
        }
    };

    private static final InfinispanQueryBuilder CONTINUOUS_QUERY_BUILDER_NO_MATCH = new InfinispanQueryBuilder() {
        @Override
        public Query build(QueryFactory queryFactory) {
            return queryFactory.from(User.class).having("name").like("%TEST%").build();
        }
    };

    private static final InfinispanQueryBuilder CONTINUOUS_QUERY_BUILDER_ALL = new InfinispanQueryBuilder() {
        @Override
        public Query build(QueryFactory queryFactory) {
            return queryFactory.from(User.class).having("name").like("%Q0%").build();
        }
    };

    private RemoteCacheManager manager;

    private RemoteCache<Object, Object> cache;

    @Test
    public void continuousQuery() throws Exception {
        MockEndpoint continuousQueryBuilderNoMatch = getMockEndpoint("mock:continuousQueryNoMatch");
        continuousQueryBuilderNoMatch.expectedMessageCount(0);
        MockEndpoint continuousQueryBuilderAll = getMockEndpoint("mock:continuousQueryAll");
        continuousQueryBuilderAll.expectedMessageCount(((UserUtils.CQ_USERS.length) * 2));
        MockEndpoint continuousQuery = getMockEndpoint("mock:continuousQuery");
        continuousQuery.expectedMessageCount(4);
        for (int i = 0; i < 4; i++) {
            continuousQuery.message(i).outHeader(KEY).isEqualTo(UserUtils.createKey(UserUtils.CQ_USERS[(i % 2)]));
            continuousQuery.message(i).outHeader(CACHE_NAME).isEqualTo(cache.getName());
            if (i >= 2) {
                continuousQuery.message(i).outHeader(EVENT_TYPE).isEqualTo(CACHE_ENTRY_LEAVING);
                continuousQuery.message(i).outHeader(EVENT_DATA).isNull();
            } else {
                continuousQuery.message(i).outHeader(EVENT_TYPE).isEqualTo(CACHE_ENTRY_JOINING);
                continuousQuery.message(i).outHeader(EVENT_DATA).isNotNull();
                continuousQuery.message(i).outHeader(EVENT_DATA).isInstanceOf(User.class);
            }
        }
        for (final User user : UserUtils.CQ_USERS) {
            cache.put(UserUtils.createKey(user), user);
        }
        assertEquals(UserUtils.CQ_USERS.length, cache.size());
        for (final User user : UserUtils.CQ_USERS) {
            cache.remove(UserUtils.createKey(user));
        }
        assertTrue(cache.isEmpty());
        continuousQuery.assertIsSatisfied();
        continuousQueryBuilderNoMatch.assertIsSatisfied();
        continuousQueryBuilderAll.assertIsSatisfied();
    }
}

