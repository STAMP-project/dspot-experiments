/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.id.sequences;


import AvailableSettings.DIALECT;
import java.util.List;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.test.annotations.id.sequences.entities.Bunny;
import org.hibernate.test.annotations.id.sequences.entities.PointyTooth;
import org.hibernate.test.annotations.id.sequences.entities.TwinkleToes;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for JIRA issue ANN-748.
 *
 * @author Hardy Ferentschik
 */
@SuppressWarnings("unchecked")
public class JoinColumnOverrideTest extends BaseUnitTestCase {
    private static final String expectedSqlPointyTooth = "create table PointyTooth (id numeric(128,0) not null, " + "bunny_id numeric(128,0), primary key (id))";

    private static final String expectedSqlTwinkleToes = "create table TwinkleToes (id numeric(128,0) not null, " + "bunny_id numeric(128,0), primary key (id))";

    @Test
    @TestForIssue(jiraKey = "ANN-748")
    public void testBlownPrecision() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(DIALECT, "SQLServer").build();
        try {
            Metadata metadata = addAnnotatedClass(TwinkleToes.class).buildMetadata();
            boolean foundPointyToothCreate = false;
            boolean foundTwinkleToesCreate = false;
            List<String> commands = new org.hibernate.tool.schema.internal.SchemaCreatorImpl(ssr).generateCreationCommands(metadata, false);
            for (String command : commands) {
                log.debug(command);
                if (JoinColumnOverrideTest.expectedSqlPointyTooth.equals(command)) {
                    foundPointyToothCreate = true;
                } else
                    if (JoinColumnOverrideTest.expectedSqlTwinkleToes.equals(command)) {
                        foundTwinkleToesCreate = true;
                    }

            }
            Assert.assertTrue("Expected create table command for PointyTooth entity not found", foundPointyToothCreate);
            Assert.assertTrue("Expected create table command for TwinkleToes entity not found", foundTwinkleToesCreate);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

