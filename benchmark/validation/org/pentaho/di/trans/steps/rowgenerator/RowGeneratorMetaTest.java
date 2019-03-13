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
package org.pentaho.di.trans.steps.rowgenerator;


import java.util.Collections;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.metastore.api.IMetaStore;


public class RowGeneratorMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private Repository rep;

    private ObjectId id_step;

    private final String launchVariable = "${ROW_LIMIT}";

    private final String rowGeneratorRowLimitCode = "limit";

    private LoadSaveTester<?> loadSaveTester;

    private Class<RowGeneratorMeta> testMetaClass = RowGeneratorMeta.class;

    /**
     * If we can read row limit as string from repository then we can run row generator.
     *
     * @see RowGeneratorTest
     * @throws KettleException
     * 		
     */
    @Test
    public void testReadRowLimitAsStringFromRepository() throws KettleException {
        RowGeneratorMeta rowGeneratorMeta = new RowGeneratorMeta();
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        rowGeneratorMeta.readRep(rep, metaStore, id_step, Collections.singletonList(dbMeta));
        Assert.assertEquals(rowGeneratorMeta.getRowLimit(), launchVariable);
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }
}

