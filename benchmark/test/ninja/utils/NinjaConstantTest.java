/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;


import NinjaConstant.applicationCookiePrefix;
import NinjaConstant.applicationLanguages;
import NinjaConstant.applicationName;
import NinjaConstant.applicationSecret;
import NinjaConstant.serverName;
import NinjaConstant.sessionExpireTimeInSeconds;
import NinjaConstant.sessionHttpOnly;
import NinjaConstant.sessionSendOnlyIfChanged;
import NinjaConstant.sessionTransferredOverHttpsOnly;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class NinjaConstantTest {
    /**
     * This testcase makes sure that all constants defined
     * in NinjaConstant are working.
     *
     * File conf/all_constants.conf simply contains all contstants.
     * We simply read them in and check if the constants are okay.
     *
     * Aim is to prevent stupid spelling mistakes.
     */
    @Test
    public void testAllConstants() {
        Configuration configuration = SwissKnife.loadConfigurationInUtf8("conf/all_constants.conf");
        Assert.assertEquals("LANGUAGES", configuration.getString(applicationLanguages));
        Assert.assertEquals("PREFIX", configuration.getString(applicationCookiePrefix));
        Assert.assertEquals("NAME", configuration.getString(applicationName));
        Assert.assertEquals("SECRET", configuration.getString(applicationSecret));
        Assert.assertEquals("SERVER_NAME", configuration.getString(serverName));
        Assert.assertEquals(9999, configuration.getInt(sessionExpireTimeInSeconds));
        Assert.assertEquals(false, configuration.getBoolean(sessionSendOnlyIfChanged));
        Assert.assertEquals(false, configuration.getBoolean(sessionTransferredOverHttpsOnly));
        Assert.assertEquals(true, configuration.getBoolean(sessionHttpOnly));
    }
}

