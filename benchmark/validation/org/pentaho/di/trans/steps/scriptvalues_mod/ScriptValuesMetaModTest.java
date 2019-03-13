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
package org.pentaho.di.trans.steps.scriptvalues_mod;


import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.powermock.reflect.Whitebox;


public class ScriptValuesMetaModTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    Class<ScriptValuesMetaMod> testMetaClass = ScriptValuesMetaMod.class;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class ScriptValuesScriptLoadSaveValidator implements FieldLoadSaveValidator<ScriptValuesScript> {
        final Random rand = new Random();

        @Override
        public ScriptValuesScript getTestObject() {
            int scriptType = rand.nextInt(4);
            if (scriptType == 3) {
                scriptType = -1;
            }
            ScriptValuesScript rtn = new ScriptValuesScript(scriptType, UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return rtn;
        }

        @Override
        public boolean validateTestObject(ScriptValuesScript testObject, Object actual) {
            if (!(actual instanceof ScriptValuesScript)) {
                return false;
            }
            return actual.toString().equals(testObject.toString());
        }
    }

    @Test
    public void testExtend() {
        ScriptValuesMetaMod meta = new ScriptValuesMetaMod();
        int size = 1;
        meta.extend(size);
        Assert.assertEquals(size, meta.getFieldname().length);
        Assert.assertNull(meta.getFieldname()[0]);
        Assert.assertEquals(size, meta.getRename().length);
        Assert.assertNull(meta.getRename()[0]);
        Assert.assertEquals(size, meta.getType().length);
        Assert.assertEquals((-1), meta.getType()[0]);
        Assert.assertEquals(size, meta.getLength().length);
        Assert.assertEquals((-1), meta.getLength()[0]);
        Assert.assertEquals(size, meta.getPrecision().length);
        Assert.assertEquals((-1), meta.getPrecision()[0]);
        Assert.assertEquals(size, meta.getReplace().length);
        Assert.assertFalse(meta.getReplace()[0]);
        meta = new ScriptValuesMetaMod();
        // set some values, uneven lengths
        Whitebox.setInternalState(meta, "fieldname", new String[]{ "Field 1", "Field 2", "Field 3" });
        Whitebox.setInternalState(meta, "rename", new String[]{ "Field 1 - new" });
        Whitebox.setInternalState(meta, "type", new int[]{ ValueMetaInterface.TYPE_STRING, ValueMetaInterface.TYPE_INTEGER, ValueMetaInterface.TYPE_NUMBER });
        meta.extend(3);
        validateExtended(meta);
    }
}

