package com.thinkaurelius.titan.diskstorage.lucene;


import BooleanClause.Occur.MUST;
import IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
import com.google.common.collect.ImmutableMap;
import com.spatial4j.core.context.SpatialContext;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class LuceneExample {
    public static final File path = new File("/tmp/lucene");

    private static final String STR_SUFFIX = "_str";

    private static final String TXT_SUFFIX = "_txt";

    private static final int MAX_RESULT = 10000;

    private Map<String, SpatialStrategy> spatial = new HashMap<String, SpatialStrategy>();

    private SpatialContext ctx = SpatialContext.GEO;

    @Test
    public void example1() throws Exception {
        Directory dir = FSDirectory.open(LuceneExample.path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
        iwc.setOpenMode(CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(dir, iwc);
        indexDocs(writer, "doc1", ImmutableMap.of("name", "The laborious work of John Doe as we know it", "city", "Blumenkamp", "location", Geoshape.point(51.687882, 6.612053), "time", 1000342034));
        indexDocs(writer, "doc2", ImmutableMap.of("name", "Life as we know it or not", "city", "Essen", "location", Geoshape.point(51.787882, 6.712053), "time", (1000342034 - 500)));
        indexDocs(writer, "doc3", ImmutableMap.of("name", "Berlin - poor but sexy and a display of the extraordinary", "city", "Berlin", "location", Geoshape.circle(52.509535, 13.425293, 50), "time", (1000342034 + 2000)));
        writer.close();
        // Search
        IndexReader reader = DirectoryReader.open(FSDirectory.open(LuceneExample.path));
        IndexSearcher searcher = new IndexSearcher(reader);
        analyzer = new StandardAnalyzer();
        // Auesee
        BooleanFilter filter = new BooleanFilter();
        // filter.add(new TermsFilter(new Term("name_txt","know")), BooleanClause.Occur.MUST);
        SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, Geoshape.circle(51.666167, 6.58905, 450).convert2Spatial4j());
        // filter.add(getSpatialStrategy("location").makeFilter(args), BooleanClause.Occur.MUST);
        filter.add(NumericRangeFilter.newLongRange("time", ((long) (1000342034)), ((long) (1000342034)), true, true), MUST);
        // filter.add(NumericRangeFilter.newLongRange("time",(long)1000342034-100,Long.MAX_VALUE,true,true), BooleanClause.Occur.MUST);
        // filter.add(NumericRangeFilter.newLongRange("time",Long.MIN_VALUE,(long)1000342034+300,true,true), BooleanClause.Occur.MUST);
        filter.add(new PrefixFilter(new Term("city_str", "B")), MUST);
        TopDocs docs = searcher.search(new MatchAllDocsQuery(), filter, LuceneExample.MAX_RESULT);
        if ((docs.totalHits) >= (LuceneExample.MAX_RESULT))
            throw new RuntimeException(("Max results exceeded: " + (LuceneExample.MAX_RESULT)));

        Set<String> result = getResults(searcher, docs);
        System.out.println(result);
    }
}

