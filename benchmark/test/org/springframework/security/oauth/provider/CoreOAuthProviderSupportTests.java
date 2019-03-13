/**
 * Copyright 2008 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth.provider;


import OAuthConsumerParameter.oauth_consumer_key;
import OAuthConsumerParameter.oauth_nonce;
import OAuthConsumerParameter.oauth_signature;
import OAuthConsumerParameter.oauth_signature_method;
import OAuthConsumerParameter.oauth_timestamp;
import OAuthConsumerParameter.oauth_token;
import OAuthConsumerParameter.oauth_version;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth.provider.filter.CoreOAuthProviderSupport;


/**
 *
 *
 * @author Ryan Heaton
 */
@RunWith(MockitoJUnitRunner.class)
public class CoreOAuthProviderSupportTests {
    @Mock
    private HttpServletRequest request;

    /**
     * tests parsing parameters.
     */
    @Test
    public void testParseParameters() throws Exception {
        CoreOAuthProviderSupport support = new CoreOAuthProviderSupport();
        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.enumeration(Arrays.asList(("OAuth realm=\"http://sp.example.com/\",\n" + (((((("                oauth_consumer_key=\"0685bd9184jfhq22\",\n" + "                oauth_token=\"ad180jjd733klru7\",\n") + "                oauth_signature_method=\"HMAC-SHA1\",\n") + "                oauth_signature=\"wOJIO9A2W5mFwDgiDvZbTSMK%2FPY%3D\",\n") + "                oauth_timestamp=\"137131200\",\n") + "                oauth_nonce=\"4572616e48616d6d65724c61686176\",\n") + "                oauth_version=\"1.0\"")))));
        Map<String, String> params = support.parseParameters(request);
        Assert.assertEquals("http://sp.example.com/", params.get("realm"));
        Assert.assertEquals("0685bd9184jfhq22", params.get(oauth_consumer_key.toString()));
        Assert.assertEquals("ad180jjd733klru7", params.get(oauth_token.toString()));
        Assert.assertEquals("HMAC-SHA1", params.get(oauth_signature_method.toString()));
        Assert.assertEquals("wOJIO9A2W5mFwDgiDvZbTSMK/PY=", params.get(oauth_signature.toString()));
        Assert.assertEquals("137131200", params.get(oauth_timestamp.toString()));
        Assert.assertEquals("4572616e48616d6d65724c61686176", params.get(oauth_nonce.toString()));
        Assert.assertEquals("1.0", params.get(oauth_version.toString()));
    }

    /**
     * tests getting the signature base string.
     */
    @Test
    public void testGetSignatureBaseString() throws Exception {
        Map<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("file", new String[]{ "vacation.jpg" });
        requestParameters.put("size", new String[]{ "original" });
        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(requestParameters.keySet()));
        for (String key : requestParameters.keySet()) {
            Mockito.when(request.getParameterValues(key)).thenReturn(requestParameters.get(key));
        }
        Mockito.when(request.getHeaders("Authorization")).thenReturn(Collections.enumeration(Arrays.asList(("OAuth realm=\"http://sp.example.com/\",\n" + (((((("                oauth_consumer_key=\"dpf43f3p2l4k3l03\",\n" + "                oauth_token=\"nnch734d00sl2jdk\",\n") + "                oauth_signature_method=\"HMAC-SHA1\",\n") + "                oauth_signature=\"unimportantforthistest\",\n") + "                oauth_timestamp=\"1191242096\",\n") + "                oauth_nonce=\"kllo9940pd9333jh\",\n") + "                oauth_version=\"1.0\"")))));
        Mockito.when(request.getMethod()).thenReturn("gEt");
        CoreOAuthProviderSupport support = new CoreOAuthProviderSupport();
        support.setBaseUrl("http://photos.example.net");
        Mockito.when(request.getRequestURI()).thenReturn("photos");
        String baseString = support.getSignatureBaseString(request);
        Assert.assertEquals("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&file%3Dvacation.jpg%26oauth_consumer_key%3Ddpf43f3p2l4k3l03%26oauth_nonce%3Dkllo9940pd9333jh%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1191242096%26oauth_token%3Dnnch734d00sl2jdk%26oauth_version%3D1.0%26size%3Doriginal", baseString);
    }
}

