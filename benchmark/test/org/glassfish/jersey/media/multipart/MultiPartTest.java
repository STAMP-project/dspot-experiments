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
package org.glassfish.jersey.media.multipart;


import MediaType.MULTIPART_FORM_DATA_TYPE;
import javax.ws.rs.core.MediaType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for {@link MultiPart}.
 *
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class MultiPartTest {
    protected MultiPart multiPart = null;

    @Test
    @SuppressWarnings("empty-statement")
    public void testCreate() {
        if ((multiPart) instanceof FormDataMultiPart) {
            Assert.assertEquals("multipart/form-data", multiPart.getMediaType().toString());
            try {
                multiPart.setMediaType(new MediaType("multipart", "foo"));
                Assert.fail("Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                // Expected result.
            }
            multiPart.setMediaType(MULTIPART_FORM_DATA_TYPE);
        } else {
            Assert.assertEquals("multipart/mixed", multiPart.getMediaType().toString());
            multiPart.setMediaType(new MediaType("multipart", "alternative"));
            Assert.assertEquals("multipart/alternative", multiPart.getMediaType().toString());
            try {
                multiPart.setMediaType(new MediaType("text", "xml"));
                Assert.fail("Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                // Expected result.
            }
        }
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testEntity() {
        try {
            multiPart.setEntity("foo bar baz");
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected result.
        }
        try {
            Assert.assertEquals("foo bar baz", multiPart.getEntity());
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected result.
        }
    }
}

