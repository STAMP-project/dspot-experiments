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
package org.jboss.as.web.session;


import java.nio.CharBuffer;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link SimpleRoutingSupport}.
 *
 * @author Paul Ferraro
 */
public class SimpleRoutingSupportTestCase {
    private final RoutingSupport routing = new SimpleRoutingSupport();

    @Test
    public void parse() {
        Map.Entry<CharSequence, CharSequence> result = this.routing.parse("session1.route1");
        Assert.assertEquals("session1", result.getKey().toString());
        Assert.assertEquals("route1", result.getValue().toString());
        result = this.routing.parse("session2");
        Assert.assertEquals("session2", result.getKey().toString());
        Assert.assertNull(result.getValue());
        result = this.routing.parse(null);
        Assert.assertNull(result.getKey());
        Assert.assertNull(result.getValue());
        result = this.routing.parse(CharBuffer.wrap("session1.route1"));
        Assert.assertEquals("session1", result.getKey().toString());
        Assert.assertEquals("route1", result.getValue().toString());
        result = this.routing.parse(new StringBuilder("session1.route1"));
        Assert.assertEquals("session1", result.getKey().toString());
        Assert.assertEquals("route1", result.getValue().toString());
    }

    @Test
    public void format() {
        Assert.assertEquals("session1.route1", this.routing.format("session1", "route1").toString());
        Assert.assertEquals("session2", this.routing.format("session2", "").toString());
        Assert.assertEquals("session3", this.routing.format("session3", null).toString());
        Assert.assertEquals("session1.route1", this.routing.format(CharBuffer.wrap("session1"), CharBuffer.wrap("route1")).toString());
        Assert.assertEquals("session1.route1", this.routing.format(new StringBuilder("session1"), new StringBuilder("route1")).toString());
        Assert.assertNull(this.routing.format(null, null));
    }
}

