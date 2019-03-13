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
package org.kie.pmml.pmml_4_2.predictive.models;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;


@Ignore
public class NeuralNetworkTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;

    private static final String source1 = "org/kie/pmml/pmml_4_2/test_ann_regression.xml";

    // 
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_ann_iris.xml";

    private static final String source22 = "org/kie/pmml/pmml_4_2/test_ann_iris_v2.xml";

    private static final String source23 = "org/kie/pmml/pmml_4_2/test_ann_iris_prediction.xml";

    private static final String source4 = "org/kie/pmml/pmml_4_2/test_ann_mixed_inputs2.xml";

    private static final String source3 = "org/kie/pmml/pmml_4_2/test_miningSchema.xml";

    private static final String source3c = "org/kie/pmml/pmml_4_2/test_annCommas.xml";

    private static final String source6 = "org/kie/pmml/pmml_4_2/mock_ptsd.xml";

    private static final String source7 = "org/kie/pmml/pmml_4_2/mock_cold.xml";

    private static final String source8 = "org/kie/pmml/pmml_4_2/mock_breastcancer.xml";

    private static final String source9 = "org/kie/pmml/pmml_4_2/test_nn_clax_output.xml";

    private static final String packageName = "org.kie.pmml.pmml_4_2.test";

    private static final String smartVent = "org/kie/pmml/pmml_4_2/smartvent.xml";

    @Test
    public void testANN() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source1, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        Assert.assertEquals(33, getNumAssertedSynapses());
        getKSession().getEntryPoint("in_Gender").insert("male");
        getKSession().getEntryPoint("in_NoOfClaims").insert("3");
        getKSession().getEntryPoint("in_Scrambled").insert(7);
        getKSession().getEntryPoint("in_Domicile").insert("urban");
        getKSession().getEntryPoint("in_AgeOfCar").insert(8.0);
        getKSession().fireAllRules();
        Thread.sleep(200);
        // System.err.println(reportWMObjects(getKSession()));
        Assert.assertEquals(828.0, Math.floor(queryDoubleField("OutAmOfClaims", "NeuralInsurance")), 0.0);
        checkGeneratedRules();
    }

    @Test
    public void testANNCompilation() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source3, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        checkGeneratedRules();
    }

    @Test
    public void testCold() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source7, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        getKSession().getEntryPoint("in_Temp").insert(28.0);
        getKSession().fireAllRules();
        Assert.assertEquals(0.44, queryDoubleField("Cold", "MockCold"), 1.0E-6);
        checkGeneratedRules();
    }

    @Test
    public void testIris() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source2, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        Assert.assertEquals(21, getNumAssertedSynapses());
        getKSession().getEntryPoint("in_PetalLen").insert(2.2);
        getKSession().getEntryPoint("in_PetalWid").insert(4.1);
        getKSession().getEntryPoint("in_SepalLen").insert(2.3);
        getKSession().getEntryPoint("in_SepalWid").insert(1.8);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        FactType t7 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_7");
        FactType t8 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_8");
        FactType t9 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_9");
        FactType s1 = getKbase().getFactType(NeuralNetworkTest.packageName, "Cspecies_virginica");
        Assert.assertEquals(0.001, truncN(getDoubleFieldValue(t7), 3), 1.0E-4);
        Assert.assertEquals(0.282, truncN(getDoubleFieldValue(t8), 3), 1.0E-4);
        Assert.assertEquals(0.716, truncN(getDoubleFieldValue(t9), 3), 1.0E-4);
        // Assert.assertEquals("virginica",
        // getFieldValue("Cspecies_virginica", "Test_MLP"));
        // Assert.assertEquals("Test_setosa",
        // getFieldValue("Cspecies_setosa", "Test_MLP"));
        // Assert.assertEquals("Test_versicolor",
        // getFieldValue("Cspecies_versicolor", "Test_MLP"));
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "SpecSetosa"), true, false, "Test_MLP", 0.001111);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "SpecVirgin"), true, false, "Test_MLP", 0.716639);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "SpecVersic"), true, false, "Test_MLP", 0.282249);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "SpecOut"), true, false, "Test_MLP", "virginica");
        checkGeneratedRules();
    }

    @Test
    public void testIris2() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source22, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        Assert.assertEquals(12, getNumAssertedSynapses());
        getKSession().getEntryPoint("in_PetalLen").insert(101);
        getKSession().getEntryPoint("in_PetalWid").insert(1);
        getKSession().getEntryPoint("in_SepalLen").insert(151);
        getKSession().getEntryPoint("in_SepalWid").insert(30);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        FactType t4 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_0");
        FactType t5 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_1");
        FactType t6 = getKbase().getFactType(NeuralNetworkTest.packageName, "Test_MLP_2");
        Assert.assertEquals(1.542, truncN(getDoubleFieldValue(t4), 3), 0.0);
        Assert.assertEquals(0.0, truncN(getDoubleFieldValue(t5), 3), 0.0);
        Assert.assertEquals(3.0, truncN(getDoubleFieldValue(t6), 3), 0.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "OutSpecies"), true, false, "Test_MLP", "versicolor");
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "OutProb"), true, false, "Test_MLP", 0.999999);
        checkGeneratedRules();
    }

    @Test
    public void testIris3() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source23, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        Assert.assertEquals(6, getNumAssertedSynapses());
        getKSession().getEntryPoint("in_PetalNum").insert(101);
        getKSession().getEntryPoint("in_PetalWid").insert(2);
        getKSession().getEntryPoint("in_Species").insert("virginica");
        getKSession().getEntryPoint("in_SepalWid").insert(30);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        Assert.assertEquals(24.0, queryIntegerField("OutSepLen", "Neuiris"), 0.0);
        checkGeneratedRules();
    }

    @Test
    public void testSimpleANN() throws Exception {
        // from mining schema test, simple network with fieldRef as output
        setKSession(getModelSession(NeuralNetworkTest.source3, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().getEntryPoint("in_Feat2").insert(4);
        getKSession().getEntryPoint("in_Feat1").insert(3.5);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "MockOutput2"), true, false, "Test_MLP", 1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "MockOutput1"), true, false, "Test_MLP", 0.0);
        checkGeneratedRules();
    }

    @Test
    public void testSimpleANNWithCommasInIds() throws Exception {
        // from mining schema test, simple network with fieldRef as output
        setKSession(getModelSession(NeuralNetworkTest.source3c, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().getEntryPoint("in_Feat2").insert(4);
        getKSession().getEntryPoint("in_Feat1").insert(3.5);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "MockOutput2"), true, false, "Test_MLP", 1.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "MockOutput1"), true, false, "Test_MLP", 0.0);
        checkGeneratedRules();
    }

    @Test
    public void testHeart() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source4, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        Assert.assertEquals(81, getNumAssertedSynapses());
        getKSession().getEntryPoint("in_Feat1").insert(83.0);
        getKSession().getEntryPoint("in_Feat2").insert(1.0);
        getKSession().getEntryPoint("in_Feat3").insert(5.0);
        getKSession().getEntryPoint("in_Feat4").insert("asympt");
        getKSession().getEntryPoint("in_Feat5").insert("yes");
        getKSession().getEntryPoint("in_Feat6").insert("t");
        getKSession().getEntryPoint("in_Feat7").insert(1.0);
        getKSession().getEntryPoint("in_Feat8").insert("normal");
        getKSession().getEntryPoint("in_Feat9").insert("male");
        getKSession().getEntryPoint("in_Feat10").insert("flat");
        getKSession().getEntryPoint("in_Feat11").insert("normal");
        getKSession().getEntryPoint("in_Feat12").insert(3.3);
        getKSession().getEntryPoint("in_Feat13").insert(2.5);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "OutN"), true, false, "HEART_MLP", ">50_1");
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "OutP"), true, false, "HEART_MLP", 0.943336);
        checkGeneratedRules();
    }

    @Test
    public void testOverride() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source3, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();
        getKSession().getEntryPoint("in_Feat1").insert(2.2);
        getKSession().fireAllRules();
        getKSession().getEntryPoint("in_Feat2").insert(5);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        FactType out1 = getKbase().getFactType("org.kie.pmml.pmml_4_2.test", "Out1");
        FactType out2 = getKbase().getFactType("org.kie.pmml.pmml_4_2.test", "Out2");
        FactType nump = getKbase().getFactType("org.kie.pmml.pmml_4_2.test", "Feat2");
        Assert.assertEquals(1, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(out1.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(out2.getFactClass())).size());
        Assert.assertEquals(2, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(nump.getFactClass())).size());
        getKSession().getEntryPoint("in_Feat1").insert(2.5);
        getKSession().getEntryPoint("in_Feat2").insert(6);
        getKSession().fireAllRules();
        // System.err.println(reportWMObjects(getKSession()));
        Assert.assertEquals(1, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(out1.getFactClass())).size());
        Assert.assertEquals(1, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(out2.getFactClass())).size());
        Assert.assertEquals(2, getKSession().getObjects(new org.kie.api.runtime.ClassObjectFilter(nump.getFactClass())).size());
        checkGeneratedRules();
    }

    @Test
    public void testSmartVent() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.smartVent, NeuralNetworkTest.VERBOSE));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        getKSession().getEntryPoint("in_PIP").insert(28.0);
        getKSession().getEntryPoint("in_PEEP").insert(5.0);
        getKSession().getEntryPoint("in_RATE").insert(30.0);
        getKSession().getEntryPoint("in_IT").insert(0.4);
        getKSession().getEntryPoint("in_Ph").insert(7.281);
        getKSession().getEntryPoint("in_CO2").insert(39.3);
        getKSession().getEntryPoint("in_PaO2").insert(126.0);
        getKSession().getEntryPoint("in_FIO2").insert(100.0);
        getKSession().fireAllRules();
        Assert.assertEquals(24.0, queryDoubleField("Out_sPIP", "SmartVent"), 0.5);
        Assert.assertEquals(5, queryDoubleField("Out_sPEEP", "SmartVent"), 0.1);
        Assert.assertEquals(30, queryDoubleField("Out_sRATE", "SmartVent"), 0.5);
        Assert.assertEquals(0.4, queryDoubleField("Out_sIT", "SmartVent"), 0.05);
        Assert.assertEquals((-1), queryDoubleField("Out_sFIO2", "SmartVent"), 0.05);
        getKSession().getEntryPoint("in_RATE").insert(20.0);
        getKSession().getEntryPoint("in_PaO2").insert(75.0);
        getKSession().getEntryPoint("in_Ph").insert(7.31);
        getKSession().getEntryPoint("in_CO2").insert(37.0);
        getKSession().getEntryPoint("in_IT").insert(0.4);
        getKSession().getEntryPoint("in_PIP").insert(20.0);
        getKSession().getEntryPoint("in_PEEP").insert(4.0);
        getKSession().getEntryPoint("in_FIO2").insert(38.0);
        getKSession().fireAllRules();
        Assert.assertEquals(18, queryDoubleField("Out_sPIP", "SmartVent"), 0.5);
        Assert.assertEquals(4.12, queryDoubleField("Out_sPEEP", "SmartVent"), 0.1);
        Assert.assertEquals(19, queryDoubleField("Out_sRATE", "SmartVent"), 0.5);
        Assert.assertEquals(0.4, queryDoubleField("Out_sIT", "SmartVent"), 0.05);
        Assert.assertEquals((-1), queryDoubleField("Out_sFIO2", "SmartVent"), 0.05);
        checkGeneratedRules();
    }

    @Test
    public void testClaxOutput() throws Exception {
        setKSession(getModelSession(NeuralNetworkTest.source9, true));
        setKbase(getKSession().getKieBase());
        getKSession().fireAllRules();// init model

        getKSession().getEntryPoint("in_Temp").insert(28.0);
        getKSession().fireAllRules();
        Thread.sleep(200);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "ColdCat"), true, false, "MockCold", "SURE");
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "ColdYES"), true, false, "MockCold", 0.6475435612444598);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(NeuralNetworkTest.packageName, "ColdNO"), true, false, "MockCold", 0.0036540476859388943);
        checkGeneratedRules();
    }
}

