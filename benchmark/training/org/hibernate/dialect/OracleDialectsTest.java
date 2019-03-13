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
package org.hibernate.dialect;


import Environment.BATCH_VERSIONED_DATA;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class OracleDialectsTest {
    @Test
    @TestForIssue(jiraKey = "HHH-9990")
    public void testDefaultBatchVersionDataProperty() {
        Oracle8iDialect oracle8iDialect = new Oracle8iDialect();
        Assert.assertEquals("false", oracle8iDialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
        OracleDialect oracleDialect = new OracleDialect();
        Assert.assertEquals("false", oracleDialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
        Oracle10gDialect oracle10gDialect = new Oracle10gDialect();
        Assert.assertEquals("false", oracle10gDialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
        Oracle9iDialect oracle9iDialect = new Oracle9iDialect();
        Assert.assertEquals("false", oracle9iDialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
        Oracle9Dialect oracle9Dialect = new Oracle9Dialect();
        Assert.assertEquals("false", oracle9Dialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
        Oracle12cDialect oracle12cDialect = new Oracle12cDialect();
        Assert.assertEquals("true", oracle12cDialect.getDefaultProperties().getProperty(BATCH_VERSIONED_DATA));
    }
}

