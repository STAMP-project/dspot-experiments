package org.apereo.cas.adaptors.radius;


import java.util.Optional;
import javax.security.auth.login.FailedLoginException;
import lombok.val;
import net.jradius.dictionary.Attr_ClientId;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This is {@link RadiusUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RadiusUtilsTests {
    @Test
    public void verifyActionPasses() throws Exception {
        val server = Mockito.mock(RadiusServer.class);
        val attribute = new Attr_ClientId("client_id");
        val response = new CasRadiusResponse(100, 100, CollectionUtils.wrapList(attribute));
        Mockito.when(server.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(response);
        val result = RadiusUtils.authenticate("casuser", "Mellon", CollectionUtils.wrapList(server), true, false, Optional.empty());
        Assertions.assertTrue(result.getKey());
        Assertions.assertTrue(result.getRight().isPresent());
    }

    @Test
    public void verifyActionFails() throws Exception {
        val server = Mockito.mock(RadiusServer.class);
        Mockito.when(server.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(null);
        Assertions.assertThrows(FailedLoginException.class, () -> RadiusUtils.authenticate("casuser", "Mellon", CollectionUtils.wrapList(server), false, false, Optional.empty()));
    }

    @Test
    public void verifyActionFailsWithException() throws Exception {
        val server = Mockito.mock(RadiusServer.class);
        Mockito.when(server.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(FailedLoginException.class);
        Assertions.assertThrows(FailedLoginException.class, () -> RadiusUtils.authenticate("casuser", "Mellon", CollectionUtils.wrapList(server), false, false, Optional.empty()));
    }
}

