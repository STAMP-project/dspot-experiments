package com.orientechnologies.lucene.tests;


import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Test;


/**
 * Created by frank on 18/11/2016.
 */
public class OLucenePhraseQueriesTest extends OLuceneBaseTest {
    @Test
    public void testPhraseQueries() throws Exception {
        OResultSet vertexes = db.command("select from Role where search_class(\' \"Business Owner\" \')=true  ");
        assertThat(vertexes).hasSize(1);
        vertexes = db.command("select from Role where search_class( \' \"Owner of Business\" \')=true  ");
        assertThat(vertexes).hasSize(0);
        vertexes = db.command("select from Role where search_class(\' \"System Owner\" \'  )=true  ");
        assertThat(vertexes).hasSize(0);
        vertexes = db.command("select from Role where search_class(\' \"System SME\"~1 \'  )=true  ");
        assertThat(vertexes).hasSize(2);
        vertexes = db.command("select from Role where search_class(\' \"System Business\"~1 \'  )=true  ");
        assertThat(vertexes).hasSize(2);
        vertexes = db.command("select from Role where search_class(' /[mb]oat/ '  )=true  ");
        assertThat(vertexes).hasSize(2);
    }

    @Test
    public void testComplexPhraseQueries() throws Exception {
        OResultSet vertexes = db.command("select from Role where search_class(?)=true", "\"System SME\"~1");
        assertThat(vertexes).allMatch(( v) -> v.<String>getProperty("name").contains("SME"));
        vertexes = db.command("select from Role where search_class(? )=true", "\"SME System\"~1");
        assertThat(vertexes).isEmpty();
        vertexes = db.command("select from Role where search_class(?) =true", "\"Owner Of Business\"");
        vertexes.stream().forEach(( v) -> System.out.println(("v = " + (v.getProperty("name")))));
        assertThat(vertexes).isEmpty();
        vertexes = db.command("select from Role where search_class(? )=true", "\"System Business SME\"");
        assertThat(vertexes).hasSize(1).allMatch(( v) -> v.<String>getProperty("name").equalsIgnoreCase("System Business SME"));
        vertexes = db.command("select from Role where search_class(? )=true", "\"System Owner\"~1 -IT");
        assertThat(vertexes).hasSize(1).allMatch(( v) -> v.<String>getProperty("name").equalsIgnoreCase("System Business Owner"));
        vertexes = db.command("select from Role where search_class(? )=true", "+System +Own*~0.0 -IT");
        assertThat(vertexes).hasSize(1).allMatch(( v) -> v.<String>getProperty("name").equalsIgnoreCase("System Business Owner"));
        vertexes = db.command("select from Role where search_class(? )=true", "\"System Owner\"~1 -Business");
        assertThat(vertexes).hasSize(1).allMatch(( v) -> v.<String>getProperty("name").equalsIgnoreCase("System IT Owner"));
    }
}

