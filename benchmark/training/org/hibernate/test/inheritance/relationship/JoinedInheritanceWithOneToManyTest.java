/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.relationship;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class JoinedInheritanceWithOneToManyTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.relationship.BuildingList buildingList = new org.hibernate.test.inheritance.relationship.BuildingList();
            buildingList.setName("ABC");
            session.persist(buildingList);
            org.hibernate.test.inheritance.relationship.BLEHome home = new org.hibernate.test.inheritance.relationship.BLEHome();
            home.setHasCtv(123);
            home.setList(buildingList);
            buildingList.getEntries().add(home);
            session.persist(home);
            org.hibernate.test.inheritance.relationship.BLENonLiving nonLiving = new org.hibernate.test.inheritance.relationship.BLENonLiving();
            nonLiving.setDelayed(true);
            nonLiving.setList(buildingList);
            buildingList.getEntries().add(nonLiving);
            session.persist(nonLiving);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.inheritance.relationship.BuildingList> buildingLists = session.createQuery("from BuildingList").getResultList();
            org.hibernate.test.inheritance.relationship.BuildingList buildingList = buildingLists.get(0);
            assertEquals(2, buildingList.getEntries().size());
        });
    }

    @MappedSuperclass
    public static class DBObject {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
        protected Integer id;

        @Temporal(TemporalType.TIMESTAMP)
        protected Date correctDate;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Date getCorrectDate() {
            return correctDate;
        }

        public void setCorrectDate(Date correctDate) {
            this.correctDate = correctDate;
        }
    }

    @Entity(name = "BuildingList")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Table(name = "TB_BUILDING_LIST")
    @SequenceGenerator(name = "seq", sequenceName = "sq_building_list_id", allocationSize = 1)
    public static class BuildingList extends JoinedInheritanceWithOneToManyTest.DBObject implements Serializable {
        @Column
        private String name;

        @OneToMany(cascade = CascadeType.ALL, mappedBy = "list")
        private Collection<JoinedInheritanceWithOneToManyTest.BuildingListEntry> entries = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Collection<JoinedInheritanceWithOneToManyTest.BuildingListEntry> getEntries() {
            return entries;
        }

        public void setEntries(Collection<JoinedInheritanceWithOneToManyTest.BuildingListEntry> entries) {
            this.entries = entries;
        }
    }

    @Entity(name = "BuildingListEntry")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Table(name = "TB_BUILDING_LIST_ENTRY")
    public static class BuildingListEntry extends JoinedInheritanceWithOneToManyTest.DBObject implements Serializable {
        @Column
        protected String comments;

        @Column
        protected Integer priority;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "list_id")
        protected JoinedInheritanceWithOneToManyTest.BuildingList list;

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public JoinedInheritanceWithOneToManyTest.BuildingList getList() {
            return list;
        }

        public void setList(JoinedInheritanceWithOneToManyTest.BuildingList list) {
            this.list = list;
        }
    }

    @Entity(name = "BLEHome")
    @Table(name = "TB_BLE_HOME")
    public static class BLEHome extends JoinedInheritanceWithOneToManyTest.BuildingListEntry {
        @Column(name = "has_ctv")
        protected Integer hasCtv;

        public Integer getHasCtv() {
            return hasCtv;
        }

        public void setHasCtv(Integer hasCtv) {
            this.hasCtv = hasCtv;
        }
    }

    @Entity(name = "BLENonLiving")
    @Table(name = "TB_BLE_NONLIVING ")
    public static class BLENonLiving extends JoinedInheritanceWithOneToManyTest.BuildingListEntry {
        @Column(name = "is_delayed")
        protected boolean delayed;

        public boolean isDelayed() {
            return delayed;
        }

        public void setDelayed(boolean delayed) {
            this.delayed = delayed;
        }
    }
}

