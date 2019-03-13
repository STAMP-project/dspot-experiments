/**
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.domain.management.cli;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for providing profile cloning ability (runtime (CLI)) to create
 * new profiles based on existing JBoss profiles.
 *
 * Test clone "default" profile to "clone-profile-test-case-default" profile.
 *
 * https://issues.jboss.org/browse/WFLY-4838
 *
 * @author Marek Kopecky <mkopecky@redhat.com>
 */
public class CloneProfileTestCase extends AbstractCliTestBase {
    private static Logger log = Logger.getLogger(CloneProfileTestCase.class);

    private static final String ORIGINAL_PROFILE = "default";

    private static final String NEW_PROFILE = "clone-profile-test-case-default";

    /**
     * Domain configuration
     */
    private static File domainCfg;

    @Test
    public void testProfile() throws IOException {
        // get domain configuration
        String domainCfgContent = FileUtils.readFileToString(CloneProfileTestCase.domainCfg);
        Assert.assertTrue("Domain configuration is not initialized correctly.", ((domainCfgContent.indexOf(CloneProfileTestCase.NEW_PROFILE)) == (-1)));
        // clone profile
        cliRequest((((("/profile=" + (CloneProfileTestCase.ORIGINAL_PROFILE)) + ":clone(to-profile=") + (CloneProfileTestCase.NEW_PROFILE)) + ")"), true);
        // get and check submodules
        String originSubmodules = cliRequest((("ls /profile=" + (CloneProfileTestCase.ORIGINAL_PROFILE)) + "/subsystem"), false);
        String newSubmodules = cliRequest((("ls /profile=" + (CloneProfileTestCase.NEW_PROFILE)) + "/subsystem"), false);
        Assert.assertEquals("New profile has different submodules than origin profile.", originSubmodules, newSubmodules);
        // check domain configuration
        domainCfgContent = FileUtils.readFileToString(CloneProfileTestCase.domainCfg);
        Assert.assertTrue((("Domain configuration doesn't contain " + (CloneProfileTestCase.NEW_PROFILE)) + "profile."), ((domainCfgContent.indexOf(CloneProfileTestCase.NEW_PROFILE)) != (-1)));
    }
}

