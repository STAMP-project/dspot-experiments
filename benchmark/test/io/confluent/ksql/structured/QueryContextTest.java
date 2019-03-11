/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.structured;


import QueryContext.Stacker;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.query.QueryId;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryContextTest {
    private final QueryId queryId = new QueryId("query");

    private final Stacker contextStacker = push("node");

    private final QueryContext queryContext = contextStacker.getQueryContext();

    @Test
    public void shouldGenerateNewContextOnPush() {
        // When:
        final QueryContext.Stacker childContextStacker = contextStacker.push("child");
        final QueryContext childContext = childContextStacker.getQueryContext();
        final QueryContext grandchildContext = childContextStacker.push("grandchild").getQueryContext();
        // Then:
        Assert.assertThat(ImmutableSet.of(queryContext, childContext, grandchildContext), Matchers.hasSize(3));
        QueryContextTest.assertQueryContext(queryContext, queryId, "node");
        QueryContextTest.assertQueryContext(childContext, queryId, "node", "child");
        QueryContextTest.assertQueryContext(grandchildContext, queryId, "node", "child", "grandchild");
    }
}

