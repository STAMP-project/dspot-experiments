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
package org.jdbi.v3.sqlobject;


import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.qualifier.QualifiedConstructorParamThing;
import org.jdbi.v3.core.qualifier.QualifiedFieldThing;
import org.jdbi.v3.core.qualifier.QualifiedGetterThing;
import org.jdbi.v3.core.qualifier.QualifiedMethodThing;
import org.jdbi.v3.core.qualifier.QualifiedSetterParamThing;
import org.jdbi.v3.core.qualifier.QualifiedSetterThing;
import org.jdbi.v3.core.qualifier.Reversed;
import org.jdbi.v3.core.qualifier.ReversedStringArgumentFactory;
import org.jdbi.v3.core.qualifier.ReversedStringMapper;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.result.ResultIterator;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindFields;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.MapTo;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TestQualifiers {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Consumer<String> consumer;

    private Handle handle;

    private TestQualifiers.Dao dao;

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface Dao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(int id, @Reversed
        String name);

        @SqlBatch("insert into something (id, name) values (:id, :name)")
        void insert(Iterable<Integer> id, @Reversed
        Iterable<String> name);

        @SqlQuery("select name from something where id = :id")
        @Reversed
        String getName(int id);

        @SqlQuery("select name from something order by id")
        @Reversed
        List<String> listNames();

        @SqlQuery("select name from something order by id")
        @Reversed
        ResultIterable<String> resultIterableNames();

        @SqlQuery("select name from something order by id")
        @Reversed
        ResultIterator<String> resultIteratorNames();

        @SqlQuery("select name from something order by id")
        @Reversed
        Iterator<String> iteratorNames();

        @SqlQuery("select name from something order by id")
        @Reversed
        Stream<String> streamNames();

        @SqlQuery("select name from something order by id")
        void consumeNames(@Reversed
        Consumer<String> consumer);
    }

    @Test
    public void bindQualified() {
        dao.insert(1, "abc");
        assertThat(handle.select("SELECT name FROM something WHERE id = 1").mapTo(String.class).findOnly()).isEqualTo("cba");
    }

    @Test
    public void bindQualifiedBatch() {
        dao.insert(ImmutableList.of(1, 2, 3), ImmutableList.of("foo", "bar", "baz"));
        assertThat(handle.select("SELECT name FROM something ORDER BY id").mapTo(String.class).list()).containsExactly("oof", "rab", "zab");
    }

    @Test
    public void singleQualifiedResult() {
        handle.execute("insert into something (id, name) values (1, 'abc')");
        assertThat(dao.getName(1)).isEqualTo("cba");
    }

    @Test
    public void multipleQualifiedResults() {
        handle.prepareBatch("insert into something (id, name) values (?, ?)").add(1, "foo").add(2, "bar").add(3, "baz").execute();
        assertThat(dao.listNames()).containsExactly("oof", "rab", "zab");
        assertThat(dao.resultIterableNames()).containsExactly("oof", "rab", "zab");
        assertThat(dao.resultIteratorNames()).toIterable().containsExactly("oof", "rab", "zab");
        assertThat(dao.iteratorNames()).toIterable().containsExactly("oof", "rab", "zab");
        assertThat(dao.streamNames()).containsExactly("oof", "rab", "zab");
        dao.consumeNames(consumer);
        Mockito.verify(consumer).accept("oof");
        Mockito.verify(consumer).accept("rab");
        Mockito.verify(consumer).accept("zab");
        Mockito.verifyNoMoreInteractions(consumer);
    }

    @Test
    public void bindBeanQualifiedProperty() {
        TestQualifiers.BeanDao beanDao = handle.attach(TestQualifiers.BeanDao.class);
        beanDao.insertBeanQualifiedGetter(new QualifiedGetterThing(1, "foo"));
        beanDao.insertBeanQualifiedSetter(new QualifiedSetterThing(2, "bar"));
        beanDao.insertBeanQualifiedSetterParam(new QualifiedSetterParamThing(3, "baz"));
        assertThat(handle.select("select name from something order by id").mapTo(String.class).list()).containsExactly("oof", "rab", "zab");
    }

    @Test
    public void mapBeanQualifiedProperty() {
        handle.execute("insert into something (id, name) values (1, 'abc')");
        TestQualifiers.BeanDao beanDao = handle.attach(TestQualifiers.BeanDao.class);
        assertThat(beanDao.getBeanQualifiedGetter(1)).isEqualTo(new QualifiedGetterThing(1, "cba"));
        assertThat(beanDao.getBeanQualifiedSetter(1)).isEqualTo(new QualifiedSetterThing(1, "cba"));
        assertThat(beanDao.getBeanQualifiedSetterParam(1)).isEqualTo(new QualifiedSetterParamThing(1, "cba"));
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface BeanDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertBeanQualifiedGetter(@BindBean
        QualifiedGetterThing bean);

        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertBeanQualifiedSetter(@BindBean
        QualifiedSetterThing bean);

        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertBeanQualifiedSetterParam(@BindBean
        QualifiedSetterParamThing bean);

        @SqlQuery("select * from something where id = :id")
        @RegisterBeanMapper(QualifiedGetterThing.class)
        QualifiedGetterThing getBeanQualifiedGetter(int id);

        @SqlQuery("select * from something where id = :id")
        @RegisterBeanMapper(QualifiedSetterThing.class)
        QualifiedSetterThing getBeanQualifiedSetter(int id);

        @SqlQuery("select * from something where id = :id")
        @RegisterBeanMapper(QualifiedSetterParamThing.class)
        QualifiedSetterParamThing getBeanQualifiedSetterParam(int id);
    }

    @Test
    public void bindQualifiedMethod() {
        TestQualifiers.FluentDao fluentDao = handle.attach(TestQualifiers.FluentDao.class);
        fluentDao.insertBindMethods(new QualifiedMethodThing(1, "abc"));
        assertThat(handle.select("select name from something").mapTo(String.class).findOnly()).isEqualTo("cba");
    }

    @Test
    public void mapConstructorMappedQualifiedParam() {
        handle.execute("insert into something (id, name) values (1, 'abc')");
        TestQualifiers.FluentDao fluentDao = handle.attach(TestQualifiers.FluentDao.class);
        assertThat(fluentDao.getConstructorInjected(1)).isEqualTo(new QualifiedConstructorParamThing(1, "cba"));
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface FluentDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertBindMethods(@BindMethods
        QualifiedMethodThing obj);

        @SqlQuery("select * from something where id = :id")
        @RegisterConstructorMapper(QualifiedConstructorParamThing.class)
        QualifiedConstructorParamThing getConstructorInjected(int id);
    }

    @Test
    public void bindQualifiedField() {
        TestQualifiers.FieldDao fieldDao = handle.attach(TestQualifiers.FieldDao.class);
        fieldDao.insertBindFields(new QualifiedFieldThing(1, "abc"));
        assertThat(handle.select("select name from something").mapTo(String.class).findOnly()).isEqualTo("cba");
    }

    @Test
    public void mapQualifiedField() {
        handle.execute("insert into something (id, name) values (1, 'abc')");
        TestQualifiers.FieldDao fieldDao = handle.attach(TestQualifiers.FieldDao.class);
        assertThat(fieldDao.getQualifiedField(1)).isEqualTo(new QualifiedFieldThing(1, "cba"));
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface FieldDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertBindFields(@BindFields
        QualifiedFieldThing obj);

        @SqlQuery("select * from something where id = :id")
        @RegisterFieldMapper(QualifiedFieldThing.class)
        QualifiedFieldThing getQualifiedField(int id);
    }

    @Test
    public void mapTo() {
        handle.execute("insert into something (id, name) values (1, 'abc')");
        TestQualifiers.MapToDao mapToDao = handle.attach(TestQualifiers.MapToDao.class);
        assertThat(mapToDao.get(1, QualifiedGetterThing.class)).isEqualTo(new QualifiedGetterThing(1, "cba"));
        assertThat(mapToDao.get(1, QualifiedSetterThing.class)).isEqualTo(new QualifiedSetterThing(1, "cba"));
        assertThat(mapToDao.get(1, QualifiedSetterParamThing.class)).isEqualTo(new QualifiedSetterParamThing(1, "cba"));
        assertThat(mapToDao.get(1, QualifiedConstructorParamThing.class)).isEqualTo(new QualifiedConstructorParamThing(1, "cba"));
        assertThat(mapToDao.get(1, QualifiedFieldThing.class)).isEqualTo(new QualifiedFieldThing(1, "cba"));
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    @RegisterBeanMapper(QualifiedGetterThing.class)
    @RegisterBeanMapper(QualifiedSetterThing.class)
    @RegisterBeanMapper(QualifiedSetterParamThing.class)
    @RegisterConstructorMapper(QualifiedConstructorParamThing.class)
    @RegisterFieldMapper(QualifiedFieldThing.class)
    public interface MapToDao {
        @SqlQuery("select * from something where id = :id")
        <T> T get(int id, @MapTo
        Class<T> mapTo);
    }

    @Test
    public void singleValue() {
        handle.execute("CREATE TABLE stuff (id INT, name VARCHAR)");
        handle.execute("INSERT INTO stuff (id, name) VALUES (1, 'abc,123,xyz')");
        handle.execute("INSERT INTO stuff (id, name) VALUES (2, 'foo,bar,baz')");
        TestQualifiers.SingleValueDao singleValueDao = handle.attach(TestQualifiers.SingleValueDao.class);
        assertThat(singleValueDao.multipleRows()).containsExactly("zyx,321,cba", "zab,rab,oof");
        assertThat(singleValueDao.singleRow()).containsExactly("cba", "321", "zyx");
    }

    // split comma separated list and reverse each element
    @Reversed
    public static class ReversedListStringMapper implements ColumnMapper<List<String>> {
        @Override
        public List<String> map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
            return Splitter.on(",").splitToList(r.getString(columnNumber)).stream().map(Reverser::reverse).collect(Collectors.toList());
        }
    }

    @RegisterColumnMapper(ReversedStringMapper.class)
    @RegisterColumnMapper(TestQualifiers.ReversedListStringMapper.class)
    public interface SingleValueDao {
        @SqlQuery("select name from stuff order by id")
        @Reversed
        List<String> multipleRows();

        @SqlQuery("select name from stuff order by id")
        @Reversed
        @SingleValue
        List<String> singleRow();
    }
}

