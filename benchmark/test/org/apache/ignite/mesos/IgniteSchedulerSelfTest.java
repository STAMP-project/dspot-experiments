/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.mesos;


import IgniteScheduler.CPU;
import IgniteScheduler.MEM;
import Protos.FrameworkInfo;
import Protos.Offer;
import Protos.OfferID;
import Protos.Status;
import Protos.TaskInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.junit.Assert;
import org.junit.Test;


/**
 * Scheduler tests.
 */
public class IgniteSchedulerSelfTest {
    /**
     *
     */
    private IgniteScheduler scheduler;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHostRegister() throws Exception {
        Protos.Offer offer = createOffer("hostname", 4, 1024);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.launchedTask);
        Assert.assertEquals(1, mock.launchedTask.size());
        Protos.TaskInfo taskInfo = mock.launchedTask.iterator().next();
        Assert.assertEquals(4.0, resources(taskInfo.getResourcesList(), CPU), 0);
        Assert.assertEquals(1024.0, resources(taskInfo.getResourcesList(), MEM), 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeclineByCpu() throws Exception {
        Protos.Offer offer = createOffer("hostname", 4, 1024);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.cpus(2);
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.launchedTask);
        Assert.assertEquals(1, mock.launchedTask.size());
        Protos.TaskInfo taskInfo = mock.launchedTask.iterator().next();
        Assert.assertEquals(2.0, resources(taskInfo.getResourcesList(), CPU), 0);
        Assert.assertEquals(1024.0, resources(taskInfo.getResourcesList(), MEM), 0);
        mock.clear();
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNull(mock.launchedTask);
        Protos.OfferID declinedOffer = mock.declinedOffer;
        Assert.assertEquals(offer.getId(), declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeclineByMem() throws Exception {
        Protos.Offer offer = createOffer("hostname", 4, 1024);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.memory(512);
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.launchedTask);
        Assert.assertEquals(1, mock.launchedTask.size());
        Protos.TaskInfo taskInfo = mock.launchedTask.iterator().next();
        Assert.assertEquals(4.0, resources(taskInfo.getResourcesList(), CPU), 0);
        Assert.assertEquals(512.0, resources(taskInfo.getResourcesList(), MEM), 0);
        mock.clear();
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNull(mock.launchedTask);
        Protos.OfferID declinedOffer = mock.declinedOffer;
        Assert.assertEquals(offer.getId(), declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeclineByMemCpu() throws Exception {
        Protos.Offer offer = createOffer("hostname", 1, 1024);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.cpus(4);
        clustProp.memory(2000);
        scheduler.setClusterProps(clustProp);
        double totalMem = 0;
        double totalCpu = 0;
        for (int i = 0; i < 2; i++) {
            scheduler.resourceOffers(mock, Collections.singletonList(offer));
            Assert.assertNotNull(mock.launchedTask);
            Assert.assertEquals(1, mock.launchedTask.size());
            Protos.TaskInfo taskInfo = mock.launchedTask.iterator().next();
            totalCpu += resources(taskInfo.getResourcesList(), CPU);
            totalMem += resources(taskInfo.getResourcesList(), MEM);
            mock.clear();
        }
        Assert.assertEquals(2.0, totalCpu);
        Assert.assertEquals(2000.0, totalMem);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNull(mock.launchedTask);
        Protos.OfferID declinedOffer = mock.declinedOffer;
        Assert.assertEquals(offer.getId(), declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeclineByCpuMinRequirements() throws Exception {
        Protos.Offer offer = createOffer("hostname", 8, 10240);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.minCpuPerNode(12);
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.declinedOffer);
        Assert.assertEquals(offer.getId(), mock.declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeclineByMemMinRequirements() throws Exception {
        Protos.Offer offer = createOffer("hostname", 8, 10240);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.minMemoryPerNode(15000);
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.declinedOffer);
        Assert.assertEquals(offer.getId(), mock.declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHosthameConstraint() throws Exception {
        Protos.Offer offer = createOffer("hostname", 8, 10240);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.hostnameConstraint(Pattern.compile("hostname"));
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.declinedOffer);
        Assert.assertEquals(offer.getId(), mock.declinedOffer);
        offer = createOffer("hostnameAccept", 8, 10240);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.launchedTask);
        Assert.assertEquals(1, mock.launchedTask.size());
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPerNode() throws Exception {
        Protos.Offer offer = createOffer("hostname", 8, 1024);
        IgniteSchedulerSelfTest.DriverMock mock = new IgniteSchedulerSelfTest.DriverMock();
        ClusterProperties clustProp = new ClusterProperties();
        clustProp.memoryPerNode(1024);
        clustProp.cpusPerNode(2);
        scheduler.setClusterProps(clustProp);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNotNull(mock.launchedTask);
        Protos.TaskInfo taskInfo = mock.launchedTask.iterator().next();
        Assert.assertEquals(2.0, resources(taskInfo.getResourcesList(), CPU), 0);
        Assert.assertEquals(1024.0, resources(taskInfo.getResourcesList(), MEM), 0);
        mock.clear();
        offer = createOffer("hostname", 1, 2048);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNull(mock.launchedTask);
        Assert.assertNotNull(mock.declinedOffer);
        Assert.assertEquals(offer.getId(), mock.declinedOffer);
        mock.clear();
        offer = createOffer("hostname", 4, 512);
        scheduler.resourceOffers(mock, Collections.singletonList(offer));
        Assert.assertNull(mock.launchedTask);
        Assert.assertNotNull(mock.declinedOffer);
        Assert.assertEquals(offer.getId(), mock.declinedOffer);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIgniteFramework() throws Exception {
        final String mesosUserValue = "userAAAAA";
        final String mesosRoleValue = "role1";
        IgniteFramework igniteFramework = new IgniteFramework() {
            @Override
            protected String getUser() {
                return mesosUserValue;
            }

            @Override
            protected String getRole() {
                return mesosRoleValue;
            }
        };
        Protos.FrameworkInfo info = igniteFramework.getFrameworkInfo();
        String actualUserValue = info.getUser();
        String actualRoleValue = info.getRole();
        Assert.assertEquals(actualUserValue, mesosUserValue);
        Assert.assertEquals(actualRoleValue, mesosRoleValue);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMesosRoleValidation() throws Exception {
        List<String> failedRoleValues = Arrays.asList("", ".", "..", "-testRole", "test/Role", "test\\Role", "test Role", null);
        for (String failedRoleValue : failedRoleValues)
            Assert.assertFalse(IgniteFramework.isRoleValid(failedRoleValue));

    }

    /**
     * No-op implementation.
     */
    public static class DriverMock implements SchedulerDriver {
        /**
         *
         */
        Collection<Protos.TaskInfo> launchedTask;

        /**
         *
         */
        OfferID declinedOffer;

        /**
         * Clears launched task.
         */
        public void clear() {
            launchedTask = null;
            declinedOffer = null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status start() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status stop(boolean failover) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status stop() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status abort() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status join() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status run() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status requestResources(Collection<Protos.Request> requests) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks, Protos.Filters filters) {
            launchedTask = tasks;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status launchTasks(Collection<Protos.OfferID> offerIds, Collection<Protos.TaskInfo> tasks) {
            launchedTask = tasks;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks, Protos.Filters filters) {
            launchedTask = tasks;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status launchTasks(Protos.OfferID offerId, Collection<Protos.TaskInfo> tasks) {
            launchedTask = tasks;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status killTask(Protos.TaskID taskId) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status acceptOffers(Collection<Protos.OfferID> collection, Collection<Protos.Offer.Operation> collection1, Protos.Filters filters) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status declineOffer(Protos.OfferID offerId, Protos.Filters filters) {
            declinedOffer = offerId;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status declineOffer(Protos.OfferID offerId) {
            declinedOffer = offerId;
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status reviveOffers() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status suppressOffers() {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status acknowledgeStatusUpdate(Protos.TaskStatus status) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status sendFrameworkMessage(Protos.ExecutorID executorId, Protos.SlaveID slaveId, byte[] data) {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Status reconcileTasks(Collection<Protos.TaskStatus> statuses) {
            return null;
        }
    }
}

