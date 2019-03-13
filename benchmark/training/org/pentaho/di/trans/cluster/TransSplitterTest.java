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
package org.pentaho.di.trans.cluster;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransMetaFactory;
import org.pentaho.di.trans.TransMetaFactoryImpl;
import org.w3c.dom.Node;


public class TransSplitterTest {
    private LogChannelInterfaceFactory oldLogChannelInterfaceFactory;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testTransSplitterReadsRep() throws KettleException {
        TransMeta meta = Mockito.mock(TransMeta.class);
        Repository rep = Mockito.mock(Repository.class);
        Mockito.when(meta.getRepository()).thenReturn(rep);
        TransMeta meta2 = Mockito.mock(TransMeta.class);
        TransMetaFactory factory = Mockito.mock(TransMetaFactory.class);
        Mockito.when(factory.create(ArgumentMatchers.any(Node.class), ArgumentMatchers.any(Repository.class))).thenReturn(meta2);
        Mockito.when(meta.getXML()).thenReturn("<transformation></transformation>");
        try {
            new TransSplitter(meta, factory);
        } catch (Exception e) {
            // ignore
        }
        Mockito.verify(rep, Mockito.times(1)).readTransSharedObjects(meta2);
    }

    @Test
    public void testTransSplitterRowsetSize() throws KettleException {
        TransMeta originalMeta = new TransMeta();
        originalMeta.setSizeRowset(0);
        TransMetaFactory factory = new TransMetaFactoryImpl();
        try {
            TransSplitter transSplitter = new TransSplitter(originalMeta, factory);
            transSplitter.splitOriginalTransformation();
            Assert.assertEquals(originalMeta.getSizeRowset(), transSplitter.getMaster().getSizeRowset());
        } catch (Exception e) {
            // ignore
        }
    }
}

