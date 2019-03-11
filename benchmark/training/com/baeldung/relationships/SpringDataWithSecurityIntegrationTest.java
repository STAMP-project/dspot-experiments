package com.baeldung.relationships;


import com.baeldung.data.repositories.TweetRepository;
import com.baeldung.data.repositories.UserRepository;
import com.baeldung.models.AppUser;
import com.baeldung.models.Tweet;
import com.baeldung.util.DummyContentUtil;
import java.util.Date;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
@DirtiesContext
public class SpringDataWithSecurityIntegrationTest {
    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

    @Autowired
    private ServletContext servletContext;

    private static UserRepository userRepository;

    private static TweetRepository tweetRepository;

    @Test
    public void givenAppUser_whenLoginSuccessful_shouldUpdateLastLogin() {
        AppUser appUser = SpringDataWithSecurityIntegrationTest.userRepository.findByUsername("lionel@messi.com");
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(new com.baeldung.security.AppUserPrincipal(appUser), null, DummyContentUtil.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        SpringDataWithSecurityIntegrationTest.userRepository.updateLastLogin(new Date());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void givenNoAppUserInSecurityContext_whenUpdateLastLoginAttempted_shouldFail() {
        SpringDataWithSecurityIntegrationTest.userRepository.updateLastLogin(new Date());
    }

    @Test
    public void givenAppUser_whenLoginSuccessful_shouldReadMyPagedTweets() {
        AppUser appUser = SpringDataWithSecurityIntegrationTest.userRepository.findByUsername("lionel@messi.com");
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(new com.baeldung.security.AppUserPrincipal(appUser), null, DummyContentUtil.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        Page<Tweet> page = null;
        do {
            page = SpringDataWithSecurityIntegrationTest.tweetRepository.getMyTweetsAndTheOnesILiked(new org.springframework.data.domain.PageRequest((page != null ? (page.getNumber()) + 1 : 0), 5));
            for (Tweet twt : page.getContent()) {
                isTrue((((twt.getOwner()) == (appUser.getUsername())) || (twt.getLikes().contains(appUser.getUsername()))), "I do not have any Tweets");
            }
        } while (page.hasNext() );
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void givenNoAppUser_whenPaginatedResultsRetrievalAttempted_shouldFail() {
        Page<Tweet> page = null;
        do {
            page = SpringDataWithSecurityIntegrationTest.tweetRepository.getMyTweetsAndTheOnesILiked(new org.springframework.data.domain.PageRequest((page != null ? (page.getNumber()) + 1 : 0), 5));
        } while ((page != null) && (page.hasNext()) );
    }
}

