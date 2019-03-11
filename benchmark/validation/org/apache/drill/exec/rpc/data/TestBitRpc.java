/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.rpc.data;


import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.scanner.ClassPathScanner;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.exception.FragmentSetupException;
import org.apache.drill.exec.ops.FragmentContext;
import org.apache.drill.exec.ops.FragmentContextImpl;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.proto.ExecProtos.FragmentHandle;
import org.apache.drill.exec.proto.GeneralRPCProtos.Ack;
import org.apache.drill.exec.proto.UserBitShared.QueryId;
import org.apache.drill.exec.record.RawFragmentBatch;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.RpcOutcomeListener;
import org.apache.drill.exec.rpc.control.WorkEventBus;
import org.apache.drill.exec.server.BootStrapContext;
import org.apache.drill.exec.server.options.SystemOptionManager;
import org.apache.drill.exec.work.WorkManager.WorkerBee;
import org.apache.drill.exec.work.fragment.FragmentExecutor;
import org.apache.drill.exec.work.fragment.FragmentManager;
import org.apache.drill.shaded.guava.com.google.common.base.Stopwatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestBitRpc extends ExecTest {
    @Test
    public void testConnectionBackpressure() throws Exception {
        final WorkerBee bee = Mockito.mock(WorkerBee.class);
        final WorkEventBus workBus = Mockito.mock(WorkEventBus.class);
        final DrillConfig config1 = DrillConfig.create();
        final BootStrapContext c = new BootStrapContext(config1, SystemOptionManager.createDefaultOptionDefinitions(), ClassPathScanner.fromPrescan(config1));
        final FragmentContextImpl fcon = Mockito.mock(FragmentContextImpl.class);
        Mockito.when(fcon.getAllocator()).thenReturn(c.getAllocator());
        final FragmentManager fman = new TestBitRpc.MockFragmentManager(c);
        Mockito.when(workBus.getFragmentManager(ArgumentMatchers.any(FragmentHandle.class))).thenReturn(fman);
        int port = 1234;
        DataConnectionConfig config = new DataConnectionConfig(c.getAllocator(), c, new DataServerRequestHandler(workBus, bee));
        DataServer server = new DataServer(config);
        port = server.bind(port, true);
        DrillbitEndpoint ep = DrillbitEndpoint.newBuilder().setAddress("localhost").setDataPort(port).build();
        DataConnectionManager manager = new DataConnectionManager(ep, config);
        DataTunnel tunnel = new DataTunnel(manager);
        AtomicLong max = new AtomicLong(0);
        for (int i = 0; i < 40; i++) {
            long t1 = System.currentTimeMillis();
            tunnel.sendRecordBatch(new TestBitRpc.TimingOutcome(max), new org.apache.drill.exec.record.FragmentWritableBatch(false, QueryId.getDefaultInstance(), 1, 1, 1, 1, TestBitRpc.getRandomBatch(c.getAllocator(), 5000)));
        }
        Assert.assertTrue(((max.get()) > 2700));
        Thread.sleep(5000);
    }

    private class TimingOutcome implements RpcOutcomeListener<Ack> {
        private AtomicLong max;

        private Stopwatch watch = Stopwatch.createStarted();

        public TimingOutcome(AtomicLong max) {
            super();
            this.max = max;
        }

        @Override
        public void failed(RpcException ex) {
            ex.printStackTrace();
        }

        @Override
        public void success(Ack value, ByteBuf buffer) {
            long micros = watch.elapsed(TimeUnit.MILLISECONDS);
            while (true) {
                long nowMax = max.get();
                if (nowMax < micros) {
                    if (max.compareAndSet(nowMax, micros)) {
                        break;
                    }
                } else {
                    break;
                }
            } 
        }

        @Override
        public void interrupted(final InterruptedException e) {
            // TODO(We don't have any interrupts in test code)
        }
    }

    public static class MockFragmentManager implements FragmentManager {
        private final BootStrapContext c;

        private int v;

        public MockFragmentManager(BootStrapContext c) {
            this.c = c;
        }

        @Override
        public boolean handle(IncomingDataBatch batch) throws IOException, FragmentSetupException {
            try {
                (v)++;
                if (((v) % 10) == 0) {
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
            }
            RawFragmentBatch rfb = batch.newRawFragmentBatch(c.getAllocator());
            rfb.sendOk();
            rfb.release();
            return true;
        }

        @Override
        public FragmentExecutor getRunnable() {
            return null;
        }

        @Override
        public void cancel() {
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public void unpause() {
        }

        @Override
        public boolean isWaiting() {
            return false;
        }

        @Override
        public FragmentHandle getHandle() {
            return null;
        }

        @Override
        public FragmentContext getFragmentContext() {
            return null;
        }

        @Override
        public void receivingFragmentFinished(FragmentHandle handle) {
        }
    }
}

