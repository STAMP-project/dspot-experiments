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
package org.jooq.academy.section1;


import AUTHOR.ID;
import org.jooq.academy.tools.Tools;
import org.junit.Test;


public class Example_1_1_PrintTheQuery {
    @Test
    public void run() {
        // This creates a simple query without executing it
        // By default, a Query's toString() method will print the SQL string to the console
        Tools.title("Create a simple query without executing it");
        Tools.print(select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME).from(AUTHOR).orderBy(ID));
    }
}

