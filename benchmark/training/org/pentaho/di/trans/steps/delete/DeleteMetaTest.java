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
package org.pentaho.di.trans.steps.delete;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class DeleteMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<DeleteMeta> testMetaClass = DeleteMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    private StepMeta stepMeta;

    private Delete del;

    private DeleteData dd;

    private DeleteMeta dmi;

    @Test
    public void testCommitCountFixed() {
        dmi.setCommitSize("100");
        Assert.assertTrue(((dmi.getCommitSize(del)) == 100));
    }

    @Test
    public void testCommitCountVar() {
        dmi.setCommitSize("${max.sz}");
        Assert.assertTrue(((dmi.getCommitSize(del)) == 10));
    }

    @Test
    public void testCommitCountMissedVar() {
        dmi.setCommitSize("missed-var");
        try {
            dmi.getCommitSize(del);
            Assert.fail();
        } catch (Exception ex) {
        }
    }
}

