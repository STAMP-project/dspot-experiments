/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.named.cluster;


import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.osgi.api.NamedClusterOsgi;
import org.pentaho.di.core.osgi.api.NamedClusterServiceOsgi;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;


/**
 * Created by tkafalas on 7/14/2017.
 */
public class NamedClusterEmbedManagerTest {
    private final String CLUSTER1_NAME = "cluster1_name";

    private final String CLUSTER2_NAME = "cluster2_name";

    private final String KEY = "KEY";

    private AbstractMeta mockMeta;

    private NamedClusterEmbedManager namedClusterEmbedManager;

    private LogChannelInterface mockLog;

    private NamedClusterOsgi mockNamedCluster1;

    private NamedClusterOsgi mockNamedCluster2;

    private MetaStoreFactory mockMetaStoreFactory;

    NamedClusterServiceOsgi mockNamedClusterService;

    @Test
    public void testRegisterUrlNc() throws Exception {
        namedClusterEmbedManager.registerUrl((("hc://" + (CLUSTER1_NAME)) + "/dir1/dir2"));
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster1);
    }

    @Test
    public void testRegisterUrlNotNc() throws Exception {
        namedClusterEmbedManager.registerUrl((("hdfs://" + (CLUSTER1_NAME)) + "/dir1/dir2"));
        Mockito.verify(mockMetaStoreFactory, Mockito.never()).saveElement(ArgumentMatchers.any());
    }

    @Test
    public void testRegisterUrlRegularFile() throws Exception {
        namedClusterEmbedManager.registerUrl((("/" + (CLUSTER1_NAME)) + "/dir1/dir2"));
        Mockito.verify(mockMetaStoreFactory, Mockito.never()).saveElement(ArgumentMatchers.any());
    }

    @Test
    public void testRegisterUrlFullVariable() throws Exception {
        Mockito.when(mockNamedClusterService.listNames(mockMeta.getMetaStore())).thenReturn(Arrays.asList(new String[]{ CLUSTER1_NAME, CLUSTER2_NAME }));
        namedClusterEmbedManager.registerUrl("${variable)");
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster1);
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster2);
    }

    @Test
    public void testRegisterUrlClusterVariable() throws Exception {
        Mockito.when(mockNamedClusterService.listNames(mockMeta.getMetaStore())).thenReturn(Arrays.asList(new String[]{ CLUSTER1_NAME, CLUSTER2_NAME }));
        namedClusterEmbedManager.registerUrl("hc://${variable)/dir1/file");
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster1);
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster2);
    }

    @Test
    public void testRegisterUrlAlreadyRegistered() throws Exception {
        Mockito.when(mockMetaStoreFactory.loadElement(CLUSTER1_NAME)).thenReturn(mockNamedCluster1);
        namedClusterEmbedManager.registerUrl((("hc://" + (CLUSTER1_NAME)) + "/dir1/dir2"));
        Mockito.verify(mockMetaStoreFactory, Mockito.times(0)).saveElement(mockNamedCluster1);
    }

    @Test
    public void testClear() throws Exception {
        Mockito.when(mockMetaStoreFactory.getElements()).thenReturn(Arrays.asList(new NamedClusterOsgi[]{ mockNamedCluster1, mockNamedCluster2 }));
        namedClusterEmbedManager.clear();
        Mockito.verify(mockMetaStoreFactory).deleteElement(CLUSTER1_NAME);
        Mockito.verify(mockMetaStoreFactory).deleteElement(CLUSTER2_NAME);
    }

    @Test
    public void testPassEmbeddedMetastoreKey() {
        Variables mockVariables = Mockito.mock(Variables.class);
        namedClusterEmbedManager.passEmbeddedMetastoreKey(mockVariables, "key");
        Mockito.verify(mockVariables).setVariable(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void testAddingClusterToMetaData() throws MetaStoreException {
        namedClusterEmbedManager.addClusterToMeta(CLUSTER1_NAME);
        Mockito.verify(mockMetaStoreFactory).saveElement(mockNamedCluster1);
    }
}

