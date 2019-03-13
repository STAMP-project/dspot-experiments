/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ServletPathMathcherTest {
    /**
     * PatternMatcher used in determining which paths to react to for a given request.
     */
    protected PatternMatcher pathMatcher = new ServletPathMatcher();

    @Test
    public void testStartsWithPattern() {
        String[] bogusPatterns = new String[]{ "/druid*", "/druid*/what*", "*/druid*" };
        String[] bogusSources = new String[]{ "/druid", "/druid/index.html", "/druid*/what/xyu" };
        boolean result = false;
        for (int i = 0; i < (bogusSources.length); i++) {
            for (int j = 0; j < (bogusPatterns.length); j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            Assert.assertThat(true, CoreMatchers.equalTo(result));
            result = false;
        }
    }

    @Test
    public void testEndsWithPattern() {
        String[] bogusPatterns = new String[]{ "*.html", "*.ico", "*.css" };
        String[] bogusSources = new String[]{ "/index.html", "/favicon.ico", "/druid.css" };
        boolean result = false;
        for (int i = 0; i < (bogusSources.length); i++) {
            for (int j = 0; j < (bogusPatterns.length); j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            Assert.assertThat(true, CoreMatchers.equalTo(result));
            result = false;
        }
    }

    @Test
    public void testEqualsPattern() {
        String[] bogusPatterns = new String[]{ "/index.html", "/favicon.ico", "/xyz" };
        String[] bogusSources = new String[]{ "/index.html", "/favicon.ico", "/xyz" };
        boolean result = false;
        for (int i = 0; i < (bogusSources.length); i++) {
            for (int j = 0; j < (bogusPatterns.length); j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            Assert.assertThat(true, CoreMatchers.equalTo(result));
            result = false;
        }
    }

    @Test
    public void testPatternPriority() {
        String[] bogusPatterns = new String[]{ "*html*", "/favicon.ico*", "*html" };
        String[] bogusSources = new String[]{ "*html/ok?", "/favicon.ico/ok?", "/index.html" };
        boolean result = false;
        for (int i = 0; i < (bogusSources.length); i++) {
            for (int j = 0; j < (bogusPatterns.length); j++) {
                String bogusSource = bogusSources[i];
                String bogusPattern = bogusPatterns[j];
                if (pathMatcher.matches(bogusPattern, bogusSource)) {
                    result = true;
                }
                if (result == true) {
                    break;
                }
            }
            Assert.assertThat(true, CoreMatchers.equalTo(result));
            result = false;
        }
    }
}

