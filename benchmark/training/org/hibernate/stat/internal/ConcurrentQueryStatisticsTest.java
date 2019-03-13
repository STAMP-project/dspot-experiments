/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.stat.internal;


import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ConcurrentQueryStatisticsTest extends BaseUnitTestCase {
    private QueryStatisticsImpl stats = new QueryStatisticsImpl("test");

    @Test
    public void testStats() {
        Assert.assertEquals(0, stats.getExecutionTotalTime());
        Assert.assertEquals(Long.MAX_VALUE, stats.getExecutionMinTime());
        Assert.assertEquals(0, stats.getExecutionMaxTime());
        Assert.assertEquals(0, stats.getExecutionAvgTime());
        stats.executed(1000, 12);
        Assert.assertEquals(12, stats.getExecutionTotalTime());
        Assert.assertEquals(12, stats.getExecutionMinTime());
        Assert.assertEquals(12, stats.getExecutionMaxTime());
        Assert.assertEquals(12, stats.getExecutionAvgTime());
        stats.executed(200, 11);
        Assert.assertEquals(23, stats.getExecutionTotalTime());
        Assert.assertEquals(11, stats.getExecutionMinTime());
        Assert.assertEquals(12, stats.getExecutionMaxTime());
        Assert.assertEquals(11, stats.getExecutionAvgTime());
        Assert.assertEquals(11.5, stats.getExecutionAvgTimeAsDouble(), 0.1);
    }
}

