/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.util;


import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Valeriy Svydenko
 */
public class TextUtilsTest {
    private static final String TEXT = "to be or not to be";

    @Test
    public void textShouldBeEncodedInMD5Hash() {
        Assert.assertEquals(TextUtils.md5(TextUtilsTest.TEXT), Hashing.md5().hashString(TextUtilsTest.TEXT, Charset.defaultCharset()).toString());
    }
}

