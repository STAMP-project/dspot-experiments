/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.entities.Author;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.data.elasticsearch.entities.Car;
import org.springframework.data.elasticsearch.entities.Person;
import org.springframework.data.elasticsearch.entities.PersonMultipleLevelNested;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/repository-test-nested-object.xml")
public class NestedObjectTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldIndexInitialLevelNestedObject() {
        final List<Car> cars = new ArrayList<>();
        final Car saturn = new Car();
        saturn.setName("Saturn");
        saturn.setModel("SL");
        final Car subaru = new Car();
        subaru.setName("Subaru");
        subaru.setModel("Imprezza");
        final Car ford = new Car();
        ford.setName("Ford");
        ford.setModel("Focus");
        cars.add(saturn);
        cars.add(subaru);
        cars.add(ford);
        final Person foo = new Person();
        foo.setName("Foo");
        foo.setId("1");
        foo.setCar(cars);
        final Car car = new Car();
        car.setName("Saturn");
        car.setModel("Imprezza");
        final Person bar = new Person();
        bar.setId("2");
        bar.setName("Bar");
        bar.setCar(Arrays.asList(car));
        final List<IndexQuery> indexQueries = new ArrayList<>();
        final IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(foo.getId());
        indexQuery1.setObject(foo);
        final IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(bar.getId());
        indexQuery2.setObject(bar);
        indexQueries.add(indexQuery1);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.putMapping(Person.class);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(Person.class);
        final QueryBuilder builder = nestedQuery("car", boolQuery().must(termQuery("car.name", "saturn")).must(termQuery("car.model", "imprezza")), ScoreMode.None);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        final List<Person> persons = elasticsearchTemplate.queryForList(searchQuery, Person.class);
        Assert.assertThat(persons.size(), CoreMatchers.is(1));
    }

    @Test
    public void shouldIndexMultipleLevelNestedObject() {
        // given
        final List<IndexQuery> indexQueries = createPerson();
        // when
        elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(PersonMultipleLevelNested.class);
        // then
        final GetQuery getQuery = new GetQuery();
        getQuery.setId("1");
        final PersonMultipleLevelNested personIndexed = elasticsearchTemplate.queryForObject(getQuery, PersonMultipleLevelNested.class);
        Assert.assertThat(personIndexed, CoreMatchers.is(Matchers.notNullValue()));
    }

    @Test
    public void shouldIndexMultipleLevelNestedObjectWithIncludeInParent() {
        // given
        final List<IndexQuery> indexQueries = createPerson();
        // when
        elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
        elasticsearchTemplate.bulkIndex(indexQueries);
        // then
        final Map mapping = elasticsearchTemplate.getMapping(PersonMultipleLevelNested.class);
        Assert.assertThat(mapping, CoreMatchers.is(Matchers.notNullValue()));
        final Map propertyMap = ((Map) (mapping.get("properties")));
        Assert.assertThat(propertyMap, CoreMatchers.is(Matchers.notNullValue()));
        final Map bestCarsAttributes = ((Map) (propertyMap.get("bestCars")));
        Assert.assertThat(bestCarsAttributes.get("include_in_parent"), CoreMatchers.is(Matchers.notNullValue()));
    }

    @Test
    public void shouldSearchUsingNestedQueryOnMultipleLevelNestedObject() {
        // given
        final List<IndexQuery> indexQueries = createPerson();
        // when
        elasticsearchTemplate.putMapping(PersonMultipleLevelNested.class);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(PersonMultipleLevelNested.class);
        // then
        final BoolQueryBuilder builder = boolQuery();
        builder.must(nestedQuery("girlFriends", termQuery("girlFriends.type", "temp"), ScoreMode.None)).must(nestedQuery("girlFriends.cars", termQuery("girlFriends.cars.name", "Ford".toLowerCase()), ScoreMode.None));
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        final Page<PersonMultipleLevelNested> personIndexed = elasticsearchTemplate.queryForPage(searchQuery, PersonMultipleLevelNested.class);
        Assert.assertThat(personIndexed, CoreMatchers.is(Matchers.notNullValue()));
        Assert.assertThat(personIndexed.getTotalElements(), CoreMatchers.is(1L));
        Assert.assertThat(getId(), CoreMatchers.is("1"));
    }

    @Test
    public void shouldSearchBooksForPersonInitialLevelNestedType() {
        final List<Car> cars = new ArrayList<>();
        final Car saturn = new Car();
        saturn.setName("Saturn");
        saturn.setModel("SL");
        final Car subaru = new Car();
        subaru.setName("Subaru");
        subaru.setModel("Imprezza");
        final Car ford = new Car();
        ford.setName("Ford");
        ford.setModel("Focus");
        cars.add(saturn);
        cars.add(subaru);
        cars.add(ford);
        final Book java = new Book();
        setId("1");
        setName("java");
        final Author javaAuthor = new Author();
        javaAuthor.setId("1");
        javaAuthor.setName("javaAuthor");
        setAuthor(javaAuthor);
        final Book spring = new Book();
        setId("2");
        setName("spring");
        final Author springAuthor = new Author();
        springAuthor.setId("2");
        springAuthor.setName("springAuthor");
        setAuthor(springAuthor);
        final Person foo = new Person();
        foo.setName("Foo");
        foo.setId("1");
        foo.setCar(cars);
        foo.setBooks(Arrays.asList(java, spring));
        final Car car = new Car();
        car.setName("Saturn");
        car.setModel("Imprezza");
        final Person bar = new Person();
        bar.setId("2");
        bar.setName("Bar");
        bar.setCar(Arrays.asList(car));
        final List<IndexQuery> indexQueries = new ArrayList<>();
        final IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(foo.getId());
        indexQuery1.setObject(foo);
        final IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(bar.getId());
        indexQuery2.setObject(bar);
        indexQueries.add(indexQuery1);
        indexQueries.add(indexQuery2);
        elasticsearchTemplate.putMapping(Person.class);
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(Person.class);
        final QueryBuilder builder = nestedQuery("books", boolQuery().must(termQuery("books.name", "java")), ScoreMode.None);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
        final List<Person> persons = elasticsearchTemplate.queryForList(searchQuery, Person.class);
        Assert.assertThat(persons.size(), CoreMatchers.is(1));
    }

    /* DATAES-73 */
    @Test
    public void shouldIndexAndSearchMapAsNestedType() {
        // given
        final Book book1 = new Book();
        final Book book2 = new Book();
        book1.setId(randomNumeric(5));
        setName("testBook1");
        book2.setId(randomNumeric(5));
        setName("testBook2");
        final Map<Integer, Collection<String>> map1 = new HashMap<>();
        map1.put(1, Arrays.asList("test1", "test2"));
        final Map<Integer, Collection<String>> map2 = new HashMap<>();
        map2.put(1, Arrays.asList("test3", "test4"));
        setBuckets(map1);
        setBuckets(map2);
        final List<IndexQuery> indexQueries = new ArrayList<>();
        final IndexQuery indexQuery1 = new IndexQuery();
        indexQuery1.setId(getId());
        indexQuery1.setObject(book1);
        final IndexQuery indexQuery2 = new IndexQuery();
        indexQuery2.setId(getId());
        indexQuery2.setObject(book2);
        indexQueries.add(indexQuery1);
        indexQueries.add(indexQuery2);
        // when
        elasticsearchTemplate.bulkIndex(indexQueries);
        elasticsearchTemplate.refresh(Book.class);
        // then
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(nestedQuery("buckets", termQuery("buckets.1", "test3"), ScoreMode.None)).build();
        final Page<Book> books = elasticsearchTemplate.queryForPage(searchQuery, Book.class);
        Assert.assertThat(books.getContent().size(), CoreMatchers.is(1));
        Assert.assertThat(getId(), CoreMatchers.is(getId()));
    }
}

