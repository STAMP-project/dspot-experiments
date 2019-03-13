/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests issues HHH-11624. The fix is also for HHH-10747 (and HHH-11476) and is a change on the enhanced setter.
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-10747")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyLoadingByEnhancerSetterTest extends BaseCoreFunctionalTestCase {
    private LazyLoadingByEnhancerSetterTest.Item item;

    private LazyLoadingByEnhancerSetterTest.Item mergedItem;

    @Test
    public void testField() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.ItemField input = new org.hibernate.test.bytecode.enhancement.lazy.ItemField();
            input.name = "F";
            input.parameters = new HashMap<>();
            input.parameters.put("aaa", "AAA");
            input.parameters.put("bbb", "BBB");
            s.persist(input);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // A parameters map is created with the class and is being compared to the persistent map (by the generated code) -- it shouldn't
            item = s.find(.class, "F");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            mergedItem = ((org.hibernate.test.bytecode.enhancement.lazy.Item) (s.merge(item)));
        });
        Assert.assertEquals(2, mergedItem.getParameters().size());
    }

    @Test
    @FailureExpected(jiraKey = "HHH-10747")
    public void testProperty() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.ItemProperty input = new org.hibernate.test.bytecode.enhancement.lazy.ItemProperty();
            input.setName("P");
            Map<String, String> parameters = new HashMap<>();
            parameters.put("ccc", "CCC");
            parameters.put("ddd", "DDD");
            input.setParameters(parameters);
            s.persist(input);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // A parameters map is created with the class and is being compared to the persistent map (by the generated code) -- it shouldn't
            item = s.find(.class, "P");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            mergedItem = ((org.hibernate.test.bytecode.enhancement.lazy.Item) (s.merge(item)));
        });
        Assert.assertEquals(2, mergedItem.getParameters().size());
    }

    // --- //
    private interface Item {
        java.util.Map<String, String> getParameters();
    }

    @Entity
    @Table(name = "ITEM_F")
    private static class ItemField implements LazyLoadingByEnhancerSetterTest.Item {
        @Id
        @Column(nullable = false)
        private String name;

        @ElementCollection(fetch = FetchType.EAGER)
        @MapKeyColumn(name = "NAME")
        @Lob
        @Column(name = "VALUE", length = 65535)
        private java.util.Map<String, String> parameters = new HashMap<>();

        @Override
        public java.util.Map<String, String> getParameters() {
            return parameters;
        }
    }

    @Entity
    @Table(name = "ITEM_P")
    private static class ItemProperty implements LazyLoadingByEnhancerSetterTest.Item {
        private String aName;

        private java.util.Map<String, String> parameterMap = new HashMap<>();

        @Id
        @Column(nullable = false)
        public String getName() {
            return aName;
        }

        public void setName(String name) {
            this.aName = name;
        }

        @ElementCollection(fetch = FetchType.EAGER)
        @MapKeyColumn(name = "NAME")
        @Lob
        @Column(name = "VALUE", length = 65535)
        @Override
        public java.util.Map<String, String> getParameters() {
            return parameterMap;
        }

        public void setParameters(java.util.Map<String, String> parameters) {
            this.parameterMap = parameters;
        }
    }
}

