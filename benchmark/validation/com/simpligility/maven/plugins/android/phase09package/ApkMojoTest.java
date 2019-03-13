package com.simpligility.maven.plugins.android.phase09package;


import com.simpligility.maven.plugins.android.AbstractAndroidMojoTestCase;
import com.simpligility.maven.plugins.android.config.ConfigHandler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Ignore("This test has to be migrated to be an IntegrationTest using AbstractAndroidMojoIntegrationTest")
@RunWith(Parameterized.class)
public class ApkMojoTest extends AbstractAndroidMojoTestCase<ApkMojo> {
    private final String projectName;

    private final String[] expected;

    public ApkMojoTest(String projectName, String[] expected) {
        this.projectName = projectName;
        this.expected = expected;
    }

    @Test
    public void testConfigHelper() throws Exception {
        final ApkMojo mojo = createMojo(this.projectName);
        final ConfigHandler cfh = new ConfigHandler(mojo, this.session, this.execution);
        cfh.parseConfiguration();
        final String[] includes = getFieldValue(mojo, "apkMetaIncludes");
        Assert.assertArrayEquals(this.expected, includes);
    }
}

