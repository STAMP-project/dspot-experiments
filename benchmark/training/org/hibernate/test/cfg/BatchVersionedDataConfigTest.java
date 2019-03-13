/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) {DATE}, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.cfg;


import AvailableSettings.DIALECT;
import org.hamcrest.core.Is;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class BatchVersionedDataConfigTest extends BaseUnitTestCase {
    private SessionFactory sessionFactory;

    private Configuration cfg;

    @Test
    public void testBatchVersionedDataForDialectNotSettingBatchVersionedDataProperty() {
        cfg.setProperty(DIALECT, H2Dialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(true));
    }

    @Test
    public void testBatchVersionedDataForOracle10gDialect() {
        cfg.setProperty(DIALECT, Oracle10gDialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(false));
    }

    @Test
    public void testBatchVersionedDataForOracle8iDialect() {
        cfg.setProperty(DIALECT, Oracle8iDialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(false));
    }

    @Test
    public void testBatchVersionedDataForOracle9iDialect() {
        cfg.setProperty(DIALECT, Oracle9iDialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(false));
    }

    @Test
    public void testBatchVersionedDataForOracle9Dialect() {
        cfg.setProperty(DIALECT, Oracle9Dialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(false));
    }

    @Test
    public void testBatchVersionedDataForOracleDialect() {
        cfg.setProperty(DIALECT, OracleDialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(false));
    }

    @Test
    public void testBatchVersionedDataForOracle12cDialect() {
        cfg.setProperty(DIALECT, Oracle12cDialect.class.getName());
        sessionFactory = cfg.buildSessionFactory();
        Assert.assertThat(sessionFactory.getSessionFactoryOptions().isJdbcBatchVersionedData(), Is.is(true));
    }
}

