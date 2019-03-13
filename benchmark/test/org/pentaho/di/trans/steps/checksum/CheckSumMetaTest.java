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
package org.pentaho.di.trans.steps.checksum;


import CheckSumMeta.TYPE_ADLER32;
import CheckSumMeta.TYPE_CRC32;
import CheckSumMeta.TYPE_MD5;
import CheckSumMeta.TYPE_SHA1;
import CheckSumMeta.TYPE_SHA256;
import CheckSumMeta.checksumtypeCodes;
import CheckSumMeta.checksumtypeCodes.length;
import CheckSumMeta.resultTypeCode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

import static CheckSumMeta.checksumtypeCodes;


public class CheckSumMetaTest implements InitializerInterface<CheckSumMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testConstants() {
        Assert.assertEquals("CRC32", TYPE_CRC32);
        Assert.assertEquals("CRC32", checksumtypeCodes[0]);
        Assert.assertEquals("ADLER32", TYPE_ADLER32);
        Assert.assertEquals("ADLER32", checksumtypeCodes[1]);
        Assert.assertEquals("MD5", TYPE_MD5);
        Assert.assertEquals("MD5", checksumtypeCodes[2]);
        Assert.assertEquals("SHA-1", TYPE_SHA1);
        Assert.assertEquals("SHA-1", checksumtypeCodes[3]);
        Assert.assertEquals("SHA-256", TYPE_SHA256);
        Assert.assertEquals("SHA-256", checksumtypeCodes[4]);
        Assert.assertEquals(length, CheckSumMeta.checksumtypeDescs.length);
    }

    @Test
    public void testSerialization() throws KettleException {
        List<String> attributes = Arrays.asList("FieldName", "ResultFieldName", "CheckSumType", "CompatibilityMode", "ResultType", "oldChecksumBehaviour");
        Map<String, String> getterMap = new HashMap<String, String>();
        Map<String, String> setterMap = new HashMap<String, String>();
        getterMap.put("CheckSumType", "getTypeByDesc");
        FieldLoadSaveValidator<String[]> stringArrayLoadSaveValidator = new org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 5);
        Map<String, FieldLoadSaveValidator<?>> attrValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        attrValidatorMap.put("FieldName", stringArrayLoadSaveValidator);
        attrValidatorMap.put("CheckSumType", new org.pentaho.di.trans.steps.loadsave.validator.IntLoadSaveValidator(checksumtypeCodes.length));
        attrValidatorMap.put("ResultType", new org.pentaho.di.trans.steps.loadsave.validator.IntLoadSaveValidator(resultTypeCode.length));
        Map<String, FieldLoadSaveValidator<?>> typeValidatorMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        LoadSaveTester<CheckSumMeta> loadSaveTester = new LoadSaveTester(CheckSumMeta.class, attributes, getterMap, setterMap, attrValidatorMap, typeValidatorMap, this);
        loadSaveTester.testSerialization();
    }
}

