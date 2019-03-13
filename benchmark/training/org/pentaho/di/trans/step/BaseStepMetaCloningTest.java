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
package org.pentaho.di.trans.step;


import StreamInterface.StreamType;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;


public class BaseStepMetaCloningTest {
    @Test
    public void testClone() throws Exception {
        final Database db1 = Mockito.mock(Database.class);
        final Database db2 = Mockito.mock(Database.class);
        final Repository repository = Mockito.mock(Repository.class);
        final StepMeta stepMeta = Mockito.mock(StepMeta.class);
        BaseStepMeta meta = new BaseStepMeta();
        meta.setChanged(true);
        meta.databases = new Database[]{ db1, db2 };
        StepIOMetaInterface ioMeta = new StepIOMeta(true, false, false, false, false, false);
        meta.setStepIOMeta(ioMeta);
        meta.repository = repository;
        meta.parentStepMeta = stepMeta;
        BaseStepMeta clone = ((BaseStepMeta) (meta.clone()));
        Assert.assertTrue(clone.hasChanged());
        // is it OK ?
        Assert.assertTrue(((clone.databases) == (meta.databases)));
        Assert.assertArrayEquals(meta.databases, clone.databases);
        Assert.assertEquals(meta.repository, clone.repository);
        Assert.assertEquals(meta.parentStepMeta, clone.parentStepMeta);
        StepIOMetaInterface cloneIOMeta = clone.getStepIOMeta();
        Assert.assertNotNull(cloneIOMeta);
        Assert.assertEquals(ioMeta.isInputAcceptor(), cloneIOMeta.isInputAcceptor());
        Assert.assertEquals(ioMeta.isInputDynamic(), cloneIOMeta.isInputDynamic());
        Assert.assertEquals(ioMeta.isInputOptional(), cloneIOMeta.isInputOptional());
        Assert.assertEquals(ioMeta.isOutputDynamic(), cloneIOMeta.isOutputDynamic());
        Assert.assertEquals(ioMeta.isOutputProducer(), cloneIOMeta.isOutputProducer());
        Assert.assertEquals(ioMeta.isSortedDataRequired(), cloneIOMeta.isSortedDataRequired());
        Assert.assertNotNull(cloneIOMeta.getInfoStreams());
        Assert.assertEquals(0, cloneIOMeta.getInfoStreams().size());
    }

    @Test
    public void testCloneWithInfoSteps() throws Exception {
        final Database db1 = Mockito.mock(Database.class);
        final Database db2 = Mockito.mock(Database.class);
        final Repository repository = Mockito.mock(Repository.class);
        final StepMeta stepMeta = Mockito.mock(StepMeta.class);
        BaseStepMeta meta = new BaseStepMeta();
        meta.setChanged(true);
        meta.databases = new Database[]{ db1, db2 };
        StepIOMetaInterface ioMeta = new StepIOMeta(true, false, false, false, false, false);
        meta.setStepIOMeta(ioMeta);
        final String refStepName = "referenced step";
        final StepMeta refStepMeta = Mockito.mock(StepMeta.class);
        Mockito.doReturn(refStepName).when(refStepMeta).getName();
        StreamInterface stream = new org.pentaho.di.trans.step.errorhandling.Stream(StreamType.INFO, refStepMeta, null, null, refStepName);
        ioMeta.addStream(stream);
        meta.repository = repository;
        meta.parentStepMeta = stepMeta;
        BaseStepMeta clone = ((BaseStepMeta) (meta.clone()));
        Assert.assertTrue(clone.hasChanged());
        // is it OK ?
        Assert.assertTrue(((clone.databases) == (meta.databases)));
        Assert.assertArrayEquals(meta.databases, clone.databases);
        Assert.assertEquals(meta.repository, clone.repository);
        Assert.assertEquals(meta.parentStepMeta, clone.parentStepMeta);
        StepIOMetaInterface cloneIOMeta = clone.getStepIOMeta();
        Assert.assertNotNull(cloneIOMeta);
        Assert.assertEquals(ioMeta.isInputAcceptor(), cloneIOMeta.isInputAcceptor());
        Assert.assertEquals(ioMeta.isInputDynamic(), cloneIOMeta.isInputDynamic());
        Assert.assertEquals(ioMeta.isInputOptional(), cloneIOMeta.isInputOptional());
        Assert.assertEquals(ioMeta.isOutputDynamic(), cloneIOMeta.isOutputDynamic());
        Assert.assertEquals(ioMeta.isOutputProducer(), cloneIOMeta.isOutputProducer());
        Assert.assertEquals(ioMeta.isSortedDataRequired(), cloneIOMeta.isSortedDataRequired());
        final List<StreamInterface> clonedInfoStreams = cloneIOMeta.getInfoStreams();
        Assert.assertNotNull(clonedInfoStreams);
        Assert.assertEquals(1, clonedInfoStreams.size());
        final StreamInterface clonedStream = clonedInfoStreams.get(0);
        Assert.assertNotSame(stream, clonedStream);
        Assert.assertEquals(stream.getStreamType(), clonedStream.getStreamType());
        Assert.assertEquals(refStepName, clonedStream.getStepname());
        Assert.assertSame(refStepMeta, clonedStream.getStepMeta());// PDI-15799

    }
}

