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
package org.glassfish.jersey.uri;


import UriTemplate.COMPARATOR;
import UriTemplate.EMPTY;
import UriTemplateParser.TEMPLATE_VALUE_PATTERN;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static UriTemplate.EMPTY;


/**
 * Taken from Jersey 1: jersey-tests: com.sun.jersey.impl.uri.UriTemplateTest
 *
 * @author Paul Sandoz
 * @author Gerard Davison (gerard.davison at oracle.com)
 */
public class UriTemplateTest {
    /**
     * Test the URI resolution as defined in RFC 3986,
     * <a href="http://tools.ietf.org/html/rfc3986#section-5.4.1">sect. 5.4.1</a> and
     * and <a href="http://tools.ietf.org/html/rfc3986#section-5.4.2">sect. 5.4.2</a>.
     */
    @Test
    public void testResolveUri() {
        final URI baseUri = URI.create("http://a/b/c/d;p?q");
        // Normal examples (RFC 3986, sect. 5.4.1)
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g:h")), CoreMatchers.equalTo(URI.create("g:h")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g:h")), CoreMatchers.equalTo(URI.create("g:h")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g")), CoreMatchers.equalTo(URI.create("http://a/b/c/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("./g")), CoreMatchers.equalTo(URI.create("http://a/b/c/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g/")), CoreMatchers.equalTo(URI.create("http://a/b/c/g/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("/g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("//g")), CoreMatchers.equalTo(URI.create("http://g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("?y")), CoreMatchers.equalTo(URI.create("http://a/b/c/d;p?y")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g?y")), CoreMatchers.equalTo(URI.create("http://a/b/c/g?y")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("#s")), CoreMatchers.equalTo(URI.create("http://a/b/c/d;p?q#s")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g#s")), CoreMatchers.equalTo(URI.create("http://a/b/c/g#s")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g?y#s")), CoreMatchers.equalTo(URI.create("http://a/b/c/g?y#s")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create(";x")), CoreMatchers.equalTo(URI.create("http://a/b/c/;x")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g;x")), CoreMatchers.equalTo(URI.create("http://a/b/c/g;x")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g;x?y#s")), CoreMatchers.equalTo(URI.create("http://a/b/c/g;x?y#s")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("")), CoreMatchers.equalTo(URI.create("http://a/b/c/d;p?q")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create(".")), CoreMatchers.equalTo(URI.create("http://a/b/c/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("./")), CoreMatchers.equalTo(URI.create("http://a/b/c/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("..")), CoreMatchers.equalTo(URI.create("http://a/b/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../")), CoreMatchers.equalTo(URI.create("http://a/b/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../g")), CoreMatchers.equalTo(URI.create("http://a/b/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../..")), CoreMatchers.equalTo(URI.create("http://a/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../../")), CoreMatchers.equalTo(URI.create("http://a/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../../g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        // Abnormal examples (RFC 3986, sect. 5.4.2)
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../../../g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("../../../../g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("/./g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("/../g")), CoreMatchers.equalTo(URI.create("http://a/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g.")), CoreMatchers.equalTo(URI.create("http://a/b/c/g.")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create(".g")), CoreMatchers.equalTo(URI.create("http://a/b/c/.g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g..")), CoreMatchers.equalTo(URI.create("http://a/b/c/g..")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("..g")), CoreMatchers.equalTo(URI.create("http://a/b/c/..g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("./../g")), CoreMatchers.equalTo(URI.create("http://a/b/g")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("./g/.")), CoreMatchers.equalTo(URI.create("http://a/b/c/g/")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g/./h")), CoreMatchers.equalTo(URI.create("http://a/b/c/g/h")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g/../h")), CoreMatchers.equalTo(URI.create("http://a/b/c/h")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g;x=1/./y")), CoreMatchers.equalTo(URI.create("http://a/b/c/g;x=1/y")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g;x=1/../y")), CoreMatchers.equalTo(URI.create("http://a/b/c/y")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g?y/./x")), CoreMatchers.equalTo(URI.create("http://a/b/c/g?y/./x")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g?y/../x")), CoreMatchers.equalTo(URI.create("http://a/b/c/g?y/../x")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g#s/./x")), CoreMatchers.equalTo(URI.create("http://a/b/c/g#s/./x")));
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("g#s/../x")), CoreMatchers.equalTo(URI.create("http://a/b/c/g#s/../x")));
        // Per RFC 3986, test below should resolve to "http:g" for strict parsers and "http://a/b/c/g" for backward compatibility
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("http:g")), CoreMatchers.equalTo(URI.create("http:g")));
        // JDK bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708535
        Assert.assertThat(UriTemplate.resolve(baseUri, URI.create("")), CoreMatchers.equalTo(baseUri));
    }

    @Test
    public void testRelativizeUri() {
        URI baseUri;
        baseUri = URI.create("http://a/b/c/d");
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e")), CoreMatchers.equalTo(URI.create("e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/./e")), CoreMatchers.equalTo(URI.create("e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e/../f")), CoreMatchers.equalTo(URI.create("f")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e/.././f")), CoreMatchers.equalTo(URI.create("f")));
        baseUri = URI.create("http://a/b/c/d?q=v");
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e")), CoreMatchers.equalTo(URI.create("e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/./e")), CoreMatchers.equalTo(URI.create("e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e/../f")), CoreMatchers.equalTo(URI.create("f")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/e/.././f")), CoreMatchers.equalTo(URI.create("f")));
        // NOTE: At the moment, in sync with the JDK implementation of relativize() method,
        // we do not support relativization of URIs that do not fully prefix the base URI.
        // Once (if) we decide to improve this support beyond what JDK supports, we may need
        // to update the assertions below.
        baseUri = URI.create("http://a/b/c/d");
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/./e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/.././e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
        baseUri = URI.create("http://a/b/c/d?q=v");
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/./e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
        Assert.assertThat(UriTemplate.relativize(baseUri, URI.create("http://a/b/c/d/.././e")), CoreMatchers.equalTo(URI.create("http://a/b/c/e")));
    }

    @Test
    public void testTemplateNames() {
        _testTemplateNames("{a}", "a");
        _testTemplateNames("{  a}", "a");
        _testTemplateNames("{  a  }", "a");
        _testTemplateNames("{a:}", "a");
        _testTemplateNames("{a :}", "a");
        _testTemplateNames("{a : }", "a");
        _testTemplateNames("http://example.org/{a}/{b}/", "a", "b");
        _testTemplateNames("http://example.org/page1#{a}", "a");
        _testTemplateNames("{scheme}://{20}.example.org?date={wilma}&option={a}", "scheme", "20", "wilma", "a");
        _testTemplateNames("http://example.org/{a-b}", "a-b");
        _testTemplateNames("http://example.org?{p}", "p");
        _testTemplateNames("http://example.com/order/{c}/{c}/{c}/", "c", "c", "c");
    }

    @Test
    public void testMatching() {
        _testMatching("http://example.org/{a}/{b}/", "http://example.org/fred/barney/", "fred", "barney");
        _testMatching("http://example.org/page1#{a}", "http://example.org/page1#fred", "fred");
        _testMatching("{scheme}://{20}.example.org?date={wilma}&option={a}", "https://this-is-spinal-tap.example.org?date=2008&option=fred", "https", "this-is-spinal-tap", "2008", "fred");
        _testMatching("http://example.org/{a-b}", "http://example.org/none%20of%20the%20above", "none%20of%20the%20above");
        _testMatching("http://example.org?{p}", "http://example.org?quote=to+bo+or+not+to+be", "quote=to+bo+or+not+to+be");
        _testMatching("http://example.com/order/{c}/{c}/{c}/", "http://example.com/order/cheeseburger/cheeseburger/cheeseburger/", "cheeseburger", "cheeseburger", "cheeseburger");
        _testMatching("http://example.com/{q}", "http://example.com/hullo#world", "hullo#world");
        _testMatching("http://example.com/{e}/", "http://example.com/xxx/", "xxx");
    }

    @Test
    public void testTemplateRegexes() {
        _testTemplateRegex("{a:}", (("(" + (TEMPLATE_VALUE_PATTERN.pattern())) + ")"));
        _testTemplateRegex("{a:.*}", "(.*)");
        _testTemplateRegex("{a:  .*}", "(.*)");
        _testTemplateRegex("{a:  .*  }", "(.*)");
        _testTemplateRegex("{a :  .*  }", "(.*)");
    }

    @Test
    public void testRegexMatching() {
        _testMatching("{b: .+}", "1", "1");
        _testMatching("{b: .+}", "1/2/3", "1/2/3");
        _testMatching("http://example.org/{a}/{b: .+}", "http://example.org/fred/barney/x/y/z", "fred", "barney/x/y/z");
        _testMatching("{b: \\d+}", "1234567890", "1234567890");
        _testMatching("{a}/{b: .+}/{c}{d: (/.*)?}", "1/2/3/4", "1", "2/3", "4", "");
        _testMatching("{a}/{b: .+}/{c}{d: (/.*)?}", "1/2/3/4/", "1", "2/3", "4", "/");
    }

    @Test
    public void testRegexMatchingWithNestedGroups() {
        _testMatching("{b: (\\d+)}", "1234567890", "1234567890");
        _testMatching("{b: (\\d+)-(\\d+)-(\\d+)}", "12-34-56", "12-34-56");
        _testMatching("{a: (\\d)(\\d*)}-{b: (\\d)(\\d*)}-{c: (\\d)(\\d*)}", "12-34-56", "12", "34", "56");
    }

    @Test
    public void testNullMatching() {
        final Map<String, String> m = new HashMap<String, String>();
        UriTemplate t = EMPTY;
        Assert.assertEquals(false, t.match("/", m));
        Assert.assertEquals(true, t.match(null, m));
        Assert.assertEquals(true, t.match("", m));
        t = new UriTemplate("/{v}");
        Assert.assertEquals(false, t.match(null, m));
        Assert.assertEquals(true, t.match("/one", m));
    }

    @Test
    public void testOrder() {
        final List<UriTemplate> l = new ArrayList<UriTemplate>();
        l.add(EMPTY);
        l.add(new UriTemplate("/{a}"));
        l.add(new UriTemplate("/{a}/{b}"));
        l.add(new UriTemplate("/{a}/one/{b}"));
        Collections.sort(l, COMPARATOR);
        Assert.assertEquals(new UriTemplate("/{a}/one/{b}").getTemplate(), l.get(0).getTemplate());
        Assert.assertEquals(new UriTemplate("/{a}/{b}").getTemplate(), l.get(1).getTemplate());
        Assert.assertEquals(new UriTemplate("/{a}").getTemplate(), l.get(2).getTemplate());
        Assert.assertEquals(EMPTY.getTemplate(), l.get(3).getTemplate());
    }

    @Test
    public void testOrderDuplicitParams() {
        final List<UriTemplate> l = new ArrayList<UriTemplate>();
        l.add(new UriTemplate("/{a}"));
        l.add(new UriTemplate("/{a}/{a}"));
        Collections.sort(l, COMPARATOR);
        Assert.assertEquals(new UriTemplate("/{a}/{a}").getTemplate(), l.get(0).getTemplate());
        Assert.assertEquals(new UriTemplate("/{a}").getTemplate(), l.get(1).getTemplate());
    }

    @Test
    public void testSubstitutionArray() {
        _testSubstitutionArray("http://example.org/{a}/{b}/", "http://example.org/fred/barney/", "fred", "barney");
        _testSubstitutionArray("http://example.org/page1#{a}", "http://example.org/page1#fred", "fred");
        _testSubstitutionArray("{scheme}://{20}.example.org?date={wilma}&option={a}", "https://this-is-spinal-tap.example.org?date=&option=fred", "https", "this-is-spinal-tap", "", "fred");
        _testSubstitutionArray("http://example.org/{a-b}", "http://example.org/none%20of%20the%20above", "none%20of%20the%20above");
        _testSubstitutionArray("http://example.org?{p}", "http://example.org?quote=to+bo+or+not+to+be", "quote=to+bo+or+not+to+be");
        _testSubstitutionArray("http://example.com/order/{c}/{c}/{c}/", "http://example.com/order/cheeseburger/cheeseburger/cheeseburger/", "cheeseburger");
        _testSubstitutionArray("http://example.com/{q}", "http://example.com/hullo#world", "hullo#world");
        _testSubstitutionArray("http://example.com/{e}/", "http://example.com//", "");
        _testSubstitutionArray("http://example.com/{a}/{b}/{a}", "http://example.com/fred/barney/fred", "fred", "barney", "joe");
    }

    @Test
    public void testGroupIndexes() throws Exception {
        UriTemplate template = new UriTemplate("/a");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[0]));
        template = new UriTemplate("/{a}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1 }));
        template = new UriTemplate("/{a}/b");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1 }));
        template = new UriTemplate("/{a}/{b}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1, 2 }));
        template = new UriTemplate("/{a}/{b}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1, 2 }));
        template = new UriTemplate("/{a}/b/{c}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1, 2 }));
        template = new UriTemplate("/{a: (abc)+}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1 }));
        template = new UriTemplate("/{a: (abc)+}/b");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1 }));
        template = new UriTemplate("/{a: (abc)+}/{b}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1, 3 }));
        template = new UriTemplate("/{a: (abc)+}/b/{c}");
        Assert.assertThat(template.getPattern().getGroupIndexes(), CoreMatchers.equalTo(new int[]{ 1, 3 }));
    }

    @Test
    public void testSubstitutionMap() {
        _testSubstitutionMap("http://example.org/{a}/{b}/", "http://example.org/fred/barney/", "a", "fred", "b", "barney");
        _testSubstitutionMap("http://example.org/page1#{a}", "http://example.org/page1#fred", "a", "fred");
        _testSubstitutionMap("{scheme}://{20}.example.org?date={wilma}&option={a}", "https://this-is-spinal-tap.example.org?date=&option=fred", "scheme", "https", "20", "this-is-spinal-tap", "wilma", "", "a", "fred");
        _testSubstitutionMap("http://example.org/{a-b}", "http://example.org/none%20of%20the%20above", "a-b", "none%20of%20the%20above");
        _testSubstitutionMap("http://example.org?{p}", "http://example.org?quote=to+bo+or+not+to+be", "p", "quote=to+bo+or+not+to+be");
        _testSubstitutionMap("http://example.com/order/{c}/{c}/{c}/", "http://example.com/order/cheeseburger/cheeseburger/cheeseburger/", "c", "cheeseburger");
        _testSubstitutionMap("http://example.com/{q}", "http://example.com/hullo#world", "q", "hullo#world");
        _testSubstitutionMap("http://example.com/{e}/", "http://example.com//", "e", "");
    }

    @Test
    public void testNormalizesURIs() throws Exception {
        this.validateNormalize("/some-path", "/some-path");
        this.validateNormalize("http://example.com/some/../path", "http://example.com/path");
        // note, that following behaviour differs from Jersey-1.x UriHelper.normalize(), the '..' segment is simply left out in
        // this case, where older UriHelper.normalize() would return the path including the '..' segment. It is also mentioned
        // in the UriTemplate.normalize() javadoc.
        this.validateNormalize("http://example.com/../path", "http://example.com/path");
        this.validateNormalize("http://example.com//path", "http://example.com//path");
    }

    @Test
    public void testSingleQueryParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{?query}");
        final Map<String, String> result = new HashMap<String, String>();
        tmpl.match("/test?query=x", result);
        Assert.assertEquals("incorrect size for match string", 1, result.size());
        Assert.assertEquals("query parameter is not matched", "x", result.get("query"));
    }

    @Test
    public void testDoubleQueryParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{?query,secondQuery}");
        final List<String> list = new ArrayList<String>();
        tmpl.match("/test?query=x&secondQuery=y", list);
        final Map<String, String> result = new HashMap<String, String>();
        tmpl.match("/test?query=x&secondQuery=y", result);
        Assert.assertEquals("incorrect size for match string", 2, result.size());
        Assert.assertEquals("query parameter is not matched", "x", result.get("query"));
        Assert.assertEquals("query parameter is not matched", "y", result.get("secondQuery"));
    }

    @Test
    public void testSettingQueryParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{?query}");
        final Map<String, String> values = new HashMap<String, String>();
        values.put("query", "example");
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is not set", "/test?query=example", uri);
    }

    @Test
    public void testSettingTwoQueryParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{?query,other}");
        final Map<String, String> values = new HashMap<String, String>();
        values.put("query", "example");
        values.put("other", "otherExample");
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is not set", "/test?query=example&other=otherExample", uri);
    }

    @Test
    public void testNotSettingQueryParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{?query}");
        final Map<String, String> values = new HashMap<String, String>();
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is set", "/test", uri);
    }

    @Test
    public void testSettingMatrixParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{;matrix}/other");
        final Map<String, String> values = new HashMap<String, String>();
        values.put("matrix", "example");
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is not set", "/test;matrix=example/other", uri);
    }

    @Test
    public void testSettingTwoMatrixParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{;matrix,other}/other");
        final Map<String, String> values = new HashMap<String, String>();
        values.put("matrix", "example");
        values.put("other", "otherExample");
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is not set", "/test;matrix=example;other=otherExample/other", uri);
    }

    @Test
    public void testSettingTwoSeperatedMatrixParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{;matrix}/other{;other}");
        final Map<String, String> values = new HashMap<String, String>();
        values.put("matrix", "example");
        values.put("other", "otherExample");
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is not set", "/test;matrix=example/other;other=otherExample", uri);
    }

    @Test
    public void testNotSettingMatrixParameter() throws Exception {
        final UriTemplate tmpl = new UriTemplate("/test{;query}/other");
        final Map<String, String> values = new HashMap<String, String>();
        final String uri = tmpl.createURI(values);
        Assert.assertEquals("query string is set", "/test/other", uri);
    }

    /* RFC 6570, section 3.2:

    count := ("one", "two", "three")
    dom   := ("example", "com")
    dub   := "me/too"
    hello := "Hello World!"
    half  := "50%"
    var   := "value"
    who   := "fred"
    base  := "http://example.com/home/"
    path  := "/foo/bar"
    list  := ("red", "green", "blue")
    keys  := [("semi",";"),("dot","."),("comma",",")]
    v     := "6"
    x     := "1024"
    y     := "768"
    empty := ""
    empty_keys  := []
    undef := null
     */
    private static final List<String> count = Arrays.asList("one", "two", "three");

    private static final List<String> dom = Arrays.asList("example", "com");

    private static final String dub = "me/too";

    private static final String hello = "Hello World!";

    private static final String half = "50%";

    private static final String var = "value";

    private static final String who = "fred";

    private static final String base = "http://example.com/home/";

    private static final String path = "/foo/bar";

    private static final List<String> list = Arrays.asList("red", "green", "blue");

    private static final Map<String, String> keys = new HashMap<String, String>() {
        {
            put("semi", ";");
            put("dot", ".");
            put("comma", ",");
        }
    };

    private static final String v = "6";

    private static final String x = "1024";

    private static final String y = "768";

    private static final String empty = "";

    private static final Map<String, String> emptyKeys = Collections.emptyMap();

    @Test
    public void testRfc6570QueryTemplateExamples() {
        /* RFC 6570, section 3.2.8:

        {?who}             ?who=fred
        {?half}            ?half=50%25
        {?x,y}             ?x=1024&y=768
        {?x,y,empty}       ?x=1024&y=768&empty=
        {?x,y,undef}       ?x=1024&y=768
        {?var:3}           ?var=val
        {?list}            ?list=red,green,blue
        {?list*}           ?list=red&list=green&list=blue
        {?keys}            ?keys=semi,%3B,dot,.,comma,%2C
        {?keys*}           ?semi=%3B&dot=.&comma=%2C
         */
        assertEncodedQueryTemplateExpansion("?who=fred", "{?who}", UriTemplateTest.who);
        assertEncodedQueryTemplateExpansion("?half=50%25", "{?half}", UriTemplateTest.half);
        assertEncodedQueryTemplateExpansion("?x=1024&y=768", "{?x,y}", UriTemplateTest.x, UriTemplateTest.y);
        assertEncodedQueryTemplateExpansion("?x=1024&y=768&empty=", "{?x,y,empty}", UriTemplateTest.x, UriTemplateTest.y, UriTemplateTest.empty);
        assertEncodedQueryTemplateExpansion("?x=1024&y=768", "{?x,y,undef}", UriTemplateTest.x, UriTemplateTest.y);
        // TODO assertEncodedQueryTemplateExpansion("?var=val", "{?var:3}", var);
        // TODO assertEncodedQueryTemplateExpansion("?list=red,green,blue", "{?list}", list);
        // TODO assertEncodedQueryTemplateExpansion("?list=red&list=green&list=blue", "{?list*}", list);
        // TODO assertEncodedQueryTemplateExpansion("?keys=semi,%3B,dot,.,comma,%2C", "{?keys}", keys);
        // TODO assertEncodedQueryTemplateExpansion("?semi=%3B&dot=.&comma=%2C", "{?keys*}", keys);
    }

    @Test
    public void testRfc6570MatrixTemplateExamples() {
        /* RFC 6570, section 3.2.7:

        {;who}             ;who=fred
        {;half}            ;half=50%25
        {;empty}           ;empty
        {;v,empty,who}     ;v=6;empty;who=fred
        {;v,bar,who}       ;v=6;who=fred
        {;x,y}             ;x=1024;y=768
        {;x,y,empty}       ;x=1024;y=768;empty
        {;x,y,undef}       ;x=1024;y=768
        {;hello:5}         ;hello=Hello
        {;list}            ;list=red,green,blue
        {;list*}           ;list=red;list=green;list=blue
        {;keys}            ;keys=semi,%3B,dot,.,comma,%2C
        {;keys*}           ;semi=%3B;dot=.;comma=%2C
         */
        assertEncodedPathTemplateExpansion(";who=fred", "{;who}", UriTemplateTest.who);
        assertEncodedPathTemplateExpansion(";half=50%25", "{;half}", UriTemplateTest.half);
        assertEncodedPathTemplateExpansion(";empty", "{;empty}", UriTemplateTest.empty);
        assertEncodedPathTemplateExpansion(";v=6;empty;who=fred", "{;v,empty,who}", UriTemplateTest.v, UriTemplateTest.empty, UriTemplateTest.who);
        assertEncodedPathTemplateExpansion(";v=6;who=fred", "{;v,bar,who}", new HashMap<String, String>() {
            {
                put("v", UriTemplateTest.v);
                put("who", UriTemplateTest.who);
            }
        });
        assertEncodedPathTemplateExpansion(";x=1024;y=768", "{;x,y}", UriTemplateTest.x, UriTemplateTest.y);
        assertEncodedPathTemplateExpansion(";x=1024;y=768;empty", "{;x,y,empty}", UriTemplateTest.x, UriTemplateTest.y, UriTemplateTest.empty);
        assertEncodedPathTemplateExpansion(";x=1024;y=768", "{;x,y,undef}", UriTemplateTest.x, UriTemplateTest.y);
        // TODO assertEncodedPathTemplateExpansion(";hello=Hello", "{;hello:5}", hello);
        // TODO assertEncodedPathTemplateExpansion(";list=red,green,blue", "{;list}", list);
        // TODO assertEncodedPathTemplateExpansion(";list=red;list=green;list=blue", "{;list*}", list);
        // TODO assertEncodedPathTemplateExpansion(";keys=semi,%3B,dot,.,comma,%2C", "{;keys}", keys);
        // TODO assertEncodedPathTemplateExpansion(";semi=%3B;dot=.;comma=%2C", "{;keys*}", keys);
    }
}

