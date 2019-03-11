/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cache;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class QualifiedRegionNameHandlingTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final String PREFIX = "app1";

    private static final String LOCAL_NAME = "a.b.c";

    @Test
    public void testValidCall() {
        MatcherAssert.assertThat(sessionFactory().getCache().unqualifyRegionName((((QualifiedRegionNameHandlingTest.PREFIX) + '.') + (QualifiedRegionNameHandlingTest.LOCAL_NAME))), CoreMatchers.is(QualifiedRegionNameHandlingTest.LOCAL_NAME));
    }

    @Test
    public void testUnqualifiedNameUsed() {
        try {
            sessionFactory().getCache().unqualifyRegionName(QualifiedRegionNameHandlingTest.LOCAL_NAME);
        } catch (IllegalArgumentException expected) {
        }
    }
}

