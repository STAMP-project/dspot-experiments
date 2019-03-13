/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.jenkins.results.parser;


import java.util.Properties;

import static JenkinsMaster.maxRecentBatchAge;


/**
 *
 *
 * @author Peter Yoo
 */
public class LoadBalancerUtilTest extends Test {
    @org.junit.Test
    public void testGetMostAvailableMasterURL() throws Exception {
        maxRecentBatchAge = 0;
        expectedMessageGenerator = new Test.ExpectedMessageGenerator() {
            @Override
            public String getMessage(TestSample testSample) throws Exception {
                Properties properties = getTestProperties(testSample);
                JenkinsResultsParserUtil.setBuildProperties(properties);
                return LoadBalancerUtil.getMostAvailableMasterURL(properties);
            }
        };
        assertSamples();
    }
}

