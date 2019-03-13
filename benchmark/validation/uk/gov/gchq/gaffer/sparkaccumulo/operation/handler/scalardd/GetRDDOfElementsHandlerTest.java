/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.sparkaccumulo.operation.handler.scalardd;


import AbstractGetRDDHandler.HADOOP_CONFIGURATION_KEY;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.rdd.RDD;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.spark.operation.scalardd.GetRDDOfElements;
import uk.gov.gchq.gaffer.sparkaccumulo.operation.handler.AbstractGetRDDHandler;
import uk.gov.gchq.gaffer.user.User;


public class GetRDDOfElementsHandlerTest {
    private static final String ENTITY_GROUP = "BasicEntity";

    private static final String EDGE_GROUP = "BasicEdge";

    @Test
    public void checkGetCorrectElementsInRDDForEntityId() throws IOException, OperationException {
        final Graph graph1 = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").build()).addSchema(getClass().getResourceAsStream("/schema/elements.json")).addSchema(getClass().getResourceAsStream("/schema/types.json")).addSchema(getClass().getResourceAsStream("/schema/serialisation.json")).storeProperties(getClass().getResourceAsStream("/store.properties")).build();
        final List<Element> elements = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Entity entity = new Entity.Builder().group(ENTITY).vertex(("" + i)).build();
            final Edge edge1 = new Edge.Builder().group(EDGE).source(("" + i)).dest("B").directed(false).property("count", 2).build();
            final Edge edge2 = new Edge.Builder().group(EDGE).source(("" + i)).dest("C").directed(false).property("count", 4).build();
            elements.add(edge1);
            elements.add(edge2);
            elements.add(entity);
        }
        final User user = new User();
        graph1.execute(new AddElements.Builder().input(elements).build(), user);
        // Create Hadoop configuration and serialise to a string
        final Configuration configuration = new Configuration();
        final String configurationString = AbstractGetRDDHandler.convertConfigurationToString(configuration);
        // Check get correct edges for "1"
        GetRDDOfElements rddQuery = new GetRDDOfElements.Builder().input(new EntitySeed("1")).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        RDD<Element> rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        Set<Element> results = new HashSet<>();
        // NB: IDE suggests the cast in the following line is unnecessary but compilation fails without it
        Element[] returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        final Set<Element> expectedElements = new HashSet<>();
        final Entity entity1 = new Entity.Builder().group(ENTITY).vertex("1").build();
        final Edge edge1B = new Edge.Builder().group(EDGE).source("1").dest("B").directed(false).property("count", 2).build();
        final Edge edge1C = new Edge.Builder().group(EDGE).source("1").dest("C").directed(false).property("count", 4).build();
        expectedElements.add(entity1);
        expectedElements.add(edge1B);
        expectedElements.add(edge1C);
        Assert.assertEquals(expectedElements, results);
        // Check get correct edges for "1" when specify entities only
        rddQuery = new GetRDDOfElements.Builder().input(new EntitySeed("1")).view(new View.Builder().entity(GetRDDOfElementsHandlerTest.ENTITY_GROUP).build()).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        expectedElements.clear();
        expectedElements.add(entity1);
        Assert.assertEquals(expectedElements, results);
        // Check get correct edges for "1" when specify edges only
        rddQuery = new GetRDDOfElements.Builder().input(new EntitySeed("1")).view(new View.Builder().edge(GetRDDOfElementsHandlerTest.EDGE_GROUP).build()).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        expectedElements.clear();
        expectedElements.add(edge1B);
        expectedElements.add(edge1C);
        Assert.assertEquals(expectedElements, results);
        // Check get correct edges for "1" and "5"
        Set seeds = new HashSet<>();
        seeds.add(new EntitySeed("1"));
        seeds.add(new EntitySeed("5"));
        rddQuery = new GetRDDOfElements.Builder().input(seeds).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        results.addAll(Arrays.asList(returnedElements));
        final Entity entity5 = new Entity.Builder().group(ENTITY).vertex("5").build();
        final Edge edge5B = new Edge.Builder().group(EDGE).source("5").dest("B").directed(false).property("count", 2).build();
        final Edge edge5C = new Edge.Builder().group(EDGE).source("5").dest("C").directed(false).property("count", 4).build();
        expectedElements.clear();
        expectedElements.add(entity1);
        expectedElements.add(edge1B);
        expectedElements.add(edge1C);
        expectedElements.add(entity5);
        expectedElements.add(edge5B);
        expectedElements.add(edge5C);
        Assert.assertEquals(expectedElements, results);
    }

    @Test
    public void checkGetCorrectElementsInRDDForEdgeId() throws IOException, OperationException {
        final Graph graph1 = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").build()).addSchema(getClass().getResourceAsStream("/schema/elements.json")).addSchema(getClass().getResourceAsStream("/schema/types.json")).addSchema(getClass().getResourceAsStream("/schema/serialisation.json")).storeProperties(getClass().getResourceAsStream("/store.properties")).build();
        final List<Element> elements = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Entity entity = new Entity.Builder().group(ENTITY).vertex(("" + i)).build();
            final Edge edge1 = new Edge.Builder().group(EDGE).source(("" + i)).dest("B").directed(false).property("count", 2).build();
            final Edge edge2 = new Edge.Builder().group(EDGE).source(("" + i)).dest("C").directed(false).property("count", 4).build();
            elements.add(edge1);
            elements.add(edge2);
            elements.add(entity);
        }
        final User user = new User();
        graph1.execute(new AddElements.Builder().input(elements).build(), user);
        // Create Hadoop configuration and serialise to a string
        final Configuration configuration = new Configuration();
        final String configurationString = AbstractGetRDDHandler.convertConfigurationToString(configuration);
        // Check get correct edges for EdgeSeed 1 -> B
        GetRDDOfElements rddQuery = new GetRDDOfElements.Builder().input(new EdgeSeed("1", "B", false)).view(new View.Builder().edge(GetRDDOfElementsHandlerTest.EDGE_GROUP).build()).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        RDD<Element> rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        Set<Element> results = new HashSet<>();
        // NB: IDE suggests the cast in the following line is unnecessary but compilation fails without it
        Element[] returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        final Set<Element> expectedElements = new HashSet<>();
        final Edge edge1B = new Edge.Builder().group(EDGE).source("1").dest("B").directed(false).property("count", 2).build();
        expectedElements.add(edge1B);
        Assert.assertEquals(expectedElements, results);
        // Check get entity for 1 when query for 1 -> B and specify entities only
        rddQuery = new GetRDDOfElements.Builder().input(new EdgeSeed("1", "B", false)).view(new View.Builder().entity(GetRDDOfElementsHandlerTest.ENTITY_GROUP).build()).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        expectedElements.clear();
        final Entity entity1 = new Entity.Builder().group(ENTITY).vertex("1").build();
        expectedElements.add(entity1);
        Assert.assertEquals(expectedElements, results);
        // Check get correct edges for 1 -> B when specify edges only
        rddQuery = new GetRDDOfElements.Builder().input(new EdgeSeed("1", "B", false)).view(new View.Builder().edge(GetRDDOfElementsHandlerTest.EDGE_GROUP).build()).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        for (int i = 0; i < (returnedElements.length); i++) {
            results.add(returnedElements[i]);
        }
        expectedElements.clear();
        expectedElements.add(edge1B);
        Assert.assertEquals(expectedElements, results);
        // Check get correct edges for 1 -> B and 5 -> C
        rddQuery = new GetRDDOfElements.Builder().view(new View.Builder().edge(GetRDDOfElementsHandlerTest.EDGE_GROUP).build()).input(new EdgeSeed("1", "B", false), new EdgeSeed("5", "C", false)).build();
        rddQuery.addOption(HADOOP_CONFIGURATION_KEY, configurationString);
        rdd = graph1.execute(rddQuery, user);
        if (rdd == null) {
            Assert.fail("No RDD returned");
        }
        results.clear();
        returnedElements = ((Element[]) (rdd.collect()));
        results.addAll(Arrays.asList(returnedElements));
        final Edge edge5C = new Edge.Builder().group(EDGE).source("5").dest("C").directed(false).property("count", 4).build();
        expectedElements.clear();
        expectedElements.add(edge1B);
        expectedElements.add(edge5C);
        Assert.assertEquals(expectedElements, results);
    }

    @Test
    public void checkHadoopConfIsPassedThrough() throws IOException, OperationException {
        final Graph graph1 = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").build()).addSchema(getClass().getResourceAsStream("/schema/elements.json")).addSchema(getClass().getResourceAsStream("/schema/types.json")).addSchema(getClass().getResourceAsStream("/schema/serialisation.json")).storeProperties(getClass().getResourceAsStream("/store.properties")).build();
        final User user = new User();
        final Configuration conf = new Configuration();
        conf.set("AN_OPTION", "A_VALUE");
        final String encodedConf = AbstractGetRDDHandler.convertConfigurationToString(conf);
        final GetRDDOfElements rddQuery = new GetRDDOfElements.Builder().input(new EdgeSeed("1", "B", false)).option(HADOOP_CONFIGURATION_KEY, encodedConf).build();
        final RDD<Element> rdd = graph1.execute(rddQuery, user);
        Assert.assertEquals(encodedConf, rddQuery.getOption(HADOOP_CONFIGURATION_KEY));
        Assert.assertEquals("A_VALUE", rdd.sparkContext().hadoopConfiguration().get("AN_OPTION"));
    }
}

