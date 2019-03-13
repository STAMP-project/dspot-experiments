/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.predicates;


import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.impl.SkipIndexPredicate;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class SqlPredicateSkipIndexTest extends HazelcastTestSupport {
    @Test
    public void testInPredicate() {
        SqlPredicate sqlPredicate = new SqlPredicate("%age in (1)");
        Predicate p = sqlPredicate.getPredicate();
        SkipIndexPredicate skipIndexPredicate = ((SkipIndexPredicate) (p));
        InPredicate inPredicate = ((InPredicate) (skipIndexPredicate.getTarget()));
        Assert.assertEquals("age", inPredicate.attributeName, "age");
        Assert.assertArrayEquals(new String[]{ "1" }, inPredicate.values);
    }

    @Test
    public void testEqualPredicate() {
        SqlPredicate sqlPredicate = new SqlPredicate("%age=40");
        Predicate p = sqlPredicate.getPredicate();
        SkipIndexPredicate skipIndexPredicate = ((SkipIndexPredicate) (p));
        EqualPredicate equalPredicate = ((EqualPredicate) (skipIndexPredicate.getTarget()));
        Assert.assertEquals("age", equalPredicate.attributeName, "age");
        Assert.assertEquals("40", equalPredicate.value);
    }

    @Test
    public void testNotEqualNotPredicate() {
        notEqualPredicate("<>");
        notEqualPredicate("!=");
    }

    @Test
    public void testGreaterLess() {
        greaterLess(">");
        greaterLess(">=");
        greaterLess("<");
        greaterLess("<=");
    }
}

