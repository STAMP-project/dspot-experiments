/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.benchmarks.driver;


import Control.ClientArgs;
import Control.ClientType.ASYNC_CLIENT;
import Control.ClientType.SYNC_CLIENT;
import Control.Mark;
import Control.RpcType.STREAMING;
import Control.RpcType.UNARY;
import Control.ServerArgs.Builder;
import Control.ServerType.ASYNC_GENERIC_SERVER;
import Control.ServerType.ASYNC_SERVER;
import WorkerServiceGrpc.WorkerServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.benchmarks.proto.Control;
import io.grpc.benchmarks.proto.Stats;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Basic tests for {@link io.grpc.benchmarks.driver.LoadWorker}
 */
@RunWith(JUnit4.class)
public class LoadWorkerTest {
    private static final int TIMEOUT = 5;

    private static final ClientArgs MARK = ClientArgs.newBuilder().setMark(Mark.newBuilder().setReset(true).build()).build();

    private LoadWorker worker;

    private ManagedChannel channel;

    private WorkerServiceStub workerServiceStub;

    private LinkedBlockingQueue<Stats.ClientStats> marksQueue;

    @Test
    public void runUnaryBlockingClosedLoop() throws Exception {
        Control.ServerArgs.Builder serverArgsBuilder = Control.ServerArgs.newBuilder();
        serverArgsBuilder.getSetupBuilder().setServerType(ASYNC_SERVER).setAsyncServerThreads(4).setPort(0).getPayloadConfigBuilder().getSimpleParamsBuilder().setRespSize(1000);
        int serverPort = startServer(serverArgsBuilder.build());
        Control.ClientArgs.Builder clientArgsBuilder = ClientArgs.newBuilder();
        String serverAddress = "localhost:" + serverPort;
        clientArgsBuilder.getSetupBuilder().setClientType(SYNC_CLIENT).setRpcType(UNARY).setClientChannels(2).setOutstandingRpcsPerChannel(2).addServerTargets(serverAddress);
        clientArgsBuilder.getSetupBuilder().getPayloadConfigBuilder().getSimpleParamsBuilder().setReqSize(1000).setRespSize(1000);
        clientArgsBuilder.getSetupBuilder().getHistogramParamsBuilder().setResolution(0.01).setMaxPossible(6.0E10);
        StreamObserver<Control.ClientArgs> clientObserver = startClient(clientArgsBuilder.build());
        assertWorkOccurred(clientObserver);
    }

    @Test
    public void runUnaryAsyncClosedLoop() throws Exception {
        Control.ServerArgs.Builder serverArgsBuilder = Control.ServerArgs.newBuilder();
        serverArgsBuilder.getSetupBuilder().setServerType(ASYNC_SERVER).setAsyncServerThreads(4).setPort(0).getPayloadConfigBuilder().getSimpleParamsBuilder().setRespSize(1000);
        int serverPort = startServer(serverArgsBuilder.build());
        Control.ClientArgs.Builder clientArgsBuilder = ClientArgs.newBuilder();
        String serverAddress = "localhost:" + serverPort;
        clientArgsBuilder.getSetupBuilder().setClientType(ASYNC_CLIENT).setClientChannels(2).setRpcType(UNARY).setOutstandingRpcsPerChannel(1).setAsyncClientThreads(4).addServerTargets(serverAddress);
        clientArgsBuilder.getSetupBuilder().getPayloadConfigBuilder().getSimpleParamsBuilder().setReqSize(1000).setRespSize(1000);
        clientArgsBuilder.getSetupBuilder().getHistogramParamsBuilder().setResolution(0.01).setMaxPossible(6.0E10);
        StreamObserver<Control.ClientArgs> clientObserver = startClient(clientArgsBuilder.build());
        assertWorkOccurred(clientObserver);
    }

    @Test
    public void runPingPongAsyncClosedLoop() throws Exception {
        Control.ServerArgs.Builder serverArgsBuilder = Control.ServerArgs.newBuilder();
        serverArgsBuilder.getSetupBuilder().setServerType(ASYNC_SERVER).setAsyncServerThreads(4).setPort(0).getPayloadConfigBuilder().getSimpleParamsBuilder().setRespSize(1000);
        int serverPort = startServer(serverArgsBuilder.build());
        Control.ClientArgs.Builder clientArgsBuilder = ClientArgs.newBuilder();
        String serverAddress = "localhost:" + serverPort;
        clientArgsBuilder.getSetupBuilder().setClientType(ASYNC_CLIENT).setClientChannels(2).setRpcType(STREAMING).setOutstandingRpcsPerChannel(1).setAsyncClientThreads(4).addServerTargets(serverAddress);
        clientArgsBuilder.getSetupBuilder().getPayloadConfigBuilder().getSimpleParamsBuilder().setReqSize(1000).setRespSize(1000);
        clientArgsBuilder.getSetupBuilder().getHistogramParamsBuilder().setResolution(0.01).setMaxPossible(6.0E10);
        StreamObserver<Control.ClientArgs> clientObserver = startClient(clientArgsBuilder.build());
        assertWorkOccurred(clientObserver);
    }

    @Test
    public void runGenericPingPongAsyncClosedLoop() throws Exception {
        Control.ServerArgs.Builder serverArgsBuilder = Control.ServerArgs.newBuilder();
        serverArgsBuilder.getSetupBuilder().setServerType(ASYNC_GENERIC_SERVER).setAsyncServerThreads(4).setPort(0).getPayloadConfigBuilder().getBytebufParamsBuilder().setReqSize(1000).setRespSize(1000);
        int serverPort = startServer(serverArgsBuilder.build());
        Control.ClientArgs.Builder clientArgsBuilder = ClientArgs.newBuilder();
        String serverAddress = "localhost:" + serverPort;
        clientArgsBuilder.getSetupBuilder().setClientType(ASYNC_CLIENT).setClientChannels(2).setRpcType(STREAMING).setOutstandingRpcsPerChannel(1).setAsyncClientThreads(4).addServerTargets(serverAddress);
        clientArgsBuilder.getSetupBuilder().getPayloadConfigBuilder().getBytebufParamsBuilder().setReqSize(1000).setRespSize(1000);
        clientArgsBuilder.getSetupBuilder().getHistogramParamsBuilder().setResolution(0.01).setMaxPossible(6.0E10);
        StreamObserver<Control.ClientArgs> clientObserver = startClient(clientArgsBuilder.build());
        assertWorkOccurred(clientObserver);
    }
}

