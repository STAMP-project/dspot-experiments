/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core.logging;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class LoggingPluginTypeTest {
    private LoggingPlugin annotation;

    @Test
    public void pickUpsId() throws Exception {
        Assert.assertEquals("id", LoggingPluginType.getInstance().extractID(annotation));
    }

    @Test
    public void pickUpName_NameIsSpecified() throws Exception {
        Mockito.when(annotation.name()).thenReturn("name");
        Assert.assertEquals("name", LoggingPluginType.getInstance().extractName(annotation));
    }

    @Test
    public void pickUpName_NameIsNotSpecified() throws Exception {
        Assert.assertEquals("id", LoggingPluginType.getInstance().extractName(annotation));
    }
}

