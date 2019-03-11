package org.hibernate.jpa.test.criteria.fetchscroll;


import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-10062")
public class CriteriaToScrollableResultsFetchTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA only supports forward-only cursors")
    public void testWithScroll() {
        // Creates data necessary for test
        Long facilityId = populate();
        // Controller iterates the data
        for (OrderLine line : getOrderLinesScrolled(facilityId)) {
            // This should ~NOT~ fail with a LazilyLoadException
            Assert.assertNotNull(line.getProduct().getFacility().getSite().getName());
        }
    }

    @Test
    public void testNoScroll() {
        // Creates data necessary for test.
        Long facilityId = populate();
        // Controller iterates the data
        for (OrderLine line : getOrderLinesJpaFetched(facilityId)) {
            Assert.assertNotNull(line.getProduct().getFacility().getSite().getName());
        }
    }
}

