/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.io.impl;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class ReaderResourceTest {
    static final Charset[] availableCharsets = Charset.availableCharsets().values().toArray(new Charset[]{  });

    @Test
    public void defaultEncodingInitialization() throws UnsupportedEncodingException {
        // setup: any default encoding
        final String anyEncoding = ReaderResourceTest.availableCharsets[0].name();
        final InputStream istream = new ByteArrayInputStream(new byte[]{  });
        InputStreamReader ireader = new InputStreamReader(istream, anyEncoding);
        // test
        ReaderResource iresource = new ReaderResource(ireader, null, null);
        // assert
        Assert.assertEquals(ireader.getEncoding(), iresource.getEncoding());
        // setup: different default encoding to prove source
        final String differentEncoding = ReaderResourceTest.availableCharsets[1].name();
        ireader = new InputStreamReader(istream, differentEncoding);
        // test
        iresource = new ReaderResource(ireader, null, null);
        // assert
        Assert.assertEquals(ireader.getEncoding(), iresource.getEncoding());
    }

    @Test
    public void overwritingEncodingInitialization() throws UnsupportedEncodingException {
        // setup: any default encoding
        final String anyEncoding = ReaderResourceTest.availableCharsets[0].name();
        final InputStream istream = new ByteArrayInputStream(new byte[]{  });
        final InputStreamReader ireader = new InputStreamReader(istream, anyEncoding);
        // test: overwrite with different encoding
        final String overwrittenEncoding = ReaderResourceTest.availableCharsets[1].name();
        final ReaderResource iresource = new ReaderResource(ireader, overwrittenEncoding, null);
        // assert
        Assert.assertEquals(overwrittenEncoding, iresource.getEncoding());
        Assert.assertNotEquals(ireader.getEncoding(), iresource.getEncoding());
    }
}

