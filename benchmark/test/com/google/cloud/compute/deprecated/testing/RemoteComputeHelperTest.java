/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated.testing;


import com.google.cloud.compute.deprecated.ComputeOptions;
import com.google.cloud.compute.deprecated.testing.RemoteComputeHelper.ComputeHelperException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Duration;


public class RemoteComputeHelperTest {
    private static final String PROJECT_ID = "project-id";

    private static final String JSON_KEY = "{\n" + (((((((((((((((((((((((("  \"private_key_id\": \"somekeyid\",\n" + "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggS") + "kAgEAAoIBAQC+K2hSuFpAdrJI\\nnCgcDz2M7t7bjdlsadsasad+fvRSW6TjNQZ3p5LLQY1kSZRqBqylRkzteMOyHg") + "aR\\n0Pmxh3ILCND5men43j3h4eDbrhQBuxfEMalkG92sL+PNQSETY2tnvXryOvmBRwa/\\nQP/9dJfIkIDJ9Fw9N4") + "Bhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nknddadwkwewcVxHFhcZJO+XWf6ofLUXpRwiTZakGMn8EE1uVa2") + "LgczOjwWHGi99MFjxSer5m9\\n1tCa3/KEGKiS/YL71JvjwX3mb+cewlkcmweBKZHM2JPTk0ZednFSpVZMtycjkbLa") + "\\ndYOS8V85AgMBewECggEBAKksaldajfDZDV6nGqbFjMiizAKJolr/M3OQw16K6o3/\\n0S31xIe3sSlgW0+UbYlF") + "4U8KifhManD1apVSC3csafaspP4RZUHFhtBywLO9pR5c\\nr6S5aLp+gPWFyIp1pfXbWGvc5VY/v9x7ya1VEa6rXvL") + "sKupSeWAW4tMj3eo/64ge\\nsdaceaLYw52KeBYiT6+vpsnYrEkAHO1fF/LavbLLOFJmFTMxmsNaG0tuiJHgjshB\\") + "n82DpMCbXG9YcCgI/DbzuIjsdj2JC1cascSP//3PmefWysucBQe7Jryb6NQtASmnv\\nCdDw/0jmZTEjpe4S1lxfHp") + "lAhHFtdgYTvyYtaLZiVVkCgYEA8eVpof2rceecw/I6\\n5ng1q3Hl2usdWV/4mZMvR0fOemacLLfocX6IYxT1zA1FF") + "JlbXSRsJMf/Qq39mOR2\\nSpW+hr4jCoHeRVYLgsbggtrevGmILAlNoqCMpGZ6vDmJpq6ECV9olliDvpPgWOP+\\nm") + "YPDreFBGxWvQrADNbRt2dmGsrsCgYEAyUHqB2wvJHFqdmeBsaacewzV8x9WgmeX\\ngUIi9REwXlGDW0Mz50dxpxcK") + "CAYn65+7TCnY5O/jmL0VRxU1J2mSWyWTo1C+17L0\\n3fUqjxL1pkefwecxwecvC+gFFYdJ4CQ/MHHXU81Lwl1iWdF") + "Cd2UoGddYaOF+KNeM\\nHC7cmqra+JsCgYEAlUNywzq8nUg7282E+uICfCB0LfwejuymR93CtsFgb7cRd6ak\\nECR") + "8FGfCpH8ruWJINllbQfcHVCX47ndLZwqv3oVFKh6pAS/vVI4dpOepP8++7y1u\\ncoOvtreXCX6XqfrWDtKIvv0vjl") + "HBhhhp6mCcRpdQjV38H7JsyJ7lih/oNjECgYAt\\nkndj5uNl5SiuVxHFhcZJO+XWf6ofLUregtevZakGMn8EE1uVa") + "2AY7eafmoU/nZPT\\n00YB0TBATdCbn/nBSuKDESkhSg9s2GEKQZG5hBmL5uCMfo09z3SfxZIhJdlerreP\\nJ7gSi") + "dI12N+EZxYd4xIJh/HFDgp7RRO87f+WJkofMQKBgGTnClK1VMaCRbJZPriw\\nEfeFCoOX75MxKwXs6xgrw4W//AYG") + "GUjDt83lD6AZP6tws7gJ2IwY/qP7+lyhjEqN\\nHtfPZRGFkGZsdaksdlaksd323423d+15/UvrlRSFPNj1tWQmNKk") + "XyRDW4IG1Oa2p\\nrALStNBx5Y9t0/LQnFI4w3aG\\n-----END PRIVATE KEY-----\\n\",\n") + "  \"client_email\": \"someclientid@developer.gserviceaccount.com\",\n") + "  \"client_id\": \"someclientid.apps.googleusercontent.com\",\n") + "  \"type\": \"service_account\"\n") + "}");

    private static final InputStream JSON_KEY_STREAM = new ByteArrayInputStream(RemoteComputeHelperTest.JSON_KEY.getBytes());

    private static final String BASE_RESOURCE_NAME_REGEX = "test-[0-9a-f]{24}-";

    private static final Pattern BASE_RESOURCE_NAME_PATTERN = Pattern.compile(RemoteComputeHelperTest.BASE_RESOURCE_NAME_REGEX);

    @Test
    public void testBaseResourceName() {
        String baseResourceName = RemoteComputeHelper.baseResourceName();
        Assert.assertTrue(RemoteComputeHelperTest.BASE_RESOURCE_NAME_PATTERN.matcher(baseResourceName).matches());
    }

    @Test
    public void testCreateFromStream() {
        RemoteComputeHelper helper = RemoteComputeHelper.create(RemoteComputeHelperTest.PROJECT_ID, RemoteComputeHelperTest.JSON_KEY_STREAM);
        ComputeOptions options = helper.getOptions();
        Assert.assertEquals(RemoteComputeHelperTest.PROJECT_ID, options.getProjectId());
        Assert.assertEquals(60000, getConnectTimeout());
        Assert.assertEquals(60000, getReadTimeout());
        Assert.assertEquals(10, options.getRetrySettings().getMaxAttempts());
        Assert.assertEquals(Duration.ofMillis(30000), options.getRetrySettings().getMaxRetryDelay());
        Assert.assertEquals(Duration.ofMillis(120000), options.getRetrySettings().getTotalTimeout());
        Assert.assertEquals(Duration.ofMillis(250), options.getRetrySettings().getInitialRetryDelay());
    }

    @Test
    public void testComputeHelperException() {
        ComputeHelperException exception = new ComputeHelperException("message", null);
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getCause());
        IOException cause = new IOException("message");
        exception = ComputeHelperException.translate(cause);
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertSame(cause, exception.getCause());
    }
}

