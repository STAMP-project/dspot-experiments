package com.simpligility.maven.plugins.android.standalonemojos;


import com.simpligility.maven.plugins.android.AbstractAndroidMojoTestCase;
import com.simpligility.maven.plugins.android.config.ConfigHandler;
import com.simpligility.maven.plugins.android.configuration.MetaInf;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Pappy ST?NESCU - pappy.stanescu@gmail.com
 */
@Ignore("This test has to be migrated to be an IntegrationTest using AbstractAndroidMojoIntegrationTest")
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnpackMojoTest extends AbstractAndroidMojoTestCase<UnpackMojo> {
    private final String projectName;

    private final MetaInf expected;

    public UnpackMojoTest(String projectName, MetaInf expected) {
        this.projectName = projectName;
        this.expected = expected;
    }

    @Test
    public void testConfigHelper() throws Exception {
        final UnpackMojo mojo = createMojo(this.projectName);
        final ConfigHandler cfh = new ConfigHandler(mojo, this.session, this.execution);
        cfh.parseConfiguration();
        MetaInf result = getFieldValue(mojo, "unpackMetaInf");
        Assert.assertEquals(this.expected, result);
        Assert.assertEquals((result == null), ((getFieldValue(mojo, "unpack")) == null));
    }
}

