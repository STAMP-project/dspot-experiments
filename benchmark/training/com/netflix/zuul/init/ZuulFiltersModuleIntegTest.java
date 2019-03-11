/**
 * Copyright 2018 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.netflix.zuul.init;


import com.netflix.governator.guice.test.ModulesForTesting;
import com.netflix.governator.guice.test.junit4.GovernatorJunit4ClassRunner;
import com.netflix.zuul.FilterFileManager.FilterFileManagerConfig;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(GovernatorJunit4ClassRunner.class)
@ModulesForTesting({ ZuulFiltersModule.class })
public class ZuulFiltersModuleIntegTest {
    @Inject
    FilterFileManagerConfig filterFileManagerConfig;

    @Test
    public void scanningWorks() {
        String[] filterLocations = filterFileManagerConfig.getDirectories();
        String[] classNames = filterFileManagerConfig.getClassNames();
        Assert.assertEquals(3, filterLocations.length);
        Assert.assertEquals("outbound", filterLocations[1]);
        Assert.assertEquals(2, classNames.length);
        Assert.assertEquals("com.netflix.zuul.init.TestZuulFilter", classNames[0]);
        Assert.assertEquals("com.netflix.zuul.init2.TestZuulFilter2", classNames[1]);
    }
}

