package baeldung.test;


import baeldung.data.UserRepository;
import javax.inject.Inject;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by adam.
 */
@RunWith(CdiTestRunner.class)
public class UserRepositoryUnitTest {
    @Inject
    private UserRepository userRepository;

    @Test
    public void givenFourUsersWhenFindAllShouldReturnFourUsers() {
        Assert.assertThat(userRepository.findAll().size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenFindByFirstNameShouldReturnTwoUsers() {
        Assert.assertThat(userRepository.findByFirstName("Adam").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenFindUsersWithFirstNameShouldReturnTwoUsers() {
        Assert.assertThat(userRepository.findUsersWithFirstName("Adam").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenFindUsersWithFirstNameNativeShouldReturnTwoUsers() {
        Assert.assertThat(userRepository.findUsersWithFirstNameNative("Adam").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenTwoUsersWithSpecifiedLastNameWhenFindByLastNameShouldReturnTwoUsers() {
        Assert.assertThat(userRepository.findByLastName("LastName3").size(), CoreMatchers.equalTo(2));
    }
}

