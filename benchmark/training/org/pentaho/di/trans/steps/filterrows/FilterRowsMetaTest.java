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
package org.pentaho.di.trans.steps.filterrows;


import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class FilterRowsMetaTest {
    LoadSaveTester loadSaveTester;

    Class<FilterRowsMeta> testMetaClass = FilterRowsMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testClone() {
        FilterRowsMeta filterRowsMeta = new FilterRowsMeta();
        filterRowsMeta.setCondition(new Condition());
        filterRowsMeta.setTrueStepname("true");
        filterRowsMeta.setFalseStepname("false");
        FilterRowsMeta clone = ((FilterRowsMeta) (filterRowsMeta.clone()));
        Assert.assertNotNull(clone.getCondition());
        Assert.assertEquals("true", clone.getTrueStepname());
        Assert.assertEquals("false", clone.getFalseStepname());
    }

    @Test
    public void modifiedTarget() throws Exception {
        FilterRowsMeta filterRowsMeta = new FilterRowsMeta();
        StepMeta trueOutput = new StepMeta("true", new DummyTransMeta());
        StepMeta falseOutput = new StepMeta("false", new DummyTransMeta());
        filterRowsMeta.setCondition(new Condition());
        filterRowsMeta.setTrueStepname(trueOutput.getName());
        filterRowsMeta.setFalseStepname(falseOutput.getName());
        filterRowsMeta.searchInfoAndTargetSteps(ImmutableList.of(trueOutput, falseOutput));
        trueOutput.setName("true renamed");
        falseOutput.setName("false renamed");
        Assert.assertEquals("true renamed", filterRowsMeta.getTrueStepname());
        Assert.assertEquals("false renamed", filterRowsMeta.getFalseStepname());
    }
}

