/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.metadata;


import DataTypes.STRING;
import RowGranularity.CLUSTER;
import com.google.common.collect.ImmutableList;
import io.crate.expression.reference.partitioned.PartitionExpression;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PartitionReferenceResolverTest extends CrateUnitTest {
    @Test
    public void testClusterExpressionsNotAllowed() throws Exception {
        Reference refInfo = TestingHelpers.refInfo("foo.bar", STRING, CLUSTER);
        PartitionReferenceResolver referenceResolver = new PartitionReferenceResolver(ImmutableList.<PartitionExpression>of());
        if (PartitionReferenceResolverTest.assertionsEnabled()) {
            try {
                referenceResolver.getImplementation(refInfo);
                fail("no assertion error thrown");
            } catch (AssertionError e) {
                assertThat(e.getMessage(), Matchers.containsString("granularity < PARTITION should have been resolved already"));
            }
        } else {
            referenceResolver.getImplementation(refInfo);
        }
    }
}

