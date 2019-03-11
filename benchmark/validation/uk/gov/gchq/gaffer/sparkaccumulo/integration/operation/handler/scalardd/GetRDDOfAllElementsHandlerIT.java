/**
 * Copyright 2016-17 Crown Copyright
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
package uk.gov.gchq.gaffer.sparkaccumulo.integration.operation.handler.scalardd;


import AbstractGetRDDHandler.HADOOP_CONFIGURATION_KEY;
import com.google.common.collect.Sets;
import java.io.IOException;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.rdd.RDD;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.spark.operation.scalardd.GetRDDOfAllElements;
import uk.gov.gchq.gaffer.sparkaccumulo.operation.handler.AbstractGetRDDHandler;
import uk.gov.gchq.gaffer.user.User;


public class GetRDDOfAllElementsHandlerIT {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private enum KeyPackage {

        BYTE_ENTITY,
        CLASSIC;}

    private final User USER = new User();

    private final User USER_WITH_PUBLIC = new User("user1", Sets.newHashSet("public"));

    private final User USER_WITH_PUBLIC_AND_PRIVATE = new User("user2", Sets.newHashSet("public", "private"));

    private final String GRAPH_ID = "graphId";

    private Entity entityRetainedAfterValidation;

    @Test
    public void testGetAllElementsInRDD() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, TableNotFoundException, OperationException {
        testGetAllElementsInRDD(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperation());
        testGetAllElementsInRDD(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperation());
        testGetAllElementsInRDD(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDD(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDD(getGraphForDirectRDD(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY, "testGetAllElementsInRDD1"), getOperationWithDirectRDDOption());
        testGetAllElementsInRDD(getGraphForDirectRDD(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC, "testGetAllElementsInRDD2"), getOperationWithDirectRDDOption());
    }

    @Test
    public void testGetAllElementsInRDDWithView() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, TableNotFoundException, OperationException {
        testGetAllElementsInRDDWithView(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperation());
        testGetAllElementsInRDDWithView(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperation());
        testGetAllElementsInRDDWithView(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithView(getGraphForMockAccumulo(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithView(getGraphForDirectRDD(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY, "testGetAllElementsInRDDWithView1"), getOperationWithDirectRDDOption());
        testGetAllElementsInRDDWithView(getGraphForDirectRDD(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC, "testGetAllElementsInRDDWithView2"), getOperationWithDirectRDDOption());
    }

    @Test
    public void testGetAllElementsInRDDWithVisibilityFilteringApplied() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, TableNotFoundException, OperationException {
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForMockAccumuloWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperation());
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForMockAccumuloWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperation());
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForMockAccumuloWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForMockAccumuloWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForDirectRDDWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY, "testGetAllElementsInRDDWithVisibilityFilteringApplied1"), getOperationWithDirectRDDOption());
        testGetAllElementsInRDDWithVisibilityFilteringApplied(getGraphForDirectRDDWithVisibility(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC, "testGetAllElementsInRDDWithVisibilityFilteringApplied2"), getOperationWithDirectRDDOption());
    }

    @Test
    public void testGetAllElementsInRDDWithValidationApplied() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, TableNotFoundException, OperationException {
        testGetAllElementsInRDDWithValidationApplied(getGraphForMockAccumuloForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperation());
        testGetAllElementsInRDDWithValidationApplied(getGraphForMockAccumuloForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperation());
        testGetAllElementsInRDDWithValidationApplied(getGraphForMockAccumuloForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithValidationApplied(getGraphForMockAccumuloForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithValidationApplied(getGraphForDirectRDDForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY, "testGetAllElementsInRDDWithValidationApplied1"), getOperationWithDirectRDDOption());
        testGetAllElementsInRDDWithValidationApplied(getGraphForDirectRDDForValidationChecking(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC, "testGetAllElementsInRDDWithValidationApplied2"), getOperationWithDirectRDDOption());
    }

    @Test
    public void testGetAllElementsInRDDWithIngestAggregationApplied() throws IOException, InterruptedException, AccumuloException, AccumuloSecurityException, TableNotFoundException, OperationException {
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForMockAccumuloForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperation());
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForMockAccumuloForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperation());
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForMockAccumuloForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForMockAccumuloForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC), getOperationWithBatchScannerEnabled());
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForDirectRDDForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.BYTE_ENTITY, "testGetAllElementsInRDDWithIngestAggregationApplied1"), getOperationWithDirectRDDOption());
        testGetAllElementsInRDDWithIngestAggregationApplied(getGraphForDirectRDDForIngestAggregation(GetRDDOfAllElementsHandlerIT.KeyPackage.CLASSIC, "testGetAllElementsInRDDWithIngestAggregationApplied2"), getOperationWithDirectRDDOption());
    }

    @Test
    public void checkHadoopConfIsPassedThrough() throws IOException, OperationException {
        final Graph graph1 = new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").build()).addSchema(getClass().getResourceAsStream("/schema/elements.json")).addSchema(getClass().getResourceAsStream("/schema/types.json")).addSchema(getClass().getResourceAsStream("/schema/serialisation.json")).storeProperties(getClass().getResourceAsStream("/store.properties")).build();
        final User user = new User();
        final Configuration conf = new Configuration();
        conf.set("AN_OPTION", "A_VALUE");
        final String encodedConf = AbstractGetRDDHandler.convertConfigurationToString(conf);
        final GetRDDOfAllElements rddQuery = new GetRDDOfAllElements.Builder().option(HADOOP_CONFIGURATION_KEY, encodedConf).build();
        final RDD<Element> rdd = graph1.execute(rddQuery, user);
        Assert.assertEquals(encodedConf, rddQuery.getOption(HADOOP_CONFIGURATION_KEY));
        Assert.assertEquals("A_VALUE", rdd.sparkContext().hadoopConfiguration().get("AN_OPTION"));
    }
}

