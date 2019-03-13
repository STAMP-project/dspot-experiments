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


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public final class BlockMasterInfoTest {
    @Test
    public void json() throws Exception {
        BlockMasterInfo blockMasterInfo = BlockMasterInfoTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        BlockMasterInfo other = mapper.readValue(mapper.writeValueAsBytes(blockMasterInfo), BlockMasterInfo.class);
        checkEquality(blockMasterInfo, other);
    }

    @Test
    public void proto() {
        BlockMasterInfo blockMasterInfo = BlockMasterInfoTest.createRandom();
        BlockMasterInfo other = BlockMasterInfo.fromProto(blockMasterInfo.toProto());
        checkEquality(blockMasterInfo, other);
    }
}

