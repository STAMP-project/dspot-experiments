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


import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Peter Yoo
 */
public class BuildMetricLabelsTest extends BuildTest {
    @Test
    public void testMetricLabelGeneration() throws Exception {
        expectedMessageGenerator = new Test.ExpectedMessageGenerator() {
            @Override
            public String getMessage(TestSample testSample) {
                StringBuilder sb = new StringBuilder();
                Build build = BuildFactory.newBuildFromArchive(testSample.getSampleDirName());
                sb.append(_getMetricLabelsString(build));
                List<Build> downstreamBuilds = build.getDownstreamBuilds(null);
                for (Build downstreamBuild : downstreamBuilds) {
                    sb.append(_getMetricLabelsString(downstreamBuild));
                }
                return sb.toString();
            }
        };
        assertSamples();
    }
}

