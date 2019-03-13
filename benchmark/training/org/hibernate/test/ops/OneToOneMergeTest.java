/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author localEvg
 */
@TestForIssue(jiraKey = "HHH-12436")
public class OneToOneMergeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMerge() throws Exception {
        Long primaId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.ops.Prima prima = new org.hibernate.test.ops.Prima();
            prima.setOptionalData(null);
            entityManager.persist(prima);
            return prima.getId();
        });
        Assert.assertNotNull(primaId);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.ops.Prima prima = entityManager.find(.class, primaId);
            org.hibernate.test.ops.Secunda sec = new org.hibernate.test.ops.Secunda();
            sec.setParent(prima);
            prima.setOptionalData(sec);
            org.hibernate.test.ops.Prima mergedPrima = entityManager.merge(prima);
            assertNotNull(mergedPrima);
        });
    }

    @Entity(name = "Prima")
    public static class Prima implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        // @PrimaryKeyJoinColumn
        @OneToOne(mappedBy = "parent", optional = true, cascade = CascadeType.ALL)
        private OneToOneMergeTest.Secunda optionalData;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OneToOneMergeTest.Secunda getOptionalData() {
            return optionalData;
        }

        public void setOptionalData(OneToOneMergeTest.Secunda optionalData) {
            this.optionalData = optionalData;
        }
    }

    @Entity(name = "Secunda")
    public static class Secunda implements Serializable {
        @Id
        @Column(name = "id", nullable = false)
        private Long id;

        @MapsId
        @OneToOne(optional = false)
        @JoinColumn(name = "id", nullable = false)
        private OneToOneMergeTest.Prima parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OneToOneMergeTest.Prima getParent() {
            return parent;
        }

        public void setParent(OneToOneMergeTest.Prima parent) {
            this.parent = parent;
        }
    }
}

