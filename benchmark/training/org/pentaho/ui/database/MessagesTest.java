/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.ui.database;


import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.i18n.LanguageChoice;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


/**
 * Unit tests for Messages.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Messages.class })
public class MessagesTest {
    @Test
    public void testGetBundle() throws Exception {
        Assert.assertNotNull(Messages.getBundle());
    }

    @Test
    public void testGetString() throws Exception {
        // These tests are meant for the en_US locale (or equivalent)
        Assert.assertEquals("Database Connection", Messages.getString("DatabaseDialog.Shell.title"));
        Assert.assertEquals("!Not.A.Message!", Messages.getString("Not.A.Message"));
        // 1 param
        Assert.assertEquals("MyParam: JDBC options help", Messages.getString("DatabaseDialog.JDBCOptions.Tab", "MyParam"));
        Assert.assertEquals("!Not.A.Message!", Messages.getString("Not.A.Message", "Unused1"));
        Assert.assertEquals("!null!", Messages.getString(null, "Unused1"));
        // 2 params
        Assert.assertEquals("MyParam: JDBC options help", Messages.getString("DatabaseDialog.JDBCOptions.Tab", "MyParam", "Unused"));
        Assert.assertEquals("!Not.A.Message!", Messages.getString("Not.A.Message", "Unused1", "Unused2"));
        Assert.assertEquals("!null!", Messages.getString(null, null, null));
        // 3 params
        Assert.assertEquals("MyParam: JDBC options help", Messages.getString("DatabaseDialog.JDBCOptions.Tab", "MyParam", "Unused2", "Unused3"));
        Assert.assertEquals("!Not.A.Message!", Messages.getString("Not.A.Message", "Unused1", "Unused2", "Unused3"));
        Assert.assertEquals("!null!", Messages.getString(null, null, null, null));
        // 4 params
        Assert.assertEquals("MyParam: JDBC options help", Messages.getString("DatabaseDialog.JDBCOptions.Tab", "MyParam", "Unused2", "Unused3", "Unused4"));
        Assert.assertEquals("!Not.A.Message!", Messages.getString("Not.A.Message", "Unused1", "Unused2", "Unused3", "Unused4"));
        Assert.assertEquals("!null!", Messages.getString(null, null, null, null, null));
    }

    @Test
    public void getStringLocalized() throws Exception {
        PowerMockito.mockStatic(Messages.class);
        PowerMockito.when(Messages.getString(Mockito.anyString())).thenCallRealMethod();
        PowerMockito.when(Messages.getBundle()).thenCallRealMethod();
        final String msgKey = "DatabaseDialog.column.Parameter";
        LanguageChoice.getInstance().setDefaultLocale(Locale.FRENCH);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("Parameter", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.FRANCE);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("Parameter", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.ENGLISH);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("Parameter", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.US);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("Parameter", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.JAPANESE);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("?????", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.JAPAN);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("?????", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("????", Messages.getString(msgKey));
        LanguageChoice.getInstance().setDefaultLocale(Locale.CHINESE);
        // force the recreation of the bundle after setting the preferred language
        Whitebox.setInternalState(Messages.class, "RESOURCE_BUNDLE", ((ResourceBundle) (null)));
        Assert.assertEquals("Parameter", Messages.getString(msgKey));
    }
}

