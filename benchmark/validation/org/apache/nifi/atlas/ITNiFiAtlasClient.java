/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.atlas;


import AtlasEntity.AtlasEntityWithExtInfo;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ITNiFiAtlasClient {
    private static final Logger logger = LoggerFactory.getLogger(ITNiFiAtlasClient.class);

    private NiFiAtlasClient atlasClient;

    @Test
    public void testFetchNiFiFlow() throws Exception {
        final NiFiFlow nifiFlow = atlasClient.fetchNiFiFlow("1fc2e0a6-0160-1000-2660-72a0db49f37c", "DEBUG");
    }

    @Test
    public void testDeleteTypeDefs() throws Exception {
        atlasClient.deleteTypeDefs(NiFiTypes.NIFI_TYPES);
    }

    @Test
    public void testRegisterNiFiTypeDefs() throws Exception {
        atlasClient.registerNiFiTypeDefs(true);
    }

    @Test
    public void testSearch() throws Exception {
        final AtlasObjectId atlasObjectId = new AtlasObjectId("kafka_topic", "topic", "nifi-test");
        final AtlasEntity.AtlasEntityWithExtInfo entityDef = atlasClient.searchEntityDef(atlasObjectId);
        ITNiFiAtlasClient.logger.info("entityDef={}", entityDef);
    }
}

