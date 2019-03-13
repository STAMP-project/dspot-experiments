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


import DrillProperties.KEYTAB;
import DrillProperties.USER;
import ExecConstants.AUTHENTICATION_MECHANISMS;
import ExecConstants.BIT_AUTHENTICATION_ENABLED;
import ExecConstants.BIT_AUTHENTICATION_MECHANISM;
import ExecConstants.BIT_ENCRYPTION_SASL_ENABLED;
import ExecConstants.BIT_ENCRYPTION_SASL_MAX_WRAPPED_SIZE;
import ExecConstants.SERVICE_KEYTAB_LOCATION;
import ExecConstants.SERVICE_PRINCIPAL;
import ExecConstants.USER_AUTHENTICATION_ENABLED;
import ExecConstants.USER_AUTHENTICATOR_IMPL;
import ExecConstants.USE_LOGIN_PRINCIPAL;
import UserBitShared.DrillPBError.ErrorType;
import com.typesafe.config.ConfigValueFactory;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.common.scanner.ClassPathScanner;
import org.apache.drill.common.scanner.persistence.ScanResult;
import org.apache.drill.exec.exception.DrillbitStartupException;
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
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.exec.server.BootStrapContext;
import org.apache.drill.exec.server.options.SystemOptionManager;
import org.apache.drill.exec.work.WorkManager.WorkerBee;
import org.apache.drill.exec.work.fragment.FragmentExecutor;
import org.apache.drill.exec.work.fragment.FragmentManager;
import org.apache.drill.shaded.guava.com.google.common.base.Preconditions;
import org.apache.drill.shaded.guava.com.google.common.base.Stopwatch;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestBitBitKerberos extends BaseTestQuery {
    private static KerberosHelper krbHelper;

    private static DrillConfig newConfig;

    private int port = 1234;

    private class TimingOutcome implements RpcOutcomeListener<Ack> {
        private AtomicLong max;

        private Stopwatch watch = Stopwatch.createStarted();

        TimingOutcome(AtomicLong max) {
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

    @Test
    public void success() throws Exception {
        final WorkerBee bee = Mockito.mock(WorkerBee.class);
        final WorkEventBus workBus = Mockito.mock(WorkEventBus.class);
        TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("kerberos"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())));
        final ScanResult result = ClassPathScanner.fromPrescan(TestBitBitKerberos.newConfig);
        final BootStrapContext c1 = new BootStrapContext(TestBitBitKerberos.newConfig, SystemOptionManager.createDefaultOptionDefinitions(), result);
        final FragmentManager manager = setupFragmentContextAndManager(c1.getAllocator());
        Mockito.when(workBus.getFragmentManager(Mockito.<FragmentHandle>any())).thenReturn(manager);
        DataConnectionConfig config = new DataConnectionConfig(c1.getAllocator(), c1, new DataServerRequestHandler(workBus, bee));
        DataServer server = new DataServer(config);
        port = server.bind(port, true);
        DrillbitEndpoint ep = DrillbitEndpoint.newBuilder().setAddress("localhost").setDataPort(port).build();
        DataConnectionManager connectionManager = new DataConnectionManager(ep, config);
        DataTunnel tunnel = new DataTunnel(connectionManager);
        AtomicLong max = new AtomicLong(0);
        try {
            for (int i = 0; i < 40; i++) {
                long t1 = System.currentTimeMillis();
                tunnel.sendRecordBatch(new TestBitBitKerberos.TimingOutcome(max), new org.apache.drill.exec.record.FragmentWritableBatch(false, QueryId.getDefaultInstance(), 1, 1, 1, 1, TestBitBitKerberos.getRandomBatch(c1.getAllocator(), 5000)));
            }
            Assert.assertTrue(((max.get()) > 2700));
            Thread.sleep(5000);
        } catch (Exception | AssertionError e) {
            TestCase.fail();
        } finally {
            server.close();
            connectionManager.close();
            c1.close();
        }
    }

    @Test
    public void successEncryption() throws Exception {
        final WorkerBee bee = Mockito.mock(WorkerBee.class);
        final WorkEventBus workBus = Mockito.mock(WorkEventBus.class);
        TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("kerberos"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())));
        final ScanResult result = ClassPathScanner.fromPrescan(TestBitBitKerberos.newConfig);
        final BootStrapContext c2 = new BootStrapContext(TestBitBitKerberos.newConfig, SystemOptionManager.createDefaultOptionDefinitions(), result);
        final FragmentManager manager = setupFragmentContextAndManager(c2.getAllocator());
        Mockito.when(workBus.getFragmentManager(Mockito.<FragmentHandle>any())).thenReturn(manager);
        final DataConnectionConfig config = new DataConnectionConfig(c2.getAllocator(), c2, new DataServerRequestHandler(workBus, bee));
        final DataServer server = new DataServer(config);
        port = server.bind(port, true);
        DrillbitEndpoint ep = DrillbitEndpoint.newBuilder().setAddress("localhost").setDataPort(port).build();
        final DataConnectionManager connectionManager = new DataConnectionManager(ep, config);
        final DataTunnel tunnel = new DataTunnel(connectionManager);
        AtomicLong max = new AtomicLong(0);
        try {
            for (int i = 0; i < 40; i++) {
                long t1 = System.currentTimeMillis();
                tunnel.sendRecordBatch(new TestBitBitKerberos.TimingOutcome(max), new org.apache.drill.exec.record.FragmentWritableBatch(false, QueryId.getDefaultInstance(), 1, 1, 1, 1, TestBitBitKerberos.getRandomBatch(c2.getAllocator(), 5000)));
            }
            Assert.assertTrue(((max.get()) > 2700));
            Thread.sleep(5000);
        } finally {
            server.close();
            connectionManager.close();
            c2.close();
        }
    }

    @Test
    public void successEncryptionChunkMode() throws Exception {
        final WorkerBee bee = Mockito.mock(WorkerBee.class);
        final WorkEventBus workBus = Mockito.mock(WorkEventBus.class);
        TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("kerberos"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_ENCRYPTION_SASL_MAX_WRAPPED_SIZE, ConfigValueFactory.fromAnyRef(100000)).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())));
        final ScanResult result = ClassPathScanner.fromPrescan(TestBitBitKerberos.newConfig);
        final BootStrapContext c2 = new BootStrapContext(TestBitBitKerberos.newConfig, SystemOptionManager.createDefaultOptionDefinitions(), result);
        final FragmentManager manager = setupFragmentContextAndManager(c2.getAllocator());
        Mockito.when(workBus.getFragmentManager(Mockito.<FragmentHandle>any())).thenReturn(manager);
        final DataConnectionConfig config = new DataConnectionConfig(c2.getAllocator(), c2, new DataServerRequestHandler(workBus, bee));
        final DataServer server = new DataServer(config);
        port = server.bind(port, true);
        final DrillbitEndpoint ep = DrillbitEndpoint.newBuilder().setAddress("localhost").setDataPort(port).build();
        final DataConnectionManager connectionManager = new DataConnectionManager(ep, config);
        final DataTunnel tunnel = new DataTunnel(connectionManager);
        AtomicLong max = new AtomicLong(0);
        try {
            for (int i = 0; i < 40; i++) {
                long t1 = System.currentTimeMillis();
                tunnel.sendRecordBatch(new TestBitBitKerberos.TimingOutcome(max), new org.apache.drill.exec.record.FragmentWritableBatch(false, QueryId.getDefaultInstance(), 1, 1, 1, 1, TestBitBitKerberos.getRandomBatch(c2.getAllocator(), 5000)));
            }
            Assert.assertTrue(((max.get()) > 2700));
            Thread.sleep(5000);
        } catch (Exception | AssertionError ex) {
            TestCase.fail();
        } finally {
            server.close();
            connectionManager.close();
            c2.close();
        }
    }

    @Test
    public void failureEncryptionOnlyPlainMechanism() throws Exception {
        try {
            TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(BIT_ENCRYPTION_SASL_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(true)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())));
            BaseTestQuery.updateTestCluster(1, TestBitBitKerberos.newConfig);
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue(((ex.getCause()) instanceof DrillbitStartupException));
        }
    }

    /**
     * Test to validate that a query which is running only on local Foreman node runs fine even if the Bit-Bit
     * Auth config is wrong. With DRILL-5721, all the local fragment setup and status update
     * doesn't happen over Control tunnel but instead happens locally. Without the fix in DRILL-5721 these queries will
     * hang.
     *
     * This test only starts up 1 Drillbit so that all fragments are scheduled on Foreman Drillbit node
     *
     * @throws Exception
     * 		
     */
    @Test
    public void localQuerySuccessWithWrongBitAuthConfig() throws Exception {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty(DrillProperties.SERVICE_PRINCIPAL, TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL);
        connectionProps.setProperty(USER, TestBitBitKerberos.krbHelper.CLIENT_PRINCIPAL);
        connectionProps.setProperty(KEYTAB, TestBitBitKerberos.krbHelper.clientKeytab.getAbsolutePath());
        TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(false)));
        BaseTestQuery.updateTestCluster(1, TestBitBitKerberos.newConfig, connectionProps);
        // Run a query using the new client
        final String query = BaseTestQuery.getFile("queries/tpch/01.sql");
        BaseTestQuery.test(query);
    }

    /**
     * Test to validate that query setup fails while scheduling remote fragments when multiple Drillbits are running with
     * wrong Bit-to-Bit Authentication configuration.
     *
     * This test starts up 2 Drillbit so that there are combination of local and remote fragments for query
     * execution. Note: When test runs with wrong config then for control connection Drillbit's uses wrong
     * service principal to talk to another Drillbit, and due to this Kerby server also fails with NullPointerException.
     * But for unit testing this should be fine.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void queryFailureWithWrongBitAuthConfig() throws Exception {
        try {
            final Properties connectionProps = new Properties();
            connectionProps.setProperty(DrillProperties.SERVICE_PRINCIPAL, TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL);
            connectionProps.setProperty(USER, TestBitBitKerberos.krbHelper.CLIENT_PRINCIPAL);
            connectionProps.setProperty(KEYTAB, TestBitBitKerberos.krbHelper.clientKeytab.getAbsolutePath());
            TestBitBitKerberos.newConfig = new DrillConfig(DrillConfig.create(BaseTestQuery.cloneDefaultTestConfigProperties()).withValue(USER_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(USER_AUTHENTICATOR_IMPL, ConfigValueFactory.fromAnyRef(UserAuthenticatorTestImpl.TYPE)).withValue(SERVICE_PRINCIPAL, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.SERVER_PRINCIPAL)).withValue(SERVICE_KEYTAB_LOCATION, ConfigValueFactory.fromAnyRef(TestBitBitKerberos.krbHelper.serverKeytab.toString())).withValue(AUTHENTICATION_MECHANISMS, ConfigValueFactory.fromIterable(Lists.newArrayList("plain", "kerberos"))).withValue(BIT_AUTHENTICATION_ENABLED, ConfigValueFactory.fromAnyRef(true)).withValue(BIT_AUTHENTICATION_MECHANISM, ConfigValueFactory.fromAnyRef("kerberos")).withValue(USE_LOGIN_PRINCIPAL, ConfigValueFactory.fromAnyRef(false)));
            BaseTestQuery.updateTestCluster(2, TestBitBitKerberos.newConfig, connectionProps);
            BaseTestQuery.test("alter session set `planner.slice_target` = 10");
            final String query = BaseTestQuery.getFile("queries/tpch/01.sql");
            BaseTestQuery.test(query);
            TestCase.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof UserRemoteException));
            Assert.assertTrue(((getErrorType()) == (ErrorType.CONNECTION)));
        }
    }

    public static class MockFragmentManager implements FragmentManager {
        private int v = 0;

        private final FragmentContextImpl fragmentContext;

        public MockFragmentManager(final FragmentContextImpl fragmentContext) {
            this.fragmentContext = Preconditions.checkNotNull(fragmentContext);
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
            RawFragmentBatch rfb = batch.newRawFragmentBatch(fragmentContext.getAllocator());
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
            return fragmentContext;
        }

        @Override
        public void receivingFragmentFinished(FragmentHandle handle) {
        }
    }
}

