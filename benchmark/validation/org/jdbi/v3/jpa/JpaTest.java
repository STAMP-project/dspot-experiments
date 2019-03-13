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
package org.jdbi.v3.jpa;


import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.jdbi.v3.core.qualifier.Reversed;
import org.jdbi.v3.core.qualifier.ReversedStringArgumentFactory;
import org.jdbi.v3.core.qualifier.ReversedStringMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapperFactory;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class JpaTest {
    private static final String INSERT_BY_PROPERTY_NAME = "insert into something(id, name) values (:id, :name)";

    private static final String SELECT_BY_PROPERTY_NAME = "select id, name from something";

    private static final String INSERT_BY_ANNOTATION_NAME = "insert into something (id, name) values (:foo, :bar)";

    private static final String SELECT_BY_ANNOTATION_NAME = "select id as foo, name as bar from something";

    private static final String ID_ANNOTATION_NAME = "foo";

    private static final String NAME_ANNOTATION_NAME = "bar";

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    interface Thing {
        int getId();

        String getName();
    }

    @Entity
    static class EntityThing implements JpaTest.Thing {
        private int id;

        private String name;

        EntityThing() {
        }

        EntityThing(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface EntityThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.EntityThing thing);

        @SqlQuery(JpaTest.SELECT_BY_PROPERTY_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.EntityThing> list();
    }

    @Test
    public void testEntityNoColumnAnnotations() {
        JpaTest.EntityThing brian = new JpaTest.EntityThing(1, "Brian");
        JpaTest.EntityThing keith = new JpaTest.EntityThing(2, "Keith");
        JpaTest.EntityThingDao dao = dbRule.getSharedHandle().attach(JpaTest.EntityThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.EntityThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class FieldThing implements JpaTest.Thing {
        @Column
        private int id;

        @Column
        private String name;

        FieldThing() {
        }

        FieldThing(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface FieldThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.FieldThing thing);

        @SqlQuery(JpaTest.SELECT_BY_PROPERTY_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.FieldThing> list();
    }

    @Test
    public void testField() {
        JpaTest.FieldThing brian = new JpaTest.FieldThing(1, "Brian");
        JpaTest.FieldThing keith = new JpaTest.FieldThing(2, "Keith");
        JpaTest.FieldThingDao dao = dbRule.getSharedHandle().attach(JpaTest.FieldThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.FieldThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class NamedFieldThing implements JpaTest.Thing {
        @Column(name = "foo")
        private int id;

        @Column(name = "bar")
        private String name;

        NamedFieldThing() {
        }

        NamedFieldThing(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface NamedFieldThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_ANNOTATION_NAME)
        void insert(@BindJpa
        JpaTest.NamedFieldThing thing);

        @SqlQuery(JpaTest.SELECT_BY_ANNOTATION_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.NamedFieldThing> list();
    }

    @Test
    public void testNamedField() {
        JpaTest.NamedFieldThing brian = new JpaTest.NamedFieldThing(1, "Brian");
        JpaTest.NamedFieldThing keith = new JpaTest.NamedFieldThing(2, "Keith");
        JpaTest.NamedFieldThingDao dao = dbRule.getSharedHandle().attach(JpaTest.NamedFieldThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.NamedFieldThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class GetterThing implements JpaTest.Thing {
        private int id;

        private String name;

        GetterThing() {
        }

        GetterThing(int id, String name) {
            setId(id);
            setName(name);
        }

        @Column
        public int getId() {
            return id;
        }

        @Column
        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface GetterThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.GetterThing thing);

        @SqlQuery(JpaTest.SELECT_BY_PROPERTY_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.GetterThing> list();
    }

    @Test
    public void testGetter() {
        JpaTest.GetterThing brian = new JpaTest.GetterThing(1, "Brian");
        JpaTest.GetterThing keith = new JpaTest.GetterThing(2, "Keith");
        JpaTest.GetterThingDao dao = dbRule.getSharedHandle().attach(JpaTest.GetterThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.GetterThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class NamedGetterThing implements JpaTest.Thing {
        private int id;

        private String name;

        NamedGetterThing() {
        }

        NamedGetterThing(int id, String name) {
            setId(id);
            setName(name);
        }

        @Column(name = "foo")
        public int getId() {
            return id;
        }

        @Column(name = "bar")
        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface NamedGetterThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_ANNOTATION_NAME)
        void insert(@BindJpa
        JpaTest.NamedGetterThing thing);

        @SqlQuery(JpaTest.SELECT_BY_ANNOTATION_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.NamedGetterThing> list();
    }

    @Test
    public void testNamedGetter() {
        JpaTest.NamedGetterThing brian = new JpaTest.NamedGetterThing(1, "Brian");
        JpaTest.NamedGetterThing keith = new JpaTest.NamedGetterThing(2, "Keith");
        JpaTest.NamedGetterThingDao dao = dbRule.getSharedHandle().attach(JpaTest.NamedGetterThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.NamedGetterThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class SetterThing implements JpaTest.Thing {
        private int id;

        private String name;

        SetterThing() {
        }

        SetterThing(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Column
        public void setId(int id) {
            this.id = id;
        }

        @Column
        public void setName(String name) {
            this.name = name;
        }
    }

    public interface SetterThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.SetterThing thing);

        @SqlQuery(JpaTest.SELECT_BY_PROPERTY_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.SetterThing> list();
    }

    @Test
    public void testSetter() {
        JpaTest.SetterThing brian = new JpaTest.SetterThing(1, "Brian");
        JpaTest.SetterThing keith = new JpaTest.SetterThing(2, "Keith");
        JpaTest.SetterThingDao dao = dbRule.getSharedHandle().attach(JpaTest.SetterThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.SetterThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class NamedSetterThing implements JpaTest.Thing {
        private int id;

        private String name;

        NamedSetterThing() {
        }

        NamedSetterThing(int id, String name) {
            setId(id);
            setName(name);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Column(name = "foo")
        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "bar")
        public void setName(String name) {
            this.name = name;
        }
    }

    public interface NamedSetterThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_ANNOTATION_NAME)
        void insert(@BindJpa
        JpaTest.NamedSetterThing thing);

        @SqlQuery(JpaTest.SELECT_BY_ANNOTATION_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.NamedSetterThing> list();
    }

    @Test
    public void testNamedSetter() {
        JpaTest.NamedSetterThing brian = new JpaTest.NamedSetterThing(1, "Brian");
        JpaTest.NamedSetterThing keith = new JpaTest.NamedSetterThing(2, "Keith");
        JpaTest.NamedSetterThingDao dao = dbRule.getSharedHandle().attach(JpaTest.NamedSetterThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.NamedSetterThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @MappedSuperclass
    static class MappedSuperclassThing {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Entity
    static class ExtendsMappedSuperclassThing extends JpaTest.MappedSuperclassThing implements JpaTest.Thing {
        ExtendsMappedSuperclassThing() {
        }

        ExtendsMappedSuperclassThing(int id, String name) {
            setId(id);
            setName(name);
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface MappedSuperclassThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.ExtendsMappedSuperclassThing thing);

        @SqlQuery(JpaTest.SELECT_BY_PROPERTY_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.ExtendsMappedSuperclassThing> list();
    }

    @Test
    public void testMappedSuperclass() {
        JpaTest.ExtendsMappedSuperclassThing brian = new JpaTest.ExtendsMappedSuperclassThing(1, "Brian");
        JpaTest.ExtendsMappedSuperclassThing keith = new JpaTest.ExtendsMappedSuperclassThing(2, "Keith");
        JpaTest.MappedSuperclassThingDao dao = dbRule.getSharedHandle().attach(JpaTest.MappedSuperclassThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.ExtendsMappedSuperclassThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Entity
    static class AnnotationPriorityThing implements JpaTest.Thing {
        @Column(name = JpaTest.ID_ANNOTATION_NAME)
        private int id;

        private String name;

        AnnotationPriorityThing() {
        }

        AnnotationPriorityThing(int id, String name) {
            setId(id);
            setName(name);
        }

        @Column(name = "ignored")
        public int getId() {
            return id;
        }

        @Column(name = JpaTest.NAME_ANNOTATION_NAME)
        public String getName() {
            return name;
        }

        @Column(name = "ignored")
        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "ignored")
        public void setName(String name) {
            this.name = name;
        }
    }

    public interface AnnotationPriorityThingDao {
        @SqlUpdate(JpaTest.INSERT_BY_ANNOTATION_NAME)
        void insert(@BindJpa
        JpaTest.AnnotationPriorityThing thing);

        @SqlQuery(JpaTest.SELECT_BY_ANNOTATION_NAME)
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.AnnotationPriorityThing> list();
    }

    @Test
    public void testAnnotationPriority() {
        // fields before getters before setters
        JpaTest.AnnotationPriorityThing brian = new JpaTest.AnnotationPriorityThing(1, "Brian");
        JpaTest.AnnotationPriorityThing keith = new JpaTest.AnnotationPriorityThing(2, "Keith");
        JpaTest.AnnotationPriorityThingDao dao = dbRule.getSharedHandle().attach(JpaTest.AnnotationPriorityThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.AnnotationPriorityThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    public interface SuperfluousColumnDao {
        @SqlUpdate(JpaTest.INSERT_BY_PROPERTY_NAME)
        void insert(@BindJpa
        JpaTest.FieldThing thing);

        @SqlQuery("select id, name, 'Rob Schneider' as extra from something")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.FieldThing> list();
    }

    @Test
    public void testMapWithSuperfluousColumn() {
        JpaTest.FieldThing brian = new JpaTest.FieldThing(1, "Brian");
        JpaTest.FieldThing keith = new JpaTest.FieldThing(2, "Keith");
        JpaTest.SuperfluousColumnDao dao = dbRule.getSharedHandle().attach(JpaTest.SuperfluousColumnDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.FieldThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    public interface MissingColumnDao {
        @SqlUpdate("insert into something(id) values (:id)")
        void insert(@BindJpa
        JpaTest.FieldThing thing);

        @SqlQuery("select id from something")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.FieldThing> list();
    }

    @Test
    public void testMissingColumn() {
        JpaTest.FieldThing brian = new JpaTest.FieldThing(1, "Brian");
        JpaTest.FieldThing keith = new JpaTest.FieldThing(2, "Keith");
        JpaTest.MissingColumnDao dao = dbRule.getSharedHandle().attach(JpaTest.MissingColumnDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.FieldThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(new JpaTest.FieldThing(1, null), new JpaTest.FieldThing(2, null));
    }

    @MappedSuperclass
    static class OverriddenSuperclassThing implements JpaTest.Thing {
        @Column(name = "foo")
        private int id;

        @Column(name = "bar")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity
    static class OverridingSubclassThing extends JpaTest.OverriddenSuperclassThing {
        OverridingSubclassThing() {
        }

        OverridingSubclassThing(int id, String name) {
            setId(id);
            setName(name);
        }

        @Override
        @Column(name = "meow")
        public int getId() {
            return super.getId();
        }
    }

    public interface OverridingSubclassThingDao {
        @SqlUpdate("insert into something(id, name) values (:meow, :bar)")
        void insert(@BindJpa
        JpaTest.OverridingSubclassThing thing);

        @SqlQuery("select id as meow, name as bar from something")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        List<JpaTest.OverridingSubclassThing> list();
    }

    @Test
    public void subclassAnnotationOverridesSuperclass() {
        // Annotated takes precedence over no annotation, even if annotated in superclass
        // Annotated member in subclass takes precedence over annotated member in superclass
        JpaTest.OverridingSubclassThing brian = new JpaTest.OverridingSubclassThing(1, "Brian");
        JpaTest.OverridingSubclassThing keith = new JpaTest.OverridingSubclassThing(2, "Keith");
        JpaTest.OverridingSubclassThingDao dao = dbRule.getSharedHandle().attach(JpaTest.OverridingSubclassThingDao.class);
        dao.insert(brian);
        dao.insert(keith);
        List<JpaTest.OverridingSubclassThing> rs = dao.list();
        JpaTest.assertThatThing(rs).containsOnlyOnce(brian, keith);
    }

    @Test
    public void qualifiedField() {
        dbRule.getJdbi().useHandle(( handle) -> {
            handle.execute("insert into something(id, name) values (1, 'abc')");
            org.jdbi.v3.jpa.QualifiedFieldDao dao = handle.attach(.class);
            assertThat(dao.get(1)).isEqualTo(new org.jdbi.v3.jpa.QualifiedFieldThing(1, "cba"));
            dao.insert(new org.jdbi.v3.jpa.QualifiedFieldThing(2, "xyz"));
            assertThat(handle.select("SELECT name FROM something WHERE id = 2").mapTo(.class).findOnly()).isEqualTo("zyx");
        });
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface QualifiedFieldDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindJpa
        JpaTest.QualifiedFieldThing thing);

        @SqlQuery("select * from something where id = :id")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        JpaTest.QualifiedFieldThing get(int id);
    }

    @Entity
    public static class QualifiedFieldThing {
        @Column
        private int id;

        @Reversed
        @Column
        private String name;

        public QualifiedFieldThing() {
        }

        public QualifiedFieldThing(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JpaTest.QualifiedFieldThing that = ((JpaTest.QualifiedFieldThing) (o));
            return ((id) == (that.id)) && (Objects.equals(name, that.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("QualifiedFieldThing{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }

    @Test
    public void qualifiedGetter() {
        dbRule.getJdbi().useHandle(( handle) -> {
            handle.execute("insert into something(id, name) values (1, 'abc')");
            org.jdbi.v3.jpa.QualifiedGetterDao dao = handle.attach(.class);
            assertThat(dao.get(1)).isEqualTo(new org.jdbi.v3.jpa.QualifiedGetterThing(1, "cba"));
            dao.insert(new org.jdbi.v3.jpa.QualifiedGetterThing(2, "xyz"));
            assertThat(handle.select("SELECT name FROM something WHERE id = 2").mapTo(.class).findOnly()).isEqualTo("zyx");
        });
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface QualifiedGetterDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindJpa
        JpaTest.QualifiedGetterThing thing);

        @SqlQuery("select * from something where id = :id")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        JpaTest.QualifiedGetterThing get(int id);
    }

    @Entity
    public static class QualifiedGetterThing {
        private int id;

        private String name;

        public QualifiedGetterThing() {
        }

        public QualifiedGetterThing(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Column
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Reversed
        @Column
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JpaTest.QualifiedGetterThing that = ((JpaTest.QualifiedGetterThing) (o));
            return ((id) == (that.id)) && (Objects.equals(name, that.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("QualifiedGetterThing{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }

    @Test
    public void qualifiedSetter() {
        dbRule.getJdbi().useHandle(( handle) -> {
            handle.execute("insert into something(id, name) values (1, 'abc')");
            org.jdbi.v3.jpa.QualifiedSetterDao dao = handle.attach(.class);
            assertThat(dao.get(1)).isEqualTo(new org.jdbi.v3.jpa.QualifiedSetterThing(1, "cba"));
            dao.insert(new org.jdbi.v3.jpa.QualifiedSetterThing(2, "xyz"));
            assertThat(handle.select("SELECT name FROM something WHERE id = 2").mapTo(.class).findOnly()).isEqualTo("zyx");
        });
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface QualifiedSetterDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindJpa
        JpaTest.QualifiedSetterThing thing);

        @SqlQuery("select * from something where id = :id")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        JpaTest.QualifiedSetterThing get(int id);
    }

    @Entity
    public static class QualifiedSetterThing {
        private int id;

        private String name;

        public QualifiedSetterThing() {
        }

        public QualifiedSetterThing(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Column
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        @Reversed
        @Column
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JpaTest.QualifiedSetterThing that = ((JpaTest.QualifiedSetterThing) (o));
            return ((id) == (that.id)) && (Objects.equals(name, that.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("QualifiedSetterThing{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }

    @Test
    public void qualifiedSetterParam() {
        dbRule.getJdbi().useHandle(( handle) -> {
            handle.execute("insert into something(id, name) values (1, 'abc')");
            org.jdbi.v3.jpa.QualifiedSetterParamDao dao = handle.attach(.class);
            assertThat(dao.get(1)).isEqualTo(new org.jdbi.v3.jpa.QualifiedSetterParamThing(1, "cba"));
            dao.insert(new org.jdbi.v3.jpa.QualifiedSetterParamThing(2, "xyz"));
            assertThat(handle.select("SELECT name FROM something WHERE id = 2").mapTo(.class).findOnly()).isEqualTo("zyx");
        });
    }

    @RegisterArgumentFactory(ReversedStringArgumentFactory.class)
    @RegisterColumnMapper(ReversedStringMapper.class)
    public interface QualifiedSetterParamDao {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindJpa
        JpaTest.QualifiedSetterParamThing thing);

        @SqlQuery("select * from something where id = :id")
        @RegisterRowMapperFactory(JpaMapperFactory.class)
        JpaTest.QualifiedSetterParamThing get(int id);
    }

    @Entity
    public static class QualifiedSetterParamThing {
        private int id;

        private String name;

        public QualifiedSetterParamThing() {
        }

        public QualifiedSetterParamThing(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Column
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        @Column
        public void setName(@Reversed
        String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JpaTest.QualifiedSetterParamThing that = ((JpaTest.QualifiedSetterParamThing) (o));
            return ((id) == (that.id)) && (Objects.equals(name, that.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return ((((("QualifiedSetterParamThing{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }
    }
}

