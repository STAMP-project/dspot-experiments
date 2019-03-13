/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.Debug.Connection.Helpers;


import com.google.security.zynamics.binnavi.Debug.Connection.CMockReader;
import com.google.security.zynamics.binnavi.debug.connection.helpers.DebugProtocolHelper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CDebugProtocolHelperTest {
    @Test
    public void testReadDword() throws IOException {
        final CMockReader reader = new CMockReader(new byte[][]{ new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) } });
        reader.next();
        Assert.assertEquals(4294967295L, DebugProtocolHelper.readDWord(reader));
    }
}

