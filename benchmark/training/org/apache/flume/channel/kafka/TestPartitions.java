/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel.kafka;


import PartitionOption.NOTANUMBER;
import PartitionOption.NOTSET;
import PartitionOption.VALIDBUTOUTOFRANGE;
import PartitionTestScenario.NO_PARTITION_HEADERS;
import PartitionTestScenario.PARTITION_ID_HEADER_ONLY;
import PartitionTestScenario.STATIC_HEADER_AND_PARTITION_ID;
import PartitionTestScenario.STATIC_HEADER_ONLY;
import org.apache.flume.ChannelException;
import org.junit.Test;


public class TestPartitions extends TestKafkaChannelBase {
    @Test
    public void testPartitionHeaderSet() throws Exception {
        doPartitionHeader(PARTITION_ID_HEADER_ONLY);
    }

    @Test
    public void testPartitionHeaderNotSet() throws Exception {
        doPartitionHeader(NO_PARTITION_HEADERS);
    }

    @Test
    public void testStaticPartitionAndHeaderSet() throws Exception {
        doPartitionHeader(STATIC_HEADER_AND_PARTITION_ID);
    }

    @Test
    public void testStaticPartitionHeaderNotSet() throws Exception {
        doPartitionHeader(STATIC_HEADER_ONLY);
    }

    @Test
    public void testPartitionHeaderMissing() throws Exception {
        doPartitionErrors(NOTSET);
    }

    @Test(expected = ChannelException.class)
    public void testPartitionHeaderOutOfRange() throws Exception {
        doPartitionErrors(VALIDBUTOUTOFRANGE);
    }

    @Test(expected = ChannelException.class)
    public void testPartitionHeaderInvalid() throws Exception {
        doPartitionErrors(NOTANUMBER);
    }
}

