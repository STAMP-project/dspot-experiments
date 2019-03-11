/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.notfound;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11591")
public class OneToOneNotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOne() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.annotations.notfound.Show show2 = session.find(.class, 1);
            assertNotNull(show2);
            assertNull(show2.getDescription());
        });
    }

    @Entity(name = "Show")
    @Table(name = "T_SHOW")
    public static class Show {
        @Id
        private Integer id;

        @OneToOne
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinTable(name = "TSHOW_SHOWDESCRIPTION", joinColumns = @JoinColumn(name = "SHOW_ID"), inverseJoinColumns = @JoinColumn(name = "DESCRIPTION_ID"), foreignKey = @ForeignKey(name = "FK_DESC"))
        private OneToOneNotFoundTest.ShowDescription description;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public OneToOneNotFoundTest.ShowDescription getDescription() {
            return description;
        }

        public void setDescription(OneToOneNotFoundTest.ShowDescription description) {
            this.description = description;
            description.setShow(this);
        }
    }

    @Entity(name = "ShowDescription")
    @Table(name = "SHOW_DESCRIPTION")
    public static class ShowDescription {
        @Id
        @Column(name = "ID")
        private Integer id;

        @NotFound(action = NotFoundAction.IGNORE)
        @OneToOne(mappedBy = "description", cascade = CascadeType.ALL)
        private OneToOneNotFoundTest.Show show;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public OneToOneNotFoundTest.Show getShow() {
            return show;
        }

        public void setShow(OneToOneNotFoundTest.Show show) {
            this.show = show;
        }
    }
}

