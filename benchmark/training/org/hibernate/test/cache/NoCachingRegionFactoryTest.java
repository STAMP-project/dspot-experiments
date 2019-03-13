package org.hibernate.test.cache;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner.
 */
public class NoCachingRegionFactoryTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12508")
    public void testSessionFactoryOptionsConsistent() {
        Assert.assertFalse(sessionFactory().getSessionFactoryOptions().isSecondLevelCacheEnabled());
    }
}

