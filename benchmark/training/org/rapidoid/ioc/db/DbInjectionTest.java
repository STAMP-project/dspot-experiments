/**
 * -
 * #%L
 * rapidoid-inject
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
package org.rapidoid.ioc.db;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbInjectionTest extends AbstractInjectTest {
    @Test
    public void shouldInject() {
        Database db = IoC.singleton(Database.class);
        isTrue((db == (IoC.singleton(Database.class))));
        isTrue((db == (IoC.singleton(Database.class))));
        notNull(db.tables);
        Table persons = db.tables.get("person");
        Table books = db.tables.get("book");
        notNullAll(persons, books);
        isTrue((persons != books));
        isTrue(((persons.transactor) == (books.transactor)));
        isTrue(((persons.transactor) == (db.transactor)));
        verifyIoC();
    }
}

