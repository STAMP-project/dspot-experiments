/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.common.nosql.mongo.dao.model;


import Warning.NONFINAL_FIELDS;
import java.util.Random;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;


public class EndpointGroupStateTest {
    public static final Random RANDOM = new Random();

    public static final int LIMIT = 1000000;

    @Test
    public void EndpointStateTest() {
        EndpointGroupStateDto state = new EndpointGroupStateDto();
        String groupId = String.valueOf(EndpointGroupStateTest.RANDOM.nextInt(EndpointGroupStateTest.LIMIT));
        String filterId = String.valueOf(EndpointGroupStateTest.RANDOM.nextInt(EndpointGroupStateTest.LIMIT));
        String configId = String.valueOf(EndpointGroupStateTest.RANDOM.nextInt(EndpointGroupStateTest.LIMIT));
        state.setConfigurationId(configId);
        state.setProfileFilterId(filterId);
        state.setEndpointGroupId(groupId);
        EndpointGroupState stateOne = new EndpointGroupState(state);
        EndpointGroupState stateTwo = new EndpointGroupState();
        stateTwo.setConfigurationId(configId);
        stateTwo.setProfileFilterId(filterId);
        stateTwo.setEndpointGroupId(groupId);
        Assert.assertEquals(stateOne, stateTwo);
        Assert.assertEquals(stateOne.hashCode(), stateTwo.hashCode());
        Assert.assertEquals(stateOne.toDto(), stateTwo.toDto());
        Assert.assertEquals(stateOne.toString(), stateTwo.toString());
    }

    @Test
    public void hashCodeEqualsTest() {
        EqualsVerifier.forClass(EndpointGroupState.class).suppress(NONFINAL_FIELDS).withRedefinedSuperclass().verify();
    }
}

