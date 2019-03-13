/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.greenhouse.events.load;


import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class NFJSLoaderTest {
    private static final int SHOW_ID = 271;

    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private EventLoaderRepository eventLoaderRepository;

    private NFJSLoader loader;

    @Test
    public void loadOnce() {
        setupMockRestServiceServer(loader, 1);
        loader.loadEventData(NFJSLoaderTest.SHOW_ID);
        assertRowCounts(1, 1, 85, 36, 112);
        assertEventData("SpringOne 2GX", "America/Chicago", "2011-10-25 00:00:00.0", "2011-10-28 23:59:59.0", "S2GX");
        assertVenueData("Chicago Marriott Downtown Magnificent Mile", "540 North Michigan Avenue null Chicago, IL 60611", 41.8920052, (-87.6247001), "Chicago, IL", 1L);
        assertLeaderData("Craig Walls", "Craig Walls is the Spring Social Project Lead.", "http://blog.springsource.com/author/cwalls/", "habuma");
        assertEventTimeSlotData(16L, 1L, "DINNER", "2011-10-26 18:30:00.0", "2011-10-26 19:30:00.0");
    }

    @Test
    public void loadOnceThenUpdateWithSameData() {
        setupMockRestServiceServer(loader, 3);
        loader.loadEventData(NFJSLoaderTest.SHOW_ID);
        assertRowCounts(1, 1, 85, 36, 112);
        loader.loadEventData(NFJSLoaderTest.SHOW_ID);
        assertRowCounts(1, 1, 85, 36, 112);
        assertEventData("SpringOne 2GX", "America/Chicago", "2011-10-25 00:00:00.0", "2011-10-28 23:59:59.0", "S2GX");
        assertVenueData("Chicago Marriott Downtown Magnificent Mile", "540 North Michigan Avenue null Chicago, IL 60611", 41.8920052, (-87.6247001), "Chicago, IL", 1L);
        assertLeaderData("Craig Walls", "Craig Walls is the Spring Social Project Lead.", "http://blog.springsource.com/author/cwalls/", "habuma");
        assertEventTimeSlotData(16L, 1L, "DINNER", "2011-10-26 18:30:00.0", "2011-10-26 19:30:00.0");
    }

    @Test
    public void loadOnceThenUpdateNewData() {
        setupMockRestServiceServerWithUpdates(loader);
        loader.loadEventData(NFJSLoaderTest.SHOW_ID);
        assertRowCounts(1, 1, 85, 36, 112);
        assertEventData("SpringOne 2GX", "America/Chicago", "2011-10-25 00:00:00.0", "2011-10-28 23:59:59.0", "S2GX");
        assertVenueData("Chicago Marriott Downtown Magnificent Mile", "540 North Michigan Avenue null Chicago, IL 60611", 41.8920052, (-87.6247001), "Chicago, IL", 1L);
        assertLeaderData("Craig Walls", "Craig Walls is the Spring Social Project Lead.", "http://blog.springsource.com/author/cwalls/", "habuma");
        assertEventTimeSlotData(16L, 1L, "DINNER", "2011-10-26 18:30:00.0", "2011-10-26 19:30:00.0");
        loader.loadEventData(NFJSLoaderTest.SHOW_ID);
        assertRowCounts(1, 1, 86, 37, 113);
        assertEventData("SpringOne/2GX", "America/Boise", "2012-06-09 00:00:00.0", "2012-06-12 23:59:59.0", "SGX");
        assertVenueData("Pocatello Convention Center", "1234 South Arizona Drive null Pocatello, ID 83201", 41.8920052, (-87.6247001), "Pocatello, ID", 1L);
        assertLeaderData("Mr. Craig Walls", "Craig Walls is the Spring Social Project Lead and an avid collector of American Way magazines.", "http://blog.springsource.com/author/craigwalls/", "habumadude");
        assertEventTimeSlotData(16L, 1L, "SUPPER", "2012-06-10 18:30:00.0", "2012-06-10 19:30:00.0");
    }
}

