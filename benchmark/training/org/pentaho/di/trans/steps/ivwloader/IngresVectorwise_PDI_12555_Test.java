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
package org.pentaho.di.trans.steps.ivwloader;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class IngresVectorwise_PDI_12555_Test {
    @Mock
    IngresVectorwiseLoader ingresVectorwiseLoaderMock;

    @Test
    public void testReplace() {
        String input = "\\\"Name\"";
        String[] from = new String[]{ "\"" };
        String[] to = new String[]{ "\\\"" };
        Mockito.doCallRealMethod().when(ingresVectorwiseLoaderMock).replace(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.any(String[].class));
        String actual = ingresVectorwiseLoaderMock.replace(input, from, to);
        String expected = "\\\\\"Name\\\"";
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testMasqueradPassword() {
        Mockito.doCallRealMethod().when(ingresVectorwiseLoaderMock).masqueradPassword(ArgumentMatchers.anyString());
        Mockito.doCallRealMethod().when(ingresVectorwiseLoaderMock).substitute(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        String cmdUsingVwload = "this is the string without brackets";
        String actual = ingresVectorwiseLoaderMock.masqueradPassword(cmdUsingVwload);
        // to make sure that there is no exceptions
        Assert.assertEquals("", actual);
        String cmdUsingSql = "/path_to_sql/sql @00.000.000.000,VW[db_user,db_pass]::db_name";
        actual = ingresVectorwiseLoaderMock.masqueradPassword(cmdUsingSql);
        String expected = "/path_to_sql/sql @00.000.000.000,VW[username,password]::db_name";
        Assert.assertEquals(expected, actual);
    }
}

