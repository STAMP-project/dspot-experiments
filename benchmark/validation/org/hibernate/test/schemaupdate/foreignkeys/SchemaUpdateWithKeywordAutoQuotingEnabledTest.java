/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys;


import TargetType.DATABASE;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11061")
public class SchemaUpdateWithKeywordAutoQuotingEnabledTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testUpdate() {
        new SchemaUpdate().setHaltOnError(true).execute(EnumSet.of(DATABASE), metadata);
    }

    @Entity(name = "Match")
    @Table(name = "MATCH")
    public static class Match {
        @Id
        private Long id;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable
        private Map<Integer, Integer> timeline = new TreeMap<>();
    }
}

