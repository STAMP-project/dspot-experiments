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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@RunWith(RobolectricTestRunner.class)
public class MyServiceTest {
    private MyService_ myService = new MyService_(null);

    @Test
    public void canOverrideRootUrl() {
        MyService_ myService = new MyService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        myService.setRestTemplate(restTemplate);
        myService.setRootUrl("http://newRootUrl");
        myService.removeEvent(42);
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.startsWith("http://newRootUrl"), ((HttpMethod) (ArgumentMatchers.any())), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any(), ArgumentMatchers.<Map<String, ?>>any());
    }

    @Test
    public void getEventsArray2() {
        addPendingResponse("[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]");
        ResponseEntity<Event[]> responseEntity = myService.getEventsArray2("test", 42);
        Event[] events = responseEntity.getBody();
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Assert.assertEquals(2, events.length);
        Assert.assertEquals(event1, events[0]);
        Assert.assertEquals(event2, events[1]);
    }

    @Test
    public void getEventsGenericsList() {
        addPendingResponse("[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]");
        List<Event> events = myService.getEventsGenericsList("test", 42);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(event1, events.get(0));
        Assert.assertEquals(event2, events.get(1));
    }

    @Test
    public void getEventsGenericsArrayList() {
        addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':3,'name':'event3'}]]");
        List<Event>[] events = myService.getEventsGenericsArrayList("test", 42);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Event event3 = new Event(3, "event3");
        Assert.assertEquals(2, events.length);
        Assert.assertEquals(event1, events[0].get(0));
        Assert.assertEquals(event2, events[0].get(1));
        Assert.assertEquals(event3, events[1].get(0));
    }

    @Test
    public void getEventsGenericsListListEvent() {
        addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':3,'name':'event3'}]]");
        List<List<Event>> events = myService.getEventsGenericsListListEvent("test", 42);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Event event3 = new Event(3, "event3");
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(event1, events.get(0).get(0));
        Assert.assertEquals(event2, events.get(0).get(1));
        Assert.assertEquals(event3, events.get(1).get(0));
    }

    @Test
    public void getEventsGenericsListListEvents() {
        addPendingResponse("[[[{'id':1,'name':'event1'}],[{'id':2,'name':'event2'}]],[[{'id':3,'name':'event3'}]]]");
        List<List<Event[]>> events = myService.getEventsGenericsListListEvents("test", 42);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Event event3 = new Event(3, "event3");
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(event1, events.get(0).get(0)[0]);
        Assert.assertEquals(event2, events.get(0).get(1)[0]);
        Assert.assertEquals(event3, events.get(1).get(0)[0]);
    }

    @Test
    public void getEventsGenericsMap() {
        addPendingResponse("{'event1':{'id':1,'name':'event1'},'event2':{'id':2,'name':'event2'}}");
        Map<String, Event> eventsMap = myService.getEventsGenericsMap("test", 42);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Assert.assertEquals(2, eventsMap.size());
        Assert.assertEquals(event1, eventsMap.get("event1"));
        Assert.assertEquals(event2, eventsMap.get("event2"));
    }

    @Test
    public void urlWithAParameterDeclaredTwiceTest() {
        addPendingResponse("[[{'id':1,'name':'event1'},{'id':2,'name':'event2'}],[{'id':1,'name':'event1'},{'id':2,'name':'event2'}]]");
        Event[][] results = myService.urlWithAParameterDeclaredTwice(1985);
        Event event1 = new Event(1, "event1");
        Event event2 = new Event(2, "event2");
        Event[][] events = new Event[][]{ new Event[]{ event1, event2 }, new Event[]{ event1, event2 } };
        for (int i = 0; i < (events.length); i++) {
            Assert.assertEquals(results[i].length, events[i].length);
            for (int j = 0; j < (events[i].length); j++) {
                Assert.assertEquals(events[i][j].getName(), results[i][j].getName());
                Assert.assertEquals(events[i][j].getId(), results[i][j].getId());
            }
        }
    }

    @Test
    public void manualFullUrl() {
        MyService_ myService = new MyService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        myService.setRestTemplate(restTemplate);
        // make sure we used the full custom url.
        // this may be used like in Google's APIs
        // to fetch an oauth token; Mockito doesn't
        // return a response with the mock'd template,
        // so we just use this weird "ping" endpoint
        addPendingResponse("fancyHeaderToken");
        myService.setHttpBasicAuth("fancyUser", "fancierPassword");
        myService.ping();
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.eq("http://company.com/client/ping"), ArgumentMatchers.<HttpMethod>any(), ArgumentMatchers.<HttpEntity<?>>any(), ArgumentMatchers.<Class<Object>>any());
    }

    @Test
    public void cookieInUrl() {
        final String xtValue = "1234";
        final String sjsaidValue = "7890";
        final String locationValue = "somePlace";
        final int yearValue = 2013;
        MyService_ myService = new MyService_(null);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        myService.setRestTemplate(restTemplate);
        addPendingResponse("{'id':1,'name':'event1'}");
        // normally this is set by a call like authenticate()
        // which is annotated with @SetsCookie
        myService.setCookie("xt", xtValue);
        myService.setCookie("sjsaid", sjsaidValue);
        myService.setHttpBasicAuth("fancyUser", "fancierPassword");
        myService.getEventsVoid(locationValue, yearValue);
        ArgumentMatcher<HttpEntity<Void>> matcher = new ArgumentMatcher<HttpEntity<Void>>() {
            @Override
            public boolean matches(HttpEntity<Void> argument) {
                final String expected = ("sjsaid=" + sjsaidValue) + ";";
                return expected.equals(argument.getHeaders().get("Cookie").get(0));
            }
        };
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("location", locationValue);
        urlVariables.put("year", yearValue);
        urlVariables.put("xt", xtValue);
        Mockito.verify(restTemplate).exchange(ArgumentMatchers.anyString(), ArgumentMatchers.<HttpMethod>any(), ArgumentMatchers.argThat(matcher), ArgumentMatchers.<Class<Object>>any(), ArgumentMatchers.eq(urlVariables));
    }

    @Test
    public void authenticate() {
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestHeader("SomeFancyHeader", "aFancyHeader").responseCookie("xt", "1234").responseCookie("sjsaid", "5678").asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.authenticate();
            }
        });
    }

    @Test
    public void removeEventWithRequires() {
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.removeEventWithRequires(0);
            }
        });
    }

    @Test
    public void updateEventWithRequires() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.updateEventWithRequires(0);
            }
        });
    }

    @Test
    public void addEventWithParameters() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithParameters("now", "param1", "param2");
            }
        });
    }

    @Test
    public void addEventWithParts() {
        // 
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").expectedHeader("Content-Type", "multipart/form-data").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithParts("now", "param1", "param2");
            }
        });
    }

    @Test
    public void addEventWithPathParameters() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithPathParameters("now", "param1");
            }
        });
    }

    @Test
    public void addEventWithHeadersOverriden() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").requestHeader("SomeFancyHeader", "aFancyHeader").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithHeaders("now", "event");
            }
        });
    }

    @Test
    public void addEventWithHeaders() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").expectedHeader("SomeFancyHeader", "fancy").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithHeaders("now", "event");
            }
        });
    }

    @Test
    public void addEventWithHeadersHeadersAnnotation() {
        // 
        // 
        // 
        // 
        // 
        RequestTestBuilder.build().requestCookie("myCookie", "myCookieValue").expectedHeader("SomeFancyHeader", "fancy").responseContent("{'id':1,'name':'event1'}").hasUrlVariables(true).asserts(new RequestTestBuilder.RequestTestBuilderExecutor() {
            @Override
            public void execute(MyService myService) {
                myService.addEventWithHeadersHeadersAnnotation("now", "event");
            }
        });
    }
}

