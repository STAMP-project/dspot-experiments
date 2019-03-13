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
package org.apache.dubbo.metadata.identifier;


import MetadataIdentifier.KeyTypeEnum.PATH;
import MetadataIdentifier.KeyTypeEnum.UNIQUE_KEY;
import org.apache.dubbo.common.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static MetadataIdentifier.SEPARATOR;


/**
 * 2019/1/7
 */
public class MetadataIdentifierTest {
    @Test
    public void testGetUniqueKey() {
        String interfaceName = "org.apache.dubbo.metadata.integration.InterfaceNameTestService";
        String version = "1.0.0.zk.md";
        String group = null;
        String application = "vic.zk.md";
        MetadataIdentifier providerMetadataIdentifier = new MetadataIdentifier(interfaceName, version, group, Constants.PROVIDER_SIDE, application);
        System.out.println(providerMetadataIdentifier.getUniqueKey(PATH));
        Assertions.assertEquals(providerMetadataIdentifier.getUniqueKey(PATH), (((((((("metadata" + (Constants.PATH_SEPARATOR)) + interfaceName) + (Constants.PATH_SEPARATOR)) + (version == null ? "" : version + (Constants.PATH_SEPARATOR))) + (group == null ? "" : group + (Constants.PATH_SEPARATOR))) + (Constants.PROVIDER_SIDE)) + (Constants.PATH_SEPARATOR)) + application));
        System.out.println(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY));
        Assertions.assertEquals(providerMetadataIdentifier.getUniqueKey(UNIQUE_KEY), ((((((interfaceName + (SEPARATOR)) + (version == null ? "" : version + (SEPARATOR))) + (group == null ? "" : group + (SEPARATOR))) + (Constants.PROVIDER_SIDE)) + (SEPARATOR)) + application));
    }
}

