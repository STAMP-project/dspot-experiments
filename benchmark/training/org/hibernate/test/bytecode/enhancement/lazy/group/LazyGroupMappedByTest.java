package org.hibernate.test.bytecode.enhancement.lazy.group;


import org.hibernate.stat.SessionStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testing OneToOne LazyToOne association
 *
 * @author Jan-Oliver Lustig, Sebastian Viefhaus
 */
@TestForIssue(jiraKey = "HHH-11986")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyGroupMappedByTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11986")
    public void test() {
        Long fromId = createEntities();
        Statistics stats = sessionFactory().getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            SessionStatistics sessionStats = session.getStatistics();
            // Should be loaded lazy.
            LGMB_From from = session.get(.class, fromId);
            assertEquals(1, sessionStats.getEntityCount());
            assertEquals(1, stats.getPrepareStatementCount());
            // Lazy text is accessed, toRelation should not be read yet.
            String bigText = from.getBigText();
            assertEquals(1, sessionStats.getEntityCount());
            assertEquals(2, stats.getPrepareStatementCount());
            // Second table is accessed and the lazy one should be reloaded.
            LGMB_To to = from.getToRelation();
            assertEquals(2, sessionStats.getEntityCount());
            assertEquals(3, stats.getPrepareStatementCount());
            to.getFromRelation().getName();
            assertEquals(3, stats.getPrepareStatementCount());
        });
    }
}

