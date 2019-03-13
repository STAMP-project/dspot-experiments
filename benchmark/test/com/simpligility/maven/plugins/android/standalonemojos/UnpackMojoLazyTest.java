package com.simpligility.maven.plugins.android.standalonemojos;


import com.simpligility.maven.plugins.android.AbstractAndroidMojoTestCase;
import com.simpligility.maven.plugins.android.config.ConfigHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Pappy ST?NESCU - pappy.stanescu@gmail.com
 */
@Ignore("This test has to be migrated to be an IntegrationTest using AbstractAndroidMojoIntegrationTest")
@RunWith(Parameterized.class)
public class UnpackMojoLazyTest extends AbstractAndroidMojoTestCase<UnpackMojo> {
    private final String projectName;

    public UnpackMojoLazyTest(String projectName) {
        this.projectName = projectName;
    }

    @Test
    public void testConfigHelper() throws Exception {
        final UnpackMojo mojo = createMojo(this.projectName);
        final ConfigHandler cfh = new ConfigHandler(mojo, this.session, this.execution);
        cfh.parseConfiguration();
        Boolean result = getFieldValue(mojo, "unpackLazy");
        Assert.assertNotNull(result);
        Assert.assertTrue(result);
    }
}

