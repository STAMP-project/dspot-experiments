package org.apereo.cas.adaptors.fortress;


import CoreAuthenticationTestUtils.CONST_USERNAME;
import FortressAuthenticationHandler.FORTRESS_SESSION_KEY;
import java.io.StringWriter;
import java.util.UUID;
import javax.security.auth.login.FailedLoginException;
import javax.xml.bind.JAXBContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.GlobalErrIds;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * This is {@link FortressAuthenticationHandler}.
 *
 * @author yudhi.k.surtan
 * @since 5.2.0
 */
@Slf4j
public class FortressAuthenticationHandlerTests {
    @Mock
    private AccessMgr accessManager;

    @InjectMocks
    private FortressAuthenticationHandler fortressAuthenticationHandler;

    @Test
    public void verifyUnauthorizedUserLoginIncorrect() throws Exception {
        Mockito.when(accessManager.createSession(ArgumentMatchers.any(User.class), ArgumentMatchers.anyBoolean())).thenThrow(new org.apache.directory.fortress.core.PasswordException(GlobalErrIds.USER_PW_INVLD, "error message"));
        Assertions.assertThrows(FailedLoginException.class, () -> fortressAuthenticationHandler.authenticateUsernamePasswordInternal(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), null));
    }

    @Test
    @SneakyThrows
    public void verifyAuthenticateSuccessfully() {
        val sessionId = UUID.randomUUID();
        val session = new Session(new User(CoreAuthenticationTestUtils.CONST_USERNAME), sessionId.toString());
        session.setAuthenticated(true);
        Mockito.when(accessManager.createSession(ArgumentMatchers.any(User.class), ArgumentMatchers.anyBoolean())).thenReturn(session);
        val handlerResult = fortressAuthenticationHandler.authenticateUsernamePasswordInternal(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(), null);
        Assertions.assertEquals(CONST_USERNAME, handlerResult.getPrincipal().getId());
        val jaxbContext = JAXBContext.newInstance(Session.class);
        val marshaller = jaxbContext.createMarshaller();
        val writer = new StringWriter();
        marshaller.marshal(session, writer);
        Assertions.assertEquals(writer.toString(), handlerResult.getPrincipal().getAttributes().get(FORTRESS_SESSION_KEY));
    }
}

