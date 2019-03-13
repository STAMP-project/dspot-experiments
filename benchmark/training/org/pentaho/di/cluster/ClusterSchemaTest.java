/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.cluster;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.www.SlaveServerDetection;


public class ClusterSchemaTest {
    private ClusterSchema clusterSchema;

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testUpdateActivityStatusInGetSlaveServers() throws Exception {
        SlaveServer master = Mockito.mock(SlaveServer.class);
        SlaveServer activeSlaveServer = Mockito.mock(SlaveServer.class);
        SlaveServer inactiveSlaveServer = Mockito.mock(SlaveServer.class);
        SlaveServerDetection masterDetection = Mockito.mock(SlaveServerDetection.class);
        SlaveServerDetection activeSlaveServerDetection = Mockito.mock(SlaveServerDetection.class);
        SlaveServerDetection inactiveSlaveServerDetection = Mockito.mock(SlaveServerDetection.class);
        List<SlaveServer> slaveServers = new ArrayList<>();
        slaveServers.add(master);
        slaveServers.add(activeSlaveServer);
        slaveServers.add(inactiveSlaveServer);
        List<SlaveServerDetection> detections = new ArrayList<>();
        detections.add(masterDetection);
        detections.add(activeSlaveServerDetection);
        detections.add(inactiveSlaveServerDetection);
        Mockito.doReturn(true).when(clusterSchema).isDynamic();
        Mockito.doReturn(true).when(master).isMaster();
        Mockito.doReturn(false).when(activeSlaveServer).isMaster();
        Mockito.doReturn(false).when(inactiveSlaveServer).isMaster();
        Mockito.doReturn(detections).when(master).getSlaveServerDetections();
        Mockito.doReturn(master).when(masterDetection).getSlaveServer();
        Mockito.doReturn(activeSlaveServer).when(activeSlaveServerDetection).getSlaveServer();
        Mockito.doReturn(inactiveSlaveServer).when(inactiveSlaveServerDetection).getSlaveServer();
        Mockito.doThrow(new Exception()).when(inactiveSlaveServer).getStatus();
        clusterSchema.setSlaveServers(slaveServers);
        clusterSchema.getSlaveServersFromMasterOrLocal();
        Mockito.verify(master).getStatus();
        Mockito.verify(masterDetection, Mockito.never()).setActive(false);
        Mockito.verify(masterDetection, Mockito.never()).setLastInactiveDate(ArgumentMatchers.anyObject());
        Mockito.verify(activeSlaveServer).getStatus();
        Mockito.verify(activeSlaveServerDetection, Mockito.never()).setActive(false);
        Mockito.verify(activeSlaveServerDetection, Mockito.never()).setLastInactiveDate(ArgumentMatchers.anyObject());
        Mockito.verify(inactiveSlaveServer).getStatus();
        Mockito.verify(inactiveSlaveServerDetection).setActive(false);
        Mockito.verify(inactiveSlaveServerDetection).setLastInactiveDate(ArgumentMatchers.anyObject());
    }
}

