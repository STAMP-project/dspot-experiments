/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.beliefs.bayes.integration;


import java.util.Arrays;
import java.util.Map;
import junit.framework.Assert;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.model.Bif;
import org.drools.beliefs.bayes.model.Definition;
import org.drools.beliefs.bayes.model.Network;
import org.drools.beliefs.bayes.model.Variable;
import org.drools.beliefs.bayes.model.XmlBifParser;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;

import static org.junit.Assert.assertNull;


public class ParserTest {
    @Test
    public void testSprinklerLoadBif() {
        Bif bif = ((Bif) (XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"))));
        Network network = bif.getNetwork();
        Assert.assertEquals("Garden", network.getName());
        Assert.assertEquals("package = org.drools.beliefs.bayes.integration", network.getProperties().get(0));
        Map<String, Variable> varMap = varToMap(network.getVariables());
        Assert.assertEquals(4, varMap.size());
        Variable var = varMap.get("WetGrass");
        Assert.assertEquals("WetGrass", var.getName());
        Assert.assertEquals(2, var.getOutComes().size());
        Assert.assertEquals(var.getOutComes(), Arrays.asList(new String[]{ "false", "true" }));
        Assert.assertEquals("position = (0,10)", var.getProperties().get(0));
        var = varMap.get("Cloudy");
        Assert.assertEquals("Cloudy", var.getName());
        Assert.assertEquals(2, var.getOutComes().size());
        Assert.assertEquals(var.getOutComes(), Arrays.asList(new String[]{ "false", "true" }));
        Assert.assertEquals("position = (0,-10)", var.getProperties().get(0));
        var = varMap.get("Sprinkler");
        Assert.assertEquals("Sprinkler", var.getName());
        Assert.assertEquals(2, var.getOutComes().size());
        Assert.assertEquals(var.getOutComes(), Arrays.asList(new String[]{ "false", "true" }));
        Assert.assertEquals("position = (13,0)", var.getProperties().get(0));
        var = varMap.get("Rain");
        Assert.assertEquals("Rain", var.getName());
        Assert.assertEquals(2, var.getOutComes().size());
        Assert.assertEquals(var.getOutComes(), Arrays.asList(new String[]{ "false", "true" }));
        Assert.assertEquals("position = (-12,0)", var.getProperties().get(0));
        Map<String, Definition> defMap = defToMap(network.getDefinitions());
        Assert.assertEquals(4, defMap.size());
        Definition def = defMap.get("WetGrass");
        Assert.assertEquals("WetGrass", def.getName());
        Assert.assertEquals(2, def.getGiven().size());
        Assert.assertEquals(def.getGiven(), Arrays.asList(new String[]{ "Sprinkler", "Rain" }));
        Assert.assertEquals("1.0 0.0 0.1 0.9 0.1 0.9 0.01 0.99", def.getProbabilities());
        def = defMap.get("Cloudy");
        Assert.assertEquals("Cloudy", def.getName());
        assertNull(def.getGiven());
        Assert.assertEquals("0.5 0.5", def.getProbabilities().trim());
        def = defMap.get("Sprinkler");
        Assert.assertEquals("Sprinkler", def.getName());
        Assert.assertEquals(1, def.getGiven().size());
        Assert.assertEquals("Cloudy", def.getGiven().get(0));
        Assert.assertEquals("0.5 0.5 0.9 0.1", def.getProbabilities().trim());
        def = defMap.get("Rain");
        Assert.assertEquals("Rain", def.getName());
        assertNull(def.getGiven());
        Assert.assertEquals("0.5 0.5", def.getProbabilities().trim());
    }

    @Test
    public void testSprinklerBuildBayesNework() {
        Bif bif = ((Bif) (XmlBifParser.loadBif(ParserTest.class.getResource("Garden.xmlbif"))));
        BayesNetwork network = XmlBifParser.buildBayesNetwork(bif);
        Map<String, GraphNode<BayesVariable>> map = nodeToMap(network);
        GraphNode<BayesVariable> node = map.get("WetGrass");
        BayesVariable wetGrass = node.getContent();
        Assert.assertEquals(Arrays.asList(new String[]{ "false", "true" }), Arrays.asList(wetGrass.getOutcomes()));
        Assert.assertEquals(2, wetGrass.getGiven().length);
        Assert.assertEquals(Arrays.asList(wetGrass.getGiven()), Arrays.asList(new String[]{ "Sprinkler", "Rain" }));
        Assert.assertTrue(Arrays.deepEquals(new double[][]{ new double[]{ 1.0, 0.0 }, new double[]{ 0.1, 0.9 }, new double[]{ 0.1, 0.9 }, new double[]{ 0.01, 0.99 } }, wetGrass.getProbabilityTable()));
        node = map.get("Sprinkler");
        BayesVariable sprinkler = node.getContent();
        Assert.assertEquals(Arrays.asList(new String[]{ "false", "true" }), Arrays.asList(sprinkler.getOutcomes()));
        Assert.assertEquals(1, sprinkler.getGiven().length);
        Assert.assertEquals("Cloudy", sprinkler.getGiven()[0]);
        Assert.assertTrue(Arrays.deepEquals(new double[][]{ new double[]{ 0.5, 0.5 }, new double[]{ 0.9, 0.1 } }, sprinkler.getProbabilityTable()));
        node = map.get("Cloudy");
        BayesVariable cloudy = node.getContent();
        Assert.assertEquals(Arrays.asList(new String[]{ "false", "true" }), Arrays.asList(cloudy.getOutcomes()));
        Assert.assertEquals(0, cloudy.getGiven().length);
        Assert.assertTrue(Arrays.deepEquals(new double[][]{ new double[]{ 0.5, 0.5 } }, cloudy.getProbabilityTable()));
        node = map.get("Rain");
        BayesVariable rain = node.getContent();
        Assert.assertEquals(Arrays.asList(new String[]{ "false", "true" }), Arrays.asList(rain.getOutcomes()));
        Assert.assertEquals(0, rain.getGiven().length);
        Assert.assertTrue(Arrays.deepEquals(new double[][]{ new double[]{ 0.5, 0.5 } }, rain.getProbabilityTable()));
    }
}

