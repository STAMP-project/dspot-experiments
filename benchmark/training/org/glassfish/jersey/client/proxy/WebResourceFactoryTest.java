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
package org.glassfish.jersey.client.proxy;


import MediaType.APPLICATION_XML;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Matula
 */
public class WebResourceFactoryTest extends JerseyTest {
    private MyResourceIfc resource;

    private MyResourceIfc resource2;

    private MyResourceIfc resourceWithXML;

    @Test
    public void testGetIt() {
        Assert.assertEquals("Got it!", resource.getIt());
    }

    @Test
    public void testPostIt() {
        final MyBean bean = new MyBean();
        bean.name = "Ahoj";
        Assert.assertEquals("Ahoj", resource.postIt(Collections.singletonList(bean)).get(0).name);
    }

    @Test
    public void testPostValid() {
        final MyBean bean = new MyBean();
        bean.name = "Ahoj";
        Assert.assertEquals("Ahoj", resource.postValid(bean).name);
    }

    @Test
    public void testPathParam() {
        Assert.assertEquals("jouda", resource.getId("jouda"));
    }

    @Test
    public void testQueryParam() {
        Assert.assertEquals("jiri", resource.getByName("jiri"));
    }

    @Test
    public void testFormParam() {
        Assert.assertEquals("jiri", resource.postByNameFormParam("jiri"));
    }

    @Test
    public void testCookieParam() {
        Assert.assertEquals("jiri", resource.getByNameCookie("jiri"));
    }

    @Test
    public void testHeaderParam() {
        Assert.assertEquals("jiri", resource.getByNameHeader("jiri"));
    }

    @Test
    public void testMatrixParam() {
        Assert.assertEquals("jiri", resource.getByNameMatrix("jiri"));
    }

    @Test
    public void testSubResource() {
        Assert.assertEquals("Got it!", resource.getSubResource().getMyBean().name);
    }

    @Test
    public void testQueryParamsAsList() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("bb");
        list.add("ccc");
        Assert.assertEquals("3:[a, bb, ccc]", resource.getByNameList(list));
    }

    @Test
    public void testQueryParamsAsSet() {
        final Set<String> set = new HashSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.getByNameSet(set);
        checkSet(result);
    }

    @Test
    public void testQueryParamsAsSortedSet() {
        final SortedSet<String> set = new TreeSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.getByNameSortedSet(set);
        Assert.assertEquals("3:[a, bb, ccc]", result);
    }

    @Test
    public void testMatrixParamsAsList() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("bb");
        list.add("ccc");
        Assert.assertEquals("3:[a, bb, ccc]", resource.getByNameMatrixList(list));
    }

    @Test
    public void testMatrixParamsAsSet() {
        final Set<String> set = new HashSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.getByNameMatrixSet(set);
        checkSet(result);
    }

    @Test
    public void testMatrixParamsAsSortedSet() {
        final SortedSet<String> set = new TreeSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.getByNameMatrixSortedSet(set);
        Assert.assertEquals("3:[a, bb, ccc]", result);
    }

    @Test
    public void testFormParamsAsList() {
        final List<String> list = new ArrayList<>();
        list.add("a");
        list.add("bb");
        list.add("ccc");
        Assert.assertEquals("3:[a, bb, ccc]", resource.postByNameFormList(list));
    }

    @Test
    public void testFormParamsAsSet() {
        final Set<String> set = new HashSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.postByNameFormSet(set);
        checkSet(result);
    }

    @Test
    public void testFormParamsAsSortedSet() {
        final SortedSet<String> set = new TreeSet<>();
        set.add("a");
        set.add("bb");
        set.add("ccc");
        final String result = resource.postByNameFormSortedSet(set);
        Assert.assertEquals("3:[a, bb, ccc]", result);
    }

    @Test
    public void testAcceptHeader() {
        Assert.assertTrue("Accept HTTP header does not match @Produces annotation", resource.isAcceptHeaderValid(null));
    }

    @Test
    public void testPutWithExplicitContentType() {
        Assert.assertEquals("Content-Type HTTP header does not match explicitly provided type", resourceWithXML.putIt(new MyBean()), APPLICATION_XML);
    }

    @Test
    public void testToString() throws Exception {
        final String actual = resource.toString();
        final String expected = target().path("myresource").toString();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testHashCode() throws Exception {
        int h1 = resource.hashCode();
        int h2 = resource2.hashCode();
        Assert.assertNotEquals("The hash codes should not match", h1, h2);
    }

    @Test
    public void testEquals() {
        Assert.assertFalse("The two resource instances should not be considered equals as they are unique", resource.equals(resource2));
    }
}

