/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.aggregation;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MetricManipulatorFnsTest {
    private static final String NAME = "name";

    private static final String FIELD = "field";

    private final AggregatorFactory aggregatorFactory;

    private final Object agg;

    private final Object identity;

    private final Object finalize;

    private final Object serialForm;

    private final Object deserForm;

    public MetricManipulatorFnsTest(AggregatorFactory aggregatorFactory, Object agg, Object identity, Object finalize, Object serialForm, Object deserForm) {
        this.aggregatorFactory = aggregatorFactory;
        this.agg = agg;
        this.identity = identity;
        this.finalize = finalize;
        this.serialForm = serialForm;
        this.deserForm = deserForm;
    }

    @Test
    public void testIdentity() {
        Assert.assertEquals(identity, agg);
        Assert.assertEquals(identity, MetricManipulatorFns.identity().manipulate(aggregatorFactory, agg));
    }

    @Test
    public void testFinalize() {
        Assert.assertEquals(identity, agg);
        Assert.assertEquals(finalize, MetricManipulatorFns.finalizing().manipulate(aggregatorFactory, agg));
    }

    @Test
    public void testDeserialize() {
        Assert.assertEquals(identity, agg);
        Assert.assertEquals(deserForm, MetricManipulatorFns.deserializing().manipulate(aggregatorFactory, serialForm));
    }
}

