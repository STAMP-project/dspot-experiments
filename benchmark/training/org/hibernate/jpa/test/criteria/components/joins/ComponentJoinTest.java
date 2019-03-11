/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.components.joins;


import JoinType.LEFT;
import javax.persistence.criteria.Join;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


public class ComponentJoinTest extends BaseEntityManagerFunctionalTestCase {
    public static final String THEVALUE = "thevalue";

    interface JoinBuilder {
        Join<EmbeddedType, ManyToOneType> buildJoinToManyToOneType(Join<Entity, EmbeddedType> source);
    }

    @Test
    public void getResultWithStringPropertyDerivedPath() {
        doTest(new ComponentJoinTest.JoinBuilder() {
            @Override
            public Join<EmbeddedType, ManyToOneType> buildJoinToManyToOneType(Join<Entity, EmbeddedType> source) {
                return source.join("manyToOneType", LEFT);
            }
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getResultWithMetamodelDerivedPath() {
        doTest(new ComponentJoinTest.JoinBuilder() {
            @Override
            public Join<EmbeddedType, ManyToOneType> buildJoinToManyToOneType(Join<Entity, EmbeddedType> source) {
                final SingularAttribute<EmbeddedType, ManyToOneType> attr = ((SingularAttribute<EmbeddedType, ManyToOneType>) (entityManagerFactory().getMetamodel().managedType(EmbeddedType.class).getDeclaredSingularAttribute("manyToOneType")));
                return source.join(attr, LEFT);
            }
        });
    }
}

