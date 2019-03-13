package com.baeldung.jhipster.uaa.web.rest;


import AuthoritiesConstants.ADMIN;
import AuthoritiesConstants.USER;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import UserRepository.USERS_BY_LOGIN_CACHE;
import com.baeldung.jhipster.uaa.UaaApp;
import com.baeldung.jhipster.uaa.domain.Authority;
import com.baeldung.jhipster.uaa.domain.User;
import com.baeldung.jhipster.uaa.repository.UserRepository;
import com.baeldung.jhipster.uaa.service.MailService;
import com.baeldung.jhipster.uaa.service.UserService;
import com.baeldung.jhipster.uaa.service.dto.UserDTO;
import com.baeldung.jhipster.uaa.service.mapper.UserMapper;
import com.baeldung.jhipster.uaa.web.rest.errors.ExceptionTranslator;
import com.baeldung.jhipster.uaa.web.rest.vm.ManagedUserVM;
import java.time.Instant;
import java.util.Collections;
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
import org.springframework.cache.CacheManager;
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
@SpringBootTest(classes = UaaApp.class)
public class UserResourceIntTest {
    private static final String DEFAULT_LOGIN = "johndoe";

    private static final String UPDATED_LOGIN = "jhipster";

    private static final Long DEFAULT_ID = 1L;

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
    private UserMapper userMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private CacheManager cacheManager;

    private MockMvc restUserMockMvc;

    private User user;

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        // Create the User
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin(UserResourceIntTest.DEFAULT_LOGIN);
        managedUserVM.setPassword(UserResourceIntTest.DEFAULT_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.DEFAULT_LASTNAME);
        managedUserVM.setEmail(UserResourceIntTest.DEFAULT_EMAIL);
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(UserResourceIntTest.DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(USER));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isCreated());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize((databaseSizeBeforeCreate + 1));
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getLogin()).isEqualTo(UserResourceIntTest.DEFAULT_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntTest.DEFAULT_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntTest.DEFAULT_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntTest.DEFAULT_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntTest.DEFAULT_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntTest.DEFAULT_LANGKEY);
    }

    @Test
    @Transactional
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(1L);
        managedUserVM.setLogin(UserResourceIntTest.DEFAULT_LOGIN);
        managedUserVM.setPassword(UserResourceIntTest.DEFAULT_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.DEFAULT_LASTNAME);
        managedUserVM.setEmail(UserResourceIntTest.DEFAULT_EMAIL);
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(UserResourceIntTest.DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(USER));
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
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin(UserResourceIntTest.DEFAULT_LOGIN);// this login should already be used

        managedUserVM.setPassword(UserResourceIntTest.DEFAULT_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.DEFAULT_LASTNAME);
        managedUserVM.setEmail("anothermail@localhost");
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(UserResourceIntTest.DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(USER));
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
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin("anotherlogin");
        managedUserVM.setPassword(UserResourceIntTest.DEFAULT_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.DEFAULT_LASTNAME);
        managedUserVM.setEmail(UserResourceIntTest.DEFAULT_EMAIL);// this email should already be used

        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(UserResourceIntTest.DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(USER));
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
        restUserMockMvc.perform(get("/api/users?sort=id,desc").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].login").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_LOGIN))).andExpect(jsonPath("$.[*].firstName").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_FIRSTNAME))).andExpect(jsonPath("$.[*].lastName").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_LASTNAME))).andExpect(jsonPath("$.[*].email").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_EMAIL))).andExpect(jsonPath("$.[*].imageUrl").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_IMAGEURL))).andExpect(jsonPath("$.[*].langKey").value(Matchers.hasItem(UserResourceIntTest.DEFAULT_LANGKEY)));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        assertThat(cacheManager.getCache(USERS_BY_LOGIN_CACHE).get(user.getLogin())).isNull();
        // Get the user
        restUserMockMvc.perform(get("/api/users/{login}", user.getLogin())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.login").value(user.getLogin())).andExpect(jsonPath("$.firstName").value(UserResourceIntTest.DEFAULT_FIRSTNAME)).andExpect(jsonPath("$.lastName").value(UserResourceIntTest.DEFAULT_LASTNAME)).andExpect(jsonPath("$.email").value(UserResourceIntTest.DEFAULT_EMAIL)).andExpect(jsonPath("$.imageUrl").value(UserResourceIntTest.DEFAULT_IMAGEURL)).andExpect(jsonPath("$.langKey").value(UserResourceIntTest.DEFAULT_LANGKEY));
        assertThat(cacheManager.getCache(USERS_BY_LOGIN_CACHE).get(user.getLogin())).isNotNull();
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
        User updatedUser = userRepository.findById(user.getId()).get();
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setLogin(updatedUser.getLogin());
        managedUserVM.setPassword(UserResourceIntTest.UPDATED_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.UPDATED_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.UPDATED_LASTNAME);
        managedUserVM.setEmail(UserResourceIntTest.UPDATED_EMAIL);
        managedUserVM.setActivated(updatedUser.getActivated());
        managedUserVM.setImageUrl(UserResourceIntTest.UPDATED_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.UPDATED_LANGKEY);
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(USER));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntTest.UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntTest.UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntTest.UPDATED_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntTest.UPDATED_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntTest.UPDATED_LANGKEY);
    }

    @Test
    @Transactional
    public void updateUserLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setLogin(UserResourceIntTest.UPDATED_LOGIN);
        managedUserVM.setPassword(UserResourceIntTest.UPDATED_PASSWORD);
        managedUserVM.setFirstName(UserResourceIntTest.UPDATED_FIRSTNAME);
        managedUserVM.setLastName(UserResourceIntTest.UPDATED_LASTNAME);
        managedUserVM.setEmail(UserResourceIntTest.UPDATED_EMAIL);
        managedUserVM.setActivated(updatedUser.getActivated());
        managedUserVM.setImageUrl(UserResourceIntTest.UPDATED_IMAGEURL);
        managedUserVM.setLangKey(UserResourceIntTest.UPDATED_LANGKEY);
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(USER));
        restUserMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(((userList.size()) - 1));
        assertThat(testUser.getLogin()).isEqualTo(UserResourceIntTest.UPDATED_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UserResourceIntTest.UPDATED_FIRSTNAME);
        assertThat(testUser.getLastName()).isEqualTo(UserResourceIntTest.UPDATED_LASTNAME);
        assertThat(testUser.getEmail()).isEqualTo(UserResourceIntTest.UPDATED_EMAIL);
        assertThat(testUser.getImageUrl()).isEqualTo(UserResourceIntTest.UPDATED_IMAGEURL);
        assertThat(testUser.getLangKey()).isEqualTo(UserResourceIntTest.UPDATED_LANGKEY);
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
        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setLogin(updatedUser.getLogin());
        managedUserVM.setPassword(updatedUser.getPassword());
        managedUserVM.setFirstName(updatedUser.getFirstName());
        managedUserVM.setLastName(updatedUser.getLastName());
        managedUserVM.setEmail("jhipster@localhost");// this email should already be used by anotherUser

        managedUserVM.setActivated(updatedUser.getActivated());
        managedUserVM.setImageUrl(updatedUser.getImageUrl());
        managedUserVM.setLangKey(updatedUser.getLangKey());
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(USER));
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
        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUser.getId());
        managedUserVM.setLogin("jhipster");// this login should already be used by anotherUser

        managedUserVM.setPassword(updatedUser.getPassword());
        managedUserVM.setFirstName(updatedUser.getFirstName());
        managedUserVM.setLastName(updatedUser.getLastName());
        managedUserVM.setEmail(updatedUser.getEmail());
        managedUserVM.setActivated(updatedUser.getActivated());
        managedUserVM.setImageUrl(updatedUser.getImageUrl());
        managedUserVM.setLangKey(updatedUser.getLangKey());
        managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(USER));
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
        assertThat(cacheManager.getCache(USERS_BY_LOGIN_CACHE).get(user.getLogin())).isNull();
        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void getAllAuthorities() throws Exception {
        restUserMockMvc.perform(get("/api/users/authorities").accept(TestUtil.APPLICATION_JSON_UTF8).contentType(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$").value(Matchers.hasItems(USER, ADMIN)));
    }

    @Test
    @Transactional
    public void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId(2L);
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    public void testUserFromId() {
        assertThat(userMapper.userFromId(UserResourceIntTest.DEFAULT_ID).getId()).isEqualTo(UserResourceIntTest.DEFAULT_ID);
        assertThat(userMapper.userFromId(null)).isNull();
    }

    @Test
    public void testUserDTOtoUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UserResourceIntTest.DEFAULT_ID);
        userDTO.setLogin(UserResourceIntTest.DEFAULT_LOGIN);
        userDTO.setFirstName(UserResourceIntTest.DEFAULT_FIRSTNAME);
        userDTO.setLastName(UserResourceIntTest.DEFAULT_LASTNAME);
        userDTO.setEmail(UserResourceIntTest.DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setImageUrl(UserResourceIntTest.DEFAULT_IMAGEURL);
        userDTO.setLangKey(UserResourceIntTest.DEFAULT_LANGKEY);
        userDTO.setCreatedBy(UserResourceIntTest.DEFAULT_LOGIN);
        userDTO.setLastModifiedBy(UserResourceIntTest.DEFAULT_LOGIN);
        userDTO.setAuthorities(Collections.singleton(USER));
        User user = userMapper.userDTOToUser(userDTO);
        assertThat(user.getId()).isEqualTo(UserResourceIntTest.DEFAULT_ID);
        assertThat(user.getLogin()).isEqualTo(UserResourceIntTest.DEFAULT_LOGIN);
        assertThat(user.getFirstName()).isEqualTo(UserResourceIntTest.DEFAULT_FIRSTNAME);
        assertThat(user.getLastName()).isEqualTo(UserResourceIntTest.DEFAULT_LASTNAME);
        assertThat(user.getEmail()).isEqualTo(UserResourceIntTest.DEFAULT_EMAIL);
        assertThat(user.getActivated()).isEqualTo(true);
        assertThat(user.getImageUrl()).isEqualTo(UserResourceIntTest.DEFAULT_IMAGEURL);
        assertThat(user.getLangKey()).isEqualTo(UserResourceIntTest.DEFAULT_LANGKEY);
        assertThat(user.getCreatedBy()).isNull();
        assertThat(user.getCreatedDate()).isNotNull();
        assertThat(user.getLastModifiedBy()).isNull();
        assertThat(user.getLastModifiedDate()).isNotNull();
        assertThat(user.getAuthorities()).extracting("name").containsExactly(USER);
    }

    @Test
    public void testUserToUserDTO() {
        user.setId(UserResourceIntTest.DEFAULT_ID);
        user.setCreatedBy(UserResourceIntTest.DEFAULT_LOGIN);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy(UserResourceIntTest.DEFAULT_LOGIN);
        user.setLastModifiedDate(Instant.now());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(USER);
        authorities.add(authority);
        user.setAuthorities(authorities);
        UserDTO userDTO = userMapper.userToUserDTO(user);
        assertThat(userDTO.getId()).isEqualTo(UserResourceIntTest.DEFAULT_ID);
        assertThat(userDTO.getLogin()).isEqualTo(UserResourceIntTest.DEFAULT_LOGIN);
        assertThat(userDTO.getFirstName()).isEqualTo(UserResourceIntTest.DEFAULT_FIRSTNAME);
        assertThat(userDTO.getLastName()).isEqualTo(UserResourceIntTest.DEFAULT_LASTNAME);
        assertThat(userDTO.getEmail()).isEqualTo(UserResourceIntTest.DEFAULT_EMAIL);
        assertThat(userDTO.isActivated()).isEqualTo(true);
        assertThat(userDTO.getImageUrl()).isEqualTo(UserResourceIntTest.DEFAULT_IMAGEURL);
        assertThat(userDTO.getLangKey()).isEqualTo(UserResourceIntTest.DEFAULT_LANGKEY);
        assertThat(userDTO.getCreatedBy()).isEqualTo(UserResourceIntTest.DEFAULT_LOGIN);
        assertThat(userDTO.getCreatedDate()).isEqualTo(user.getCreatedDate());
        assertThat(userDTO.getLastModifiedBy()).isEqualTo(UserResourceIntTest.DEFAULT_LOGIN);
        assertThat(userDTO.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
        assertThat(userDTO.getAuthorities()).containsExactly(USER);
        assertThat(userDTO.toString()).isNotNull();
    }

    @Test
    public void testAuthorityEquals() {
        Authority authorityA = new Authority();
        assertThat(authorityA).isEqualTo(authorityA);
        assertThat(authorityA).isNotEqualTo(null);
        assertThat(authorityA).isNotEqualTo(new Object());
        assertThat(authorityA.hashCode()).isEqualTo(0);
        assertThat(authorityA.toString()).isNotNull();
        Authority authorityB = new Authority();
        assertThat(authorityA).isEqualTo(authorityB);
        authorityB.setName(ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);
        authorityA.setName(USER);
        assertThat(authorityA).isNotEqualTo(authorityB);
        authorityB.setName(USER);
        assertThat(authorityA).isEqualTo(authorityB);
        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode());
    }
}

