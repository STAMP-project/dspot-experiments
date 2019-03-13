/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.wire;


import alluxio.grpc.GrpcUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class WorkerInfoTest {
    @Test
    public void json() throws Exception {
        WorkerInfo workerInfo = WorkerInfoTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        WorkerInfo other = mapper.readValue(mapper.writeValueAsBytes(workerInfo), WorkerInfo.class);
        checkEquality(workerInfo, other);
    }

    @Test
    public void proto() {
        WorkerInfo workerInfo = WorkerInfoTest.createRandom();
        WorkerInfo other = GrpcUtils.fromProto(GrpcUtils.toProto(workerInfo));
        checkEquality(workerInfo, other);
    }

    @Test
    public void lastContactSecComparator() {
        Assert.assertTrue(((WorkerInfoTest.compareLostWorkersWithTimes(0, 1)) < 0));
        Assert.assertTrue(((WorkerInfoTest.compareLostWorkersWithTimes(1, 0)) > 0));
        Assert.assertTrue(((WorkerInfoTest.compareLostWorkersWithTimes(1, 1)) == 0));
        Assert.assertTrue(((WorkerInfoTest.compareLostWorkersWithTimes((-1), 1)) < 0));
        Assert.assertTrue(((WorkerInfoTest.compareLostWorkersWithTimes(1, (-1))) > 0));
    }
}

