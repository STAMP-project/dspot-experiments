/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rest.messages.job.metrics;


import java.util.Collections;
import org.apache.flink.runtime.rest.messages.RestResponseMarshallingTestBase;
import org.apache.flink.runtime.rest.util.RestMapperUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Tests for {@link MetricCollectionResponseBody}.
 */
public class MetricCollectionResponseBodyTest extends RestResponseMarshallingTestBase<MetricCollectionResponseBody> {
    private static final String TEST_METRIC_NAME = "metric1";

    private static final String TEST_METRIC_VALUE = "1000";

    @Test
    public void testNullValueNotSerialized() throws Exception {
        final String json = RestMapperUtils.getStrictObjectMapper().writeValueAsString(new MetricCollectionResponseBody(Collections.singleton(new Metric(MetricCollectionResponseBodyTest.TEST_METRIC_NAME))));
        MatcherAssert.assertThat(json, Matchers.not(Matchers.containsString("\"value\"")));
        MatcherAssert.assertThat(json, Matchers.not(Matchers.containsString("\"metrics\"")));
    }
}

