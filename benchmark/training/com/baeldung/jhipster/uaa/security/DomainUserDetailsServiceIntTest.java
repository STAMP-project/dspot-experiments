package com.baeldung.jhipster.uaa.security;


import com.baeldung.jhipster.uaa.UaaApp;
import com.baeldung.jhipster.uaa.domain.User;
import com.baeldung.jhipster.uaa.repository.UserRepository;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for DomainUserDetailsService.
 *
 * @see DomainUserDetailsService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class)
@Transactional
public class DomainUserDetailsServiceIntTest {
    private static final String USER_ONE_LOGIN = "test-user-one";

    private static final String USER_ONE_EMAIL = "test-user-one@localhost";

    private static final String USER_TWO_LOGIN = "test-user-two";

    private static final String USER_TWO_EMAIL = "test-user-two@localhost";

    private static final String USER_THREE_LOGIN = "test-user-three";

    private static final String USER_THREE_EMAIL = "test-user-three@localhost";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService domainUserDetailsService;

    private User userOne;

    private User userTwo;

    private User userThree;

    @Test
    @Transactional
    public void assertThatUserCanBeFoundByLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_ONE_LOGIN);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(DomainUserDetailsServiceIntTest.USER_ONE_LOGIN);
    }

    @Test
    @Transactional
    public void assertThatUserCanBeFoundByLoginIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(DomainUserDetailsServiceIntTest.USER_ONE_LOGIN);
    }

    @Test
    @Transactional
    public void assertThatUserCanBeFoundByEmail() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_TWO_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(DomainUserDetailsServiceIntTest.USER_TWO_LOGIN);
    }

    @Test(expected = UsernameNotFoundException.class)
    @Transactional
    public void assertThatUserCanNotBeFoundByEmailIgnoreCase() {
        domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH));
    }

    @Test
    @Transactional
    public void assertThatEmailIsPrioritizedOverLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_ONE_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(DomainUserDetailsServiceIntTest.USER_ONE_LOGIN);
    }

    @Test(expected = UserNotActivatedException.class)
    @Transactional
    public void assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        domainUserDetailsService.loadUserByUsername(DomainUserDetailsServiceIntTest.USER_THREE_LOGIN);
    }
}

