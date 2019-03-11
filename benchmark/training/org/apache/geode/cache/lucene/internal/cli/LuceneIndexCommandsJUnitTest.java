/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.lucene.internal.cli;


import CliFunctionResult.StatusState;
import LuceneCliStrings.LUCENE_DESTROY_INDEX__MSG__COULD_NOT_FIND__MEMBERS_GREATER_THAN_VERSION_0;
import LuceneIndexStatus.INITIALIZED;
import LuceneIndexStatus.NOT_INITIALIZED;
import Status.OK;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.SystemUtils;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.lucene.LuceneSerializer;
import org.apache.geode.cache.lucene.internal.LuceneIndexStats;
import org.apache.geode.cache.lucene.internal.cli.functions.LuceneCreateIndexFunction;
import org.apache.geode.cache.lucene.internal.cli.functions.LuceneDescribeIndexFunction;
import org.apache.geode.cache.lucene.internal.cli.functions.LuceneListIndexFunction;
import org.apache.geode.cache.lucene.internal.repository.serializer.HeterogeneousLuceneSerializer;
import org.apache.geode.cache.lucene.internal.repository.serializer.PrimitiveSerializer;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.Version;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.execute.AbstractExecution;
import org.apache.geode.internal.util.CollectionUtils;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.management.internal.cli.i18n.CliStrings;
import org.apache.geode.management.internal.cli.result.CommandResult;
import org.apache.geode.management.internal.cli.result.TabularResultData;
import org.apache.geode.test.junit.categories.LuceneTest;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * The LuceneIndexCommandsJUnitTest class is a test suite of test cases testing the contract and
 * functionality of the LuceneIndexCommands class.
 *
 * @see LuceneIndexCommands
 * @see LuceneIndexDetails
 * @see LuceneListIndexFunction
 * @see org.junit.Test
 * @since GemFire 7.0
 */
@Category(LuceneTest.class)
@RunWith(JUnitParamsRunner.class)
public class LuceneIndexCommandsJUnitTest {
    private InternalCache mockCache;

    @Test
    public void testListIndexWithoutStats() {
        final String serverName = "mockServer";
        final AbstractExecution mockFunctionExecutor = Mockito.mock(AbstractExecution.class, "Function Executor");
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        String[] searchableFields = new String[]{ "field1", "field2", "field3" };
        Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
        fieldAnalyzers.put("field1", new StandardAnalyzer());
        fieldAnalyzers.put("field2", new KeywordAnalyzer());
        fieldAnalyzers.put("field3", null);
        LuceneSerializer serializer = new HeterogeneousLuceneSerializer();
        final LuceneIndexDetails indexDetails1 = createIndexDetails("memberFive", "/Employees", searchableFields, fieldAnalyzers, INITIALIZED, serverName, serializer);
        final LuceneIndexDetails indexDetails2 = createIndexDetails("memberSix", "/Employees", searchableFields, fieldAnalyzers, NOT_INITIALIZED, serverName, serializer);
        final LuceneIndexDetails indexDetails3 = createIndexDetails("memberTen", "/Employees", searchableFields, fieldAnalyzers, INITIALIZED, serverName, serializer);
        final List<Set<LuceneIndexDetails>> results = new ArrayList<>();
        results.add(CollectionUtils.asSet(indexDetails2, indexDetails1, indexDetails3));
        Mockito.when(mockFunctionExecutor.execute(ArgumentMatchers.any(LuceneListIndexFunction.class))).thenReturn(mockResultCollector);
        Mockito.when(mockResultCollector.getResult()).thenReturn(results);
        final LuceneIndexCommands commands = createIndexCommands(this.mockCache, mockFunctionExecutor);
        CommandResult result = ((CommandResult) (commands.listIndex(false)));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("Index Name")).isEqualTo(Arrays.asList("memberFive", "memberSix", "memberTen"));
        assertThat(data.retrieveAllValues("Region Path")).isEqualTo(Arrays.asList("/Employees", "/Employees", "/Employees"));
        assertThat(data.retrieveAllValues("Indexed Fields")).isEqualTo(Arrays.asList("[field1, field2, field3]", "[field1, field2, field3]", "[field1, field2, field3]"));
        assertThat(data.retrieveAllValues("Field Analyzer")).isEqualTo(Arrays.asList("{field1=StandardAnalyzer, field2=KeywordAnalyzer}", "{field1=StandardAnalyzer, field2=KeywordAnalyzer}", "{field1=StandardAnalyzer, field2=KeywordAnalyzer}"));
        assertThat(data.retrieveAllValues("Status")).isEqualTo(Arrays.asList("INITIALIZED", "NOT_INITIALIZED", "INITIALIZED"));
    }

    @Test
    public void testListIndexWithStats() {
        final String serverName = "mockServer";
        final AbstractExecution mockFunctionExecutor = Mockito.mock(AbstractExecution.class, "Function Executor");
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        final LuceneIndexStats mockIndexStats1 = getMockIndexStats(1, 10, 5, 1);
        final LuceneIndexStats mockIndexStats2 = getMockIndexStats(2, 20, 10, 2);
        final LuceneIndexStats mockIndexStats3 = getMockIndexStats(3, 30, 15, 3);
        String[] searchableFields = new String[]{ "field1", "field2", "field3" };
        Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
        fieldAnalyzers.put("field1", new StandardAnalyzer());
        fieldAnalyzers.put("field2", new KeywordAnalyzer());
        fieldAnalyzers.put("field3", null);
        LuceneSerializer serializer = new HeterogeneousLuceneSerializer();
        final LuceneIndexDetails indexDetails1 = createIndexDetails("memberFive", "/Employees", searchableFields, fieldAnalyzers, mockIndexStats1, INITIALIZED, serverName, serializer);
        final LuceneIndexDetails indexDetails2 = createIndexDetails("memberSix", "/Employees", searchableFields, fieldAnalyzers, mockIndexStats2, INITIALIZED, serverName, serializer);
        final LuceneIndexDetails indexDetails3 = createIndexDetails("memberTen", "/Employees", searchableFields, fieldAnalyzers, mockIndexStats3, INITIALIZED, serverName, serializer);
        final List<Set<LuceneIndexDetails>> results = new ArrayList<>();
        results.add(CollectionUtils.asSet(indexDetails2, indexDetails1, indexDetails3));
        Mockito.when(mockFunctionExecutor.execute(ArgumentMatchers.any(LuceneListIndexFunction.class))).thenReturn(mockResultCollector);
        Mockito.when(mockResultCollector.getResult()).thenReturn(results);
        final LuceneIndexCommands commands = createIndexCommands(this.mockCache, mockFunctionExecutor);
        CommandResult result = ((CommandResult) (commands.listIndex(true)));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("Index Name")).isEqualTo(Arrays.asList("memberFive", "memberSix", "memberTen"));
        assertThat(data.retrieveAllValues("Region Path")).isEqualTo(Arrays.asList("/Employees", "/Employees", "/Employees"));
        assertThat(data.retrieveAllValues("Indexed Fields")).isEqualTo(Arrays.asList("[field1, field2, field3]", "[field1, field2, field3]", "[field1, field2, field3]"));
        assertThat(data.retrieveAllValues("Field Analyzer")).isEqualTo(Arrays.asList("{field1=StandardAnalyzer, field2=KeywordAnalyzer}", "{field1=StandardAnalyzer, field2=KeywordAnalyzer}", "{field1=StandardAnalyzer, field2=KeywordAnalyzer}"));
        assertThat(data.retrieveAllValues("Query Executions")).isEqualTo(Arrays.asList("1", "2", "3"));
        assertThat(data.retrieveAllValues("Commits")).isEqualTo(Arrays.asList("10", "20", "30"));
        assertThat(data.retrieveAllValues("Updates")).isEqualTo(Arrays.asList("5", "10", "15"));
        assertThat(data.retrieveAllValues("Documents")).isEqualTo(Arrays.asList("1", "2", "3"));
        assertThat(data.retrieveAllValues("Serializer")).isEqualTo(Arrays.asList(HeterogeneousLuceneSerializer.class.getSimpleName(), HeterogeneousLuceneSerializer.class.getSimpleName(), HeterogeneousLuceneSerializer.class.getSimpleName()));
    }

    @Test
    public void testCreateIndex() throws Exception {
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class);
        final LuceneIndexCommands commands = Mockito.spy(createIndexCommands(this.mockCache, null));
        final List<CliFunctionResult> cliFunctionResults = new ArrayList<>();
        cliFunctionResults.add(new CliFunctionResult("member1", StatusState.OK, "Index Created"));
        cliFunctionResults.add(new CliFunctionResult("member2", StatusState.ERROR, "Index creation failed"));
        cliFunctionResults.add(new CliFunctionResult("member3", StatusState.OK, "Index Created"));
        Mockito.doReturn(mockResultCollector).when(commands).executeFunctionOnAllMembers(ArgumentMatchers.any(LuceneCreateIndexFunction.class), ArgumentMatchers.any(LuceneIndexInfo.class));
        Mockito.doReturn(cliFunctionResults).when(mockResultCollector).getResult();
        String indexName = "index";
        String regionPath = "regionPath";
        String[] searchableFields = new String[]{ "field1", "field2", "field3" };
        String[] fieldAnalyzers = new String[]{ StandardAnalyzer.class.getCanonicalName(), KeywordAnalyzer.class.getCanonicalName(), StandardAnalyzer.class.getCanonicalName() };
        String serializer = PrimitiveSerializer.class.getCanonicalName();
        CommandResult result = ((CommandResult) (commands.createIndex(indexName, regionPath, searchableFields, fieldAnalyzers, serializer)));
        assertThat(result.getStatus()).isEqualTo(OK);
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("Member")).isEqualTo(Arrays.asList("member1", "member2", "member3"));
        assertThat(data.retrieveAllValues("Status")).isEqualTo(Arrays.asList("Successfully created lucene index", "Failed: Index creation failed", "Successfully created lucene index"));
    }

    @Test
    public void testDescribeIndex() throws Exception {
        final String serverName = "mockServer";
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        final LuceneIndexCommands commands = Mockito.spy(createIndexCommands(this.mockCache, null));
        String[] searchableFields = new String[]{ "field1", "field2", "field3" };
        Map<String, Analyzer> fieldAnalyzers = new HashMap<>();
        fieldAnalyzers.put("field1", new StandardAnalyzer());
        fieldAnalyzers.put("field2", new KeywordAnalyzer());
        fieldAnalyzers.put("field3", null);
        final LuceneIndexStats mockIndexStats = getMockIndexStats(1, 10, 5, 1);
        final List<LuceneIndexDetails> indexDetails = new ArrayList<>();
        LuceneSerializer serializer = new HeterogeneousLuceneSerializer();
        indexDetails.add(createIndexDetails("memberFive", "/Employees", searchableFields, fieldAnalyzers, mockIndexStats, INITIALIZED, serverName, serializer));
        Mockito.doReturn(mockResultCollector).when(commands).executeFunctionOnRegion(ArgumentMatchers.any(LuceneDescribeIndexFunction.class), ArgumentMatchers.any(LuceneIndexInfo.class), ArgumentMatchers.eq(true));
        Mockito.doReturn(indexDetails).when(mockResultCollector).getResult();
        CommandResult result = ((CommandResult) (commands.describeIndex("memberFive", "/Employees")));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("Index Name")).isEqualTo(Collections.singletonList("memberFive"));
        assertThat(data.retrieveAllValues("Region Path")).isEqualTo(Collections.singletonList("/Employees"));
        assertThat(data.retrieveAllValues("Indexed Fields")).isEqualTo(Collections.singletonList("[field1, field2, field3]"));
        assertThat(data.retrieveAllValues("Field Analyzer")).isEqualTo(Collections.singletonList("{field1=StandardAnalyzer, field2=KeywordAnalyzer}"));
        assertThat(data.retrieveAllValues("Status")).isEqualTo(Collections.singletonList("INITIALIZED"));
        assertThat(data.retrieveAllValues("Query Executions")).isEqualTo(Collections.singletonList("1"));
        assertThat(data.retrieveAllValues("Commits")).isEqualTo(Collections.singletonList("10"));
        assertThat(data.retrieveAllValues("Updates")).isEqualTo(Collections.singletonList("5"));
        assertThat(data.retrieveAllValues("Documents")).isEqualTo(Collections.singletonList("1"));
    }

    @Test
    public void testSearchIndex() throws Exception {
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        final LuceneIndexCommands commands = Mockito.spy(createIndexCommands(this.mockCache, null));
        final List<Set<LuceneSearchResults>> queryResultsList = new ArrayList<>();
        HashSet<LuceneSearchResults> queryResults = new HashSet<>();
        queryResults.add(createQueryResults("A", "Result1", Float.valueOf("1.3")));
        queryResults.add(createQueryResults("B", "Result1", Float.valueOf("1.2")));
        queryResults.add(createQueryResults("C", "Result1", Float.valueOf("1.1")));
        queryResultsList.add(queryResults);
        Mockito.doReturn(mockResultCollector).when(commands).executeSearch(ArgumentMatchers.any(LuceneQueryInfo.class));
        Mockito.doReturn(queryResultsList).when(mockResultCollector).getResult();
        CommandResult result = ((CommandResult) (commands.searchIndex("index", "region", "Result1", "field1", (-1), false)));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("key")).isEqualTo(Arrays.asList("A", "B", "C"));
        assertThat(data.retrieveAllValues("value")).isEqualTo(Arrays.asList("Result1", "Result1", "Result1"));
        assertThat(data.retrieveAllValues("score")).isEqualTo(Arrays.asList("1.3", "1.2", "1.1"));
    }

    @Test
    public void testSearchIndexWithKeysOnly() throws Exception {
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        final LuceneIndexCommands commands = Mockito.spy(createIndexCommands(this.mockCache, null));
        final List<Set<LuceneSearchResults>> queryResultsList = new ArrayList<>();
        HashSet<LuceneSearchResults> queryResults = new HashSet<>();
        queryResults.add(createQueryResults("A", "Result1", Float.valueOf("1.3")));
        queryResults.add(createQueryResults("B", "Result1", Float.valueOf("1.2")));
        queryResults.add(createQueryResults("C", "Result1", Float.valueOf("1.1")));
        queryResultsList.add(queryResults);
        Mockito.doReturn(mockResultCollector).when(commands).executeSearch(ArgumentMatchers.any(LuceneQueryInfo.class));
        Mockito.doReturn(queryResultsList).when(mockResultCollector).getResult();
        CommandResult result = ((CommandResult) (commands.searchIndex("index", "region", "Result1", "field1", (-1), true)));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("key")).isEqualTo(Arrays.asList("A", "B", "C"));
    }

    @Test
    public void testSearchIndexWhenSearchResultsHaveSameScore() throws Exception {
        final ResultCollector mockResultCollector = Mockito.mock(ResultCollector.class, "ResultCollector");
        final LuceneIndexCommands commands = Mockito.spy(createIndexCommands(this.mockCache, null));
        final List<Set<LuceneSearchResults>> queryResultsList = new ArrayList<>();
        HashSet<LuceneSearchResults> queryResults = new HashSet<>();
        queryResults.add(createQueryResults("A", "Result1", 1));
        queryResults.add(createQueryResults("B", "Result1", 1));
        queryResults.add(createQueryResults("C", "Result1", 1));
        queryResults.add(createQueryResults("D", "Result1", 1));
        queryResults.add(createQueryResults("E", "Result1", 1));
        queryResults.add(createQueryResults("F", "Result1", 1));
        queryResults.add(createQueryResults("G", "Result1", 1));
        queryResults.add(createQueryResults("H", "Result1", 1));
        queryResults.add(createQueryResults("I", "Result1", 1));
        queryResults.add(createQueryResults("J", "Result1", 1));
        queryResults.add(createQueryResults("K", "Result1", 1));
        queryResults.add(createQueryResults("L", "Result1", 1));
        queryResults.add(createQueryResults("M", "Result1", 1));
        queryResults.add(createQueryResults("N", "Result1", 1));
        queryResults.add(createQueryResults("P", "Result1", 1));
        queryResults.add(createQueryResults("Q", "Result1", 1));
        queryResults.add(createQueryResults("R", "Result1", 1));
        queryResults.add(createQueryResults("S", "Result1", 1));
        queryResults.add(createQueryResults("T", "Result1", 1));
        queryResultsList.add(queryResults);
        Mockito.doReturn(mockResultCollector).when(commands).executeSearch(ArgumentMatchers.any(LuceneQueryInfo.class));
        Mockito.doReturn(queryResultsList).when(mockResultCollector).getResult();
        CommandResult result = ((CommandResult) (commands.searchIndex("index", "region", "Result1", "field1", (-1), true)));
        TabularResultData data = ((TabularResultData) (result.getResultData()));
        assertThat(data.retrieveAllValues("key").size()).isEqualTo(queryResults.size());
    }

    @Test
    public void testDestroySingleIndexNoRegionMembers() {
        LuceneIndexCommands commands = createTestLuceneIndexCommandsForDestroyIndex();
        final List<CliFunctionResult> cliFunctionResults = new ArrayList<>();
        String expectedStatus = (CliStrings.format(LUCENE_DESTROY_INDEX__MSG__COULD_NOT_FIND__MEMBERS_GREATER_THAN_VERSION_0, new Object[]{ Version.GEODE_1_7_0 })) + (SystemUtils.LINE_SEPARATOR);
        cliFunctionResults.add(new CliFunctionResult("member0", StatusState.OK));
        Mockito.doReturn(Collections.emptySet()).when(commands).getNormalMembersWithSameOrNewerVersion(ArgumentMatchers.any());
        CommandResult result = ((CommandResult) (commands.destroyIndex("index", "regionPath")));
        verifyDestroyIndexCommandResult(result, cliFunctionResults, expectedStatus);
    }

    @Test
    public void testDestroyAllIndexesNoRegionMembers() {
        LuceneIndexCommands commands = createTestLuceneIndexCommandsForDestroyIndex();
        Mockito.doReturn(Collections.emptySet()).when(commands).getNormalMembersWithSameOrNewerVersion(ArgumentMatchers.any());
        final List<CliFunctionResult> cliFunctionResults = new ArrayList<>();
        String expectedStatus = (CliStrings.format(LUCENE_DESTROY_INDEX__MSG__COULD_NOT_FIND__MEMBERS_GREATER_THAN_VERSION_0, new Object[]{ Version.GEODE_1_7_0 })) + (SystemUtils.LINE_SEPARATOR);
        cliFunctionResults.add(new CliFunctionResult("member0", StatusState.OK));
        CommandResult result = ((CommandResult) (commands.destroyIndex(null, "regionPath")));
        verifyDestroyIndexCommandResult(result, cliFunctionResults, expectedStatus);
    }

    private static class LuceneTestIndexCommands extends LuceneIndexCommands {
        private final Execution functionExecutor;

        LuceneTestIndexCommands(final InternalCache cache, final Execution functionExecutor) {
            assert cache != null : "The InternalCache cannot be null!";
            setCache(cache);
            this.functionExecutor = functionExecutor;
        }

        @Override
        public Set<DistributedMember> getAllMembers() {
            return Collections.emptySet();
        }

        @Override
        public Execution getMembersFunctionExecutor(final Set<DistributedMember> members) {
            assertThat(members).isNotNull();
            return this.functionExecutor;
        }
    }
}

