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
import org.junit.Test;


public final class BlockLocationTest {
    @Test
    public void json() throws Exception {
        BlockLocation blockLocation = BlockLocationTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        BlockLocation other = mapper.readValue(mapper.writeValueAsBytes(blockLocation), BlockLocation.class);
        checkEquality(blockLocation, other);
    }

    @Test
    public void proto() {
        BlockLocation blockLocation = BlockLocationTest.createRandom();
        BlockLocation other = GrpcUtils.fromProto(GrpcUtils.toProto(blockLocation));
        checkEquality(blockLocation, other);
    }
}

