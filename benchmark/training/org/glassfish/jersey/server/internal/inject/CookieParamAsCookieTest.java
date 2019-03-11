/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.internal.inject;


import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class CookieParamAsCookieTest extends AbstractTest {
    @Path("/")
    public static class CookieTypeResource {
        @POST
        public String post(@Context
        HttpHeaders h, @CookieParam("one")
        Cookie one, @CookieParam("two")
        Cookie two, @CookieParam("three")
        Cookie three) {
            Assert.assertEquals("one", one.getName());
            Assert.assertEquals("value_one", one.getValue());
            Assert.assertEquals("two", two.getName());
            Assert.assertEquals("value_two", two.getValue());
            Assert.assertEquals(null, three);
            Map<String, Cookie> cs = h.getCookies();
            Assert.assertEquals(2, cs.size());
            Assert.assertEquals("value_one", cs.get("one").getValue());
            Assert.assertEquals("value_two", cs.get("two").getValue());
            return "content";
        }
    }

    @Test
    public void testCookieParam() throws InterruptedException, ExecutionException {
        initiateWebApplication(CookieParamAsCookieTest.CookieTypeResource.class);
        Cookie one = new Cookie("one", "value_one");
        Cookie two = new Cookie("two", "value_two");
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "POST").cookie(one).cookie(two).build()).getEntity());
    }
}

