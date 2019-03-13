package baeldung.test;


import baeldung.data.SimpleUserRepository;
import baeldung.model.User;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by adam.
 */
@RunWith(CdiTestRunner.class)
public class SimpleUserRepositoryUnitTest {
    @Inject
    private EntityManager entityManager;

    @Inject
    private SimpleUserRepository simpleUserRepository;

    @Test
    public void givenFourUsersWhenFindAllShouldReturnFourUsers() {
        Assert.assertThat(simpleUserRepository.findAll().size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenFindByFirstNameShouldReturnTwoUsers() {
        Assert.assertThat(simpleUserRepository.findByFirstName("Adam").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenFindAnyByFirstNameShouldReturnTwoUsers() {
        Assert.assertThat(simpleUserRepository.findAnyByFirstName("Adam").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenCountByFirstNameShouldReturnSizeTwo() {
        Assert.assertThat(simpleUserRepository.count(), CoreMatchers.equalTo(4));
    }

    @Test
    public void givenTwoUsersWithSpecifiedNameWhenRemoveByFirstNameShouldReturnSizeTwo() {
        simpleUserRepository.remove(entityManager.merge(simpleUserRepository.findById(1L)));
        Assert.assertThat(entityManager.find(User.class, 1L), CoreMatchers.nullValue());
    }

    @Test
    public void givenOneUserWithSpecifiedFirstNameAndLastNameWhenFindByFirstNameAndLastNameShouldReturnOneUser() {
        Assert.assertThat(simpleUserRepository.findByFirstNameAndLastName("Adam", "LastName1").size(), CoreMatchers.equalTo(1));
        Assert.assertThat(simpleUserRepository.findByFirstNameAndLastName("David", "LastName2").size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void givenOneUserWithSpecifiedLastNameWhenFindAnyByLastNameShouldReturnOneUser() {
        Assert.assertThat(simpleUserRepository.findAnyByLastName("LastName1"), CoreMatchers.notNullValue());
    }

    @Test
    public void givenOneUserWithSpecifiedAddressCityWhenFindByCityShouldReturnOneUser() {
        Assert.assertThat(simpleUserRepository.findByAddress_city("London").size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void givenUsersWithSpecifiedFirstOrLastNameWhenFindByFirstNameOrLastNameShouldReturnTwoUsers() {
        Assert.assertThat(simpleUserRepository.findByFirstNameOrLastName("David", "LastName1").size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenUsersWhenFindAllOrderByFirstNameAscShouldReturnFirstAdamLastPeter() {
        List<User> users = simpleUserRepository.findAllOrderByFirstNameAsc();
        Assert.assertThat(users.get(0).getFirstName(), CoreMatchers.equalTo("Adam"));
        Assert.assertThat(users.get(3).getFirstName(), CoreMatchers.equalTo("Peter"));
    }

    @Test
    public void givenUsersWhenFindAllOrderByFirstNameAscLastNameDescShouldReturnFirstAdamLastPeter() {
        List<User> users = simpleUserRepository.findAllOrderByFirstNameAscLastNameDesc();
        Assert.assertThat(users.get(0).getFirstName(), CoreMatchers.equalTo("Adam"));
        Assert.assertThat(users.get(3).getFirstName(), CoreMatchers.equalTo("Peter"));
    }

    @Test
    public void givenUsersWhenFindTop2ShouldReturnTwoUsers() {
        Assert.assertThat(simpleUserRepository.findTop2OrderByFirstNameAsc().size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenUsersWhenFindFirst2ShouldReturnTwoUsers() {
        Assert.assertThat(simpleUserRepository.findFirst2OrderByFirstNameAsc().size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void givenPagesWithSizeTwoWhenFindAllOrderByFirstNameAscShouldReturnTwoPages() {
        Assert.assertThat(simpleUserRepository.findAllOrderByFirstNameAsc(0, 2).size(), CoreMatchers.equalTo(2));
        Assert.assertThat(simpleUserRepository.findAllOrderByFirstNameAsc(2, 4).size(), CoreMatchers.equalTo(2));
    }
}

