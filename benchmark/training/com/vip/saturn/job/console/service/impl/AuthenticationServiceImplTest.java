package com.vip.saturn.job.console.service.impl;


import SaturnJobConsoleException.ERROR_CODE_AUTHN_FAIL;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.mybatis.entity.User;
import com.vip.saturn.job.console.mybatis.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authnService;

    @Test
    public void testAuthenticateSuccessfully() throws SaturnJobConsoleException {
        authnService.setHashMethod("plaintext");
        User user = createUser("jeff", "password");
        Mockito.when(userRepository.select("jeff")).thenReturn(user);
        Assert.assertEquals(user, authnService.authenticate("jeff", "password"));
    }

    @Test
    public void testAuthenticationFailWhenUserIsNotFound() throws SaturnJobConsoleException {
        authnService.setHashMethod("plaintext");
        Mockito.when(userRepository.select("john")).thenReturn(null);
        boolean hasException = false;
        try {
            Assert.assertNull(authnService.authenticate("john", "password"));
        } catch (SaturnJobConsoleException e) {
            hasException = true;
            Assert.assertEquals(ERROR_CODE_AUTHN_FAIL, e.getErrorCode());
        }
        Assert.assertTrue(hasException);
    }

    @Test
    public void testAuthenticationFailWhenPasswordInputIsEmpty() throws SaturnJobConsoleException {
        authnService.setHashMethod("plaintext");
        boolean hasException = false;
        try {
            authnService.authenticate("john", "");
        } catch (SaturnJobConsoleException e) {
            hasException = true;
            Assert.assertEquals(ERROR_CODE_AUTHN_FAIL, e.getErrorCode());
        }
        Assert.assertTrue(hasException);
    }
}

