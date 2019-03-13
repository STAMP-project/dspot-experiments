/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.postgres;


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.ClassRule;
import org.junit.Test;


public class TestTypedEnum {
    @ClassRule
    public static JdbiRule db = PostgresDbRule.rule();

    public Handle h;

    @Test
    public void testBind() {
        h.createUpdate("INSERT INTO values VALUES(:value)").bind("value", TestTypedEnum.EnumT.BAR).execute();
        assertThat(h.createQuery("SELECT * FROM values").mapTo(String.class).findOnly()).isEqualTo("BAR");
    }

    @Test
    public void testMap() {
        h.createUpdate("INSERT INTO values VALUES('BAZ')").execute();
        assertThat(h.createQuery("SELECT * FROM values").mapTo(TestTypedEnum.EnumT.class).findOnly()).isEqualTo(TestTypedEnum.EnumT.BAZ);
    }

    public enum EnumT {

        FOO,
        BAR,
        BAZ;}
}

