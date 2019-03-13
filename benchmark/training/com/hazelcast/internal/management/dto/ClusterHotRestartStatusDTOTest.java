/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.management.dto;


import MemberHotRestartStatus.PENDING;
import MemberHotRestartStatus.SUCCESSFUL;
import com.hazelcast.config.HotRestartClusterDataRecoveryPolicy;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO.ClusterHotRestartStatus;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO.MemberHotRestartStatus;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterHotRestartStatusDTOTest {
    @Test
    public void testSerialization() {
        Map<String, MemberHotRestartStatus> memberHotRestartStatusMap = new HashMap<String, MemberHotRestartStatus>();
        memberHotRestartStatusMap.put("127.0.0.1:5701", PENDING);
        memberHotRestartStatusMap.put("127.0.0.1:5702", SUCCESSFUL);
        ClusterHotRestartStatusDTO dto = new ClusterHotRestartStatusDTO(HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY, ClusterHotRestartStatus.IN_PROGRESS, 23, 42, memberHotRestartStatusMap);
        JsonObject json = dto.toJson();
        ClusterHotRestartStatusDTO deserialized = new ClusterHotRestartStatusDTO();
        deserialized.fromJson(json);
        Assert.assertEquals(dto.getDataRecoveryPolicy(), deserialized.getDataRecoveryPolicy());
        Assert.assertEquals(dto.getHotRestartStatus(), deserialized.getHotRestartStatus());
        Assert.assertEquals(dto.getRemainingValidationTimeMillis(), deserialized.getRemainingValidationTimeMillis());
        Assert.assertEquals(dto.getRemainingDataLoadTimeMillis(), deserialized.getRemainingDataLoadTimeMillis());
        Assert.assertEquals(dto.getMemberHotRestartStatusMap(), deserialized.getMemberHotRestartStatusMap());
    }
}

