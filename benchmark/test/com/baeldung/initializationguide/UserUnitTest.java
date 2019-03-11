package com.baeldung.initializationguide;


import java.lang.reflect.InvocationTargetException;
import org.junit.Test;


public class UserUnitTest {
    @Test
    public void givenUserInstance_whenIntializedWithNew_thenInstanceIsNotNull() {
        User user = new User("Alice", 1);
        assertThat(user).isNotNull();
    }

    @Test
    public void givenUserInstance_whenInitializedWithReflection_thenInstanceIsNotNull() throws IllegalAccessException, IllegalArgumentException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException {
        User user = User.class.getConstructor(String.class, int.class).newInstance("Alice", 2);
        assertThat(user).isNotNull();
    }

    @Test
    public void givenUserInstance_whenCopiedWithClone_thenExactMatchIsCreated() throws CloneNotSupportedException {
        User user = new User("Alice", 3);
        User clonedUser = ((User) (user.clone()));
        assertThat(clonedUser).isEqualTo(user);
    }

    @Test
    public void givenUserInstance_whenValuesAreNotInitialized_thenUserNameAndIdReturnDefault() {
        User user = new User();
        assertThat(user.getName()).isNull();
        assertThat(((user.getId()) == 0));
    }
}

