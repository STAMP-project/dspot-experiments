/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.rest.spring.test;


import HttpMethod.GET;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;


@RunWith(RobolectricTestRunner.class)
public class PathRestServiceTest {
    @Test
    public void useAnnotationValueForUrlVariable() {
        PathRestService pathRestService = new PathRestService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        pathRestService.setRestTemplate(restTemplate);
        pathRestService.get("first", "second", "last");
        @SuppressWarnings("checkstyle:illegaltype")
        HashMap<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("hello", "first");
        urlVariables.put("parameterName", "last");
        urlVariables.put("bye", "second");
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(GET), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any(), ArgumentMatchers.eq(urlVariables));
    }
}

