/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.common;


import java.lang.reflect.Field;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class EnvironmentTest {
    @Test
    public void logStateTest() throws Exception {
        Logger LOG = Mockito.mock(Logger.class);
        Field logField = Environment.class.getDeclaredField("LOG");
        // set final static field
        setFinalStatic(logField, LOG);
        Environment.logState();
        Mockito.verify(LOG, Mockito.atLeastOnce()).info(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Version.class), ArgumentMatchers.any(Version.class));
    }
}

