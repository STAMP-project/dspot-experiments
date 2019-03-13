/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.statistic;


import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/* ------------------------------------------------------------ */
public class RateStatisticTest {
    @Test
    public void testRate() throws Exception {
        RateStatistic rs = new RateStatistic(1, TimeUnit.HOURS);
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(0L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(0));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(0L));
        rs.record();
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(1));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(1L));
        rs.age(35, TimeUnit.MINUTES);
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(1));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(1L));
        MatcherAssert.assertThat(rs.getOldest(TimeUnit.MINUTES), Matchers.is(35L));
        rs.record();
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(2L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(2));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(2L));
        rs.age(35, TimeUnit.MINUTES);
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(2L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(1));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(2L));
        rs.record();
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(3L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(2));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(2L));
        rs.age(35, TimeUnit.MINUTES);
        MatcherAssert.assertThat(rs.getCount(), Matchers.equalTo(3L));
        MatcherAssert.assertThat(rs.getRate(), Matchers.equalTo(1));
        MatcherAssert.assertThat(rs.getMax(), Matchers.equalTo(2L));
    }
}

