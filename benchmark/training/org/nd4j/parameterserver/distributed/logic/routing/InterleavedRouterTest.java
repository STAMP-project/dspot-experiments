/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.parameterserver.distributed.logic.routing;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.messages.VoidMessage;
import org.nd4j.parameterserver.distributed.messages.requests.InitializationRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;
import org.nd4j.parameterserver.distributed.transport.Transport;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
@Deprecated
public class InterleavedRouterTest {
    VoidConfiguration configuration;

    Transport transport;

    long originator;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    /**
     * Testing default assignment for everything, but training requests
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget1() throws Exception {
        InterleavedRouter router = new InterleavedRouter();
        router.init(configuration, transport);
        for (int i = 0; i < 100; i++) {
            VoidMessage message = new InitializationRequestMessage(100, 10, 123, false, false, 10);
            int target = router.assignTarget(message);
            Assert.assertTrue(((target >= 0) && (target <= 3)));
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }

    /**
     * Testing assignment for training message
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget2() throws Exception {
        InterleavedRouter router = new InterleavedRouter();
        router.init(configuration, transport);
        int[] w1 = new int[]{ 512, 345, 486, 212 };
        for (int i = 0; i < (w1.length); i++) {
            SkipGramRequestMessage message = new SkipGramRequestMessage(w1[i], 1, new int[]{ 1, 2, 3 }, new byte[]{ 0, 0, 1 }, ((short) (0)), 0.02, 119);
            int target = router.assignTarget(message);
            Assert.assertEquals(((w1[i]) % (configuration.getNumberOfShards())), target);
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }

    /**
     * Testing default assignment for everything, but training requests.
     * Difference here is pre-defined default index, for everything but TrainingMessages
     *
     * @throws Exception
     * 		
     */
    @Test
    public void assignTarget3() throws Exception {
        InterleavedRouter router = new InterleavedRouter(2);
        router.init(configuration, transport);
        for (int i = 0; i < 3; i++) {
            VoidMessage message = new InitializationRequestMessage(100, 10, 123, false, false, 10);
            int target = router.assignTarget(message);
            Assert.assertEquals(2, target);
            Assert.assertEquals(originator, message.getOriginatorId());
        }
    }
}

