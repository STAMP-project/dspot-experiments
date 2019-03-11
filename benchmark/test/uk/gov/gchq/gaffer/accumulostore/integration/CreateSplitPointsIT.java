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
package uk.gov.gchq.gaffer.accumulostore.integration;


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.generator.OneToOneElementGenerator;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.hdfs.operation.SampleDataForSplitPoints;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.TextJobInitialiser;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.TextMapperGenerator;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.SplitStoreFromFile;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class CreateSplitPointsIT {
    private static final String VERTEX_ID_PREFIX = "vertexId";

    public static final int NUM_ENTITIES = 100;

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private String inputDir;

    private String outputDir;

    public String splitsDir;

    public String splitsFile;

    @Test
    public void shouldAddElementsFromHdfs() throws Exception {
        // Given
        createInputFile();
        final CreateSplitPointsIT.SingleUseMockAccumuloStoreWithTabletServers store = new CreateSplitPointsIT.SingleUseMockAccumuloStoreWithTabletServers();
        store.initialise("graphId1", Schema.fromJson(StreamUtil.schemas(getClass())), AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(getClass())));
        final Graph graph = new Graph.Builder().store(store).build();
        // When
        graph.execute(new OperationChain.Builder().first(new SampleDataForSplitPoints.Builder().jobInitialiser(new TextJobInitialiser()).addInputMapperPair(inputDir, CreateSplitPointsIT.TextMapperGeneratorImpl.class.getName()).outputPath(outputDir).proportionToSample(1.0F).validate(true).mappers(5).splitsFilePath(splitsFile).compressionCodec(null).build()).then(new SplitStoreFromFile.Builder().inputPath(splitsFile).build()).build(), new User());
        // Then
        final List<Text> splitsOnTable = Lists.newArrayList(getConnection().tableOperations().listSplits(getTableName(), 10));
        final List<String> stringSplitsOnTable = Lists.transform(splitsOnTable, ( t) -> StringUtil.toString(t.getBytes()));
        final List<String> fileSplits = FileUtils.readLines(new File(splitsFile));
        final List<String> fileSplitsDecoded = Lists.transform(fileSplits, ( t) -> StringUtil.toString(Base64.decodeBase64(t)));
        Assert.assertEquals(fileSplitsDecoded, stringSplitsOnTable);
        Assert.assertEquals(2, splitsOnTable.size());
        Assert.assertEquals(((CreateSplitPointsIT.VERTEX_ID_PREFIX) + "53\u0000\u0001"), stringSplitsOnTable.get(0));
        Assert.assertEquals(((CreateSplitPointsIT.VERTEX_ID_PREFIX) + "99\u0000\u0001"), stringSplitsOnTable.get(1));
    }

    public static final class TextMapperGeneratorImpl extends TextMapperGenerator {
        public TextMapperGeneratorImpl() {
            super(new CreateSplitPointsIT.ExampleGenerator());
        }
    }

    public static final class ExampleGenerator implements OneToOneElementGenerator<String> {
        @Override
        public Element _apply(final String domainObject) {
            final String[] parts = domainObject.split(",");
            return new Entity(parts[0], parts[1]);
        }
    }

    private static final class SingleUseMockAccumuloStoreWithTabletServers extends SingleUseMockAccumuloStore {
        @Override
        public List<String> getTabletServers() throws StoreException {
            return Arrays.asList("1", "2", "3");
        }
    }
}

