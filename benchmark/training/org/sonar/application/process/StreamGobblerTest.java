/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.application.process;


import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class StreamGobblerTest {
    @Test
    public void forward_stream_to_log() {
        InputStream stream = IOUtils.toInputStream("one\nsecond log\nthird log\n");
        Logger logger = Mockito.mock(Logger.class);
        StreamGobbler gobbler = new StreamGobbler(stream, "WEB", logger);
        Mockito.verifyZeroInteractions(logger);
        gobbler.start();
        StreamGobbler.waitUntilFinish(gobbler);
        Mockito.verify(logger).info("one");
        Mockito.verify(logger).info("second log");
        Mockito.verify(logger).info("third log");
        Mockito.verifyNoMoreInteractions(logger);
    }
}

