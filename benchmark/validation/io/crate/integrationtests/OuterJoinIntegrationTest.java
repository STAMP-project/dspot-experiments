/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.integrationtests;


import io.crate.testing.TestingHelpers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class OuterJoinIntegrationTest extends SQLTransportIntegrationTest {
    @Test
    public void testLeftOuterJoin() {
        // which employee works in which office?
        execute(("select persons.name, offices.name from" + (" employees as persons left join offices on office_id = offices.id" + " order by persons.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Trillian| Entresol\n" + ("Ford Perfect| NULL\n" + "Douglas Adams| Chief Office\n"))));
    }

    @Test
    public void testLeftOuterJoinOrderOnOuterTable() {
        // which employee works in which office?
        execute(("select persons.name, offices.name from" + (" employees as persons left join offices on office_id = offices.id" + " order by offices.name nulls first")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Ford Perfect| NULL\n" + ("Douglas Adams| Chief Office\n" + "Trillian| Entresol\n"))));
    }

    @Test
    public void test3TableLeftOuterJoin() {
        execute(("select professions.name, employees.name, offices.name from" + ((" professions left join employees on profession_id = professions.id" + " left join offices on office_id = offices.id") + " order by professions.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Writer| Douglas Adams| Chief Office\n" + (("Traveler| Ford Perfect| NULL\n" + "Commander| Trillian| Entresol\n") + "Janitor| NULL| NULL\n"))));
    }

    @Test
    public void test3TableLeftOuterJoinOrderByOuterTable() {
        execute(("select professions.name, employees.name, offices.name from" + ((" professions left join employees on profession_id = professions.id" + " left join offices on office_id = offices.id") + " order by offices.name nulls first, professions.id nulls first")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Traveler| Ford Perfect| NULL\n" + (("Janitor| NULL| NULL\n" + "Writer| Douglas Adams| Chief Office\n") + "Commander| Trillian| Entresol\n"))));
    }

    @Test
    public void testRightOuterJoin() {
        execute(("select offices.name, persons.name from" + (" employees as persons right join offices on office_id = offices.id" + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Hobbit House| NULL\n" + ("Entresol| Trillian\n" + "Chief Office| Douglas Adams\n"))));
    }

    @Test
    public void test3TableLeftAndRightOuterJoin() {
        execute(("select professions.name, employees.name, offices.name from" + ((" offices left join employees on office_id = offices.id" + " right join professions on profession_id = professions.id") + " order by professions.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Writer| Douglas Adams| Chief Office\n" + (("Traveler| NULL| NULL\n" + "Commander| Trillian| Entresol\n") + "Janitor| NULL| NULL\n"))));
    }

    @Test
    public void testFullOuterJoin() {
        execute(("select persons.name, offices.name from" + (" offices full join employees as persons on office_id = offices.id" + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("NULL| Hobbit House\n" + (("Trillian| Entresol\n" + "Douglas Adams| Chief Office\n") + "Ford Perfect| NULL\n"))));
    }

    @Test
    public void testFullOuterJoinWithFilters() {
        // It's rewritten to an Inner Join because of the filtering condition in where clause
        execute(("select persons.name, offices.name from" + ((" offices full join employees as persons on office_id = offices.id" + " where offices.name='Entresol' and persons.name='Trillian' ") + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("Trillian| Entresol\n"));
    }

    @Test
    public void testOuterJoinWithFunctionsInOrderBy() {
        execute(("select coalesce(persons.name, ''), coalesce(offices.name, '') from" + (" offices full join employees as persons on office_id = offices.id" + " order by 1, 2")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("| Hobbit House\n" + (("Douglas Adams| Chief Office\n" + "Ford Perfect| \n") + "Trillian| Entresol\n"))));
    }

    @Test
    public void testLeftJoinWithFilterOnInner() {
        execute(("select employees.name, offices.name from" + ((" employees left join offices on office_id = offices.id" + " where employees.id < 3") + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Trillian| Entresol\n" + "Ford Perfect| NULL\n")));
    }

    @Test
    public void testLeftJoinWithFilterOnOuter() {
        // It's rewritten to an Inner Join because of the filtering condition in where clause
        execute(("select employees.name, offices.name from" + ((" employees left join offices on office_id = offices.id" + " where offices.size > 100") + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is("Douglas Adams| Chief Office\n"));
    }

    @Test
    public void testLeftJoinWithCoalesceOnOuter() throws Exception {
        // coalesce causes a NULL row which is emitted from the join to become a match
        execute(("select employees.name, offices.name from" + ((" employees left join offices on office_id = offices.id" + " where coalesce(offices.size, cast(110 as integer)) > 100") + " order by offices.id")));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("Douglas Adams| Chief Office\n" + "Ford Perfect| NULL\n")));
    }
}

