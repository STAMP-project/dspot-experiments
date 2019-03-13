/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.threadlocals;


import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthenticationThreadLocalTransferTest extends AbstractThreadLocalTransferTest {
    @Test
    public void testRequest() throws InterruptedException, ExecutionException {
        // setup the state
        final Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
        // test it's transferred properly using the base class machinery
        testThreadLocalTransfer(new AbstractThreadLocalTransferTest.ThreadLocalTransferCallable(new AuthenticationThreadLocalTransfer()) {
            @Override
            void assertThreadLocalCleaned() {
                Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
            }

            @Override
            void assertThreadLocalApplied() {
                Assert.assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
            }
        });
    }
}

