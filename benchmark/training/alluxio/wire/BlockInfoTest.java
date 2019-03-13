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


public final class BlockInfoTest {
    /**
     * Test to convert between a BlockInfo type and a json type.
     */
    @Test
    public void json() throws Exception {
        BlockInfo blockInfo = BlockInfoTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        BlockInfo other = mapper.readValue(mapper.writeValueAsBytes(blockInfo), BlockInfo.class);
        checkEquality(blockInfo, other);
    }

    /**
     * Test to convert between a proto type and a wire type.
     */
    @Test
    public void proto() {
        BlockInfo blockInfo = BlockInfoTest.createRandom();
        BlockInfo other = GrpcUtils.fromProto(GrpcUtils.toProto(blockInfo));
        checkEquality(blockInfo, other);
    }
}

