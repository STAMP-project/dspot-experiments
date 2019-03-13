/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016 - 2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.www;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TransformationMapTest {
    private static final String TEST_HOST = "127.0.0.1";

    private static final String CLUSTERED_RUN_ID = "CLUSTERED_RUN_ID";

    private static final String TEST_TRANSFORMATION_NAME = "TEST_TRANSFORMATION_NAME";

    private static final String TEST_SOURCE_SLAVE_NAME = "TEST_SOURCE_SLAVE_NAME";

    private static final String TEST_SOURCE_STEP_NAME = "TEST_SOURCE_STEP_NAME";

    private static final String TEST_SOURCE_STEP_COPY = "TEST_SOURCE_STEP_COPY";

    private static final String TEST_TARGET_SLAVE_NAME = "TEST_TARGET_SLAVE_NAME";

    private static final String TEST_TARGET_STEP_NAME = "TEST_TARGET_STEP_NAME";

    private static final String TEST_TARGET_STEP_COPY = "TEST_TARGET_STEP_COPY";

    private TransformationMap transformationMap;

    @Test
    public void getHostServerSocketPorts() {
        transformationMap.allocateServerSocketPort(1, TransformationMapTest.TEST_HOST, TransformationMapTest.CLUSTERED_RUN_ID, TransformationMapTest.TEST_TRANSFORMATION_NAME, TransformationMapTest.TEST_SOURCE_SLAVE_NAME, TransformationMapTest.TEST_SOURCE_STEP_NAME, TransformationMapTest.TEST_SOURCE_STEP_COPY, TransformationMapTest.TEST_TARGET_SLAVE_NAME, TransformationMapTest.TEST_TARGET_STEP_NAME, TransformationMapTest.TEST_TARGET_STEP_COPY);
        List<SocketPortAllocation> actualResult = transformationMap.getHostServerSocketPorts(TransformationMapTest.TEST_HOST);
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(1, actualResult.size());
    }

    @Test
    public void getHostServerSocketPortsWithoutAllocatedPorts() {
        List<SocketPortAllocation> actualResult = transformationMap.getHostServerSocketPorts(TransformationMapTest.TEST_HOST);
        Assert.assertNotNull(actualResult);
        Assert.assertTrue(actualResult.isEmpty());
    }
}

