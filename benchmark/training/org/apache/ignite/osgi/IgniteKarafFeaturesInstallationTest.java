/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.osgi;


import org.apache.karaf.features.Feature;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;


/**
 * Pax Exam test class to check if all features could be resolved and installed.
 */
public class IgniteKarafFeaturesInstallationTest extends AbstractIgniteKarafTest {
    /**
     * Number of features expected to exist.
     */
    private static final int EXPECTED_FEATURES = 25;

    private static final String CAMEL_REPO_URI = ("mvn:org.apache.camel.karaf/apache-camel/" + (System.getProperty("camelVersion"))) + "/xml/features";

    /**
     *
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAllBundlesActiveAndFeaturesInstalled() throws Exception {
        // Asssert all bundles except fragments are ACTIVE.
        for (Bundle b : bundleCtx.getBundles()) {
            System.out.println(String.format("Checking state of bundle [symbolicName=%s, state=%s]", b.getSymbolicName(), b.getState()));
            if ((b.getHeaders().get(Constants.FRAGMENT_HOST)) == null)
                Assert.assertTrue(((b.getState()) == (Bundle.ACTIVE)));

        }
        // Check that according to the FeaturesService, all Ignite features except ignite-log4j are installed.
        Feature[] features = featuresSvc.getFeatures(AbstractIgniteKarafTest.IGNITE_FEATURES_NAME_REGEX);
        Assert.assertNotNull(features);
        Assert.assertEquals(IgniteKarafFeaturesInstallationTest.EXPECTED_FEATURES, features.length);
        for (Feature f : features) {
            if (AbstractIgniteKarafTest.IGNORED_FEATURES.contains(f.getName()))
                continue;

            boolean installed = featuresSvc.isInstalled(f);
            System.out.println(String.format("Checking if feature is installed [featureName=%s, installed=%s]", f.getName(), installed));
            Assert.assertTrue(installed);
            Assert.assertEquals(AbstractIgniteKarafTest.PROJECT_VERSION.replaceAll("-", "."), f.getVersion().replaceAll("-", "."));
        }
    }
}

