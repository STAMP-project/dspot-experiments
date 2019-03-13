/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.tomcat;


import com.navercorp.pinpoint.bootstrap.context.ServerMetaData;
import com.navercorp.pinpoint.test.junit4.BasePinpointTest;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.util.ServerInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author hyungil.jeong
 */
public class StandardServiceModifierTest extends BasePinpointTest {
    private StandardService service;

    @Mock
    private StandardEngine engine;

    @Test
    public void startShouldCollectServerInfo() throws Exception {
        // Given
        String expectedServerInfo = ServerInfo.getServerInfo();
        // When
        service.start();
        service.stop();
        // Then
        ServerMetaData serverMetaData = getServerMetaData();
        Assert.assertEquals(expectedServerInfo, serverMetaData.getServerInfo());
    }
}

