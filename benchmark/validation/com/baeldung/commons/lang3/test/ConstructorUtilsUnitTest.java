package com.baeldung.commons.lang3.test;


import com.baeldung.commons.lang3.beans.User;
import java.lang.reflect.Constructor;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.junit.Test;


public class ConstructorUtilsUnitTest {
    @Test
    public void givenConstructorUtilsClass_whenCalledgetAccessibleConstructor_thenCorrect() {
        assertThat(ConstructorUtils.getAccessibleConstructor(User.class, String.class, String.class)).isInstanceOf(Constructor.class);
    }

    @Test
    public void givenConstructorUtilsClass_whenCalledinvokeConstructor_thenCorrect() throws Exception {
        assertThat(ConstructorUtils.invokeConstructor(User.class, "name", "email")).isInstanceOf(User.class);
    }

    @Test
    public void givenConstructorUtilsClass_whenCalledinvokeExactConstructor_thenCorrect() throws Exception {
        String[] args = new String[]{ "name", "email" };
        Class[] parameterTypes = new Class[]{ String.class, String.class };
        assertThat(ConstructorUtils.invokeExactConstructor(User.class, args, parameterTypes)).isInstanceOf(User.class);
    }
}

