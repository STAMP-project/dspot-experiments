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
package org.glassfish.jersey.tests.e2e.server;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing that {@link Context injection} is done before invoking method annotated with {@link PostConstruct}.
 *
 * @author Michal Gajdos
 */
public class PostConstructTest extends JerseyTest {
    @Path("/")
    public static class Resource {
        private int value;

        @Context
        private UriInfo uri;

        @Context
        private Configuration configuration;

        @PostConstruct
        public void postConstruct() {
            value = ((configuration) != null) ? 1 : 0;
        }

        @GET
        public String get() {
            return (("value=" + (value)) + "|") + (configuration.getProperty("value"));
        }
    }

    public static class MyApplication extends Application {
        private int value;

        @Context
        private UriInfo uriInfo;

        @PostConstruct
        public void postConstruct() {
            value = ((uriInfo) != null) ? 1 : 0;
        }

        @Override
        public Set<Class<?>> getClasses() {
            return Collections.singleton(PostConstructTest.Resource.class);
        }

        @Override
        public Map<String, Object> getProperties() {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("value", value);
            return map;
        }
    }

    @Test
    public void testApplicationResourcePostConstruct() throws Exception {
        Assert.assertEquals("value=1|1", target().request().get(String.class));
    }
}

