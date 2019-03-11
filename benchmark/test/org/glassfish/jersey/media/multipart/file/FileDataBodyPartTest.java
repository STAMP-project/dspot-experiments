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
package org.glassfish.jersey.media.multipart.file;


import DefaultMediaTypePredictor.CommonMediaTypes.AVI;
import DefaultMediaTypePredictor.CommonMediaTypes.PNG;
import DefaultMediaTypePredictor.CommonMediaTypes.ZIP;
import java.io.File;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.BodyPartTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for {@link FileDataBodyPart}.
 *
 * @author Imran M Yousuf (imran at smartitengineering.com)
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class FileDataBodyPartTest extends BodyPartTest {
    @Override
    @Test
    public void testEntity() {
        try {
            bodyPart.setEntity("foo bar baz");
        } catch (UnsupportedOperationException exception) {
            // exception expected.
        }
    }

    @Test
    public void testCreateFDBP() throws Exception {
        FileDataBodyPart fdbp = ((FileDataBodyPart) (bodyPart));
        Assert.assertNull(fdbp.getFormDataContentDisposition());
        Assert.assertNull(fdbp.getName());
        Assert.assertNull(fdbp.getValue());
        Assert.assertTrue(fdbp.isSimple());
        String name;
        File file = new File("pom.xml");
        name = "xml";
        fdbp = new FileDataBodyPart(name, file);
        MediaType expectedType = MediaType.APPLICATION_XML_TYPE;
        checkEntityAttributes(name, fdbp, file, expectedType);
        fdbp.setName(name);
        checkEntityAttributes(name, fdbp, file, expectedType);
        fdbp.setFileEntity(file);
        checkEntityAttributes(name, fdbp, file, expectedType);
        fdbp = new FileDataBodyPart(name, file, expectedType);
        checkEntityAttributes(name, fdbp, file, expectedType);
        fdbp.setFileEntity(file, expectedType);
        checkEntityAttributes(name, fdbp, file, expectedType);
        file = new File("pom.png");
        name = "png";
        fdbp = new FileDataBodyPart("png", file);
        expectedType = PNG.getMediaType();
        checkEntityAttributes(name, fdbp, file, expectedType);
        file = new File("pom.zip");
        fdbp = new FileDataBodyPart(name, file);
        expectedType = ZIP.getMediaType();
        checkEntityAttributes(name, fdbp, file, expectedType);
        file = new File("pom.avi");
        fdbp = new FileDataBodyPart(name, file);
        expectedType = AVI.getMediaType();
        checkEntityAttributes(name, fdbp, file, expectedType);
    }
}

