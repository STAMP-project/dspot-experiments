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


import org.essentials4j.Do;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPAPagingTest extends IsolatedIntegrationTest {
    @Test
    public void testPaging() {
        JPA.bootstrap(path());
        int total = 15;
        JPA.transaction(() -> {
            for (int i = 0; i < total; i++) {
                Book book = JPA.insert(new Book(("b" + i)));
                notNull(book.getId());
            }
        });
        JPA.transaction(() -> {
            eq(JPA.getAllEntities().size(), total);
            eq(JPA.count(.class), total);
            eq(JPA.of(.class).all().size(), total);
            List<Book> first3 = JPA.of(.class).page(0, 3);
            eq(first3.size(), 3);
            eq(Do.map(first3).toList(Book::getTitle), U.list("b0", "b1", "b2"));
            List<Book> next5 = JPA.of(.class).page(3, 5);
            eq(Do.map(next5).toList(Book::getTitle), U.list("b3", "b4", "b5", "b6", "b7"));
            List<Book> last2 = JPA.of(.class).page(13, 2);
            eq(Do.map(last2).toList(Book::getTitle), U.list("b13", "b14"));
        });
    }
}

