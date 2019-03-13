/**
 * -
 * #%L
 * rapidoid-sql
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
package org.rapidoid.sql.test;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;


@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class JDBCPagingTest extends SQLTestCommons {
    @Test
    public void testExplicitPaging() {
        testPagingWithSql("SELECT * FROM movie WHERE id < ? OFFSET $skip LIMIT $limit");
    }

    @Test
    public void testImplicitPaging() {
        testPagingWithSql("SELECT * FROM movie WHERE id < ?");
    }
}

