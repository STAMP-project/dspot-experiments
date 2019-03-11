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
package org.glassfish.jersey.uri;


import UriComponent.Type.HOST;
import UriComponent.Type.MATRIX_PARAM;
import UriComponent.Type.PATH;
import UriComponent.Type.PATH_SEGMENT;
import UriComponent.Type.QUERY;
import UriComponent.Type.QUERY_PARAM;
import UriComponent.Type.QUERY_PARAM_SPACE_ENCODED;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link UriComponent} class.
 * <p/>
 * Migrated from Jersey 1.x E2E tests:
 * <ul>
 * <li>{@code com.sun.jersey.impl.uri.riComponentDecodeTest}</li>
 * <li>{@code com.sun.jersey.impl.uri.riComponentEncodeTest}</li>
 * <li>{@code com.sun.jersey.impl.uri.riComponentValidateTest}</li>
 * </ul>
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class UriComponentTest {
    @Test
    public void testNull() {
        decodeCatch(null);
    }

    @Test
    public void testEmpty() {
        Assert.assertEquals("", UriComponent.decode("", null));
    }

    @Test
    public void testNoPercentEscapedOctets() {
        Assert.assertEquals("xyz", UriComponent.decode("xyz", null));
    }

    @Test
    public void testZeroValuePercentEscapedOctet() {
        Assert.assertEquals("\u0000", UriComponent.decode("%00", null));
    }

    @Test
    public void testASCIIPercentEscapedOctets() {
        Assert.assertEquals(" ", UriComponent.decode("%20", null));
        Assert.assertEquals("   ", UriComponent.decode("%20%20%20", null));
        Assert.assertEquals("a b c ", UriComponent.decode("a%20b%20c%20", null));
        Assert.assertEquals("a  b  c  ", UriComponent.decode("a%20%20b%20%20c%20%20", null));
        Assert.assertEquals("0123456789", UriComponent.decode("%30%31%32%33%34%35%36%37%38%39", null));
        Assert.assertEquals("00112233445566778899", UriComponent.decode("%300%311%322%333%344%355%366%377%388%399", null));
    }

    @Test
    public void testPercentUnicodeEscapedOctets() {
        Assert.assertEquals("copyright\u00a9", UriComponent.decode("copyright%c2%a9", null));
        Assert.assertEquals("copyright\u00a9", UriComponent.decode("copyright%C2%A9", null));
        Assert.assertEquals("thumbsup\ud83d\udc4d", UriComponent.decode("thumbsup%f0%9f%91%8d", null));
        Assert.assertEquals("thumbsup\ud83d\udc4d", UriComponent.decode("thumbsup%F0%9F%91%8D", null));
    }

    @Test
    public void testHost() {
        Assert.assertEquals("[fec0::abcd%251]", UriComponent.decode("[fec0::abcd%251]", HOST));
    }

    @Test
    public void testInvalidPercentEscapedOctets() {
        Assert.assertTrue(decodeCatch("%"));
        Assert.assertTrue(decodeCatch("%1"));
        Assert.assertTrue(decodeCatch(" %1"));
        Assert.assertTrue(decodeCatch("%z1"));
        Assert.assertTrue(decodeCatch("%1z"));
    }

    @Test
    public void testDecodePathEmptySlash() {
        _testDecodePath("", "");
        _testDecodePath("/", "", "");
        _testDecodePath("//", "", "", "");
        _testDecodePath("///", "", "", "", "");
    }

    @Test
    public void testDecodePath() {
        _testDecodePath("a", "a");
        _testDecodePath("/a", "", "a");
        _testDecodePath("/a/", "", "a", "");
        _testDecodePath("/a//", "", "a", "", "");
        _testDecodePath("/a///", "", "a", "", "", "");
        _testDecodePath("a/b/c", "a", "b", "c");
        _testDecodePath("a//b//c//", "a", "", "b", "", "c", "", "");
        _testDecodePath("//a//b//c//", "", "", "a", "", "b", "", "c", "", "");
        _testDecodePath("///a///b///c///", "", "", "", "a", "", "", "b", "", "", "c", "", "", "");
    }

    @Test
    public void testDecodeUnicodePath() {
        _testDecodePath("/%F0%9F%91%8D/thumbsup", "", "\ud83d\udc4d", "thumbsup");
    }

    @Test
    public void testDecodeQuery() {
        _testDecodeQuery("");
        _testDecodeQuery("&");
        _testDecodeQuery("=");
        _testDecodeQuery("&=junk");
        _testDecodeQuery("&&");
        _testDecodeQuery("a", "a", "");
        _testDecodeQuery("a&", "a", "");
        _testDecodeQuery("&a&", "a", "");
        _testDecodeQuery("a&&", "a", "");
        _testDecodeQuery("a=", "a", "");
        _testDecodeQuery("a=&", "a", "");
        _testDecodeQuery("a=&&", "a", "");
        _testDecodeQuery("a=x", "a", "x");
        _testDecodeQuery("a==x", "a", "=x");
        _testDecodeQuery("a=x&", "a", "x");
        _testDecodeQuery("a=x&&", "a", "x");
        _testDecodeQuery("a=x&b=y", "a", "x", "b", "y");
        _testDecodeQuery("a=x&&b=y", "a", "x", "b", "y");
        _testDecodeQuery("+a+", true, " a ", "");
        _testDecodeQuery("+a+=", true, " a ", "");
        _testDecodeQuery("+a+=+x+", true, " a ", " x ");
        _testDecodeQuery("%20a%20=%20x%20", true, " a ", " x ");
        _testDecodeQuery("+a+", false, " a ", "");
        _testDecodeQuery("+a+=", false, " a ", "");
        _testDecodeQuery("+a+=+x+", false, " a ", "+x+");
        _testDecodeQuery("%20a%20=%20x%20", false, " a ", "%20x%20");
        _testDecodeQuery("+a+", false, true, "+a+", "");
        _testDecodeQuery("+a+=", false, true, "+a+", "");
        _testDecodeQuery("+a+=+x+", false, true, "+a+", " x ");
        _testDecodeQuery("%20a%20=%20x%20", false, true, "%20a%20", " x ");
        _testDecodeQuery("+a+", false, false, "+a+", "");
        _testDecodeQuery("+a+=", false, false, "+a+", "");
        _testDecodeQuery("+a+=+x+", false, false, "+a+", "+x+");
        _testDecodeQuery("%20a%20=%20x%20", false, false, "%20a%20", "%20x%20");
    }

    @Test
    public void testDecodeUnicodeQuery() {
        _testDecodeQuery("+thumbsup%F0%9F%91%8D+=+chicken%F0%9F%90%94+", true, " thumbsup\ud83d\udc4d ", " chicken\ud83d\udc14 ");
        _testDecodeQuery("%20thumbsup%F0%9F%91%8D%20=%20chicken%F0%9F%90%94%20", true, " thumbsup\ud83d\udc4d ", " chicken\ud83d\udc14 ");
        _testDecodeQuery("+thumbsup%F0%9F%91%8D+=+chicken%F0%9F%90%94+", false, " thumbsup\ud83d\udc4d ", "+chicken%F0%9F%90%94+");
        _testDecodeQuery("%20thumbsup%F0%9F%91%8D%20=%20chicken%F0%9F%90%94%20", false, " thumbsup\ud83d\udc4d ", "%20chicken%F0%9F%90%94%20");
    }

    @Test
    public void testDecodeQueryParam() {
        Assert.assertEquals(" ", UriComponent.decode("+", QUERY_PARAM));
        Assert.assertEquals("a b c ", UriComponent.decode("a+b+c+", QUERY_PARAM));
        Assert.assertEquals(" ", UriComponent.decode("%20", QUERY_PARAM));
        Assert.assertEquals("a b c ", UriComponent.decode("a%20b%20c%20", QUERY_PARAM));
    }

    @Test
    public void testDecodeUnicodeQueryParam() {
        Assert.assertEquals("thumbsup \ud83d\udc4d chicken \ud83d\udc14", UriComponent.decode("thumbsup+%F0%9F%91%8D+chicken+%F0%9F%90%94", QUERY_PARAM));
    }

    @Test
    public void testDecodeQueryParamSpaceEncoded() {
        Assert.assertEquals("+", UriComponent.decode("+", QUERY_PARAM_SPACE_ENCODED));
        Assert.assertEquals("a+b+c+", UriComponent.decode("a+b+c+", QUERY_PARAM_SPACE_ENCODED));
        Assert.assertEquals(" ", UriComponent.decode("%20", QUERY_PARAM_SPACE_ENCODED));
        Assert.assertEquals("a b c ", UriComponent.decode("a%20b%20c%20", QUERY_PARAM_SPACE_ENCODED));
    }

    @Test
    public void testDecodeMatrix() {
        _testDecodeMatrix("path", "path");
        _testDecodeMatrix("path;", "path");
        _testDecodeMatrix("path;=", "path");
        _testDecodeMatrix("path;=junk", "path");
        _testDecodeMatrix("path;;", "path");
        _testDecodeMatrix("path;a", "path", "a", "");
        _testDecodeMatrix("path;;a", "path", "a", "");
        _testDecodeMatrix("path;a;", "path", "a", "");
        _testDecodeMatrix("path;a;;", "path", "a", "");
        _testDecodeMatrix("path;a=", "path", "a", "");
        _testDecodeMatrix("path;a=;", "path", "a", "");
        _testDecodeMatrix("path;a=;;", "path", "a", "");
        _testDecodeMatrix("path;a=x", "path", "a", "x");
        _testDecodeMatrix("path;a=x;", "path", "a", "x");
        _testDecodeMatrix("path;a=x;;", "path", "a", "x");
        _testDecodeMatrix("path;a=x;b=y", "path", "a", "x", "b", "y");
        _testDecodeMatrix("path;a=x;;b=y", "path", "a", "x", "b", "y");
        _testDecodeMatrix("path;a==x;", "path", "a", "=x");
        _testDecodeMatrix("", "");
        _testDecodeMatrix(";", "");
        _testDecodeMatrix(";=", "");
        _testDecodeMatrix(";=junk", "");
        _testDecodeMatrix(";;", "");
        _testDecodeMatrix(";a", "", "a", "");
        _testDecodeMatrix(";;a", "", "a", "");
        _testDecodeMatrix(";a;", "", "a", "");
        _testDecodeMatrix(";a;;", "", "a", "");
        _testDecodeMatrix(";a=", "", "a", "");
        _testDecodeMatrix(";a=;", "", "a", "");
        _testDecodeMatrix(";a=;;", "", "a", "");
        _testDecodeMatrix(";a=x", "", "a", "x");
        _testDecodeMatrix(";a==x", "", "a", "=x");
        _testDecodeMatrix(";a=x;", "", "a", "x");
        _testDecodeMatrix(";a=x;;", "", "a", "x");
        _testDecodeMatrix(";a=x;b=y", "", "a", "x", "b", "y");
        _testDecodeMatrix(";a=x;;b=y", "", "a", "x", "b", "y");
        _testDecodeMatrix(";%20a%20=%20x%20", "", true, " a ", " x ");
        _testDecodeMatrix(";%20a%20=%20x%20", "", false, " a ", "%20x%20");
    }

    @Test
    public void testDecodeUnicodeMatrix() {
        _testDecodeMatrix(";thumbsup%F0%9F%91%8D=chicken%F0%9F%90%94", "", true, "thumbsup\ud83d\udc4d", "chicken\ud83d\udc14");
        _testDecodeMatrix(";thumbsup%F0%9F%91%8D=chicken%F0%9F%90%94", "", false, "thumbsup\ud83d\udc4d", "chicken%F0%9F%90%94");
    }

    @Test
    public void testEncodePathSegment() {
        Assert.assertEquals("%2Fa%2Fb%2Fc", UriComponent.encode("/a/b/c", PATH_SEGMENT));
        Assert.assertEquals("%2Fa%20%2Fb%20%2Fc%20", UriComponent.encode("/a /b /c ", PATH_SEGMENT));
        Assert.assertEquals("%2Fcopyright%C2%A9", UriComponent.encode("/copyright\u00a9", PATH_SEGMENT));
        Assert.assertEquals("%2Fa%3Bx%2Fb%3Bx%2Fc%3Bx", UriComponent.encode("/a;x/b;x/c;x", PATH_SEGMENT));
        Assert.assertEquals("%2Fthumbsup%F0%9F%91%8D", UriComponent.encode("/thumbsup\ud83d\udc4d", PATH_SEGMENT));
    }

    @Test
    public void testEncodePath() {
        Assert.assertEquals("/a/b/c", UriComponent.encode("/a/b/c", PATH));
        Assert.assertEquals("/a%20/b%20/c%20", UriComponent.encode("/a /b /c ", PATH));
        Assert.assertEquals("/copyright%C2%A9", UriComponent.encode("/copyright\u00a9", PATH));
        Assert.assertEquals("/a;x/b;x/c;x", UriComponent.encode("/a;x/b;x/c;x", PATH));
        Assert.assertEquals("/thumbsup%F0%9F%91%8D", UriComponent.encode("/thumbsup\ud83d\udc4d", PATH));
    }

    @Test
    public void testContextualEncodePath() {
        Assert.assertEquals("/a/b/c", UriComponent.contextualEncode("/a/b/c", PATH));
        Assert.assertEquals("/a%20/b%20/c%20", UriComponent.contextualEncode("/a /b /c ", PATH));
        Assert.assertEquals("/copyright%C2%A9", UriComponent.contextualEncode("/copyright\u00a9", PATH));
        Assert.assertEquals("/thumbsup%F0%9F%91%8D", UriComponent.contextualEncode("/thumbsup\ud83d\udc4d", PATH));
        Assert.assertEquals("/a%20/b%20/c%20", UriComponent.contextualEncode("/a%20/b%20/c%20", PATH));
        Assert.assertEquals("/copyright%C2%A9", UriComponent.contextualEncode("/copyright%C2%A9", PATH));
        Assert.assertEquals("/thumbsup%F0%9F%91%8D", UriComponent.contextualEncode("/thumbsup%F0%9F%91%8D", PATH));
    }

    @Test
    public void testEncodeQuery() {
        Assert.assertEquals("a%20b%20c.-%2A_=+&%25xx%2520", UriComponent.encode("a b c.-*_=+&%xx%20", QUERY));
        Assert.assertEquals("a+b+c.-%2A_%3D%2B%26%25xx%2520", UriComponent.encode("a b c.-*_=+&%xx%20", QUERY_PARAM));
        Assert.assertEquals("a%20b%20c.-%2A_%3D%2B%26%25xx%2520", UriComponent.encode("a b c.-*_=+&%xx%20", QUERY_PARAM_SPACE_ENCODED));
        Assert.assertEquals("thumbsup%F0%9F%91%8D", UriComponent.encode("thumbsup\ud83d\udc4d", QUERY));
        Assert.assertEquals("thumbsup%F0%9F%91%8D", UriComponent.encode("thumbsup\ud83d\udc4d", QUERY_PARAM));
    }

    @Test
    public void testContextualEncodeQuery() {
        Assert.assertEquals("a%20b%20c.-%2A_=+&%25xx%20", UriComponent.contextualEncode("a b c.-*_=+&%xx%20", QUERY));
        Assert.assertEquals("a+b+c.-%2A_%3D%2B%26%25xx%20", UriComponent.contextualEncode("a b c.-*_=+&%xx%20", QUERY_PARAM));
        Assert.assertEquals("a%20b%20c.-%2A_%3D%2B%26%25xx%20", UriComponent.contextualEncode("a b c.-*_=+&%xx%20", QUERY_PARAM_SPACE_ENCODED));
        Assert.assertEquals("thumbsup%F0%9F%91%8Dthumbsup%F0%9F%91%8D", UriComponent.contextualEncode("thumbsup%F0%9F%91%8Dthumbsup\ud83d\udc4d", QUERY));
        Assert.assertEquals("thumbsup%F0%9F%91%8Dthumbsup%F0%9F%91%8D", UriComponent.contextualEncode("thumbsup%F0%9F%91%8Dthumbsup\ud83d\udc4d", QUERY_PARAM));
    }

    @Test
    public void testContextualEncodeMatrixParam() {
        Assert.assertEquals("a%3Db%20c%3Bx", UriComponent.contextualEncode("a=b c;x", MATRIX_PARAM));
        Assert.assertEquals("a%3Db%20c%3Bx%3Dthumbsup%F0%9F%91%8D", UriComponent.contextualEncode("a=b c;x=thumbsup\ud83d\udc4d", MATRIX_PARAM));
    }

    @Test
    public void testContextualEncodePercent() {
        Assert.assertEquals("%25", UriComponent.contextualEncode("%", PATH));
        Assert.assertEquals("a%25", UriComponent.contextualEncode("a%", PATH));
        Assert.assertEquals("a%25x", UriComponent.contextualEncode("a%x", PATH));
        Assert.assertEquals("a%25%20%20", UriComponent.contextualEncode("a%  ", PATH));
        Assert.assertEquals("a%20a%20%20", UriComponent.contextualEncode("a a%20 ", PATH));
    }

    @Test
    public void testEncodeTemplateNames() {
        Assert.assertEquals("%7Bfoo%7D", UriComponent.encodeTemplateNames("{foo}"));
    }

    @Test
    public void testValidatePath() {
        Assert.assertEquals(false, UriComponent.valid("/x y", PATH));
        Assert.assertEquals(true, UriComponent.valid("/x20y", PATH));
        Assert.assertEquals(true, UriComponent.valid("/x%20y", PATH));
    }
}

