/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.merge;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-11459")
@RunWith(BytecodeEnhancerRunner.class)
public class MergeEnhancedEntityTest extends BaseCoreFunctionalTestCase {
    private MergeEnhancedEntityTest.Person person;

    @Test
    public void testMerge() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.merge.Person entity = s.find(.class, 1L);
            entity.name = "John";
            try {
                s.merge(entity);
            } catch ( e) {
                fail(("Enhanced entity can't be merged: " + (e.getMessage())));
            }
        });
    }

    @Test
    public void testRefresh() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.merge.Person entity = s.find(.class, 1L);
            entity.name = "John";
            try {
                s.refresh(entity);
            } catch ( e) {
                fail(("Enhanced entity can't be refreshed: " + (e.getMessage())));
            }
        });
    }

    // --- //
    @Entity
    @Table(name = "PERSON")
    private static class Person {
        @Id
        Long id;

        @Column(name = "name", length = 10, nullable = false)
        String name;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
        List<MergeEnhancedEntityTest.PersonAddress> details = new ArrayList<>();

        Person() {
        }

        Person(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity
    @Table(name = "PERSON_ADDRESS")
    private static class PersonAddress {
        @Id
        Long id;

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        MergeEnhancedEntityTest.Person parent;
    }
}

