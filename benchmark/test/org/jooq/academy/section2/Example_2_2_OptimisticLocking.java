/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 */
package org.jooq.academy.section2;


import BOOK.ID;
import java.sql.Connection;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.academy.tools.Tools;
import org.jooq.conf.Settings;
import org.jooq.example.db.h2.tables.records.BookRecord;
import org.jooq.exception.DataChangedException;
import org.jooq.impl.DSL;
import org.junit.Test;


public class Example_2_2_OptimisticLocking {
    @Test
    public void run() throws SQLException {
        Connection connection = Tools.connection();
        DSLContext dsl = DSL.using(connection, new Settings().withExecuteWithOptimisticLocking(true));
        // Don't store the changes
        try {
            Tools.title("Applying optimistic locking");
            BookRecord book1 = dsl.selectFrom(BOOK).where(ID.eq(1)).fetchOne();
            BookRecord book2 = dsl.selectFrom(BOOK).where(ID.eq(1)).fetchOne();
            book1.setTitle("New Title");
            book1.store();
            book2.setTitle("Another Title");
            book2.store();
        } catch (DataChangedException expected) {
            expected.printStackTrace();
        } finally {
            connection.rollback();
        }
    }
}

