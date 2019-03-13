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


import AUTHOR.FIRST_NAME;
import AUTHOR.ID;
import AUTHOR.LAST_NAME;
import org.jooq.academy.tools.Tools;
import org.jooq.impl.DSL;
import org.junit.Test;


public class Example_1_2_ExecuteTheQuery {
    @Test
    public void run() {
        // All we need to execute a query is provide it with a connection and then
        // call fetch() on it.
        Tools.title("Selecting FIRST_NAME and LAST_NAME from the AUTHOR table");
        Tools.print(DSL.using(Tools.connection()).select(FIRST_NAME, LAST_NAME).from(AUTHOR).orderBy(ID).fetch());
    }
}

