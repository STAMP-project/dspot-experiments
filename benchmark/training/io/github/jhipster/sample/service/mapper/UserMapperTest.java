package io.github.jhipster.sample.service.mapper;


import io.github.jhipster.sample.JhipsterSampleApplicationApp;
import io.github.jhipster.sample.domain.User;
import io.github.jhipster.sample.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test class for the UserMapper.
 *
 * @see UserMapper
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class UserMapperTest {
    private static final String DEFAULT_LOGIN = "johndoe";

    @Autowired
    private UserMapper userMapper;

    private User user;

    private UserDTO userDto;

    private static final Long DEFAULT_ID = 1L;

    @Test
    public void usersToUserDTOsShouldMapOnlyNonNullUsers() {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(null);
        List<UserDTO> userDTOS = userMapper.usersToUserDTOs(users);
        assertThat(userDTOS).isNotEmpty();
        assertThat(userDTOS).size().isEqualTo(1);
    }

    @Test
    public void userDTOsToUsersShouldMapOnlyNonNullUsers() {
        List<UserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        usersDto.add(null);
        List<User> users = userMapper.userDTOsToUsers(usersDto);
        assertThat(users).isNotEmpty();
        assertThat(users).size().isEqualTo(1);
    }

    @Test
    public void userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
        Set<String> authoritiesAsString = new HashSet<>();
        authoritiesAsString.add("ADMIN");
        userDto.setAuthorities(authoritiesAsString);
        List<UserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        List<User> users = userMapper.userDTOsToUsers(usersDto);
        assertThat(users).isNotEmpty();
        assertThat(users).size().isEqualTo(1);
        assertThat(users.get(0).getAuthorities()).isNotNull();
        assertThat(users.get(0).getAuthorities()).isNotEmpty();
        assertThat(users.get(0).getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
    }

    @Test
    public void userDTOsToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.setAuthorities(null);
        List<UserDTO> usersDto = new ArrayList<>();
        usersDto.add(userDto);
        List<User> users = userMapper.userDTOsToUsers(usersDto);
        assertThat(users).isNotEmpty();
        assertThat(users).size().isEqualTo(1);
        assertThat(users.get(0).getAuthorities()).isNotNull();
        assertThat(users.get(0).getAuthorities()).isEmpty();
    }

    @Test
    public void userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
        Set<String> authoritiesAsString = new HashSet<>();
        authoritiesAsString.add("ADMIN");
        userDto.setAuthorities(authoritiesAsString);
        userDto.setAuthorities(authoritiesAsString);
        User user = userMapper.userDTOToUser(userDto);
        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAuthorities()).isNotEmpty();
        assertThat(user.getAuthorities().iterator().next().getName()).isEqualTo("ADMIN");
    }

    @Test
    public void userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.setAuthorities(null);
        User user = userMapper.userDTOToUser(userDto);
        assertThat(user).isNotNull();
        assertThat(user.getAuthorities()).isNotNull();
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    public void userDTOToUserMapWithNullUserShouldReturnNull() {
        assertThat(userMapper.userDTOToUser(null)).isNull();
    }

    @Test
    public void testUserFromId() {
        assertThat(userMapper.userFromId(UserMapperTest.DEFAULT_ID).getId()).isEqualTo(UserMapperTest.DEFAULT_ID);
        assertThat(userMapper.userFromId(null)).isNull();
    }
}

