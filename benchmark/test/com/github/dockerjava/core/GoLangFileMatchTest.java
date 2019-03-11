/**
 * Copyright (C) 2014 SignalFuse, Inc.
 */
package com.github.dockerjava.core;


import com.github.dockerjava.core.exception.GoLangFileMatchException;
import java.io.IOException;
import junit.framework.Assert;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static GoLangFileMatch.IS_WINDOWS;


@RunWith(Parameterized.class)
public class GoLangFileMatchTest {
    @Parameterized.Parameter
    public GoLangFileMatchTest.MatchTestCase testCase;

    @Test
    public void testMatch() throws IOException {
        String pattern = testCase.pattern;
        String s = testCase.s;
        if (IS_WINDOWS) {
            if ((pattern.indexOf('\\')) >= 0) {
                // no escape allowed on windows.
                return;
            }
            pattern = FilenameUtils.normalize(pattern);
            s = FilenameUtils.normalize(s);
        }
        try {
            Boolean matched = GoLangFileMatch.match(pattern, s);
            if (testCase.expectException) {
                Assert.fail("Expected GoFileMatchException");
            }
            Assert.assertEquals(testCase.toString(), testCase.matches, matched);
        } catch (GoLangFileMatchException e) {
            if (!(testCase.expectException)) {
                throw e;
            }
        }
    }

    private static final class MatchTestCase {
        private final String pattern;

        private final String s;

        private final Boolean matches;

        private final Boolean expectException;

        public MatchTestCase(String pattern, String s, Boolean matches, Boolean expectException) {
            super();
            this.pattern = pattern;
            this.s = s;
            this.matches = matches;
            this.expectException = expectException;
        }

        @Override
        public String toString() {
            return ((((((("MatchTestCase [pattern=" + (pattern)) + ", s=") + (s)) + ", matches=") + (matches)) + ", expectException=") + (expectException)) + "]";
        }
    }
}

