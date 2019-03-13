/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11596")
public class OneToOneJoinTableOptionalTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSavingEntitiesWithANullOneToOneAssociationValue() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.onetoone.Show show = new org.hibernate.test.annotations.onetoone.Show();
            session.save(show);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.onetoone.ShowDescription showDescription = new org.hibernate.test.annotations.onetoone.ShowDescription();
            session.save(showDescription);
        });
    }

    @Entity(name = "Show")
    @Table(name = "T_SHOW")
    public static class Show {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToOne
        @JoinTable(name = "TSHOW_SHOWDESCRIPTION", joinColumns = @JoinColumn(name = "SHOW_ID"), inverseJoinColumns = @JoinColumn(name = "DESCRIPTION_ID"), foreignKey = @ForeignKey(name = "FK_DESC"))
        private OneToOneJoinTableOptionalTest.ShowDescription description;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public OneToOneJoinTableOptionalTest.ShowDescription getDescription() {
            return description;
        }

        public void setDescription(OneToOneJoinTableOptionalTest.ShowDescription description) {
            this.description = description;
            description.setShow(this);
        }
    }

    @Entity(name = "ShowDescription")
    @Table(name = "SHOW_DESCRIPTION")
    public static class ShowDescription {
        @Id
        @Column(name = "ID")
        @GeneratedValue
        private Integer id;

        @OneToOne(mappedBy = "description", cascade = CascadeType.ALL)
        private OneToOneJoinTableOptionalTest.Show show;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public OneToOneJoinTableOptionalTest.Show getShow() {
            return show;
        }

        public void setShow(OneToOneJoinTableOptionalTest.Show show) {
            this.show = show;
        }
    }
}

