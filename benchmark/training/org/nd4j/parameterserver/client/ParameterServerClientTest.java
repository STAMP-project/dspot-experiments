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


import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.ParameterServerListener;
import org.nd4j.parameterserver.ParameterServerSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by agibsonccc on 10/3/16.
 */
public class ParameterServerClientTest {
    private static MediaDriver mediaDriver;

    private static Logger log = LoggerFactory.getLogger(ParameterServerClientTest.class);

    private static Aeron aeron;

    private static ParameterServerSubscriber masterNode;

    private static ParameterServerSubscriber slaveNode;

    private static int parameterLength = 1000;

    @Test(timeout = 60000L)
    public void testServer() throws Exception {
        int subscriberPort = 40625 + (new Random().nextInt(100));
        ParameterServerClient client = ParameterServerClient.builder().aeron(ParameterServerClientTest.aeron).ndarrayRetrieveUrl(ParameterServerClientTest.masterNode.getResponder().connectionUrl()).ndarraySendUrl(ParameterServerClientTest.slaveNode.getSubscriber().connectionUrl()).subscriberHost("localhost").subscriberPort(subscriberPort).subscriberStream(12).build();
        Assert.assertEquals(String.format("localhost:%d:12", subscriberPort), client.connectionUrl());
        // flow 1:
        /**
         * Client (40125:12): sends array to listener on slave(40126:10)
         * which publishes to master (40123:11)
         * which adds the array for parameter averaging.
         * In this case totalN should be 1.
         */
        client.pushNDArray(Nd4j.ones(ParameterServerClientTest.parameterLength));
        ParameterServerClientTest.log.info("Pushed ndarray");
        Thread.sleep(30000);
        ParameterServerListener listener = ((ParameterServerListener) (ParameterServerClientTest.masterNode.getCallback()));
        Assert.assertEquals(1, listener.getUpdater().numUpdates());
        Assert.assertEquals(Nd4j.ones(ParameterServerClientTest.parameterLength), listener.getUpdater().ndArrayHolder().get());
        INDArray arr = client.getArray();
        Assert.assertEquals(Nd4j.ones(1000), arr);
    }
}

