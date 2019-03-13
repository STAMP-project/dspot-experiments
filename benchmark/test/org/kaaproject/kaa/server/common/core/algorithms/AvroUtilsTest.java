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
package org.kaaproject.kaa.server.common.core.algorithms;


import java.io.IOException;
import org.apache.avro.Schema;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Test;


public class AvroUtilsTest {
    private JsonNode data;

    private JsonNode dataWithUUIDs;

    private Schema avroSchema;

    @Test
    public void testInjectUuids() throws IOException {
        String jsonWithUUIds = AvroUtils.injectUuids(data, avroSchema);
        Assert.assertTrue("Generated json is not equal json with UUIDs", jsonWithUUIds.equals(dataWithUUIDs.toString()));
    }

    @Test
    public void testRemoveUuids() throws IOException {
        AvroUtils.removeUuids(dataWithUUIDs);
        String json = dataWithUUIDs.toString();
        Assert.assertTrue("Generated json is not equal json without UUIDs", json.equals(data.toString()));
    }
}

