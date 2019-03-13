/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.exception;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Const;


public class KettleFileNotFoundExceptionTest {
    private String expectedNullMessage = ((Const.CR) + "null") + (Const.CR);

    private String errorMessage = "error message";

    private String causeExceptionMessage = "Cause exception";

    private String filepath = "file.txt";

    private Throwable cause = new RuntimeException(causeExceptionMessage);

    @Test
    public void testConstructor() {
        try {
            throw new KettleFileNotFoundException();
        } catch (KettleFileNotFoundException e) {
            Assert.assertEquals(null, e.getCause());
            Assert.assertTrue(e.getMessage().contains(expectedNullMessage));
            Assert.assertEquals(null, e.getFilepath());
        }
    }

    @Test
    public void testConstructorMessage() {
        try {
            throw new KettleFileNotFoundException(errorMessage);
        } catch (KettleFileNotFoundException e) {
            Assert.assertEquals(null, e.getCause());
            Assert.assertTrue(e.getMessage().contains(errorMessage));
            Assert.assertEquals(null, e.getFilepath());
        }
    }

    @Test
    public void testConstructorMessageAndFilepath() {
        try {
            throw new KettleFileNotFoundException(errorMessage, filepath);
        } catch (KettleFileNotFoundException e) {
            Assert.assertEquals(null, e.getCause());
            Assert.assertTrue(e.getMessage().contains(errorMessage));
            Assert.assertEquals(filepath, e.getFilepath());
        }
    }

    @Test
    public void testConstructorThrowable() {
        try {
            throw new KettleFileNotFoundException(cause);
        } catch (KettleFileNotFoundException e) {
            Assert.assertEquals(cause, e.getCause());
            Assert.assertTrue(e.getMessage().contains(causeExceptionMessage));
            Assert.assertEquals(null, e.getFilepath());
        }
    }

    @Test
    public void testConstructorMessageAndThrowable() {
        Throwable cause = new RuntimeException(causeExceptionMessage);
        try {
            throw new KettleFileNotFoundException(errorMessage, cause);
        } catch (KettleFileNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains(errorMessage));
            Assert.assertTrue(e.getMessage().contains(causeExceptionMessage));
            Assert.assertEquals(cause, e.getCause());
            Assert.assertEquals(null, e.getFilepath());
        }
    }
}

