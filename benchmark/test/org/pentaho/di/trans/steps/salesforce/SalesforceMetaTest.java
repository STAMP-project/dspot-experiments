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
package org.pentaho.di.trans.steps.salesforce;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SalesforceMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @SuppressWarnings("deprecation")
    @Test
    public void testBaseCheck() {
        SalesforceStepMeta meta = Mockito.mock(SalesforceStepMeta.class, Mockito.CALLS_REAL_METHODS);
        meta.setDefault();
        List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        boolean hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertTrue(hasError);
        remarks.clear();
        meta.setDefault();
        meta.setUsername("anonymous");
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertFalse(hasError);
        remarks.clear();
        meta.setDefault();
        meta.setTargetURL(null);
        meta.setUserName("anonymous");
        meta.setPassword("password");
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertTrue(hasError);
        remarks.clear();
        meta.setDefault();
        meta.setUsername("anonymous");
        meta.setModule(null);
        meta.check(remarks, null, null, null, null, null, null, null, null, null);
        hasError = false;
        for (CheckResultInterface cr : remarks) {
            if ((cr.getType()) == (CheckResult.TYPE_RESULT_ERROR)) {
                hasError = true;
            }
        }
        Assert.assertFalse(remarks.isEmpty());
        Assert.assertTrue(hasError);
    }
}

