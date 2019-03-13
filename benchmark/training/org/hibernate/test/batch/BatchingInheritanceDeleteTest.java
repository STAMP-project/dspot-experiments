/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batch;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12470")
public class BatchingInheritanceDeleteTest extends BaseCoreFunctionalTestCase {
    // @FailureExpected( jiraKey = "HHH-12470" )
    @Test
    public void testDelete() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.batch.Bar bar = new org.hibernate.test.batch.Bar("bar");
            org.hibernate.test.batch.Foo foo = new org.hibernate.test.batch.Foo("foo");
            foo.setBar(bar);
            s.persist(foo);
            s.persist(bar);
            s.flush();
            s.remove(foo);
            s.remove(bar);
            s.flush();
            assertThat(s.find(.class, foo.getId()), nullValue());
            assertThat(s.find(.class, bar.getId()), nullValue());
        });
    }

    @Entity(name = "Bar")
    public static class Bar extends BatchingInheritanceDeleteTest.AbstractBar {
        @Column(nullable = false)
        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        List<BatchingInheritanceDeleteTest.Baz> bazList = new ArrayList<>();

        public Bar() {
            super();
        }

        public Bar(final String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public List<BatchingInheritanceDeleteTest.Baz> getBazList() {
            return bazList;
        }

        public void setBazList(final List<BatchingInheritanceDeleteTest.Baz> bazList) {
            this.bazList = bazList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Bar [name=").append(name).append("]");
            return builder.toString();
        }
    }

    @Entity(name = "Baz")
    public static class Baz {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        @Column(nullable = false)
        private String name;

        public Baz() {
            super();
        }

        public Baz(final String name) {
            super();
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(final long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Bar [name=").append(name).append("]");
            return builder.toString();
        }
    }

    @Entity(name = "Foo")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Foo extends BatchingInheritanceDeleteTest.AbstractFoo {
        public Foo() {
            super();
        }

        public Foo(final String name) {
            super();
            this.name = name;
        }

        @Column(name = "NAME")
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "BAR_ID")
        private BatchingInheritanceDeleteTest.Bar bar;

        public BatchingInheritanceDeleteTest.Bar getBar() {
            return bar;
        }

        public void setBar(final BatchingInheritanceDeleteTest.Bar bar) {
            this.bar = bar;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    @Entity(name = "AbstractBar")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class AbstractBar {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        public long getId() {
            return id;
        }

        public void setId(final long id) {
            this.id = id;
        }
    }

    @MappedSuperclass
    public static class AbstractFoo {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;

        public long getId() {
            return id;
        }

        public void setId(final long id) {
            this.id = id;
        }
    }
}

