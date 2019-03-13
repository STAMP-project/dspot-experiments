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
package org.hibernate.test.id;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9287")
public class PooledHiLoSequenceIdentifierTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSequenceIdentifierGenerator() {
        Session s = null;
        Transaction tx = null;
        try {
            s = openSession();
            tx = s.beginTransaction();
            for (int i = 0; i < 5; i++) {
                s.persist(new PooledHiLoSequenceIdentifierTest.SequenceIdentifier());
            }
            s.flush();
            Assert.assertEquals(5, countInsertedRows(s));
            insertNewRow(s);
            insertNewRow(s);
            Assert.assertEquals(7, countInsertedRows(s));
            List<Number> ids = s.createQuery("SELECT id FROM sequenceIdentifier").list();
            for (Number id : ids) {
                log.debug(("Found id: " + id));
            }
            for (int i = 0; i < 3; i++) {
                s.persist(new PooledHiLoSequenceIdentifierTest.SequenceIdentifier());
            }
            s.flush();
            Assert.assertEquals(10, countInsertedRows(s));
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            s.close();
        }
    }

    @Entity(name = "sequenceIdentifier")
    public static class SequenceIdentifier {
        @Id
        @GenericGenerator(name = "sampleGenerator", strategy = "enhanced-sequence", parameters = { @Parameter(name = "optimizer", value = "pooled"), @Parameter(name = "initial_value", value = "1"), @Parameter(name = "increment_size", value = "2") })
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "sampleGenerator")
        private Long id;
    }
}

