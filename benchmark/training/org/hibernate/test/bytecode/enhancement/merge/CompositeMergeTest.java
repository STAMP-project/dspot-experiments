/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.merge;


import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@RunWith(BytecodeEnhancerRunner.class)
public class CompositeMergeTest extends BaseCoreFunctionalTestCase {
    private long entityId;

    @Test
    public void test() {
        CompositeMergeTest.ParentEntity[] parent = new CompositeMergeTest.ParentEntity[3];
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            parent[0] = s.get(.class, entityId);
        });
        checkDirtyTracking(parent[0]);
        parent[0].address.country.name = "Paraguai";
        checkDirtyTracking(parent[0], "address.country");
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            parent[1] = ((org.hibernate.test.bytecode.enhancement.merge.ParentEntity) (s.merge(parent[0])));
            checkDirtyTracking(parent[0], "address.country");
            checkDirtyTracking(parent[1], "address.country");
        });
        checkDirtyTracking(parent[0], "address.country");
        checkDirtyTracking(parent[1]);
        parent[1].address.country.name = "Honduras";
        checkDirtyTracking(parent[1], "address.country");
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.saveOrUpdate(parent[1]);
            checkDirtyTracking(parent[1], "address.country");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            parent[2] = s.get(.class, entityId);
            Assert.assertEquals("Honduras", parent[2].address.country.name);
        });
    }

    // --- //
    @Entity
    @Table(name = "PARENT_ENTITY")
    private static class ParentEntity {
        @Id
        @GeneratedValue
        Long id;

        String description;

        @Embedded
        CompositeMergeTest.Address address;

        @Basic(fetch = FetchType.LAZY)
        byte[] lazyField;
    }

    @Embeddable
    @Table(name = "ADDRESS")
    private static class Address {
        String street;

        @Embedded
        CompositeMergeTest.Country country;
    }

    @Embeddable
    @Table(name = "COUNTRY")
    private static class Country {
        String name;

        @ElementCollection
        @CollectionTable(name = "languages", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
        List<String> languages;
    }
}

