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
package org.jdbi.v3.core.mapper;


import EnumStrategy.BY_ORDINAL;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.enums.EnumByName;
import org.jdbi.v3.core.enums.EnumByOrdinal;
import org.jdbi.v3.core.enums.Enums;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.core.rule.SqliteDatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class QualifiedEnumMappingTest {
    @Rule
    public SqliteDatabaseRule db = new SqliteDatabaseRule();

    private Handle h;

    @Test
    public void methodCallCanBeAnnotatedAsByName() {
        h.getConfig(Enums.class).setEnumStrategy(BY_ORDINAL);
        Object byName = h.createQuery("select :name").bind("name", QualifiedEnumMappingTest.Foobar.FOO.name()).mapTo(QualifiedType.of(QualifiedEnumMappingTest.Foobar.class).with(EnumByName.class)).findOnly();
        assertThat(byName).isEqualTo(QualifiedEnumMappingTest.Foobar.FOO);
    }

    @Test
    public void methodCallCanBeAnnotatedAsByOrdinal() {
        Object byOrdinal = h.createQuery("select :ordinal").bind("ordinal", QualifiedEnumMappingTest.Foobar.FOO.ordinal()).mapTo(QualifiedType.of(QualifiedEnumMappingTest.Foobar.class).with(EnumByOrdinal.class)).findOnly();
        assertThat(byOrdinal).isEqualTo(QualifiedEnumMappingTest.Foobar.FOO);
    }

    @Test
    public void enumCanBeAnnotatedAsByName() {
        h.getConfig(Enums.class).setEnumStrategy(BY_ORDINAL);
        QualifiedEnumMappingTest.ByName byName = h.createQuery("select :name").bind("name", QualifiedEnumMappingTest.ByName.ALPHABETIC.name()).mapTo(QualifiedEnumMappingTest.ByName.class).findOnly();
        assertThat(byName).isEqualTo(QualifiedEnumMappingTest.ByName.ALPHABETIC);
    }

    @Test
    public void enumCanBeAnnotatedAsByOrdinal() {
        QualifiedEnumMappingTest.ByOrdinal byOrdinal = h.createQuery("select :ordinal").bind("ordinal", QualifiedEnumMappingTest.ByOrdinal.NUMERIC.ordinal()).mapTo(QualifiedEnumMappingTest.ByOrdinal.class).findOnly();
        assertThat(byOrdinal).isEqualTo(QualifiedEnumMappingTest.ByOrdinal.NUMERIC);
    }

    @Test
    public void methodCallOverridesClassForName() {
        h.getConfig(Enums.class).setEnumStrategy(BY_ORDINAL);
        Object byName = h.createQuery("select :name").bind("name", QualifiedEnumMappingTest.ByOrdinal.NUMERIC.name()).mapTo(QualifiedType.of(QualifiedEnumMappingTest.ByOrdinal.class).with(EnumByName.class)).findOnly();
        assertThat(byName).isEqualTo(QualifiedEnumMappingTest.ByOrdinal.NUMERIC);
    }

    @Test
    public void methodCallOverridesClassForOrdinal() {
        Object byOrdinal = h.createQuery("select :ordinal").bind("ordinal", QualifiedEnumMappingTest.ByName.ALPHABETIC.ordinal()).mapTo(QualifiedType.of(QualifiedEnumMappingTest.ByName.class).with(EnumByOrdinal.class)).findOnly();
        assertThat(byOrdinal).isEqualTo(QualifiedEnumMappingTest.ByName.ALPHABETIC);
    }

    // bar is unused to make sure we don't have any coincidental correctness
    private enum Foobar {

        BAR,
        FOO;}

    @EnumByName
    private enum ByName {

        BAR,
        ALPHABETIC;}

    @EnumByOrdinal
    private enum ByOrdinal {

        BAR,
        NUMERIC;}
}

