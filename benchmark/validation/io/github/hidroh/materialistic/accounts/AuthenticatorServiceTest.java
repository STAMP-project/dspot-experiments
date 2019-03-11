package io.github.hidroh.materialistic.accounts;


import android.content.Intent;
import io.github.hidroh.materialistic.test.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.android.controller.ServiceController;


@RunWith(TestRunner.class)
public class AuthenticatorServiceTest {
    private AuthenticatorService service;

    private ServiceController<AuthenticatorService> controller;

    @Test
    public void testBinder() {
        Assert.assertNotNull(service.onBind(new Intent()));
    }
}

