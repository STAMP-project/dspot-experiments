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
package org.jdbi.v3.core.qualifier;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.Arguments;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.ColumnMappers;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jdbi.v3.core.rule.DatabaseRule;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.Rule;
import org.junit.Test;


public class TestCustomQualifier {
    @Rule
    public DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    @Test
    public void registerArgumentFactory() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (1, :name)").bindByType("name", "abc", QualifiedType.of(.class).with(.class)).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void configArgumentsRegister() {
        dbRule.getJdbi().configure(Arguments.class, ( config) -> config.register(new ReversedStringArgumentFactory())).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (1, :name)").bindByType("name", "abc", QualifiedType.of(.class).with(.class)).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void registerColumnMapper() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'abc')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void configColumnMappersRegister() {
        dbRule.getJdbi().configure(ColumnMappers.class, ( config) -> config.register(new ReversedStringMapper())).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'abc')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void registerColumnMapperByQualifiedType() {
        dbRule.getJdbi().registerColumnMapper(QualifiedType.of(String.class).with(Reversed.class), ( r, c, ctx) -> reverse(r.getString(c))).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'abcdef')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("fedcba");
        });
    }

    @Test
    public void configColumnMappersRegisterByQualifiedType() {
        dbRule.getJdbi().configure(ColumnMappers.class, ( config) -> config.register(QualifiedType.of(.class).with(.class), ( r, c, ctx) -> reverse(r.getString(c)))).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'abcdef')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("fedcba");
        });
    }

    @Test
    public void registerColumnMapperFactory() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapperFactory()).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'xyz')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("zyx");
        });
    }

    @Test
    public void configColumnMappersRegisterFactory() {
        dbRule.getJdbi().configure(ColumnMappers.class, ( config) -> config.register(new ReversedStringMapperFactory())).useHandle(( handle) -> {
            handle.execute("insert into something (id, name) values (1, 'xyz')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class)).findOnly()).isEqualTo("zyx");
        });
    }

    @Test
    public void bindBeanQualifiedGetter() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (:id, :name)").bindBean(new QualifiedGetterThing(1, "abc")).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void mapBeanQualifiedGetter() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).registerRowMapper(BeanMapper.factory(QualifiedGetterThing.class)).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT * FROM something").mapTo(.class).findOnly()).isEqualTo(new QualifiedGetterThing(1, "cba"));
        });
    }

    @Test
    public void bindBeanQualifiedSetter() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (:id, :name)").bindBean(new QualifiedSetterThing(1, "abc")).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void mapBeanQualifiedSetter() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).registerRowMapper(BeanMapper.factory(QualifiedSetterThing.class)).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT * FROM something").mapTo(.class).findOnly()).isEqualTo(new QualifiedSetterThing(1, "cba"));
        });
    }

    @Test
    public void bindBeanQualifiedSetterParam() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (:id, :name)").bindBean(new QualifiedSetterParamThing(1, "abc")).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void mapBeanQualifiedSetterParam() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).registerRowMapper(BeanMapper.factory(QualifiedSetterParamThing.class)).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT * FROM something").mapTo(.class).findOnly()).isEqualTo(new QualifiedSetterParamThing(1, "cba"));
        });
    }

    @Test
    public void bindMethodsQualifiedMethod() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (:id, :name)").bindMethods(new QualifiedMethodThing(1, "abc")).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void mapConstructorQualifiedParam() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).registerRowMapper(ConstructorMapper.factory(QualifiedConstructorParamThing.class)).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT * FROM something").mapTo(.class).findOnly()).isEqualTo(new QualifiedConstructorParamThing(1, "cba"));
        });
    }

    @Test
    public void bindFieldsQualified() {
        dbRule.getJdbi().registerArgument(new ReversedStringArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (:id, :name)").bindFields(new QualifiedFieldThing(1, "abc")).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("cba");
        });
    }

    @Test
    public void mapFieldsQualified() {
        dbRule.getJdbi().registerColumnMapper(new ReversedStringMapper()).registerRowMapper(FieldMapper.factory(QualifiedFieldThing.class)).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT * FROM something").mapTo(.class).findOnly()).isEqualTo(new QualifiedFieldThing(1, "cba"));
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface UpperCase {}

    @TestCustomQualifier.UpperCase
    static class UpperCaseArgumentFactory extends AbstractArgumentFactory<String> {
        UpperCaseArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(String value, ConfigRegistry config) {
            return ( pos, stmt, ctx) -> stmt.setString(pos, value.toUpperCase());
        }
    }

    @TestCustomQualifier.UpperCase
    static class UpperCaseStringMapper implements ColumnMapper<String> {
        @Override
        public String map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
            return r.getString(columnNumber).toUpperCase();
        }
    }

    @Reversed
    @TestCustomQualifier.UpperCase
    static class ReversedUpperCaseStringArgumentFactory extends AbstractArgumentFactory<String> {
        ReversedUpperCaseStringArgumentFactory() {
            super(Types.VARCHAR);
        }

        @Override
        protected Argument build(String value, ConfigRegistry config) {
            return ( pos, stmt, ctx) -> stmt.setString(pos, reverse(value).toUpperCase());
        }
    }

    @Reversed
    @TestCustomQualifier.UpperCase
    static class ReversedUpperCaseStringMapper implements ColumnMapper<String> {
        @Override
        public String map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
            return Reverser.reverse(r.getString(columnNumber)).toUpperCase();
        }
    }

    @Test
    public void bindMultipleQualifiers() {
        // should use this one - register first so it's consulted last
        dbRule.getJdbi().registerArgument(new TestCustomQualifier.ReversedUpperCaseStringArgumentFactory()).registerArgument(new ReversedStringArgumentFactory()).registerArgument(new TestCustomQualifier.UpperCaseArgumentFactory()).useHandle(( handle) -> {
            handle.createUpdate("INSERT INTO something (id, name) VALUES (1, :name)").bindByType("name", "abc", QualifiedType.of(.class).with(.class, .class)).execute();
            assertThat(handle.select("SELECT name FROM something").mapTo(.class).findOnly()).isEqualTo("CBA");
        });
    }

    @Test
    public void mapMultipleQualifiers() {
        // should use this one - register first so it's consulted last
        dbRule.getJdbi().registerColumnMapper(new TestCustomQualifier.ReversedUpperCaseStringMapper()).registerColumnMapper(new ReversedStringMapper()).registerColumnMapper(new TestCustomQualifier.UpperCaseStringMapper()).useHandle(( handle) -> {
            handle.execute("INSERT INTO something (id, name) VALUES (1, 'abc')");
            assertThat(handle.select("SELECT name FROM something").mapTo(QualifiedType.of(.class).with(.class, .class)).findOnly()).isEqualTo("CBA");
        });
    }
}

