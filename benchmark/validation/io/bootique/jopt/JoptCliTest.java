/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.jopt;


import java.util.Collections;
import joptsimple.OptionSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JoptCliTest {
    private OptionSet mockParsed;

    @Test
    public void testStringsFor_Missing() {
        Mockito.when(mockParsed.valueOf(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());
        JoptCli opts = new JoptCli(mockParsed, "aname");
        Assert.assertNotNull(opts.optionStrings("no_such_opt"));
        Assert.assertEquals(0, opts.optionStrings("no_such_opt").size());
    }

    @Test
    public void testCommandName() {
        JoptCli o1 = new JoptCli(mockParsed, "aname");
        Assert.assertEquals("aname", o1.commandName());
        JoptCli o2 = new JoptCli(mockParsed, null);
        Assert.assertNull(o2.commandName());
    }
}

