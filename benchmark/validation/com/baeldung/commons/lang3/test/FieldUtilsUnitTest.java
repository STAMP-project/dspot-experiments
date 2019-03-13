package com.baeldung.commons.lang3.test;


import com.baeldung.commons.lang3.beans.User;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;


public class FieldUtilsUnitTest {
    private static User user;

    @Test
    public void givenFieldUtilsClass_whenCalledgetField_thenCorrect() {
        assertThat(FieldUtils.getField(User.class, "name", true).getName()).isEqualTo("name");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledgetFieldForceAccess_thenCorrect() {
        assertThat(FieldUtils.getField(User.class, "name", true).getName()).isEqualTo("name");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledgetDeclaredFieldForceAccess_thenCorrect() {
        assertThat(FieldUtils.getDeclaredField(User.class, "name", true).getName()).isEqualTo("name");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledgetAllField_thenCorrect() {
        assertThat(FieldUtils.getAllFields(User.class).length).isEqualTo(2);
    }

    @Test
    public void givenFieldUtilsClass_whenCalledreadField_thenCorrect() throws IllegalAccessException {
        assertThat(FieldUtils.readField(FieldUtilsUnitTest.user, "name", true)).isEqualTo("Julie");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledreadDeclaredField_thenCorrect() throws IllegalAccessException {
        assertThat(FieldUtils.readDeclaredField(FieldUtilsUnitTest.user, "name", true)).isEqualTo("Julie");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledwriteField_thenCorrect() throws IllegalAccessException {
        FieldUtils.writeField(FieldUtilsUnitTest.user, "name", "Julie", true);
        assertThat(FieldUtils.readField(FieldUtilsUnitTest.user, "name", true)).isEqualTo("Julie");
    }

    @Test
    public void givenFieldUtilsClass_whenCalledwriteDeclaredField_thenCorrect() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(FieldUtilsUnitTest.user, "name", "Julie", true);
        assertThat(FieldUtils.readField(FieldUtilsUnitTest.user, "name", true)).isEqualTo("Julie");
    }
}

