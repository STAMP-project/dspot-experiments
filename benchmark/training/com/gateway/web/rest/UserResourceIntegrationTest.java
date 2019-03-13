package com.gateway.web.rest;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.gateway.GatewayApp;
import com.gateway.domain.User;
import com.gateway.repository.UserRepository;
import com.gateway.service.MailService;
import com.gateway.service.UserService;
import com.gateway.web.rest.errors.ExceptionTranslator;
import com.gateway.web.rest.vm.ManagedUserVM;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewayApp.class)
public class UserResourceIntegrationTest {
    private static final String DEFAULT_LOGIN = "johndoe";

    private static final String UPDATED_LOGIN = "jhipster";

    private static final String DEFAULT_PASSWORD = "passjohndoe";

    private static final String UPDATED_PASSWORD = "passjhipster";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";

    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";

    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";

    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";

    private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

    private static final String DEFAULT_LANGKEY = "en";

    private static final String UPDATED_LANGKEY = "fr";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserMockMvc;

    private User user;

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        // Create the User
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = new ManagedUserVM(null, UserResourceIntegrationTest.DEFAULT_LOGIN, UserResourceIntegrationTest.DEFAULT_PASSWORD, UserResourceIntegrationTest.DEFAULT_FIRSTNAME, UserResourceIntegrationTest.DEFAULT_LASTNAME, UserResourceIntegrationTest.DEFAULT_EMAIL, true, UserResourceIntegrationTest.DEFAULT_IMAGEURL, UserResourceIntegrationTest.DEFAULT_LANGKEY, null, null, null, null, autorities);
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isCreated());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize((databaseSizeBeforeCreate + 1));
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getLogin()).isEqualTo(UserResourceIntegrationTest.DEFAULT_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntegrationTest.DEFAULT_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntegrationTest.DEFAULT_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntegrationTest.DEFAULT_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntegrationTest.DEFAULT_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntegrationTest.DEFAULT_LANGKEY);
    }

    @Test
    @Transactional
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = new ManagedUserVM(1L, UserResourceIntegrationTest.DEFAULT_LOGIN, UserResourceIntegrationTest.DEFAULT_PASSWORD, UserResourceIntegrationTest.DEFAULT_FIRSTNAME, UserResourceIntegrationTest.DEFAULT_LASTNAME, UserResourceIntegrationTest.DEFAULT_EMAIL, true, UserResourceIntegrationTest.DEFAULT_IMAGEURL, UserResourceIntegrationTest.DEFAULT_LANGKEY, null, null, null, null, autorities);
        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = // this login should already be used
        new ManagedUserVM(null, UserResourceIntegrationTest.DEFAULT_LOGIN, UserResourceIntegrationTest.DEFAULT_PASSWORD, UserResourceIntegrationTest.DEFAULT_FIRSTNAME, UserResourceIntegrationTest.DEFAULT_LASTNAME, "anothermail@localhost", true, UserResourceIntegrationTest.DEFAULT_IMAGEURL, UserResourceIntegrationTest.DEFAULT_LANGKEY, null, null, null, null, autorities);
        // Create the User
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = // this email should already be used
        new ManagedUserVM(null, "anotherlogin", UserResourceIntegrationTest.DEFAULT_PASSWORD, UserResourceIntegrationTest.DEFAULT_FIRSTNAME, UserResourceIntegrationTest.DEFAULT_LASTNAME, UserResourceIntegrationTest.DEFAULT_EMAIL, true, UserResourceIntegrationTest.DEFAULT_IMAGEURL, UserResourceIntegrationTest.DEFAULT_LANGKEY, null, null, null, null, autorities);
        // Create the User
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        // Get all the users
        restUserMockMvc.perform(get("/api/users?sort=id,desc").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].login").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_LOGIN))).andExpect(jsonPath("$.[*].firstName").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_FIRSTNAME))).andExpect(jsonPath("$.[*].lastName").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_LASTNAME))).andExpect(jsonPath("$.[*].email").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_EMAIL))).andExpect(jsonPath("$.[*].imageUrl").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_IMAGEURL))).andExpect(jsonPath("$.[*].langKey").value(Matchers.hasItem(UserResourceIntegrationTest.DEFAULT_LANGKEY)));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        // Get the user
        restUserMockMvc.perform(get("/api/users/{login}", user.getLogin())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.login").value(user.getLogin())).andExpect(jsonPath("$.firstName").value(UserResourceIntegrationTest.DEFAULT_FIRSTNAME)).andExpect(jsonPath("$.lastName").value(UserResourceIntegrationTest.DEFAULT_LASTNAME)).andExpect(jsonPath("$.email").value(UserResourceIntegrationTest.DEFAULT_EMAIL)).andExpect(jsonPath("$.imageUrl").value(UserResourceIntegrationTest.DEFAULT_IMAGEURL)).andExpect(jsonPath("$.langKey").value(UserResourceIntegrationTest.DEFAULT_LANGKEY));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = new ManagedUserVM(updatedUser.getId(), updatedUser.getLogin(), UserResourceIntegrationTest.UPDATED_PASSWORD, UserResourceIntegrationTest.UPDATED_FIRSTNAME, UserResourceIntegrationTest.UPDATED_LASTNAME, UserResourceIntegrationTest.UPDATED_EMAIL, updatedUser.getActivated(), UserResourceIntegrationTest.UPDATED_IMAGEURL, UserResourceIntegrationTest.UPDATED_LANGKEY, updatedUser.getCreatedBy(), updatedUser.getCreatedDate(), updatedUser.getLastModifiedBy(), updatedUser.getLastModifiedDate(), autorities);
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntegrationTest.UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntegrationTest.UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntegrationTest.UPDATED_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntegrationTest.UPDATED_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntegrationTest.UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    public void updateUserLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = new ManagedUserVM(updatedUser.getId(), UserResourceIntegrationTest.UPDATED_LOGIN, UserResourceIntegrationTest.UPDATED_PASSWORD, UserResourceIntegrationTest.UPDATED_FIRSTNAME, UserResourceIntegrationTest.UPDATED_LASTNAME, UserResourceIntegrationTest.UPDATED_EMAIL, updatedUser.getActivated(), UserResourceIntegrationTest.UPDATED_IMAGEURL, UserResourceIntegrationTest.UPDATED_LANGKEY, updatedUser.getCreatedBy(), updatedUser.getCreatedDate(), updatedUser.getLastModifiedBy(), updatedUser.getLastModifiedDate(), autorities);
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getLogin()).isEqualTo(UserResourceIntegrationTest.UPDATED_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntegrationTest.UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntegrationTest.UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntegrationTest.UPDATED_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntegrationTest.UPDATED_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntegrationTest.UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    public void updateUserExistingEmail() throws Exception {
        // Initialize the database with 2 users
        userRepository.saveAndFlush(user);
        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setImageUrl("");
        anotherUser.setLangKey("en");
        userRepository.saveAndFlush(anotherUser);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = // this email should already be used by anotherUser
        new ManagedUserVM(updatedUser.getId(), updatedUser.getLogin(), updatedUser.getPassword(), updatedUser.getFirstName(), updatedUser.getLastName(), "jhipster@localhost", updatedUser.getActivated(), updatedUser.getImageUrl(), updatedUser.getLangKey(), updatedUser.getCreatedBy(), updatedUser.getCreatedDate(), updatedUser.getLastModifiedBy(), updatedUser.getLastModifiedDate(), autorities);
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateUserExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.random(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setImageUrl("");
        anotherUser.setLangKey("en");
        userRepository.saveAndFlush(anotherUser);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        // Update the user
        User updatedUser = userRepository.findOne(user.getId());
        Set<String> autorities = new HashSet<>();
        autorities.add("ROLE_USER");
        ManagedUserVM managedUserVM = // this login should already be used by anotherUser
        new ManagedUserVM(updatedUser.getId(), "jhipster", updatedUser.getPassword(), updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getEmail(), updatedUser.getActivated(), updatedUser.getImageUrl(), updatedUser.getLangKey(), updatedUser.getCreatedBy(), updatedUser.getCreatedDate(), updatedUser.getLastModifiedBy(), updatedUser.getLastModifiedDate(), autorities);
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();
        // Delete the user
        restUserMockMvc.perform(delete("/api/users/{login}", user.getLogin()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        User userA = new User();
        userA.setLogin("AAA");
        User userB = new User();
        userB.setLogin("BBB");
        assertThat(userA).isNotEqualTo(userB);
    }
}

