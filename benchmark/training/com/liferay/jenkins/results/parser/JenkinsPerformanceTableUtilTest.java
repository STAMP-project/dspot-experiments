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


import java.util.regex.Pattern;


/**
 *
 *
 * @author Peter Yoo
 */
public class JenkinsPerformanceTableUtilTest extends Test {
    @org.junit.Test
    public void testGenerateHTML() throws Exception {
        expectedMessageGenerator = new Test.ExpectedMessageGenerator() {
            @Override
            public String getMessage(TestSample testSample) throws Exception {
                String content = JenkinsResultsParserUtil.toString(JenkinsResultsParserUtil.getLocalURL((("${dependencies.url}" + (testSample.getSampleDirName())) + "/urls.txt")));
                if ((content.length()) == 0) {
                    return "";
                }
                for (String url : content.split("\\|")) {
                    JenkinsPerformanceDataUtil.processPerformanceData("build", url.trim(), 100);
                }
                return JenkinsPerformanceTableUtil.generateHTML();
            }
        };
        assertSamples();
    }

    private static final Pattern _progressiveTextPattern = Pattern.compile("\\\'.*\\\' completed at (?<url>.+)\\.");
}

