/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.common.nosql.cassandra.dao.model;


import Warning.NONFINAL_FIELDS;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.credentials.EndpointRegistrationDto;


/**
 *
 *
 * @author Bohdan Khablenko
 * @since v0.9.0
 */
public class CassandraEndpointRegistrationTest {
    @Test
    public void equalsVerifierTest() throws Exception {
        EqualsVerifier.forClass(CassandraEndpointRegistration.class).suppress(NONFINAL_FIELDS).verify();
    }

    @Test
    public void dataConversionTest() throws Exception {
        EndpointRegistrationDto endpointRegistrationDto = new EndpointRegistrationDto("1", "2", "3", 42, "test");
        CassandraEndpointRegistration cassandraEndpointRegistration = new CassandraEndpointRegistration(endpointRegistrationDto);
        Assert.assertEquals(endpointRegistrationDto, cassandraEndpointRegistration.toDto());
    }
}

