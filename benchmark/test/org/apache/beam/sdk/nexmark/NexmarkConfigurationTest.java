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
package org.apache.beam.sdk.nexmark;


import NexmarkQueryName.HIGHEST_BID;
import NexmarkQueryName.LOCAL_ITEM_SUGGESTION;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static NexmarkConfiguration.DEFAULT;


/**
 * Test of {@link NexmarkConfiguration} conversion from {@link NexmarkOptions}.
 */
public class NexmarkConfigurationTest {
    @Test
    public void testNumericQueryParsing() {
        NexmarkConfiguration configuration = DEFAULT;
        configuration.overrideFromOptions(PipelineOptionsFactory.fromArgs("--query=3").as(NexmarkOptions.class));
        Assert.assertThat(configuration.query, Matchers.equalTo(LOCAL_ITEM_SUGGESTION));
    }

    @Test
    public void testNamedQueryParsing() {
        NexmarkConfiguration configuration = DEFAULT;
        configuration.overrideFromOptions(PipelineOptionsFactory.fromArgs("--query=HIGHEST_BID").as(NexmarkOptions.class));
        Assert.assertThat(configuration.query, Matchers.equalTo(HIGHEST_BID));
    }
}

