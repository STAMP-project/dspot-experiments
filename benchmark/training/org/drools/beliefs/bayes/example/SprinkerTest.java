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
package org.drools.beliefs.bayes.example;


import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.BayesVariable;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.JunctionTreeClique;
import org.drools.beliefs.bayes.JunctionTreeTest;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;


public class SprinkerTest {
    Graph<BayesVariable> graph = new BayesNetwork();

    GraphNode<BayesVariable> cloudyNode = graph.addNode();

    GraphNode<BayesVariable> sprinklerNode = graph.addNode();

    GraphNode<BayesVariable> rainNode = graph.addNode();

    GraphNode<BayesVariable> wetGrassNode = graph.addNode();

    BayesVariable cloudy = new BayesVariable<String>("Cloudy", cloudyNode.getId(), new String[]{ "true", "false" }, new double[][]{ new double[]{ 0.5, 0.5 } });

    BayesVariable sprinkler = new BayesVariable<String>("Sprinkler", sprinklerNode.getId(), new String[]{ "true", "false" }, new double[][]{ new double[]{ 0.5, 0.5 }, new double[]{ 0.9, 0.1 } });

    BayesVariable rain = new BayesVariable<String>("Rain", rainNode.getId(), new String[]{ "true", "false" }, new double[][]{ new double[]{ 0.8, 0.2 }, new double[]{ 0.2, 0.8 } });

    BayesVariable wetGrass = new BayesVariable<String>("WetGrass", wetGrassNode.getId(), new String[]{ "true", "false" }, new double[][]{ new double[]{ 1.0, 0.0 }, new double[]{ 0.1, 0.9 }, new double[]{ 0.1, 0.9 }, new double[]{ 0.01, 0.99 } });

    JunctionTree jTree;

    @Test
    public void testInitialize() {
        JunctionTreeClique jtNode = jTree.getRoot();
        // cloud, rain sprinkler
        JunctionTreeTest.assertArray(new double[]{ 0.2, 0.05, 0.2, 0.05, 0.09, 0.36, 0.01, 0.04 }, JunctionTreeTest.scaleDouble(3, jtNode.getPotentials()));
        // wetGrass
        jtNode = jTree.getRoot().getChildren().get(0).getChild();
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0, 0.1, 0.9, 0.1, 0.9, 0.01, 0.99 }, JunctionTreeTest.scaleDouble(3, jtNode.getPotentials()));
    }

    @Test
    public void testNoEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTree jTree = jtBuilder.build();
        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 0.5, 0.5 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.5, 0.5 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Rain").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.7, 0.3 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.353, 0.647 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("WetGrass").getDistribution()));
    }

    @Test
    public void testGrassWetEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTree jTree = jtBuilder.build();
        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("WetGrass", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 0.639, 0.361 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.881, 0.119 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Rain").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.938, 0.062 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("WetGrass").getDistribution()));
    }

    @Test
    public void testSprinklerEvidence() {
        JunctionTreeBuilder jtBuilder = new JunctionTreeBuilder(graph);
        JunctionTree jTree = jtBuilder.build();
        JunctionTreeClique jtNode = jTree.getRoot();
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("Sprinkler", new double[]{ 1.0, 0.0 });
        bayesInstance.setLikelyhood("Cloudy", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Cloudy").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.8, 0.2 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Rain").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Sprinkler").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.82, 0.18 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("WetGrass").getDistribution()));
    }
}

