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
package org.pentaho.di.trans.steps.mondrianinput;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class MondrianInputMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testLoadSave() throws KettleException {
        List<String> attributes = Arrays.asList("DatabaseMeta", "SQL", "Catalog", "Role", "VariableReplacementActive");
        LoadSaveTester<MondrianInputMeta> loadSaveTester = new LoadSaveTester(MondrianInputMeta.class, attributes);
        loadSaveTester.testSerialization();
    }

    @Test
    public void testDefaults() {
        MondrianInputMeta meta = new MondrianInputMeta();
        meta.setDefault();
        Assert.assertNull(meta.getDatabaseMeta());
        Assert.assertNotNull(meta.getSQL());
        Assert.assertFalse(Utils.isEmpty(meta.getSQL()));
        Assert.assertFalse(meta.isVariableReplacementActive());
    }

    @Test
    public void testGetData() {
        MondrianInputMeta meta = new MondrianInputMeta();
        Assert.assertTrue(((meta.getStepData()) instanceof MondrianData));
    }
}

