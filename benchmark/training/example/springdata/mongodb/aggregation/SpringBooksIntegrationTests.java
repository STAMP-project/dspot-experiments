/**
 * Copyright 2017-2018 the original author or authors.
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
package example.springdata.mongodb.aggregation;


import Granularities.SERIES_1_2_5;
import java.util.List;
import lombok.Getter;
import lombok.Value;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Examples for Spring Books using the MongoDB Aggregation Framework. Data originates from Google's Book search.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @see <a href=
"https://www.googleapis.com/books/v1/volumes?q=intitle:spring+framework">https://www.googleapis.com/books/v1/volumes?q=intitle:spring+framework</a>
 * @see <a href="/books.json>books.json</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBooksIntegrationTests {
    @Autowired
    MongoOperations operations;

    /**
     * Project Book titles.
     */
    @Test
    public void shouldRetrieveOrderedBookTitles() {
        Aggregation aggregation = // 
        // 
        newAggregation(sort(Direction.ASC, "volumeInfo.title"), project().and("volumeInfo.title").as("title"));
        AggregationResults<SpringBooksIntegrationTests.BookTitle> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.BookTitle.class);
        // 
        // 
        assertThat(result.getMappedResults()).extracting("title").containsSequence("Aprende a Desarrollar con Spring Framework", "Beginning Spring", "Beginning Spring 2");
    }

    /**
     * Get number of books that were published by the particular publisher.
     */
    @Test
    public void shouldRetrieveBooksPerPublisher() {
        Aggregation aggregation = // 
        // 
        // 
        newAggregation(// 
        group("volumeInfo.publisher").count().as("count"), sort(Direction.DESC, "count"), project("count").and("_id").as("publisher"));
        AggregationResults<SpringBooksIntegrationTests.BooksPerPublisher> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.BooksPerPublisher.class);
        assertThat(result).hasSize(27);
        assertThat(result).extracting("publisher").containsSequence("Apress", "Packt Publishing Ltd");
        assertThat(result).extracting("count").containsSequence(26, 22, 11);
    }

    /**
     * Get number of books that were published by the particular publisher with their titles.
     */
    @Test
    public void shouldRetrieveBooksPerPublisherWithTitles() {
        Aggregation aggregation = // 
        // 
        // 
        newAggregation(// 
        // 
        group("volumeInfo.publisher").count().as("count").addToSet("volumeInfo.title").as("titles"), sort(Direction.DESC, "count"), project("count", "titles").and("_id").as("publisher"));
        AggregationResults<SpringBooksIntegrationTests.BooksPerPublisher> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.BooksPerPublisher.class);
        SpringBooksIntegrationTests.BooksPerPublisher booksPerPublisher = result.getMappedResults().get(0);
        assertThat(getPublisher()).isEqualTo("Apress");
        assertThat(getCount()).isEqualTo(26);
        assertThat(getTitles()).contains("Expert Spring MVC and Web Flow", "Pro Spring Boot");
    }

    /**
     * Filter for Data-related books in their title and output the title and authors.
     */
    @Test
    public void shouldRetrieveDataRelatedBooks() {
        Aggregation aggregation = // 
        // 
        // 
        // 
        newAggregation(match(Criteria.where("volumeInfo.title").regex("data", "i")), replaceRoot("volumeInfo"), project("title", "authors"), sort(Direction.ASC, "title"));
        AggregationResults<SpringBooksIntegrationTests.BookAndAuthors> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.BookAndAuthors.class);
        SpringBooksIntegrationTests.BookAndAuthors bookAndAuthors = result.getMappedResults().get(1);
        assertThat(getTitle()).isEqualTo("Spring Data");
        assertThat(getAuthors()).contains("Mark Pollack", "Oliver Gierke", "Thomas Risberg", "Jon Brisbin", "Michael Hunger");
    }

    /**
     * Retrieve the number of pages per author (and divide the number of pages by the number of authors).
     */
    @Test
    public void shouldRetrievePagesPerAuthor() {
        Aggregation aggregation = // 
        // 
        // 
        // 
        // 
        newAggregation(match(Criteria.where("volumeInfo.authors").exists(true)), replaceRoot("volumeInfo"), // 
        project("authors", "pageCount").and(// 
        ArithmeticOperators.valueOf("pageCount").divideBy(ArrayOperators.arrayOf("authors").length())).as("pagesPerAuthor"), unwind("authors"), // 
        // 
        group("authors").sum("pageCount").as("totalPageCount").sum("pagesPerAuthor").as("approxWritten"), sort(Direction.DESC, "totalPageCount"));
        AggregationResults<SpringBooksIntegrationTests.PagesPerAuthor> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.PagesPerAuthor.class);
        SpringBooksIntegrationTests.PagesPerAuthor pagesPerAuthor = result.getMappedResults().get(0);
        assertThat(getAuthor()).isEqualTo("Josh Long");
        assertThat(getTotalPageCount()).isEqualTo(1892);
        assertThat(getApproxWritten()).isEqualTo(573);
    }

    /**
     * Categorize books by their page count into buckets.
     */
    @Test
    public void shouldCategorizeBooksInBuckets() {
        Aggregation aggregation = // 
        // 
        newAggregation(replaceRoot("volumeInfo"), match(Criteria.where("pageCount").exists(true)), // 
        // 
        // 
        bucketAuto("pageCount", 10).withGranularity(SERIES_1_2_5).andOutput("title").push().as("titles").andOutput("titles").count().as("count"));
        AggregationResults<SpringBooksIntegrationTests.BookFacetPerPage> result = operations.aggregate(aggregation, "books", SpringBooksIntegrationTests.BookFacetPerPage.class);
        List<SpringBooksIntegrationTests.BookFacetPerPage> mappedResults = result.getMappedResults();
        SpringBooksIntegrationTests.BookFacetPerPage facet_20_to_100_pages = mappedResults.get(0);
        assertThat(getMin()).isEqualTo(20);
        assertThat(getMax()).isEqualTo(100);
        assertThat(getCount()).isEqualTo(12);
        SpringBooksIntegrationTests.BookFacetPerPage facet_100_to_500_pages = mappedResults.get(1);
        assertThat(getMin()).isEqualTo(100);
        assertThat(getMax()).isEqualTo(500);
        assertThat(getCount()).isEqualTo(63);
        assertThat(getTitles()).contains("Spring Data");
    }

    /**
     * Run a multi-faceted aggregation to get buckets by price (1-10, 10-50, 50-100 EURO) and by the first letter of the
     * author name.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldCategorizeInMultipleFacetsByPriceAndAuthor() {
        Aggregation aggregation = // 
        newAggregation(match(Criteria.where("volumeInfo.authors").exists(true).and("volumeInfo.publisher").exists(true)), // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        facet().and(match(Criteria.where("saleInfo.listPrice").exists(true)), replaceRoot("saleInfo"), // 
        bucket("listPrice.amount").withBoundaries(1, 10, 50, 100)).as("prices").and(unwind("volumeInfo.authors"), replaceRoot("volumeInfo"), match(Criteria.where("authors").not().size(0)), // 
        // 
        project().andExpression("substrCP(authors, 0, 1)").as("startsWith").and("authors").as("author"), // 
        bucketAuto("startsWith", 10).andOutput("author").push().as("authors")).as("authors"));
        AggregationResults<Document> result = operations.aggregate(aggregation, "books", Document.class);
        Document uniqueMappedResult = result.getUniqueMappedResult();
        assertThat(((List<Object>) (uniqueMappedResult.get("prices")))).hasSize(3);
        assertThat(((List<Object>) (uniqueMappedResult.get("authors")))).hasSize(8);
    }

    @Value
    @Getter
    static class BookTitle {
        String title;
    }

    @Value
    @Getter
    static class BooksPerPublisher {
        String publisher;

        int count;

        List<String> titles;
    }

    @Value
    @Getter
    static class BookAndAuthors {
        String title;

        List<String> authors;
    }

    @Value
    @Getter
    static class PagesPerAuthor {
        @Id
        String author;

        int totalPageCount;

        int approxWritten;
    }

    @Value
    @Getter
    static class BookFacetPerPage {
        int min;

        int max;

        int count;

        List<String> titles;
    }
}

