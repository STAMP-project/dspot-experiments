/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.container.common.transport.server.ratis;


import java.io.File;
import java.util.Set;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos.ContainerCommandRequestProto;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos.ContainerCommandResponseProto;
import org.apache.hadoop.hdds.scm.container.common.helpers.StorageContainerException;
import org.apache.hadoop.ozone.RatisTestHelper;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.ozone.container.common.interfaces.ContainerDispatcher;
import org.apache.hadoop.ozone.container.common.interfaces.Handler;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


/**
 * This class tests the metrics of ContainerStateMachine.
 */
public class TestCSMMetrics {
    static final String TEST_DIR = (GenericTestUtils.getTestDir("dfs").getAbsolutePath()) + (File.separator);

    @FunctionalInterface
    interface CheckedBiFunction<LEFT, RIGHT, OUT, THROWABLE extends Throwable> {
        OUT apply(LEFT left, RIGHT right) throws THROWABLE;
    }

    @Test
    public void testContainerStateMachineMetrics() throws Exception {
        TestCSMMetrics.runContainerStateMachineMetrics(1, ( pipeline, conf) -> RatisTestHelper.initRatisConf(GRPC, conf), XceiverClientRatis::newXceiverClientRatis, TestCSMMetrics::newXceiverServerRatis, ( dn, p) -> RatisTestHelper.initXceiverServerRatis(GRPC, dn, p));
    }

    private static class TestContainerDispatcher implements ContainerDispatcher {
        /**
         * Dispatches commands to container layer.
         *
         * @param msg
         * 		- Command Request
         * @return Command Response
         */
        @Override
        public ContainerCommandResponseProto dispatch(ContainerCommandRequestProto msg, DispatcherContext context) {
            return ContainerTestHelper.getCreateContainerResponse(msg);
        }

        @Override
        public void validateContainerCommand(ContainerCommandRequestProto msg) throws StorageContainerException {
        }

        @Override
        public void init() {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public Handler getHandler(ContainerProtos.ContainerType containerType) {
            return null;
        }

        @Override
        public void setScmId(String scmId) {
        }

        @Override
        public void buildMissingContainerSet(Set<Long> createdContainerSet) {
        }
    }
}

