/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Dominique Toupin
 */
@TestForIssue(jiraKey = "HHH-10456")
public class AnnotationBinderTest {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AnnotationBinder.class.getName()));

    @Test
    public void testInvalidPrimaryKeyJoinColumnAnnotationMessageContainsClassName() throws Exception {
        Triggerable triggerable = logInspection.watchForLogMessages("HHH000137");
        StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder();
        Metadata metadata = addAnnotatedClass(AnnotationBinderTest.InvalidPrimaryKeyJoinColumnAnnotationEntity.class).buildMetadata();
        Assert.assertTrue("Expected warning HHH00137 but it wasn't triggered", triggerable.wasTriggered());
        Assert.assertTrue(("Expected invalid class name in warning HHH00137 message but it does not apper to be present; got " + (triggerable.triggerMessage())), triggerable.triggerMessage().matches(((".*\\b\\Q" + (AnnotationBinderTest.InvalidPrimaryKeyJoinColumnAnnotationEntity.class.getName())) + "\\E\\b.*")));
    }

    @Entity
    @PrimaryKeyJoinColumn
    public static class InvalidPrimaryKeyJoinColumnAnnotationEntity {
        private String id;

        @Id
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

