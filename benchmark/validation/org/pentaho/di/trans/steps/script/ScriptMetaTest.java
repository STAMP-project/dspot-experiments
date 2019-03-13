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
package org.pentaho.di.trans.steps.script;


import java.util.Random;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class ScriptMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<ScriptMeta> testMetaClass = ScriptMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

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
}

