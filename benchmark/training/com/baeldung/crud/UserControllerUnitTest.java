package com.baeldung.crud;


import com.baeldung.crud.controllers.UserController;
import com.baeldung.crud.entities.User;
import com.baeldung.crud.repositories.UserRepository;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


public class UserControllerUnitTest {
    private static UserController userController;

    private static UserRepository mockedUserRepository;

    private static BindingResult mockedBindingResult;

    private static Model mockedModel;

    @Test
    public void whenCalledshowSignUpForm_thenCorrect() {
        User user = new User("John", "john@domain.com");
        assertThat(UserControllerUnitTest.userController.showSignUpForm(user)).isEqualTo("add-user");
    }

    @Test
    public void whenCalledaddUserAndValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");
        Mockito.when(UserControllerUnitTest.mockedBindingResult.hasErrors()).thenReturn(false);
        assertThat(UserControllerUnitTest.userController.addUser(user, UserControllerUnitTest.mockedBindingResult, UserControllerUnitTest.mockedModel)).isEqualTo("index");
    }

    @Test
    public void whenCalledaddUserAndInValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");
        Mockito.when(UserControllerUnitTest.mockedBindingResult.hasErrors()).thenReturn(true);
        assertThat(UserControllerUnitTest.userController.addUser(user, UserControllerUnitTest.mockedBindingResult, UserControllerUnitTest.mockedModel)).isEqualTo("add-user");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCalledshowUpdateForm_thenIllegalArgumentException() {
        assertThat(UserControllerUnitTest.userController.showUpdateForm(0, UserControllerUnitTest.mockedModel)).isEqualTo("update-user");
    }

    @Test
    public void whenCalledupdateUserAndValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");
        Mockito.when(UserControllerUnitTest.mockedBindingResult.hasErrors()).thenReturn(false);
        assertThat(UserControllerUnitTest.userController.updateUser(1L, user, UserControllerUnitTest.mockedBindingResult, UserControllerUnitTest.mockedModel)).isEqualTo("index");
    }

    @Test
    public void whenCalledupdateUserAndInValidUser_thenCorrect() {
        User user = new User("John", "john@domain.com");
        Mockito.when(UserControllerUnitTest.mockedBindingResult.hasErrors()).thenReturn(true);
        assertThat(UserControllerUnitTest.userController.updateUser(1L, user, UserControllerUnitTest.mockedBindingResult, UserControllerUnitTest.mockedModel)).isEqualTo("update-user");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCalleddeleteUser_thenIllegalArgumentException() {
        assertThat(UserControllerUnitTest.userController.deleteUser(1L, UserControllerUnitTest.mockedModel)).isEqualTo("index");
    }
}

