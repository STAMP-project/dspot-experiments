package com.gitblit.tests;


import Keys.web.avatarClass;
import com.gitblit.AvatarGenerator;
import com.gitblit.GravatarGenerator;
import com.gitblit.IStoredSettings;
import com.gitblit.guice.AvatarGeneratorProvider;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.manager.RuntimeManager;
import com.gitblit.tests.mock.MemorySettings;
import com.gitblit.utils.ActivityUtils;
import com.gitblit.utils.XssFilter;
import com.gitblit.utils.XssFilter.AllowXssFilter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;


public class GravatarTest extends GitblitUnitTest {
    public static class AvatarModule extends AbstractModule {
        private final IStoredSettings settings;

        AvatarModule(IStoredSettings settings) {
            this.settings = settings;
        }

        @Override
        protected void configure() {
            bind(IStoredSettings.class).toInstance(settings);
            bind(XssFilter.class).to(AllowXssFilter.class);
            bind(IRuntimeManager.class).to(RuntimeManager.class);
            bind(AvatarGenerator.class).toProvider(AvatarGeneratorProvider.class);
        }
    }

    @Test
    public void gravatarIdenticonTest() {
        IStoredSettings settings = new MemorySettings();
        settings.overrideSetting(avatarClass, GravatarGenerator.class.getName());
        Injector injector = Guice.createInjector(new GravatarTest.AvatarModule(settings));
        AvatarGenerator avatarGenerator = injector.getInstance(AvatarGenerator.class);
        String username = "username";
        String emailAddress = "emailAddress";
        int width = 10;
        String url = avatarGenerator.getURL(username, emailAddress, true, width);
        Assert.assertNotNull(url);
        Assert.assertEquals(ActivityUtils.getGravatarIdenticonUrl(emailAddress, width), url);
    }

    @Test
    public void gravatarThumbnailTest() {
        IStoredSettings settings = new MemorySettings();
        settings.overrideSetting(avatarClass, GravatarGenerator.class.getName());
        Injector injector = Guice.createInjector(new GravatarTest.AvatarModule(settings));
        AvatarGenerator avatarGenerator = injector.getInstance(AvatarGenerator.class);
        String username = "username";
        String emailAddress = "emailAddress";
        int width = 10;
        String url = avatarGenerator.getURL(username, emailAddress, false, width);
        Assert.assertNotNull(url);
        Assert.assertEquals(ActivityUtils.getGravatarThumbnailUrl(emailAddress, width), url);
    }
}

