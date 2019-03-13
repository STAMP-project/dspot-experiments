package com.vaadin.tests.components.loginform;


import org.junit.Assert;
import org.junit.Test;


public class CustomizedLoginFormUITest extends LoginFormUITest {
    private static final String LABELLED_BY = "aria-labelledby";

    @Test
    public void captionsCorrect() {
        openTestURL();
        Assert.assertEquals("Identifiant", getUsernameCaption());
        Assert.assertEquals("Mot de passe", getPasswordCaption());
        Assert.assertEquals("Se connecter", getLoginCaption());
    }
}

