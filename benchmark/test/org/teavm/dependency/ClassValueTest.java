/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.dependency;


import org.junit.Assert;
import org.junit.Test;
import org.teavm.tooling.TeaVMToolLog;


public class ClassValueTest {
    @Test
    public void simple() {
        ValueDependencyInfo info = runTestWithConsume("simpleSnippet").getClassValueNode();
        Assert.assertTrue("Long must be consumed", info.hasType("java.lang.Long"));
        Assert.assertTrue("String must be consumed", info.hasType("java.lang.String"));
        Assert.assertTrue("Nothing except Long and String expected", ((info.getTypes().length) == 2));
    }

    @Test
    public void fromGetClass() {
        ValueDependencyInfo info = runTestWithConsume("fromGetClassSnippet").getClassValueNode();
        Assert.assertTrue("Long must be consumed", info.hasType("java.lang.Long"));
        Assert.assertTrue("String must be consumed", info.hasType("java.lang.String"));
        Assert.assertTrue("Nothing except Long and String expected", ((info.getTypes().length) == 2));
    }

    static class Log implements TeaVMToolLog {
        StringBuilder sb = new StringBuilder();

        @Override
        public void info(String text) {
            appendLine(text);
        }

        @Override
        public void debug(String text) {
            appendLine(text);
        }

        @Override
        public void warning(String text) {
            appendLine(text);
        }

        @Override
        public void error(String text) {
            appendLine(text);
        }

        @Override
        public void info(String text, Throwable e) {
            appendLine(text);
        }

        @Override
        public void debug(String text, Throwable e) {
            appendLine(text);
        }

        @Override
        public void warning(String text, Throwable e) {
            appendLine(text);
        }

        @Override
        public void error(String text, Throwable e) {
            appendLine(text);
        }

        private void appendLine(String text) {
            sb.append(text).append('\n');
        }
    }
}

