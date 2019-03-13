/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.state;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Alert}.
 */
public class AlertTest extends Alert {
    @Test
    public void testSetTextMiddleEllipsizing() throws Exception {
        Alert alert = new Alert();
        String shortText = "Short alert text";
        String longText = ("Not so short" + (new String(new char[MAX_ALERT_TEXT_SIZE]))) + "alert text";
        // Normal alert text
        alert.setText(shortText);
        Assert.assertEquals(shortText.length(), alert.getText().length());
        Assert.assertEquals(shortText, alert.getText());
        // Toooo long alert text
        alert.setText(longText);
        Assert.assertFalse(((shortText.length()) == (alert.getText().length())));
        Assert.assertEquals(MAX_ALERT_TEXT_SIZE, alert.getText().length());
        Assert.assertTrue(alert.getText().startsWith("Not so short"));
        Assert.assertTrue(alert.getText().endsWith("alert text"));
        Assert.assertTrue(alert.getText().contains("?"));
    }
}

