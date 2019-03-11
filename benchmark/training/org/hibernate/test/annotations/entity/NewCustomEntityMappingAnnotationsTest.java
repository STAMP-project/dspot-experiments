/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.entity;


import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.mapping.RootClass;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class NewCustomEntityMappingAnnotationsTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private Metadata metadata;

    @Test
    public void testSameMappingValues() {
        RootClass forest = ((RootClass) (metadata.getEntityBinding(Forest.class.getName())));
        RootClass forest2 = ((RootClass) (metadata.getEntityBinding(Forest2.class.getName())));
        Assert.assertEquals(forest.useDynamicInsert(), forest2.useDynamicInsert());
        Assert.assertEquals(forest.useDynamicUpdate(), forest2.useDynamicUpdate());
        Assert.assertEquals(forest.hasSelectBeforeUpdate(), forest2.hasSelectBeforeUpdate());
        Assert.assertEquals(forest.getOptimisticLockStyle(), forest2.getOptimisticLockStyle());
        Assert.assertEquals(forest.isExplicitPolymorphism(), forest2.isExplicitPolymorphism());
    }
}

