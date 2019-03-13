/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.stat.internal;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.hibernate.SessionFactory;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class ConcurrentStatisticsTest extends BaseCoreFunctionalTestCase {
    private static final String REGION_PREFIX = "my-app";

    private static final String TRIVIAL_REGION_NAME = "noname";

    private StatisticsImpl statistics;

    private SessionFactory sessionFactory;

    @Test
    public void testThatGetSecondLevelCacheStatisticsWhenSecondLevelCacheIsNotEnabledReturnsNull() {
        final SecondLevelCacheStatistics secondLevelCacheStatistics = statistics.getSecondLevelCacheStatistics(StringHelper.qualify(ConcurrentStatisticsTest.REGION_PREFIX, ConcurrentStatisticsTest.TRIVIAL_REGION_NAME));
        Assert.assertThat(secondLevelCacheStatistics, Is.is(IsNull.nullValue()));
    }
}

