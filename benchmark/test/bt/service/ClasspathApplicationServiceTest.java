/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.service;


import org.junit.Assert;
import org.junit.Test;


public class ClasspathApplicationServiceTest {
    @Test
    public void testDetermineVersion() {
        ClasspathApplicationService applicationService = new ClasspathApplicationService();
        Version version = applicationService.getVersion();
        Assert.assertNotNull(version);
        Assert.assertNotEquals(0, version.getMajor());
    }
}

