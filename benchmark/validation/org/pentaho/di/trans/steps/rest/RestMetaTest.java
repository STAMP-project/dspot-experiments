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
package org.pentaho.di.trans.steps.rest;


import RestMeta.HTTP_METHOD_DELETE;
import RestMeta.HTTP_METHOD_GET;
import RestMeta.HTTP_METHOD_HEAD;
import RestMeta.HTTP_METHOD_OPTIONS;
import RestMeta.HTTP_METHOD_PATCH;
import RestMeta.HTTP_METHOD_POST;
import RestMeta.HTTP_METHOD_PUT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;
import org.pentaho.metastore.api.IMetaStore;


public class RestMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testLoadSaveRoundTrip() throws KettleException {
        List<String> attributes = Arrays.asList("applicationType", "method", "url", "urlInField", "dynamicMethod", "methodFieldName", "urlField", "bodyField", "httpLogin", "httpPassword", "proxyHost", "proxyPort", "preemptive", "trustStoreFile", "trustStorePassword", "headerField", "headerName", "parameterField", "parameterName", "matrixParameterField", "matrixParameterName", "fieldName", "resultCodeFieldName", "responseTimeFieldName", "responseHeaderFieldName");
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorAttributeMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        // Arrays need to be consistent length
        FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator = new ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 25);
        fieldLoadSaveValidatorAttributeMap.put("headerField", stringArrayLoadSaveValidator);
        fieldLoadSaveValidatorAttributeMap.put("headerName", stringArrayLoadSaveValidator);
        fieldLoadSaveValidatorAttributeMap.put("parameterField", stringArrayLoadSaveValidator);
        fieldLoadSaveValidatorAttributeMap.put("parameterName", stringArrayLoadSaveValidator);
        fieldLoadSaveValidatorAttributeMap.put("matrixParameterField", stringArrayLoadSaveValidator);
        fieldLoadSaveValidatorAttributeMap.put("matrixParameterName", stringArrayLoadSaveValidator);
        LoadSaveTester<RestMeta> loadSaveTester = new LoadSaveTester<RestMeta>(RestMeta.class, attributes, new HashMap<String, String>(), new HashMap<String, String>(), fieldLoadSaveValidatorAttributeMap, new HashMap<String, FieldLoadSaveValidator<?>>());
        loadSaveTester.testSerialization();
    }

    @Test
    public void testStepChecks() {
        RestMeta meta = new RestMeta();
        List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
        TransMeta transMeta = new TransMeta();
        StepMeta step = new StepMeta();
        RowMetaInterface prev = new RowMeta();
        RowMetaInterface info = new RowMeta();
        String[] input = new String[0];
        String[] output = new String[0];
        VariableSpace variables = new Variables();
        Repository repo = null;
        IMetaStore metaStore = null;
        // In a default configuration, it's expected that some errors will occur.
        // For this, we'll grab a baseline count of the number of errors
        // as the error count should decrease as we change configuration settings to proper values.
        remarks.clear();
        meta.check(remarks, transMeta, step, prev, input, output, info, variables, repo, metaStore);
        final int errorsDefault = RestMetaTest.getCheckResultErrorCount(remarks);
        Assert.assertTrue((errorsDefault > 0));
        // Setting the step to read the URL from a field should fix one of the check() errors
        meta.setUrlInField(true);
        meta.setUrlField("urlField");
        prev.addValueMeta(new ValueMetaString("urlField"));
        remarks.clear();
        meta.check(remarks, transMeta, step, prev, input, output, info, variables, repo, metaStore);
        int errorsCurrent = RestMetaTest.getCheckResultErrorCount(remarks);
        Assert.assertTrue((errorsDefault > errorsCurrent));
    }

    @Test
    public void testEntityEnclosingMethods() {
        Assert.assertTrue(RestMeta.isActiveBody(HTTP_METHOD_POST));
        Assert.assertTrue(RestMeta.isActiveBody(HTTP_METHOD_PUT));
        Assert.assertTrue(RestMeta.isActiveBody(HTTP_METHOD_PATCH));
        Assert.assertFalse(RestMeta.isActiveBody(HTTP_METHOD_GET));
        Assert.assertFalse(RestMeta.isActiveBody(HTTP_METHOD_DELETE));
        Assert.assertFalse(RestMeta.isActiveBody(HTTP_METHOD_HEAD));
        Assert.assertFalse(RestMeta.isActiveBody(HTTP_METHOD_OPTIONS));
    }
}

