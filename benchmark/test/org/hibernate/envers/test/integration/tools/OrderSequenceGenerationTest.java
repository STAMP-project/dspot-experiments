/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.tools;


import DialectChecks.SupportsSequences;
import java.io.File;
import java.nio.file.Files;
import javax.persistence.EntityManager;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test verifies that the sequence is created properly both for database operations and export
 * scripts and that a basic entity using said sequence can be persisted and fetched via the
 * audit reader.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11131")
@RequiresDialectFeature(SupportsSequences.class)
public class OrderSequenceGenerationTest extends BaseEnversJPAFunctionalTestCase {
    private File createSchema;

    private File dropSchema;

    @Test
    public void testCreateSequenceExportScripts() throws Exception {
        final String[] createStrings = getDialect().getCreateSequenceStrings("REVISION_GENERATOR", 1, 1);
        final String content = new String(Files.readAllBytes(createSchema.toPath())).toLowerCase();
        for (int i = 0; i < (createStrings.length); ++i) {
            if ((getDialect()) instanceof Oracle8iDialect) {
                Assert.assertTrue(content.contains(((createStrings[i]) + " ORDER").toLowerCase()));
            } else {
                Assert.assertTrue(content.contains(createStrings[i].toLowerCase()));
            }
        }
    }

    @Test
    public void testBasicPersistAndAuditFetch() throws Exception {
        EntityManager entityManager = getOrCreateEntityManager();
        try {
            StrTestEntity e = new StrTestEntity("Acme");
            entityManager.getTransaction().begin();
            entityManager.persist(e);
            entityManager.getTransaction().commit();
            entityManager.clear();
            StrTestEntity rev1 = getAuditReader().find(StrTestEntity.class, e.getId(), 1);
            Assert.assertEquals(e, rev1);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}

