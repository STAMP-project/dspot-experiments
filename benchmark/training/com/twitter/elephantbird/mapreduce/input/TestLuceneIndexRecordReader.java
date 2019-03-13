package com.twitter.elephantbird.mapreduce.input;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.easymock.EasyMockSupport;
import org.junit.Test;


/**
 *
 *
 * @author Alex Levenson
 */
public class TestLuceneIndexRecordReader extends EasyMockSupport {
    @Test
    @SuppressWarnings("unchecked")
    public void testOneIndexMultipleQueries() throws Exception {
        ArrayList<ArrayList<ArrayList<Integer>>> indexesQueriesDocIds = Lists.newArrayList();
        indexesQueriesDocIds.add(Lists.<ArrayList<Integer>>newArrayList(Lists.newArrayList(0, 1, 2), Lists.<Integer>newArrayList(), Lists.newArrayList(3, 4)));
        testLuceneIndexRecordReader(Lists.newArrayList("query1", "query2", "query3"), Lists.newArrayList(new Path("/mock/index")), indexesQueriesDocIds);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleIndexesOneQuery() throws Exception {
        ArrayList<ArrayList<ArrayList<Integer>>> indexesQueriesDocIds = Lists.newArrayList();
        ArrayList<ArrayList<Integer>> index1 = Lists.newArrayList();
        index1.add(Lists.newArrayList(0, 1, 2));
        ArrayList<ArrayList<Integer>> index2 = Lists.newArrayList();
        index2.add(Lists.<Integer>newArrayList());
        ArrayList<ArrayList<Integer>> index3 = Lists.newArrayList();
        index3.add(Lists.newArrayList(3, 4));
        indexesQueriesDocIds.add(index1);
        indexesQueriesDocIds.add(index2);
        indexesQueriesDocIds.add(index3);
        testLuceneIndexRecordReader(Lists.newArrayList("query1"), Lists.newArrayList(new Path("/mock/index1"), new Path("/mock/index2"), new Path("/mock/index3")), indexesQueriesDocIds);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleIndexesMultipleQueries() throws Exception {
        ArrayList<ArrayList<ArrayList<Integer>>> indexesQueriesDocIds = Lists.newArrayList();
        ArrayList<ArrayList<Integer>> index1 = Lists.newArrayList();
        index1.add(Lists.newArrayList(0, 1));
        index1.add(Lists.newArrayList(2));
        ArrayList<ArrayList<Integer>> index2 = Lists.newArrayList();
        index2.add(Lists.<Integer>newArrayList());
        index2.add(Lists.<Integer>newArrayList());
        ArrayList<ArrayList<Integer>> index3 = Lists.newArrayList();
        index3.add(Lists.newArrayList(3));
        index3.add(Lists.newArrayList(4));
        indexesQueriesDocIds.add(index1);
        indexesQueriesDocIds.add(index2);
        indexesQueriesDocIds.add(index3);
        testLuceneIndexRecordReader(Lists.newArrayList("query1", "query2"), Lists.newArrayList(new Path("/mock/index1"), new Path("/mock/index2"), new Path("/mock/index3")), indexesQueriesDocIds);
    }

    // Document is final and therefore not easy to mock
    // so this is the ugly work around
    private static final Document[] docs = new Document[]{ new Document(), new Document(), new Document(), new Document(), new Document() };

    private static final Map<Document, Integer> docsAndValues = ImmutableMap.of(TestLuceneIndexRecordReader.docs[0], 10, TestLuceneIndexRecordReader.docs[1], 11, TestLuceneIndexRecordReader.docs[2], 12, TestLuceneIndexRecordReader.docs[3], 13, TestLuceneIndexRecordReader.docs[4], 14);

    private abstract static class MockRecordReader extends LuceneIndexCollectAllRecordReader<IntWritable> {
        @Override
        protected IntWritable docToValue(Document doc) {
            return new IntWritable(Integer.valueOf(TestLuceneIndexRecordReader.docsAndValues.get(doc)));
        }

        @Override
        protected void closeIndexReader(IndexReader reader) throws IOException {
        }
    }
}

