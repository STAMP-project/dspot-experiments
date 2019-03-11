/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.infra.service;


import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class DefaultPluginLoggingServiceTest {
    @Test
    public void shouldTrimDownLogFileNameToAReasonableSizeIfThePluginIdIsTooBig() throws Exception {
        assertPluginLogFile("abcd", "plugin-abcd.log");
        String pluginIdWithLengthOf189 = StringUtils.repeat("a", 189);
        assertPluginLogFile(pluginIdWithLengthOf189, (("plugin-" + pluginIdWithLengthOf189) + ".log"));
        String pluginIdWithLengthOf190 = StringUtils.repeat("a", 190);
        assertPluginLogFile(pluginIdWithLengthOf190, (("plugin-" + pluginIdWithLengthOf189) + ".log"));
        String pluginIdWithLengthOf200 = StringUtils.repeat("a", 200);
        assertPluginLogFile(pluginIdWithLengthOf200, (("plugin-" + pluginIdWithLengthOf189) + ".log"));
    }
}

