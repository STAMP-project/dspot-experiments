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
package org.jooq.example;


import AUTHORS.FIRST_NAME;
import AUTHORS.ID;
import AUTHORS.LAST_NAME;
import org.jooq.example.db.oracle.sp.packages.Library;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class OracleProcedureExamples extends Utils {
    @Test
    public void testProcedures() {
        // TODO: Work on this nice table unnesting syntax, which currently doesn't work.
        // dsl.selectFrom(table(Library.getAuthors(null, 1)))
        // .fetch();
        Utils.dsl.select(FIRST_NAME, LAST_NAME, Library.getBooks(ID)).from(AUTHORS).fetch().forEach(( author) -> {
            System.out.println();
            System.out.println((((("Author " + (author.get(AUTHORS.FIRST_NAME))) + " ") + (author.get(AUTHORS.LAST_NAME))) + " wrote: "));
            author.value3().forEach(( book) -> {
                System.out.println(book.getTitle());
            });
        });
    }
}

