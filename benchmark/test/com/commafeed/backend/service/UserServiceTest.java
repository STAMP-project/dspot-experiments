package com.commafeed.backend.service;


import com.commafeed.CommaFeedConfiguration;
import com.commafeed.backend.dao.FeedCategoryDAO;
import com.commafeed.backend.dao.FeedSubscriptionDAO;
import com.commafeed.backend.dao.UserDAO;
import com.commafeed.backend.dao.UserRoleDAO;
import com.commafeed.backend.dao.UserSettingsDAO;
import com.commafeed.backend.model.User;
import com.commafeed.backend.service.internal.PostLoginActivities;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UserServiceTest {
    private static final byte[] SALT = new byte[]{ 1, 2, 3 };

    private static final byte[] ENCRYPTED_PASSWORD = new byte[]{ 5, 6, 7 };

    @Mock
    private CommaFeedConfiguration commaFeedConfiguration;

    @Mock
    private FeedCategoryDAO feedCategoryDAO;

    @Mock
    private FeedSubscriptionDAO feedSubscriptionDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private UserSettingsDAO userSettingsDAO;

    @Mock
    private UserRoleDAO userRoleDAO;

    @Mock
    private PasswordEncryptionService passwordEncryptionService;

    @Mock
    private PostLoginActivities postLoginActivities;

    private User disabledUser;

    private User normalUser;

    private UserService userService;

    @Test
    public void calling_login_should_not_return_user_object_when_given_null_nameOrEmail() {
        Optional<User> user = userService.login(null, "password");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void calling_login_should_not_return_user_object_when_given_null_password() {
        Optional<User> user = userService.login("testusername", null);
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void calling_login_should_lookup_user_by_name() {
        userService.login("test", "password");
        Mockito.verify(userDAO).findByName("test");
    }

    @Test
    public void calling_login_should_lookup_user_by_email_if_lookup_by_name_failed() {
        Mockito.when(userDAO.findByName("test@test.com")).thenReturn(null);
        userService.login("test@test.com", "password");
        Mockito.verify(userDAO).findByEmail("test@test.com");
    }

    @Test
    public void calling_login_should_not_return_user_object_if_could_not_find_user_by_name_or_email() {
        Mockito.when(userDAO.findByName("test@test.com")).thenReturn(null);
        Mockito.when(userDAO.findByEmail("test@test.com")).thenReturn(null);
        Optional<User> user = userService.login("test@test.com", "password");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void calling_login_should_not_return_user_object_if_user_is_disabled() {
        Mockito.when(userDAO.findByName("test")).thenReturn(disabledUser);
        Optional<User> user = userService.login("test", "password");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void calling_login_should_try_to_authenticate_user_who_is_not_disabled() {
        Mockito.when(userDAO.findByName("test")).thenReturn(normalUser);
        Mockito.when(passwordEncryptionService.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class))).thenReturn(false);
        userService.login("test", "password");
        Mockito.verify(passwordEncryptionService).authenticate("password", UserServiceTest.ENCRYPTED_PASSWORD, UserServiceTest.SALT);
    }

    @Test
    public void calling_login_should_not_return_user_object_on_unsuccessful_authentication() {
        Mockito.when(userDAO.findByName("test")).thenReturn(normalUser);
        Mockito.when(passwordEncryptionService.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class))).thenReturn(false);
        Optional<User> authenticatedUser = userService.login("test", "password");
        Assert.assertFalse(authenticatedUser.isPresent());
    }

    @Test
    public void calling_login_should_execute_post_login_activities_for_user_on_successful_authentication() {
        Mockito.when(userDAO.findByName("test")).thenReturn(normalUser);
        Mockito.when(passwordEncryptionService.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class))).thenReturn(true);
        Mockito.doNothing().when(postLoginActivities).executeFor(ArgumentMatchers.any(User.class));
        userService.login("test", "password");
        Mockito.verify(postLoginActivities).executeFor(normalUser);
    }

    @Test
    public void calling_login_should_return_user_object_on_successful_authentication() {
        Mockito.when(userDAO.findByName("test")).thenReturn(normalUser);
        Mockito.when(passwordEncryptionService.authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.any(byte[].class))).thenReturn(true);
        Mockito.doNothing().when(postLoginActivities).executeFor(ArgumentMatchers.any(User.class));
        Optional<User> authenticatedUser = userService.login("test", "password");
        Assert.assertTrue(authenticatedUser.isPresent());
        Assert.assertEquals(normalUser, authenticatedUser.get());
    }

    @Test
    public void api_login_should_not_return_user_if_apikey_null() {
        Optional<User> user = userService.login(null);
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void api_login_should_lookup_user_by_apikey() {
        Mockito.when(userDAO.findByApiKey("apikey")).thenReturn(null);
        userService.login("apikey");
        Mockito.verify(userDAO).findByApiKey("apikey");
    }

    @Test
    public void api_login_should_not_return_user_if_user_not_found_from_lookup_by_apikey() {
        Mockito.when(userDAO.findByApiKey("apikey")).thenReturn(null);
        Optional<User> user = userService.login("apikey");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void api_login_should_not_return_user_if_user_found_from_apikey_lookup_is_disabled() {
        Mockito.when(userDAO.findByApiKey("apikey")).thenReturn(disabledUser);
        Optional<User> user = userService.login("apikey");
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void api_login_should_perform_post_login_activities_if_user_found_from_apikey_lookup_not_disabled() {
        Mockito.when(userDAO.findByApiKey("apikey")).thenReturn(normalUser);
        userService.login("apikey");
        Mockito.verify(postLoginActivities).executeFor(normalUser);
    }

    @Test
    public void api_login_should_return_user_if_user_found_from_apikey_lookup_not_disabled() {
        Mockito.when(userDAO.findByApiKey("apikey")).thenReturn(normalUser);
        Optional<User> returnedUser = userService.login("apikey");
        Assert.assertEquals(normalUser, returnedUser.get());
    }
}

