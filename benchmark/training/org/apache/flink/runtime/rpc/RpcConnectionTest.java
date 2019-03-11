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
package org.apache.flink.runtime.rpc;


import AkkaOptions.ASK_TIMEOUT;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceConfiguration;
import org.apache.flink.runtime.rpc.exceptions.RpcConnectionException;
import org.apache.flink.runtime.taskexecutor.TaskExecutorGateway;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import scala.Option;
import scala.Tuple2;


/**
 * This test validates that the RPC service gives a good message when it cannot
 * connect to an RpcEndpoint.
 */
public class RpcConnectionTest extends TestLogger {
    @Test
    public void testConnectFailure() throws Exception {
        ActorSystem actorSystem = null;
        RpcService rpcService = null;
        try {
            actorSystem = AkkaUtils.createActorSystem(new Configuration(), Option.<Tuple2<String, Object>>apply(new Tuple2("localhost", 0)));
            // we start the RPC service with a very long timeout to ensure that the test
            // can only pass if the connection problem is not recognized merely via a timeout
            Configuration configuration = new Configuration();
            configuration.setString(ASK_TIMEOUT, "10000000 s");
            rpcService = new org.apache.flink.runtime.rpc.akka.AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.fromConfiguration(configuration));
            CompletableFuture<TaskExecutorGateway> future = rpcService.connect("foo.bar.com.test.invalid", TaskExecutorGateway.class);
            future.get(10000000, TimeUnit.SECONDS);
            Assert.fail("should never complete normally");
        } catch (TimeoutException e) {
            Assert.fail("should not fail with a generic timeout exception");
        } catch (ExecutionException e) {
            // that is what we want
            Assert.assertTrue(((e.getCause()) instanceof RpcConnectionException));
            Assert.assertTrue("wrong error message", e.getCause().getMessage().contains("foo.bar.com.test.invalid"));
        } catch (Throwable t) {
            Assert.fail(("wrong exception: " + t));
        } finally {
            final CompletableFuture<Void> rpcTerminationFuture;
            if (rpcService != null) {
                rpcTerminationFuture = rpcService.stopService();
            } else {
                rpcTerminationFuture = CompletableFuture.completedFuture(null);
            }
            final CompletableFuture<Terminated> actorSystemTerminationFuture;
            if (actorSystem != null) {
                actorSystemTerminationFuture = FutureUtils.toJava(actorSystem.terminate());
            } else {
                actorSystemTerminationFuture = CompletableFuture.completedFuture(null);
            }
            FutureUtils.waitForAll(Arrays.asList(rpcTerminationFuture, actorSystemTerminationFuture)).get();
        }
    }
}

