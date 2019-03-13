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
package org.pentaho.di.trans.steps.datagrid;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class DataGridMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<DataGridMeta> testMetaClass = DataGridMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class DataGridLinesLoadSaveValidator implements FieldLoadSaveValidator<List<List<String>>> {
        final Random rand = new Random();

        @Override
        public List<List<String>> getTestObject() {
            List<List<String>> dataLinesList = new ArrayList<List<String>>();
            for (int i = 0; i < 3; i++) {
                List<String> dl = new ArrayList<String>();
                dl.add(("line" + ((i * 2) + 1)));
                dl.add(("line" + ((i * 2) + 2)));
                dl.add(("line" + ((i * 2) + 3)));
                dataLinesList.add(dl);
            }
            return dataLinesList;
        }

        @Override
        public boolean validateTestObject(List<List<String>> testObject, Object actual) {
            if (!(actual instanceof List<?>)) {
                return false;
            }
            boolean rtn = true;
            List<?> act0 = ((List<?>) (actual));
            Assert.assertTrue(((act0.size()) == 3));
            Object obj0 = act0.get(0);
            Assert.assertTrue((obj0 instanceof List<?>));// establishes list of lists

            List<?> act1 = act0;
            rtn = rtn && ((act1.size()) == 3);
            Object obj2 = act1.get(0);
            rtn = rtn && (obj2 instanceof List<?>);
            List<?> obj3 = ((List<?>) (obj2));
            rtn = rtn && ((obj3.size()) == 3);
            List<List<String>> realActual = ((List<List<String>>) (actual));
            for (int i = 0; i < (realActual.size()); i++) {
                List<String> metaList = realActual.get(i);
                List<String> testList = testObject.get(i);
                rtn = rtn && ((metaList.size()) == (testList.size()));
                for (int j = 0; j < (metaList.size()); j++) {
                    rtn = rtn && (metaList.get(j).equals(testList.get(j)));
                }
            }
            return rtn;
        }
    }
}

