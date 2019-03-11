package com.baeldung.web.rest;


import AuthoritiesConstants.ADMIN;
import AuthoritiesConstants.USER;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.baeldung.BaeldungApp;
import com.baeldung.domain.Authority;
import com.baeldung.domain.User;
import com.baeldung.repository.AuthorityRepository;
import com.baeldung.repository.UserRepository;
import com.baeldung.service.MailService;
import com.baeldung.service.UserService;
import com.baeldung.service.dto.UserDTO;
import com.baeldung.web.rest.vm.ManagedUserVM;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the AccountResource REST controller.
 *
 * @see AccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaeldungApp.class)
public class AccountResourceIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserService userService;

    @Mock
    private UserService mockUserService;

    @Mock
    private MailService mockMailService;

    private MockMvc restUserMockMvc;

    private MockMvc restMvc;

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(""));
    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/api/authenticate").with(( request) -> {
            request.setRemoteUser("test");
            return request;
        }).accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(ADMIN);
        authorities.add(authority);
        User user = new User();
        user.setLogin("test");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipster.com");
        user.setImageUrl("http://placehold.it/50x50");
        user.setAuthorities(authorities);
        Mockito.when(mockUserService.getUserWithAuthorities()).thenReturn(user);
        restUserMockMvc.perform(get("/api/account").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.login").value("test")).andExpect(jsonPath("$.firstName").value("john")).andExpect(jsonPath("$.lastName").value("doe")).andExpect(jsonPath("$.email").value("john.doe@jhipster.com")).andExpect(jsonPath("$.imageUrl").value("http://placehold.it/50x50")).andExpect(jsonPath("$.authorities").value(ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        Mockito.when(mockUserService.getUserWithAuthorities()).thenReturn(null);
        restUserMockMvc.perform(get("/api/account").accept(APPLICATION_JSON)).andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void testRegisterValid() throws Exception {
        ManagedUserVM validUser = // id
        // login
        // password
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "joe", "password", "Joe", "Shmoe", "joe@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
        Optional<User> user = userRepository.findOneByLogin("joe");
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Transactional
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserVM invalidUser = // id
        // login <-- invalid
        // password
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "funky-log!n", "password", "Funky", "One", "funky@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
        Optional<User> user = userRepository.findOneByEmail("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserVM invalidUser = // id
        // login
        // password
        // firstName
        // lastName
        // e-mail <-- invalid
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "bob", "password", "Bob", "Green", "invalid", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserVM invalidUser = // id
        // login
        // password with only 3 digits
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "bob", "123", "Bob", "Green", "bob@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
        Optional<User> user = userRepository.findOneByLogin("bob");
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateLogin() throws Exception {
        // Good
        ManagedUserVM validUser = // id
        // login
        // password
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "alice", "password", "Alice", "Something", "alice@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        // Duplicate login, different e-mail
        ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), validUser.getLogin(), validUser.getPassword(), validUser.getLogin(), validUser.getLastName(), "alicejr@example.com", true, validUser.getImageUrl(), validUser.getLangKey(), validUser.getCreatedBy(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate(), validUser.getAuthorities());
        // Good user
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
        // Duplicate login
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(duplicatedUser))).andExpect(status().is4xxClientError());
        Optional<User> userDup = userRepository.findOneByEmail("alicejr@example.com");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterDuplicateEmail() throws Exception {
        // Good
        ManagedUserVM validUser = // id
        // login
        // password
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "john", "password", "John", "Doe", "john@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        // Duplicate e-mail, different login
        ManagedUserVM duplicatedUser = new ManagedUserVM(validUser.getId(), "johnjr", validUser.getPassword(), validUser.getLogin(), validUser.getLastName(), validUser.getEmail(), true, validUser.getImageUrl(), validUser.getLangKey(), validUser.getCreatedBy(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate(), validUser.getAuthorities());
        // Good user
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
        // Duplicate e-mail
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(duplicatedUser))).andExpect(status().is4xxClientError());
        Optional<User> userDup = userRepository.findOneByLogin("johnjr");
        assertThat(userDup.isPresent()).isFalse();
    }

    @Test
    @Transactional
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = // id
        // login
        // password
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new ManagedUserVM(null, "badguy", "password", "Bad", "Guy", "badguy@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(ADMIN)));
        restMvc.perform(content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
        Optional<User> userDup = userRepository.findOneByLogin("badguy");
        assertThat(userDup.isPresent()).isTrue();
        assertThat(userDup.get().getAuthorities()).hasSize(1).containsExactly(authorityRepository.findOne(USER));
    }

    @Test
    @Transactional
    public void testSaveInvalidLogin() throws Exception {
        UserDTO invalidUser = // id
        // login <-- invalid
        // firstName
        // lastName
        // e-mail
        // activated
        // imageUrl
        // langKey
        // createdBy
        // createdDate
        // lastModifiedBy
        // lastModifiedDate
        new UserDTO(null, "funky-log!n", "Funky", "One", "funky@example.com", true, "http://placehold.it/50x50", "en", null, null, null, null, new HashSet(Arrays.asList(USER)));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
        Optional<User> user = userRepository.findOneByEmail("funky@example.com");
        assertThat(user.isPresent()).isFalse();
    }
}

