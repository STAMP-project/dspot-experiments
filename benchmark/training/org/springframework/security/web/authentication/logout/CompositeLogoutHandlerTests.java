/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.authentication.logout;


import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;


/**
 *
 *
 * @author Edd? Mel?ndez
 * @author Rob Winch
 * @since 4.2.0
 */
public class CompositeLogoutHandlerTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void buildEmptyCompositeLogoutHandlerThrowsException() {
        this.exception.expect(IllegalArgumentException.class);
        this.exception.expectMessage("LogoutHandlers are required");
        new CompositeLogoutHandler();
    }

    @Test
    public void callLogoutHandlersSuccessfullyWithArray() {
        LogoutHandler securityContextLogoutHandler = Mockito.mock(SecurityContextLogoutHandler.class);
        LogoutHandler csrfLogoutHandler = Mockito.mock(SecurityContextLogoutHandler.class);
        LogoutHandler handler = new CompositeLogoutHandler(securityContextLogoutHandler, csrfLogoutHandler);
        handler.logout(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class), Mockito.mock(Authentication.class));
        Mockito.verify(securityContextLogoutHandler, Mockito.times(1)).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
        Mockito.verify(csrfLogoutHandler, Mockito.times(1)).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
    }

    @Test
    public void callLogoutHandlersSuccessfully() {
        LogoutHandler securityContextLogoutHandler = Mockito.mock(SecurityContextLogoutHandler.class);
        LogoutHandler csrfLogoutHandler = Mockito.mock(SecurityContextLogoutHandler.class);
        List<LogoutHandler> logoutHandlers = Arrays.asList(securityContextLogoutHandler, csrfLogoutHandler);
        LogoutHandler handler = new CompositeLogoutHandler(logoutHandlers);
        handler.logout(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class), Mockito.mock(Authentication.class));
        Mockito.verify(securityContextLogoutHandler, Mockito.times(1)).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
        Mockito.verify(csrfLogoutHandler, Mockito.times(1)).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
    }

    @Test
    public void callLogoutHandlersThrowException() {
        LogoutHandler firstLogoutHandler = Mockito.mock(LogoutHandler.class);
        LogoutHandler secondLogoutHandler = Mockito.mock(LogoutHandler.class);
        Mockito.doThrow(new IllegalArgumentException()).when(firstLogoutHandler).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
        List<LogoutHandler> logoutHandlers = Arrays.asList(firstLogoutHandler, secondLogoutHandler);
        LogoutHandler handler = new CompositeLogoutHandler(logoutHandlers);
        try {
            handler.logout(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class), Mockito.mock(Authentication.class));
            fail("Expected Exception");
        } catch (IllegalArgumentException success) {
        }
        InOrder logoutHandlersInOrder = Mockito.inOrder(firstLogoutHandler, secondLogoutHandler);
        logoutHandlersInOrder.verify(firstLogoutHandler, Mockito.times(1)).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
        logoutHandlersInOrder.verify(secondLogoutHandler, Mockito.never()).logout(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Authentication.class));
    }
}

