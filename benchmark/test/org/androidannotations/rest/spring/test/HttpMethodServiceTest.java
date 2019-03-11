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


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpMethod.PUT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@RunWith(RobolectricTestRunner.class)
public class HttpMethodServiceTest {
    @Test
    public void useDeleteHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        service.setRestTemplate(restTemplate);
        service.delete();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(DELETE), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    public void useGetHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        service.setRestTemplate(restTemplate);
        service.get();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(GET), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void useHeadHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<Object> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(HEAD), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any())).thenReturn(response);
        service.setRestTemplate(restTemplate);
        service.head();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(HEAD), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void useOptionsHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<Object> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(OPTIONS), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any())).thenReturn(response);
        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(response.getHeaders()).thenReturn(headers);
        service.setRestTemplate(restTemplate);
        service.options();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(OPTIONS), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    public void usePostHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        service.setRestTemplate(restTemplate);
        service.post();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(POST), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    public void usePutHttpMethod() {
        HttpMethodsService_ service = new HttpMethodsService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        service.setRestTemplate(restTemplate);
        service.put();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>eq(PUT), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }
}

