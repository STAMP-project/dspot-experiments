/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.wildfly.clustering.web.undertow.session;


import java.util.AbstractMap;
import org.jboss.as.web.session.RoutingSupport;
import org.jboss.as.web.session.SessionIdentifierCodec;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.web.session.RouteLocator;


/**
 * Unit test for {@link DistributableSessionIdentifierCodec}.
 *
 * @author Paul Ferraro
 */
public class DistributableSessionIdentifierCodecTestCase {
    private final RoutingSupport routing = Mockito.mock(RoutingSupport.class);

    private final RouteLocator locator = Mockito.mock(RouteLocator.class);

    private SessionIdentifierCodec codec = new DistributableSessionIdentifierCodec(this.locator, this.routing);

    @Test
    public void encode() {
        String sessionId = "session";
        Mockito.when(this.locator.locate(sessionId)).thenReturn(null);
        CharSequence result = this.codec.encode(sessionId);
        Assert.assertSame(sessionId, result);
        String route = "route";
        String encodedSessionId = "session.route";
        Mockito.when(this.locator.locate(sessionId)).thenReturn(route);
        Mockito.when(this.routing.format(sessionId, route)).thenReturn(encodedSessionId);
        result = this.codec.encode(sessionId);
        Assert.assertSame(encodedSessionId, result);
    }

    @Test
    public void decode() {
        String encodedSessionId = "session.route";
        String sessionId = "session";
        String route = "route";
        Mockito.when(this.routing.parse(encodedSessionId)).thenReturn(new AbstractMap.SimpleImmutableEntry(sessionId, route));
        CharSequence result = this.codec.decode(encodedSessionId);
        Assert.assertSame(sessionId, result);
    }
}

