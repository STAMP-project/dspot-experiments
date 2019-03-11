package org.opentripplanner.model.impl;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opentripplanner.model.FeedInfo;
import org.opentripplanner.model.IdentityBean;


/**
 *
 *
 * @author Thomas Gran (Capra) - tgr@capraconsulting.no (30.10.2017)
 */
public class OtpTransitServiceBuilderTest {
    private static final String ID_1 = "1";

    private static final String ID_4 = "4";

    private static final String ID_5 = "5";

    private static final String ID_6 = "6";

    @Test
    public void testGenerateNoneExistentIds() throws Exception {
        List<? extends IdentityBean<String>> list;
        // An empty list should not cause any trouble (Exception)
        OtpTransitServiceBuilder.generateNoneExistentIds(Collections.<FeedInfo>emptyList());
        // Generate id for one value
        list = Collections.singletonList(OtpTransitServiceBuilderTest.newEntity());
        OtpTransitServiceBuilder.generateNoneExistentIds(list);
        Assert.assertEquals(OtpTransitServiceBuilderTest.ID_1, getId());
        // Given two entities with no id and max ?d = 4
        list = Arrays.asList(OtpTransitServiceBuilderTest.newEntity(), OtpTransitServiceBuilderTest.newEntity(OtpTransitServiceBuilderTest.ID_4), OtpTransitServiceBuilderTest.newEntity());
        // When
        OtpTransitServiceBuilder.generateNoneExistentIds(list);
        // Then expect
        // First new id to be: maxId + 1 = 4+1 = 5
        Assert.assertEquals(OtpTransitServiceBuilderTest.ID_5, OtpTransitServiceBuilderTest.id(list, 0));
        // Existing to still be 4
        Assert.assertEquals(OtpTransitServiceBuilderTest.ID_4, OtpTransitServiceBuilderTest.id(list, 1));
        // Next to be 6
        Assert.assertEquals(OtpTransitServiceBuilderTest.ID_6, OtpTransitServiceBuilderTest.id(list, 2));
    }
}

