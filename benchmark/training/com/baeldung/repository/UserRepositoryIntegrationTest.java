package com.baeldung.repository;


import com.baeldung.domain.User;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Created by adam.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenTwoImportFilesWhenFindAllShouldReturnSixUsers() {
        Collection<User> users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(9);
    }
}

