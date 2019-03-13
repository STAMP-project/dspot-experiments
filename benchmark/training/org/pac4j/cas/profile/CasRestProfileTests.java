package org.pac4j.cas.profile;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * General test cases for {@link CasRestProfile}.
 *
 * @author Jacob Severson
 * @since 1.8.0
 */
public final class CasRestProfileTests implements TestsConstants {
    @Test
    public void testClearProfile() {
        final CasRestProfile profile = new CasRestProfile(ID, USERNAME);
        profile.clearSensitiveData();
        Assert.assertNull(profile.getTicketGrantingTicketId());
    }
}

