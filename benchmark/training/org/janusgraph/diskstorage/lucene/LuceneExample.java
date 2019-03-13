/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.lucene;


import BooleanClause.Occur.FILTER;
import BooleanClause.Occur.MUST;
import BooleanClause.Occur.SHOULD;
import BooleanQuery.Builder;
import IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.janusgraph.core.attribute.Geoshape;
import org.junit.jupiter.api.Test;
import org.locationtech.spatial4j.context.SpatialContext;


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

    private final Map<String, SpatialStrategy> spatial = new HashMap<>();

    private final SpatialContext ctx = SpatialContext.GEO;

    @Test
    public void example1() throws Exception {
        Directory dir = FSDirectory.open(LuceneExample.path.toPath());
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(dir, iwc);
        indexDocs(writer, "doc1", ImmutableMap.of("name", "The laborious work of John Doe as we know it", "city", "Blumenkamp", "location", Geoshape.point(51.687882, 6.612053), "time", 1000342034));
        indexDocs(writer, "doc2", ImmutableMap.of("name", "Life as we know it or not", "city", "Essen", "location", Geoshape.point(51.787882, 6.712053), "time", (1000342034 - 500)));
        indexDocs(writer, "doc3", ImmutableMap.of("name", "Berlin - poor but sexy and a display of the extraordinary", "city", "Berlin", "location", Geoshape.circle(52.509535, 13.425293, 50), "time", (1000342034 + 2000)));
        writer.close();
        // Search
        IndexReader reader = DirectoryReader.open(FSDirectory.open(LuceneExample.path.toPath()));
        IndexSearcher searcher = new IndexSearcher(reader);
        // Auesee
        BooleanQuery.Builder filter = new BooleanQuery.Builder();
        SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, Geoshape.circle(51.666167, 6.58905, 450).getShape());
        filter.add(LongPoint.newRangeQuery("time", ((long) (1000342034)), ((long) (1000342034))), MUST);
        filter.add(new PrefixQuery(new Term("city_str", "B")), MUST);
        BooleanQuery.Builder qb = new BooleanQuery.Builder();
        qb.add(new MatchAllDocsQuery(), SHOULD);
        qb.add(filter.build(), FILTER);
        TopDocs docs = searcher.search(qb.build(), LuceneExample.MAX_RESULT);
        if ((docs.totalHits) >= (LuceneExample.MAX_RESULT))
            throw new RuntimeException(("Max results exceeded: " + (LuceneExample.MAX_RESULT)));

        Set<String> result = getResults(searcher, docs);
        System.out.println(result);
    }
}

