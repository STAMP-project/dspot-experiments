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
package org.glassfish.jersey.tests.e2e.common.message.internal;


import MatchingEntityTag.ANY_MATCH;
import java.text.ParseException;
import java.util.Set;
import javax.ws.rs.core.EntityTag;
import org.glassfish.jersey.message.internal.HttpHeaderReader;
import org.glassfish.jersey.message.internal.MatchingEntityTag;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link MatchingEntityTag} unit tests ported from Jersey 1.x.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class MatchingEntityTagTest {
    @Test
    public void testOneEntityTag() throws Exception {
        String header = "\"1\"";
        Set<MatchingEntityTag> s = HttpHeaderReader.readMatchingEntityTag(header);
        Assert.assertEquals(1, s.size());
        Assert.assertTrue(s.contains(new EntityTag("1")));
    }

    @Test
    public void testMultipleEntityTag() throws Exception {
        String header = "\"1\", W/\"2\", \"3\"";
        Set<MatchingEntityTag> s = HttpHeaderReader.readMatchingEntityTag(header);
        Assert.assertEquals(3, s.size());
        Assert.assertTrue(s.contains(new EntityTag("1")));
        Assert.assertTrue(s.contains(new EntityTag("2", true)));
        Assert.assertTrue(s.contains(new EntityTag("3")));
    }

    @Test
    public void testAnyMatch() throws Exception {
        String header = "*";
        Set<MatchingEntityTag> s = HttpHeaderReader.readMatchingEntityTag(header);
        MatcherAssert.assertThat(s.size(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        MatcherAssert.assertThat(ANY_MATCH, CoreMatchers.is(s));
    }

    /**
     * Reproducer for JERSEY-1278.
     */
    @Test
    public void testBadEntityTag() {
        String header = "1\"";
        try {
            HttpHeaderReader.readMatchingEntityTag(header);
            Assert.fail("ParseException expected");
        } catch (ParseException pe) {
            MatcherAssert.assertThat(pe.getMessage(), CoreMatchers.containsString(header));
        }
    }
}

