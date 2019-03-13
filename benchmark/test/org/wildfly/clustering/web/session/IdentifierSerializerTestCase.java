/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.web.session;


import IdentifierSerializer.BASE64;
import IdentifierSerializer.HEX;
import IdentifierSerializer.UTF8;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.server.session.SessionIdGenerator;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;


/**
 * Unit test for {@link IdentifierSerializer}.
 *
 * @author Paul Ferraro
 */
public class IdentifierSerializerTestCase {
    @Test
    public void testUTF8() throws IOException {
        IdentifierSerializerTestCase.test(UTF8, () -> UUID.randomUUID().toString());
    }

    @Test
    public void testBase64() throws IOException {
        SessionIdGenerator generator = new SecureRandomSessionIdGenerator();
        IdentifierSerializerTestCase.test(BASE64, () -> generator.createSessionId());
    }

    @Test
    public void testHex() throws IOException {
        IdentifierSerializerTestCase.test(HEX, () -> {
            // Adapted from org.apache.catalina.util.StandardSessionIdGenerator
            byte[] buffer = new byte[16];
            int sessionIdLength = 16;
            // Render the result as a String of hexadecimal digits
            StringBuilder builder = new StringBuilder((2 * sessionIdLength));
            int resultLenBytes = 0;
            Random random = new Random(System.currentTimeMillis());
            while (resultLenBytes < sessionIdLength) {
                random.nextBytes(buffer);
                for (int j = 0; (j < buffer.length) && (resultLenBytes < sessionIdLength); j++) {
                    byte b1 = ((byte) (((buffer[j]) & 240) >> 4));
                    byte b2 = ((byte) ((buffer[j]) & 15));
                    if (b1 < 10)
                        builder.append(((char) ('0' + b1)));
                    else
                        builder.append(((char) ('A' + (b1 - 10))));

                    if (b2 < 10)
                        builder.append(((char) ('0' + b2)));
                    else
                        builder.append(((char) ('A' + (b2 - 10))));

                    resultLenBytes++;
                }
            } 
            return builder.toString();
        });
    }
}

