/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import DialectChecks.SupportsExpectedLobUsagePattern;
import DialectChecks.SupportsNClob;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.util.Arrays;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-12555")
@RequiresDialectFeature(SupportsExpectedLobUsagePattern.class)
@RunWith(BytecodeEnhancerRunner.class)
public class LobUnfetchedPropertyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBlob() throws SQLException {
        final int id = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileBlob file = new org.hibernate.test.type.FileBlob();
            file.setBlob(s.getLobHelper().createBlob("TEST CASE".getBytes()));
            // merge transient entity
            file = ((org.hibernate.test.type.FileBlob) (s.merge(file)));
            return file.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileBlob file = s.get(.class, id);
            assertFalse(Hibernate.isPropertyInitialized(file, "blob"));
            Blob blob = file.getBlob();
            try {
                assertTrue(Arrays.equals("TEST CASE".getBytes(), blob.getBytes(1, ((int) (file.getBlob().length())))));
            } catch ( ex) {
                fail("could not determine Lob length");
            }
        });
    }

    @Test
    public void testClob() {
        final int id = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileClob file = new org.hibernate.test.type.FileClob();
            file.setClob(s.getLobHelper().createClob("TEST CASE"));
            // merge transient entity
            file = ((org.hibernate.test.type.FileClob) (s.merge(file)));
            return file.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileClob file = s.get(.class, id);
            assertFalse(Hibernate.isPropertyInitialized(file, "clob"));
            Clob clob = file.getClob();
            try {
                final char[] chars = new char[((int) (file.getClob().length()))];
                clob.getCharacterStream().read(chars);
                assertTrue(Arrays.equals("TEST CASE".toCharArray(), chars));
            } catch ( ex) {
                fail("could not determine Lob length");
            } catch ( ex) {
                fail("could not read Lob");
            }
        });
    }

    @Test
    @RequiresDialectFeature(SupportsNClob.class)
    public void testNClob() {
        final int id = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileNClob file = new org.hibernate.test.type.FileNClob();
            file.setClob(s.getLobHelper().createNClob("TEST CASE"));
            // merge transient entity
            file = ((org.hibernate.test.type.FileNClob) (s.merge(file)));
            return file.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.type.FileNClob file = s.get(.class, id);
            assertFalse(Hibernate.isPropertyInitialized(file, "clob"));
            NClob nClob = file.getClob();
            try {
                final char[] chars = new char[((int) (file.getClob().length()))];
                nClob.getCharacterStream().read(chars);
                assertTrue(Arrays.equals("TEST CASE".toCharArray(), chars));
            } catch ( ex) {
                fail("could not determine Lob length");
            } catch ( ex) {
                fail("could not read Lob");
            }
        });
    }

    @Entity(name = "FileBlob")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "non-lazy")
    public static class FileBlob {
        private int id;

        private Blob blob;

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "filedata", length = 1024 * 1024)
        @Lob
        @Basic(fetch = FetchType.LAZY)
        public Blob getBlob() {
            return blob;
        }

        public void setBlob(Blob blob) {
            this.blob = blob;
        }
    }

    @Entity(name = "FileClob")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "non-lazy")
    public static class FileClob {
        private int id;

        private Clob clob;

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "filedata", length = 1024 * 1024)
        @Lob
        @Basic(fetch = FetchType.LAZY)
        public Clob getClob() {
            return clob;
        }

        public void setClob(Clob clob) {
            this.clob = clob;
        }
    }

    @Entity(name = "FileNClob")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "non-lazy")
    public static class FileNClob {
        private int id;

        private NClob clob;

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Column(name = "filedata", length = 1024 * 1024)
        @Lob
        @Basic(fetch = FetchType.LAZY)
        public NClob getClob() {
            return clob;
        }

        public void setClob(NClob clob) {
            this.clob = clob;
        }
    }
}

