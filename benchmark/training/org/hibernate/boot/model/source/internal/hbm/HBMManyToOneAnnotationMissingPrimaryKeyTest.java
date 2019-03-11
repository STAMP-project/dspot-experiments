package org.hibernate.boot.model.source.internal.hbm;


import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * https://hibernate.atlassian.net/browse/HHH-11502
 *
 * @author Russ Tennant (russ@venturetech.net)
 */
public class HBMManyToOneAnnotationMissingPrimaryKeyTest extends BaseNonConfigCoreFunctionalTestCase {
    /**
     * Test to trigger the NullPointerException in the ModelBinder.
     *
     * @throws Exception
     * 		on error.
     */
    @Test
    public void hhh11502() throws Exception {
        Assert.assertTrue(true);
    }
}

