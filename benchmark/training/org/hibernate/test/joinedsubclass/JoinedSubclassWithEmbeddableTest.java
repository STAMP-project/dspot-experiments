/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.joinedsubclass;


import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class JoinedSubclassWithEmbeddableTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10920")
    public void testEmbeddedFieldIsNotNull() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.joinedsubclass.ConcreteEntity entity = session.get(.class, 1L);
            assertThat(entity.getEmbedded().getField(), is("field_embedded"));
            assertThat(entity.getField(), is("field_base"));
            entity.getEmbedded().setField("field_subclass");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.joinedsubclass.ConcreteEntity entity = session.get(.class, 1L);
            assertThat(entity.getEmbedded().getField(), is("field_subclass"));
            assertThat(entity.getField(), is("field_base"));
        });
    }

    @Entity(name = "BaseEntity")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class BaseEntity {
        @Id
        private Long id;

        private String field;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }

    @Entity(name = "ConcreteEntity")
    public static class ConcreteEntity extends JoinedSubclassWithEmbeddableTest.BaseEntity {
        private JoinedSubclassWithEmbeddableTest.EmbeddedValue embeddedValue;

        public JoinedSubclassWithEmbeddableTest.EmbeddedValue getEmbedded() {
            return embeddedValue;
        }

        public void setEmbedded(JoinedSubclassWithEmbeddableTest.EmbeddedValue embeddedValue) {
            this.embeddedValue = embeddedValue;
        }
    }

    @Embeddable
    public static class EmbeddedValue {
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

