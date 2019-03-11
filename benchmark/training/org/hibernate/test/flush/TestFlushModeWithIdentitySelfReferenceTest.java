/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.flush;


import DialectChecks.SupportsIdentityColumns;
import FlushMode.AUTO;
import FlushMode.COMMIT;
import FlushMode.MANUAL;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-13042")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class TestFlushModeWithIdentitySelfReferenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testFlushModeCommit() throws Exception {
        Session session = openSession();
        try {
            session.setHibernateFlushMode(COMMIT);
            loadAndAssert(session, createAndInsertEntity(session));
            loadAndInsert(session, createAndInsertEntityEmbeddable(session));
        } finally {
            session.close();
        }
    }

    @Test
    public void testFlushModeManual() throws Exception {
        Session session = openSession();
        try {
            session.setHibernateFlushMode(MANUAL);
            loadAndAssert(session, createAndInsertEntity(session));
            loadAndInsert(session, createAndInsertEntityEmbeddable(session));
        } finally {
            session.close();
        }
    }

    @Test
    public void testFlushModeAuto() throws Exception {
        Session session = openSession();
        try {
            session.setHibernateFlushMode(AUTO);
            loadAndAssert(session, createAndInsertEntity(session));
            loadAndInsert(session, createAndInsertEntityEmbeddable(session));
        } finally {
            session.close();
        }
    }

    @Entity(name = "SelfRefEntity")
    public static class SelfRefEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @OneToOne(cascade = CascadeType.ALL, optional = true)
        private TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntity selfRefEntity;

        private String data;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntity getSelfRefEntity() {
            return selfRefEntity;
        }

        public void setSelfRefEntity(TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntity selfRefEntity) {
            this.selfRefEntity = selfRefEntity;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    @Entity(name = "SelfRefEntityWithEmbeddable")
    public static class SelfRefEntityWithEmbeddable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Embedded
        private TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityInfo info;

        private String data;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityInfo getInfo() {
            return info;
        }

        public void setInfo(TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityInfo info) {
            this.info = info;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    @Embeddable
    public static class SelfRefEntityInfo {
        @OneToOne(cascade = CascadeType.ALL, optional = true)
        private TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityWithEmbeddable seflRefEntityEmbedded;

        public TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityWithEmbeddable getSeflRefEntityEmbedded() {
            return seflRefEntityEmbedded;
        }

        public void setSeflRefEntityEmbedded(TestFlushModeWithIdentitySelfReferenceTest.SelfRefEntityWithEmbeddable seflRefEntityEmbedded) {
            this.seflRefEntityEmbedded = seflRefEntityEmbedded;
        }
    }
}

