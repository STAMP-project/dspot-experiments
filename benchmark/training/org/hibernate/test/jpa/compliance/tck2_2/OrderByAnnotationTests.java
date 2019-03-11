/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
// @Override
// protected void applyMetadataSources(MetadataSources metadataSources) {
// super.applyMetadataSources( metadataSources );
// metadataSources.addAnnotatedClass( A.class );
// }
// 
// @Entity( name = "A" )
// @Table( name = "T_A" )
// public static class A {
// @Id
// public Integer id;
// @ElementCollection
// @Column( name = "name" )
// @OrderBy
// public List<String> names;
// }
public class OrderByAnnotationTests extends BaseNonConfigCoreFunctionalTestCase {
    private static final String ELEMENT_TOKEN = "$element$";

    private static final String TABLE_ALIAS = "a";

    private static final String COLUMN_NAME = "name";

    @Test
    public void testOrderByEmpty() {
        MatcherAssert.assertThat(translate(""), CoreMatchers.is(((((OrderByAnnotationTests.TABLE_ALIAS) + '.') + (OrderByAnnotationTests.COLUMN_NAME)) + " asc")));
    }

    @Test
    public void testOrderByJustDesc() {
        MatcherAssert.assertThat(translate("desc"), CoreMatchers.is(((((OrderByAnnotationTests.TABLE_ALIAS) + '.') + (OrderByAnnotationTests.COLUMN_NAME)) + " desc")));
        MatcherAssert.assertThat(translate("DESC"), CoreMatchers.is(((((OrderByAnnotationTests.TABLE_ALIAS) + '.') + (OrderByAnnotationTests.COLUMN_NAME)) + " desc")));
    }

    @Test
    public void testOrderByJustAsc() {
        MatcherAssert.assertThat(translate("asc"), CoreMatchers.is(((((OrderByAnnotationTests.TABLE_ALIAS) + '.') + (OrderByAnnotationTests.COLUMN_NAME)) + " asc")));
        MatcherAssert.assertThat(translate("ASC"), CoreMatchers.is(((((OrderByAnnotationTests.TABLE_ALIAS) + '.') + (OrderByAnnotationTests.COLUMN_NAME)) + " asc")));
    }
}

