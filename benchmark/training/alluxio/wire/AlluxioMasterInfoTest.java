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


import alluxio.test.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public class AlluxioMasterInfoTest {
    @Test
    public void json() throws Exception {
        AlluxioMasterInfo alluxioMasterInfo = AlluxioMasterInfoTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        AlluxioMasterInfo other = mapper.readValue(mapper.writeValueAsBytes(alluxioMasterInfo), AlluxioMasterInfo.class);
        checkEquality(alluxioMasterInfo, other);
    }

    @Test
    public void equals() {
        CommonUtils.testEquals(AlluxioMasterInfo.class);
    }
}

