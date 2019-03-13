package com.orientechnologies.lucene.analyzer;


import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;


/**
 * Created by frank on 30/10/2015.
 */
public class OLuceneAnalyzerFactoryTest {
    private OLuceneAnalyzerFactory analyzerFactory;

    private ODocument metadata;

    private OIndexDefinition indexDef;

    @Test
    public void shouldAssignStandardAnalyzerForIndexingUndefined() throws Exception {
        OLucenePerFieldAnalyzerWrapper analyzer = ((OLucenePerFieldAnalyzerWrapper) (analyzerFactory.createAnalyzer(indexDef, AnalyzerKind.INDEX, metadata)));
        // default analyzer for indexing
        assertThat(analyzer.getWrappedAnalyzer("undefined")).isInstanceOf(StandardAnalyzer.class);
    }

    @Test
    public void shouldAssignKeywordAnalyzerForIndexing() throws Exception {
        OLucenePerFieldAnalyzerWrapper analyzer = ((OLucenePerFieldAnalyzerWrapper) (analyzerFactory.createAnalyzer(indexDef, AnalyzerKind.INDEX, metadata)));
        // default analyzer for indexing
        assertThat(analyzer.getWrappedAnalyzer("genre")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.genre")).isInstanceOf(KeywordAnalyzer.class);
    }

    @Test
    public void shouldAssignConfiguredAnalyzerForIndexing() throws Exception {
        OLucenePerFieldAnalyzerWrapper analyzer = ((OLucenePerFieldAnalyzerWrapper) (analyzerFactory.createAnalyzer(indexDef, AnalyzerKind.INDEX, metadata)));
        assertThat(analyzer.getWrappedAnalyzer("title")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.title")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("author")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.author")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("lyrics")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.lyrics")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("description")).isInstanceOf(StandardAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.description")).isInstanceOf(StandardAnalyzer.class);
        StopwordAnalyzerBase description = ((StopwordAnalyzerBase) (analyzer.getWrappedAnalyzer("description")));
        assertThat(description.getStopwordSet()).isNotEmpty();
        assertThat(description.getStopwordSet()).hasSize(2);
        assertThat(description.getStopwordSet().contains("the")).isTrue();
        assertThat(description.getStopwordSet().contains("is")).isTrue();
    }

    @Test
    public void shouldAssignConfiguredAnalyzerForQuery() throws Exception {
        OLucenePerFieldAnalyzerWrapper analyzer = ((OLucenePerFieldAnalyzerWrapper) (analyzerFactory.createAnalyzer(indexDef, AnalyzerKind.QUERY, metadata)));
        assertThat(analyzer.getWrappedAnalyzer("title")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.title")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("author")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.author")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("genre")).isInstanceOf(StandardAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.genre")).isInstanceOf(StandardAnalyzer.class);
    }

    @Test
    public void shouldUseClassNameToPrefixFieldName() throws Exception {
        OLucenePerFieldAnalyzerWrapper analyzer = ((OLucenePerFieldAnalyzerWrapper) (analyzerFactory.createAnalyzer(indexDef, AnalyzerKind.QUERY, metadata)));
        assertThat(analyzer.getWrappedAnalyzer("Song.title")).isInstanceOf(EnglishAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.author")).isInstanceOf(KeywordAnalyzer.class);
        assertThat(analyzer.getWrappedAnalyzer("Song.genre")).isInstanceOf(StandardAnalyzer.class);
    }
}

