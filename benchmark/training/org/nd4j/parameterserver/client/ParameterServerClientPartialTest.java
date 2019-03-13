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
package org.nd4j.parameterserver.client;


import Aeron.Context;
import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.ParameterServerListener;
import org.nd4j.parameterserver.ParameterServerSubscriber;


/**
 * Created by agibsonccc on 10/3/16.
 */
@Slf4j
public class ParameterServerClientPartialTest {
    private static MediaDriver mediaDriver;

    private static Context ctx;

    private static ParameterServerSubscriber masterNode;

    private static ParameterServerSubscriber slaveNode;

    private int[] shape = new int[]{ 2, 2 };

    private static Aeron aeron;

    @Test(timeout = 60000L)
    public void testServer() throws Exception {
        ParameterServerClient client = ParameterServerClient.builder().aeron(ParameterServerClientPartialTest.aeron).ndarrayRetrieveUrl(ParameterServerClientPartialTest.masterNode.getResponder().connectionUrl()).ndarraySendUrl(ParameterServerClientPartialTest.slaveNode.getSubscriber().connectionUrl()).subscriberHost("localhost").subscriberPort(40325).subscriberStream(12).build();
        Assert.assertEquals("localhost:40325:12", client.connectionUrl());
        // flow 1:
        /**
         * Client (40125:12): sends array to listener on slave(40126:10)
         * which publishes to master (40123:11)
         * which adds the array for parameter averaging.
         * In this case totalN should be 1.
         */
        client.pushNDArrayMessage(NDArrayMessage.of(Nd4j.ones(2), new int[]{ 0 }, 0));
        log.info("Pushed ndarray");
        Thread.sleep(30000);
        ParameterServerListener listener = ((ParameterServerListener) (ParameterServerClientPartialTest.masterNode.getCallback()));
        Assert.assertEquals(1, listener.getUpdater().numUpdates());
        INDArray assertion = Nd4j.create(new int[]{ 2, 2 });
        assertion.getColumn(0).addi(1.0);
        Assert.assertEquals(assertion, listener.getUpdater().ndArrayHolder().get());
        INDArray arr = client.getArray();
        Assert.assertEquals(assertion, arr);
    }
}

