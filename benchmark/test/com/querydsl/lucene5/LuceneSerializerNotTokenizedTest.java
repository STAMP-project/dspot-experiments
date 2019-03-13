/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.lucene5;


import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.RAMDirectory;
import org.joda.time.LocalDate;
import org.junit.Test;
import person.birthDate;
import person.id;
import person.name;


public class LuceneSerializerNotTokenizedTest {
    private RAMDirectory idx;

    private IndexWriter writer;

    private IndexSearcher searcher;

    private LuceneSerializer serializer;

    private final QueryMetadata metadata = new DefaultQueryMetadata();

    private final Person clooney = new Person("actor_1", "George Clooney", new LocalDate(1961, 4, 6));

    private final Person pitt = new Person("actor_2", "Brad Pitt", new LocalDate(1963, 12, 18));

    @Test
    public void equals_by_id_matches() throws Exception {
        testQuery(id.eq("actor_1"), "id:actor_1", 1);
    }

    @Test
    public void equals_by_id_does_not_match() throws Exception {
        testQuery(id.eq("actor_8"), "id:actor_8", 0);
    }

    @Test
    public void equals_by_name_matches() throws Exception {
        testQuery(name.eq("George Clooney"), "name:George Clooney", 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void equals_by_name_ignoring_case_does_not_match() throws Exception {
        testQuery(name.equalsIgnoreCase("george clooney"), "name:george clooney", 0);
    }

    @Test
    public void equals_by_name_does_not_match() throws Exception {
        testQuery(name.eq("George Looney"), "name:George Looney", 0);
    }

    @Test
    public void starts_with_name_should_match() throws Exception {
        testQuery(name.startsWith("George C"), "name:George C*", 1);
    }

    @Test
    public void starts_with_name_should_not_match() throws Exception {
        testQuery(name.startsWith("George L"), "name:George L*", 0);
    }

    @Test
    public void ends_with_name_should_match() throws Exception {
        testQuery(name.endsWith("e Clooney"), "name:*e Clooney", 1);
    }

    @Test
    public void ends_with_name_should_not_match() throws Exception {
        testQuery(name.endsWith("e Looney"), "name:*e Looney", 0);
    }

    @Test
    public void contains_name_should_match() throws Exception {
        testQuery(name.contains("oney"), "name:*oney*", 1);
    }

    @Test
    public void contains_name_should_not_match() throws Exception {
        testQuery(name.contains("bloney"), "name:*bloney*", 0);
    }

    @Test
    public void in_names_should_match_2() throws Exception {
        testQuery(name.in("Brad Pitt", "George Clooney"), "name:Brad Pitt name:George Clooney", 2);
    }

    @Test
    public void or_by_name_should_match_2() throws Exception {
        testQuery(name.eq("Brad Pitt").or(name.eq("George Clooney")), "name:Brad Pitt name:George Clooney", 2);
    }

    @Test
    public void equals_by_birth_date() throws Exception {
        testQuery(birthDate.eq(clooney.getBirthDate()), "birthDate:1961-04-06", 1);
    }

    @Test
    public void between_phrase() throws Exception {
        testQuery(name.between("Brad Pitt", "George Clooney"), "name:[Brad Pitt TO George Clooney]", 2);
    }

    @Test
    public void not_equals_finds_the_actors_and_movies() throws Exception {
        testQuery(name.ne("Michael Douglas"), "-name:Michael Douglas +*:*", 3);
    }

    @Test
    public void not_equals_finds_only_clooney_and_movies() throws Exception {
        testQuery(name.ne("Brad Pitt"), "-name:Brad Pitt +*:*", 2);
    }

    @Test
    public void and_with_two_not_equals_doesnt_find_the_actors() throws Exception {
        testQuery(name.ne("Brad Pitt").and(name.ne("George Clooney")), "+(-name:Brad Pitt +*:*) +(-name:George Clooney +*:*)", 1);
    }

    @Test
    public void or_with_two_not_equals_finds_movies_and_actors() throws Exception {
        testQuery(name.ne("Brad Pitt").or(name.ne("George Clooney")), "(-name:Brad Pitt +*:*) (-name:George Clooney +*:*)", 3);
    }

    @Test
    public void negation_of_equals_finds_movies_and_actors() throws Exception {
        testQuery(name.eq("Michael Douglas").not(), "-name:Michael Douglas +*:*", 3);
    }

    @Test
    public void negation_of_equals_finds_pitt_and_movies() throws Exception {
        testQuery(name.eq("Brad Pitt").not(), "-name:Brad Pitt +*:*", 2);
    }

    @Test
    public void multiple_field_search_from_movies() throws Exception {
        StringPath movie = Expressions.stringPath("movie");
        testQuery(movie.in("Interview with the Vampire"), "movie:Interview with the Vampire", 1);
        testQuery(movie.eq("Up in the Air"), "movie:Up in the Air", 1);
    }
}

