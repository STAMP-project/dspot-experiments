/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.global;


import java.util.Collection;
import org.drools.core.common.EventFactHandle;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;


@Ignore
public class ConstrainedDataDictionaryTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = false;

    private static final String source = "org/kie/pmml/pmml_4_2/test_constr_data_dic.xml";

    private static final String packageName = "org.kie.pmml.pmml_4_2.test";

    @Test
    public void testContinuousDomainIntervals() throws Exception {
        FactType ivals = getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Intervalled");
        Assert.assertNotNull(ivals);
        Object data1 = ivals.newInstance();
        ivals.set(data1, "value", (-0.4));
        Object data2 = ivals.newInstance();
        ivals.set(data2, "value", 0.3);
        Object data3 = ivals.newInstance();
        ivals.set(data3, "value", 1.6);
        Object data4 = ivals.newInstance();
        ivals.set(data4, "value", 2.0);
        Object data5 = ivals.newInstance();
        ivals.set(data5, "value", 3.0);
        Object data6 = ivals.newInstance();
        ivals.set(data6, "value", 8.2);
        Object data7 = ivals.newInstance();
        ivals.set(data7, "value", 12.4);
        Object data8 = ivals.newInstance();
        ivals.set(data8, "value", 999.9);
        getKSession().insert(data1);
        getKSession().insert(data2);
        getKSession().insert(data3);
        getKSession().insert(data4);
        getKSession().insert(data5);
        getKSession().insert(data6);
        getKSession().insert(data7);
        getKSession().insert(data8);
        getKSession().fireAllRules();
        Assert.assertEquals(false, ivals.get(data1, "valid"));
        Assert.assertEquals(true, ivals.get(data2, "valid"));
        Assert.assertEquals(false, ivals.get(data3, "valid"));
        Assert.assertEquals(true, ivals.get(data4, "valid"));
        Assert.assertEquals(false, ivals.get(data5, "valid"));
        Assert.assertEquals(false, ivals.get(data6, "valid"));
        Assert.assertEquals(true, ivals.get(data7, "valid"));
        Assert.assertEquals(false, ivals.get(data8, "valid"));
        checkGeneratedRules();
    }

    @Test
    public void testProperties() throws Exception {
        getKSession().getEntryPoint("in_Vallued").insert(1);
        getKSession().getEntryPoint("in_Intervalled").insert(8.3);
        getKSession().getEntryPoint("in_Cat").insert("aa");
        getKSession().getEntryPoint("in_Sort").insert(1);
        getKSession().fireAllRules();
        Collection<EventFactHandle> fact1 = getKSession().getFactHandles(new org.kie.api.runtime.ClassObjectFilter(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Vallued").getFactClass()));
        Assert.assertEquals(1, fact1.size());
        // assertEquals(true, getKbase().getFactType(packageName, "Vallued").get(fact1.iterator().next().getObject(), "continuous"));
        Collection<EventFactHandle> fact2 = getKSession().getFactHandles(new org.kie.api.runtime.ClassObjectFilter(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Intervalled").getFactClass()));
        Assert.assertEquals(1, fact2.size());
        // assertEquals(true, getKbase().getFactType(packageName,"Intervalled").get(fact2.iterator().next().getObject(),"continuous"));
        Collection<EventFactHandle> fact3 = getKSession().getFactHandles(new org.kie.api.runtime.ClassObjectFilter(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Cat").getFactClass()));
        Assert.assertEquals(1, fact3.size());
        // assertEquals(true, getKbase().getFactType(packageName,"Cat").get(fact3.iterator().next().getObject(),"categorical"));
        Collection<EventFactHandle> fact4 = getKSession().getFactHandles(new org.kie.api.runtime.ClassObjectFilter(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Sort").getFactClass()));
        Assert.assertEquals(1, fact4.size());
        // assertEquals(true, getKbase().getFactType(packageName,"Sort").get(fact4.iterator().next().getObject(),"ordinal"));
        checkGeneratedRules();
    }

    @Test
    public void testContinuousDomainValues() throws Exception {
        FactType vals = getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Vallued");
        Assert.assertNotNull(vals);
        // FactType defval = getKbase().getFactType(packageName,"DefaultValid");
        // assertNotNull(vals);
        // FactType definv = getKbase().getFactType(packageName,"DefaultInvalid");
        // assertNotNull(vals);
        Object data1 = vals.newInstance();
        vals.set(data1, "value", 1);
        Object data2 = vals.newInstance();
        vals.set(data2, "value", 2);
        Object data3 = vals.newInstance();
        vals.set(data3, "value", 3);
        Object data0 = vals.newInstance();
        vals.set(data0, "value", 0);
        Object data99 = vals.newInstance();
        vals.set(data99, "value", (-1));
        getKSession().insert(data1);
        getKSession().insert(data2);
        getKSession().insert(data3);
        getKSession().insert(data0);
        getKSession().insert(data99);
        getKSession().fireAllRules();
        Assert.assertEquals(true, vals.get(data1, "valid"));
        Assert.assertEquals(false, vals.get(data1, "missing"));
        Assert.assertEquals(true, vals.get(data2, "valid"));
        Assert.assertEquals(false, vals.get(data2, "missing"));
        Assert.assertEquals(false, vals.get(data3, "valid"));
        Assert.assertEquals(false, vals.get(data3, "missing"));
        Assert.assertEquals(false, vals.get(data0, "valid"));
        Assert.assertEquals(true, vals.get(data0, "missing"));
        Assert.assertEquals(false, vals.get(data99, "valid"));
        Assert.assertEquals(false, vals.get(data99, "missing"));
        checkGeneratedRules();
    }

    @Test
    public void testContinuousDomainAsInsert() throws Exception {
        getKSession().getEntryPoint("in_Vallued").insert(1);
        getKSession().getEntryPoint("in_Intervalled").insert(8.3);
        getKSession().getEntryPoint("in_DefaultValid").insert(1);
        getKSession().getEntryPoint("in_DefaultInvalid").insert(1);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Vallued"), true, false, null, 1);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "Intervalled"), false, false, null, 8.3);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "DefaultValid"), true, false, null, 1);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(ConstrainedDataDictionaryTest.packageName, "DefaultInvalid"), false, false, null, 1);
        checkGeneratedRules();
    }
}

