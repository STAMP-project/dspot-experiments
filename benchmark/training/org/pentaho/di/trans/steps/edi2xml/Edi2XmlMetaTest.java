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
package org.pentaho.di.trans.steps.edi2xml;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class Edi2XmlMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testStepMeta() throws KettleException {
        List<String> attributes = Arrays.asList("inputfield", "outputfield");
        Map<String, String> getterMap = new HashMap<String, String>();
        getterMap.put("inputfield", "getInputField");
        getterMap.put("outputfield", "getOutputField");
        Map<String, String> setterMap = new HashMap<String, String>();
        setterMap.put("inputfield", "setInputField");
        setterMap.put("outputfield", "setOutputField");
        LoadSaveTester loadSaveTester = new LoadSaveTester(Edi2XmlMeta.class, attributes, getterMap, setterMap);
        loadSaveTester.testSerialization();
    }
}

