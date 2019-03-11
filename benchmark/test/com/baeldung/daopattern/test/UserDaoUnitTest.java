package com.baeldung.daopattern.test;


import com.baeldung.daopattern.daos.UserDao;
import com.baeldung.daopattern.entities.User;
import java.util.List;
import java.util.Optional;
import org.junit.Test;


public class UserDaoUnitTest {
    private static UserDao userDao;

    @Test
    public void givenUserDaoInstance_whenCalledget_thenOneAssertion() {
        assertThat(UserDaoUnitTest.userDao.get(0)).isInstanceOf(Optional.class);
    }

    @Test
    public void givenUserDaoInstance_whenCalledgetAll_thenOneAssertion() {
        assertThat(UserDaoUnitTest.userDao.getAll()).isInstanceOf(List.class);
    }

    @Test
    public void givenUserDaoInstance_whenCalledupdate_thenTwoAssertions() {
        User user = new User("Julie", "julie@domain.com");
        UserDaoUnitTest.userDao.update(user, new String[]{ "Julie", "julie@domain.com" });
        assertThat(UserDaoUnitTest.userDao.get(2).get().getName()).isEqualTo("Julie");
        assertThat(UserDaoUnitTest.userDao.get(2).get().getEmail()).isEqualTo("julie@domain.com");
    }

    @Test
    public void givenUserDaoInstance_whenCalledsave_thenTwoAssertions() {
        User user = new User("Julie", "julie@domain.com");
        UserDaoUnitTest.userDao.save(user);
        assertThat(UserDaoUnitTest.userDao.get(2).get().getName()).isEqualTo("Julie");
        assertThat(UserDaoUnitTest.userDao.get(2).get().getEmail()).isEqualTo("julie@domain.com");
    }

    @Test
    public void givenUserDaoInstance_whenCalleddelete_thenOneAssertion() {
        User user = new User("Julie", "julie@domain.com");
        UserDaoUnitTest.userDao.delete(user);
        assertThat(UserDaoUnitTest.userDao.getAll().size()).isEqualTo(2);
    }
}

