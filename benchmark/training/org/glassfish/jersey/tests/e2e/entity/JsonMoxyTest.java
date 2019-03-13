/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.entity;


import MediaType.APPLICATION_JSON_TYPE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@RunWith(ConcurrentRunner.class)
public class JsonMoxyTest extends AbstractTypeTester {
    @Path("JAXBElementListResource")
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public static class JAXBElementListResource extends AbstractTypeTester.AResource<List<JAXBElement<String>>> {}

    public static final class MoxyJsonConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {
        @Override
        public MoxyJsonConfig getContext(final Class<?> type) {
            final MoxyJsonConfig configuration = new MoxyJsonConfig();
            configuration.setIncludeRoot(true);
            return configuration;
        }
    }

    @Test
    public void testJAXBElementListJSONRepresentation() {
        _testListOrArray(true, APPLICATION_JSON_TYPE);
    }

    @Path("JAXBElementArrayResource")
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public static class JAXBElementArrayResource extends AbstractTypeTester.AResource<JAXBElement<String>[]> {}

    @Test
    public void testJAXBElementArrayJSONRepresentation() {
        _testListOrArray(false, APPLICATION_JSON_TYPE);
    }

    @Path("JAXBElementBeanJSONResource")
    @Consumes("application/json")
    @Produces("application/json")
    public static class JAXBElementBeanJSONResource extends AbstractTypeTester.AResource<JAXBElement<String>> {}

    @Test
    public void testJAXBElementBeanJSONRepresentation() {
        final WebTarget target = target("JAXBElementBeanJSONResource");
        final GenericType<JAXBElement<String>> genericType = new GenericType<JAXBElement<String>>() {};
        final GenericEntity<JAXBElement<String>> jaxbElementGenericEntity = new GenericEntity(new JAXBElement(new QName("test"), String.class, "CONTENT"), genericType.getType());
        final Response rib = target.request().post(Entity.entity(jaxbElementGenericEntity, "application/json"));
        // TODO: the following would not be needed if i knew how to workaround JAXBElement<String>.class literal
        final byte[] inBytes = AbstractTypeTester.getRequestEntity();
        final byte[] outBytes = AbstractTypeTester.getEntityAsByteArray(rib);
        Assert.assertEquals(new String(outBytes), inBytes.length, outBytes.length);
        for (int i = 0; i < (inBytes.length); i++) {
            if ((inBytes[i]) != (outBytes[i])) {
                Assert.assertEquals(("Index: " + i), inBytes[i], outBytes[i]);
            }
        }
    }

    @Path("JaxbBeanResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JaxbBeanResourceJSON extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentationJSON() {
        final WebTarget target = target("JaxbBeanResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(Entity.entity(in, "application/json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JaxbBeanResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JaxbBeanResourceJSONMediaType extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentationJSONMediaType() {
        final WebTarget target = target("JaxbBeanResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(Entity.entity(in, "application/foo+json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBElementBeanResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBElementBeanResourceJSON extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentationJSON() {
        final WebTarget target = target("JAXBElementBeanResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(Entity.entity(in, "application/json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBElementBeanResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBElementBeanResourceJSONMediaType extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentationJSONMediaType() {
        final WebTarget target = target("JAXBElementBeanResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(Entity.entity(in, "application/foo+json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBTypeResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBTypeResourceJSON {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Test
    public void testJAXBTypeRepresentationJSON() {
        final WebTarget target = target("JAXBTypeResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(Entity.entity(in, "application/json"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBTypeResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBTypeResourceJSONMediaType {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Test
    public void testJAXBTypeRepresentationJSONMediaType() {
        final WebTarget target = target("JAXBTypeResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(Entity.entity(in, "application/foo+json"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBListResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBListResource {
        @POST
        public List<JaxbBean> post(final List<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("set")
        public Set<JaxbBean> postSet(final Set<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("queue")
        public Queue<JaxbBean> postQueue(final Queue<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("stack")
        public Stack<JaxbBean> postStack(final Stack<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("custom")
        public MyArrayList<JaxbBean> postCustom(final MyArrayList<JaxbBean> l) {
            return l;
        }

        @GET
        public Collection<JaxbBean> get() {
            final ArrayList<JaxbBean> l = new ArrayList<>();
            l.add(new JaxbBean("one"));
            l.add(new JaxbBean("two"));
            l.add(new JaxbBean("three"));
            return l;
        }

        @POST
        @Path("type")
        public List<JaxbBean> postType(final Collection<JaxbBeanType> l) {
            final List<JaxbBean> beans = new ArrayList<>();
            for (final JaxbBeanType t : l) {
                beans.add(new JaxbBean(t.value));
            }
            return beans;
        }
    }

    @Path("JAXBListResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JAXBListResourceMediaType extends JsonMoxyTest.JAXBListResource {}

    @Path("JAXBListResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBListResourceJSON extends JsonMoxyTest.JAXBListResource {}

    @Test
    public void testJAXBListRepresentationJSONCollection() throws Exception {
        final WebTarget target = target("JAXBListResourceJSON");
        final Collection<JaxbBean> a = target.request().get(new GenericType<Collection<JaxbBean>>() {});
        Collection<JaxbBean> b = target.request().post(Entity.entity(new GenericEntity<Collection<JaxbBean>>(a) {}, "application/json"), new GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        b = target.path("type").request().post(Entity.entity(new GenericEntity<Collection<JaxbBean>>(a) {}, "application/json"), new GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
    }

    @Test
    public void testJAXBListRepresentationJSONLinkedList() throws Exception {
        final WebTarget target = target("JAXBListResourceJSON");
        Collection<JaxbBean> a = target.request().get(new GenericType<Collection<JaxbBean>>() {});
        final Collection<JaxbBean> b;
        a = new LinkedList<>(a);
        b = target.path("queue").request().post(Entity.entity(new GenericEntity<Queue<JaxbBean>>(((Queue<JaxbBean>) (a))) {}, "application/json"), new GenericType<Queue<JaxbBean>>() {});
        Assert.assertEquals(a, b);
    }

    @Test
    public void testJAXBListRepresentationJSONSet() throws Exception {
        final WebTarget target = target("JAXBListResourceJSON");
        Collection<JaxbBean> a = target.request().get(new GenericType<Collection<JaxbBean>>() {});
        final Collection<JaxbBean> b;
        a = new HashSet<>(a);
        b = target.path("set").request().post(Entity.entity(new GenericEntity<Set<JaxbBean>>(((Set<JaxbBean>) (a))) {}, "application/json"), new GenericType<Set<JaxbBean>>() {});
        final Comparator<JaxbBean> c = new Comparator<JaxbBean>() {
            @Override
            public int compare(final JaxbBean t, final JaxbBean t1) {
                return t.value.compareTo(t1.value);
            }
        };
        final TreeSet<JaxbBean> t1 = new TreeSet<>(c);
        final TreeSet<JaxbBean> t2 = new TreeSet<>(c);
        t1.addAll(a);
        t2.addAll(b);
        Assert.assertEquals(t1, t2);
    }

    @Test
    public void testJAXBListRepresentationJSONStack() throws Exception {
        final WebTarget target = target("JAXBListResourceJSON");
        final Collection<JaxbBean> a = target.request().get(new GenericType<Collection<JaxbBean>>() {});
        final Collection<JaxbBean> b;
        final Stack<JaxbBean> s = new Stack<>();
        s.addAll(a);
        b = target.path("stack").request().post(Entity.entity(new GenericEntity<Stack<JaxbBean>>(s) {}, "application/json"), new GenericType<Stack<JaxbBean>>() {});
        Assert.assertEquals(s, b);
    }

    @Path("JAXBListResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBListResourceJSONMediaType extends JsonMoxyTest.JAXBListResource {}

    @Test
    public void testJAXBListRepresentationJSONMediaType() throws Exception {
        final WebTarget target = target("JAXBListResourceJSONMediaType");
        final Collection<JaxbBean> a = target.request().get(new GenericType<Collection<JaxbBean>>() {});
        Collection<JaxbBean> b = target.request().post(Entity.entity(new GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+json"), new GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        b = target.path("type").request().post(Entity.entity(new GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+json"), new GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
    }
}

