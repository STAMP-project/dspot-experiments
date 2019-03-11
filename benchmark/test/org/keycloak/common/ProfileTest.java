package org.keycloak.common;


import Profile.Feature;
import Profile.Feature.ACCOUNT2;
import Profile.Feature.ACCOUNT_API;
import Profile.Feature.ADMIN_FINE_GRAINED_AUTHZ;
import Profile.Feature.AUTHZ_DROOLS_POLICY;
import Profile.Feature.DOCKER;
import Profile.Feature.IMPERSONATION;
import Profile.Feature.OPENSHIFT_INTEGRATION;
import Profile.Feature.SCRIPTS;
import Profile.Feature.TOKEN_EXCHANGE;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class ProfileTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void checkDefaults() {
        Assert.assertEquals("community", Profile.getName());
        ProfileTest.assertEquals(Profile.getDisabledFeatures(), ACCOUNT2, ACCOUNT_API, ADMIN_FINE_GRAINED_AUTHZ, DOCKER, SCRIPTS, TOKEN_EXCHANGE, AUTHZ_DROOLS_POLICY, OPENSHIFT_INTEGRATION);
        ProfileTest.assertEquals(Profile.getPreviewFeatures(), ACCOUNT_API, ADMIN_FINE_GRAINED_AUTHZ, SCRIPTS, TOKEN_EXCHANGE, AUTHZ_DROOLS_POLICY, OPENSHIFT_INTEGRATION);
        ProfileTest.assertEquals(Profile.getExperimentalFeatures(), ACCOUNT2);
    }

    @Test
    public void configWithSystemProperties() {
        Assert.assertEquals("community", Profile.getName());
        Assert.assertFalse(Profile.isFeatureEnabled(DOCKER));
        Assert.assertFalse(Profile.isFeatureEnabled(OPENSHIFT_INTEGRATION));
        Assert.assertTrue(Profile.isFeatureEnabled(IMPERSONATION));
        System.setProperty("keycloak.profile", "preview");
        System.setProperty("keycloak.profile.feature.docker", "enabled");
        System.setProperty("keycloak.profile.feature.impersonation", "disabled");
        Profile.init();
        Assert.assertEquals("preview", Profile.getName());
        Assert.assertTrue(Profile.isFeatureEnabled(DOCKER));
        Assert.assertTrue(Profile.isFeatureEnabled(OPENSHIFT_INTEGRATION));
        Assert.assertFalse(Profile.isFeatureEnabled(IMPERSONATION));
        System.getProperties().remove("keycloak.profile");
        System.getProperties().remove("keycloak.profile.feature.docker");
        System.getProperties().remove("keycloak.profile.feature.impersonation");
        Profile.init();
    }

    @Test
    public void configWithPropertiesFile() throws IOException {
        Assert.assertEquals("community", Profile.getName());
        Assert.assertFalse(Profile.isFeatureEnabled(DOCKER));
        Assert.assertTrue(Profile.isFeatureEnabled(IMPERSONATION));
        File d = temporaryFolder.newFolder();
        File f = new File(d, "profile.properties");
        Properties p = new Properties();
        p.setProperty("profile", "preview");
        p.setProperty("feature.docker", "enabled");
        p.setProperty("feature.impersonation", "disabled");
        PrintWriter pw = new PrintWriter(f);
        p.list(pw);
        pw.close();
        System.setProperty("jboss.server.config.dir", d.getAbsolutePath());
        Profile.init();
        Assert.assertEquals("preview", Profile.getName());
        Assert.assertTrue(Profile.isFeatureEnabled(DOCKER));
        Assert.assertTrue(Profile.isFeatureEnabled(OPENSHIFT_INTEGRATION));
        Assert.assertFalse(Profile.isFeatureEnabled(IMPERSONATION));
        System.getProperties().remove("jboss.server.config.dir");
        Profile.init();
    }

    private static class FeatureComparator implements Comparator<Profile.Feature> {
        @Override
        public int compare(Profile.Feature o1, Profile.Feature o2) {
            return o1.name().compareTo(o2.name());
        }
    }
}

