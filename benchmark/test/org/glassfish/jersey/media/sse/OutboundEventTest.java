/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.media.sse;


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import SseFeature.RECONNECT_NOT_SET;
import java.util.ArrayList;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic set of unit tests for OutboundEvent creation.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class OutboundEventTest {
    @Test
    public void testGetCommonFields() throws Exception {
        OutboundEvent event;
        event = new OutboundEvent.Builder().id("id").name("name").data("data").build();
        Assert.assertEquals("id", event.getId());
        Assert.assertEquals("name", event.getName());
        Assert.assertEquals("data", event.getData());
        Assert.assertEquals(TEXT_PLAIN_TYPE, event.getMediaType());
        Assert.assertEquals(RECONNECT_NOT_SET, event.getReconnectDelay());
        Assert.assertFalse(event.isReconnectDelaySet());
        event = new OutboundEvent.Builder().mediaType(APPLICATION_JSON_TYPE).data("data").build();
        Assert.assertEquals(APPLICATION_JSON_TYPE, event.getMediaType());
        try {
            new OutboundEvent.Builder().mediaType(null);
            Assert.fail("NullPointerException expected when setting null mediaType.");
        } catch (NullPointerException ex) {
            // success
        }
        event = new OutboundEvent.Builder().reconnectDelay((-1000)).data("data").build();
        Assert.assertEquals(RECONNECT_NOT_SET, event.getReconnectDelay());
        Assert.assertFalse(event.isReconnectDelaySet());
        event = new OutboundEvent.Builder().reconnectDelay(1000).data("data").build();
        Assert.assertEquals(1000, event.getReconnectDelay());
        Assert.assertTrue(event.isReconnectDelaySet());
    }

    @Test
    public void testGetCommentOrData() throws Exception {
        Assert.assertEquals("comment", new OutboundEvent.Builder().comment("comment").build().getComment());
        Assert.assertEquals("data", new OutboundEvent.Builder().data("data").build().getData());
        try {
            new OutboundEvent.Builder().data(null);
            Assert.fail("NullPointerException expected when setting null data or data type.");
        } catch (NullPointerException ex) {
            // success
        }
        try {
            new OutboundEvent.Builder().data(((Class) (null)), null);
            Assert.fail("NullPointerException expected when setting null data or data type.");
        } catch (NullPointerException ex) {
            // success
        }
        try {
            new OutboundEvent.Builder().data(((GenericType) (null)), null);
            Assert.fail("NullPointerException expected when setting null data or data type.");
        } catch (NullPointerException ex) {
            // success
        }
        try {
            new OutboundEvent.Builder().build();
            Assert.fail("IllegalStateException when building event with no comment or data.");
        } catch (IllegalStateException ex) {
            // success
        }
    }

    @Test
    public void testDataType() throws Exception {
        OutboundEvent event;
        event = new OutboundEvent.Builder().data("data").build();
        Assert.assertEquals(String.class, event.getType());
        Assert.assertEquals(String.class, event.getGenericType());
        final GenericEntity<ArrayList<String>> data = new GenericEntity<ArrayList<String>>(new ArrayList<String>()) {};
        event = new OutboundEvent.Builder().data(data).build();
        Assert.assertEquals(ArrayList.class, event.getType());
        Assert.assertEquals(ArrayList.class, ReflectionHelper.erasure(event.getGenericType()));
        Assert.assertEquals(data.getType(), event.getGenericType());
        // data part set to an arbitrary instance as it is irrelevant for the test
        event = new OutboundEvent.Builder().data(Integer.class, "data").build();
        Assert.assertEquals(Integer.class, event.getType());
        Assert.assertEquals(Integer.class, event.getGenericType());
        // data part set to an arbitrary instance as it is irrelevant for the test
        event = new OutboundEvent.Builder().data(new GenericType<ArrayList<String>>() {}, "data").build();
        Assert.assertEquals(ArrayList.class, event.getType());
        Assert.assertEquals(ArrayList.class, ReflectionHelper.erasure(event.getGenericType()));
        Assert.assertEquals(getType(), event.getGenericType());
    }
}

