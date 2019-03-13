/**
 * Copyright 2014-2016 the original author or authors.
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
package sample.session;


import SessionDetailsFilter.UNKNOWN;
import com.maxmind.geoip2.DatabaseReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sample.config.GeoConfig;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GeoConfig.class)
public class SessionDetailsFilterTests {
    @Autowired
    DatabaseReader reader;

    SessionDetailsFilter filter;

    @Test
    public void getGeoLocationHanldesInvalidIp() {
        assertThat(this.filter.getGeoLocation("a")).isEqualTo(UNKNOWN);
    }

    @Test
    public void getGeoLocationNullCity() {
        assertThat(this.filter.getGeoLocation("22.231.113.64")).isEqualTo("United States");
    }

    @Test
    public void getGeoLocationBoth() {
        assertThat(this.filter.getGeoLocation("184.154.83.119")).isEqualTo("Chicago, United States");
    }
}

