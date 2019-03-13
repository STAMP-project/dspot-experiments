/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.jpa;


import java.util.List;
import org.essentials4j.Do;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.job.Jobs;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPATest extends IsolatedIntegrationTest {
    @Test
    public void testBasicCRUD() {
        JPA.bootstrap(path());
        tx(() -> {
            Book b1 = new Book("book 1");
            Book b2 = new Book("book 2");
            Movie m1 = new Movie("movie 1");
            JPA.insert(b1);
            JPA.insert(b2);
            JPA.insert(m1);
            JPA.flush();// not actually required

        });
        tx(() -> {
            eq(JPA.getAllEntities().size(), 3);
            List<Book> books = JPA.of(Book.class).all();
            eq(Do.map(books).toList(Book::getTitle), U.list("book 1", "book 2"));
            List<Movie> movies = JPA.of(Movie.class).all();
            eq(Do.map(movies).toList(Movie::getTitle), U.list("movie 1"));
            eq(JPA.jpql("select title from Book where id = ?1", 2L).all(), U.list("book 2"));
        });
        eq(Jobs.errorCounter().get(), 0);
    }
}

