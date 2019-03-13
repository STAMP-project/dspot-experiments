/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata.upgrade;


import io.crate.test.integration.CrateUnitTest;
import java.io.IOException;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Test;


public class MetaDataIndexUpgraderTest extends CrateUnitTest {
    @Test
    public void testDynamicStringTemplateIsPurged() throws IOException {
        MetaDataIndexUpgrader metaDataIndexUpgrader = new MetaDataIndexUpgrader();
        MappingMetaData mappingMetaData = new MappingMetaData(MetaDataIndexUpgraderTest.createDynamicStringMappingTemplate());
        MappingMetaData newMappingMetaData = metaDataIndexUpgrader.purgeDynamicStringTemplate(mappingMetaData, "dummy");
        Object dynamicTemplates = newMappingMetaData.getSourceAsMap().get("dynamic_templates");
        assertThat(dynamicTemplates, IsNull.nullValue());
        // Check that the new metadata still has the root "default" element
        assertThat("{\"default\":{}}", CoreMatchers.is(newMappingMetaData.source().toString()));
    }
}

