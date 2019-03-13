/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.persister.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Mykhaylo Gnylorybov
 */
public abstract class AbstractSchemaSubstitutionFormulaTest extends BaseCoreFunctionalTestCase {
    protected static final String SCHEMA_PLACEHOLDER = "h-schema";

    @Test
    public void test() {
        final String className = AbstractSchemaSubstitutionFormulaTest.FooBar.class.getName();
        final AbstractEntityPersister persister = ((AbstractEntityPersister) (sessionFactory().getMetamodel().entityPersister(className)));
        final String formula = persister.getSubclassFormulaTemplateClosure()[0];
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.persister.entity.Foo foo = new org.hibernate.persister.entity.Foo();
            foo.id = 1;
            foo.name = "fooName";
            session.persist(foo);
            org.hibernate.persister.entity.Bar bar = new org.hibernate.persister.entity.Bar();
            bar.id = 2;
            bar.name = "barName";
            session.persist(bar);
            org.hibernate.persister.entity.FooBar fooBar = new org.hibernate.persister.entity.FooBar();
            fooBar.id = 3;
            fooBar.bar = bar;
            fooBar.foo = foo;
            session.persist(fooBar);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.persister.entity.FooBar entity = session.find(.class, 3);
            assertTrue("Invalid result of formula expression: ", entity.isValid);
        });
    }

    @Entity(name = "FOOBAR")
    @Table(name = "FOOBAR")
    public static class FooBar {
        @Id
        @Column(name = "ID")
        public Integer id;

        @ManyToOne
        @JoinColumn(name = "FOO_ID")
        public AbstractSchemaSubstitutionFormulaTest.Foo foo;

        @ManyToOne
        @JoinColumn(name = "BAR_ID")
        public AbstractSchemaSubstitutionFormulaTest.Bar bar;

        @Formula("CASE WHEN (\n" + ((((((((((((((((("    EXISTS (\n" + "        SELECT *\n") + "            FROM {h-schema}Foo foo\n") + "            JOIN {h-schema}FooBar fooBar\n") + "            ON foo.ID = fooBar.FOO_ID") + "            WHERE foo.name IS NOT NULL\n") + "         )\n") + "    AND\n") + "    EXISTS (\n") + "        SELECT *\n") + "            FROM {h-schema}Bar bar\n") + "            JOIN {h-schema}FooBar fooBar\n") + "            ON bar.ID = fooBar.BAR_ID\n") + "            WHERE bar.name IS NOT NULL\n") + "        ))\n") + "    THEN 1\n") + "    ELSE 0\n") + "END"))
        public Boolean isValid;
    }

    @Entity(name = "FOO")
    @Table(name = "FOO")
    public static class Foo {
        @Id
        @Column(name = "ID")
        public Integer id;

        @Column(name = "name")
        public String name;
    }

    @Entity(name = "BAR")
    @Table(name = "BAR")
    public static class Bar {
        @Id
        @Column(name = "ID")
        public Integer id;

        @Column(name = "name")
        public String name;
    }
}

