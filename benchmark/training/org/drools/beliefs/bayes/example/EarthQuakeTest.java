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
import org.drools.beliefs.bayes.BayesVariableState;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.JunctionTreeClique;
import org.drools.beliefs.bayes.JunctionTreeTest;
import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.junit.Test;


public class EarthQuakeTest {
    Graph<BayesVariable> graph = new BayesNetwork();

    GraphNode<BayesVariable> burglaryNode = graph.addNode();

    GraphNode<BayesVariable> earthquakeNode = graph.addNode();

    GraphNode<BayesVariable> alarmNode = graph.addNode();

    GraphNode<BayesVariable> johnCallsNode = graph.addNode();

    GraphNode<BayesVariable> maryCallsNode = graph.addNode();

    BayesVariable burglary = new BayesVariable<String>("Burglary", burglaryNode.getId(), new String[]{ "false", "true" }, new double[][]{ new double[]{ 0.001, 0.999 } });

    BayesVariable earthquake = new BayesVariable<String>("Earthquake", earthquakeNode.getId(), new String[]{ "false", "true" }, new double[][]{ new double[]{ 0.002, 0.998 } });

    BayesVariable alarm = new BayesVariable<String>("Alarm", alarmNode.getId(), new String[]{ "false", "true" }, new double[][]{ new double[]{ 0.95, 0.05 }, new double[]{ 0.94, 0.06 }, new double[]{ 0.29, 0.71 }, new double[]{ 0.001, 0.999 } });

    BayesVariable johnCalls = new BayesVariable<String>("JohnCalls", johnCallsNode.getId(), new String[]{ "false", "true" }, new double[][]{ new double[]{ 0.9, 0.1 }, new double[]{ 0.05, 0.95 } });

    BayesVariable maryCalls = new BayesVariable<String>("MaryCalls", maryCallsNode.getId(), new String[]{ "false", "true" }, new double[][]{ new double[]{ 0.7, 0.3 }, new double[]{ 0.01, 0.99 } });

    BayesVariableState burglaryState;

    BayesVariableState earthquakeState;

    BayesVariableState alarmState;

    BayesVariableState johnCallsState;

    BayesVariableState maryCallsState;

    JunctionTreeClique jtNode1;

    JunctionTreeClique jtNode2;

    JunctionTreeClique jtNode3;

    JunctionTree jTree;

    BayesInstance bayesInstance;

    @Test
    public void testInitialize() {
        // johnCalls
        JunctionTreeTest.assertArray(new double[]{ 0.9, 0.1, 0.05, 0.95 }, JunctionTreeTest.scaleDouble(3, jtNode1.getPotentials()));
        // maryCalls
        JunctionTreeTest.assertArray(new double[]{ 0.7, 0.3, 0.01, 0.99 }, JunctionTreeTest.scaleDouble(3, jtNode2.getPotentials()));
        // burglary, earthquake, alarm
        JunctionTreeTest.assertArray(new double[]{ 1.9E-6, 1.0E-7, 9.381E-4, 5.99E-5, 5.794E-4, 0.0014186, 9.97E-4, 0.996005 }, JunctionTreeTest.scaleDouble(7, jtNode3.getPotentials()));
    }

    @Test
    public void testNoEvidence() {
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 0.052139, 0.947861 }, JunctionTreeTest.scaleDouble(6, bayesInstance.marginalize("JohnCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.011736, 0.988264 }, JunctionTreeTest.scaleDouble(6, bayesInstance.marginalize("MaryCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.001, 0.999 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.002, 0.998 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.002516, 0.997484 }, JunctionTreeTest.scaleDouble(6, bayesInstance.marginalize("Alarm").getDistribution()));
    }

    @Test
    public void testAlarmEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("Alarm", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 0.9, 0.1 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.7, 0.3 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.374, 0.626 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.231, 0.769 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()));
    }

    @Test
    public void testEathQuakeEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("Earthquake", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 0.297, 0.703 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.211, 0.789 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.001, 0.999 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.291, 0.709 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()));
    }

    @Test
    public void testJoinCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("JohnCalls", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.04, 0.96 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.016, 0.984 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.011, 0.989 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.043, 0.957 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()));
    }

    @Test
    public void testEarthquakeAndJohnCallsEvidence() {
        BayesInstance bayesInstance = new BayesInstance(jTree);
        bayesInstance.setLikelyhood("JohnCalls", new double[]{ 1.0, 0.0 });
        bayesInstance.setLikelyhood("Earthquake", new double[]{ 1.0, 0.0 });
        bayesInstance.globalUpdate();
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("JohnCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.618, 0.382 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("MaryCalls").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.003, 0.997 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Burglary").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 1.0, 0.0 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Earthquake").getDistribution()));
        JunctionTreeTest.assertArray(new double[]{ 0.881, 0.119 }, JunctionTreeTest.scaleDouble(3, bayesInstance.marginalize("Alarm").getDistribution()));
    }
}

