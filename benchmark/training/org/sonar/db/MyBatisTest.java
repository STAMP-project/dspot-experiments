/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db;


import org.apache.ibatis.session.Configuration;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.db.rule.RuleMapper;


public class MyBatisTest {
    private static H2Database database;

    private MyBatis underTest = new MyBatis(MyBatisTest.database);

    @Test
    public void shouldConfigureMyBatis() {
        underTest.start();
        Configuration conf = underTest.getSessionFactory().getConfiguration();
        Assert.assertThat(conf.isUseGeneratedKeys(), Is.is(true));
        Assert.assertThat(conf.hasMapper(RuleMapper.class), Is.is(true));
        Assert.assertThat(conf.isLazyLoadingEnabled(), Is.is(false));
    }

    @Test
    public void shouldOpenBatchSession() {
        underTest.start();
        try (DbSession session = underTest.openSession(false)) {
            Assert.assertThat(session.getConnection(), CoreMatchers.notNullValue());
            Assert.assertThat(session.getMapper(RuleMapper.class), CoreMatchers.notNullValue());
        }
    }
}

