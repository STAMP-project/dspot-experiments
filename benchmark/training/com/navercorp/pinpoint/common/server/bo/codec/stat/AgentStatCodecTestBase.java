/**
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.bo.codec.stat;


import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public abstract class AgentStatCodecTestBase<T extends AgentStatDataPoint> {
    private static final String AGENT_ID = "testAgentId";

    private static final long AGENT_START_TIMESTAMP = System.currentTimeMillis();

    private static final int NUM_TEST_RUNS = 20;

    @Test
    public void should_be_encoded_and_decoded_to_same_value() {
        for (int i = 0; i < (AgentStatCodecTestBase.NUM_TEST_RUNS); i++) {
            runTest();
        }
    }
}

