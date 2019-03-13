/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.template;


import freemarker.template.SimpleDate;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author James Moger
 */
public class TemplateEngineFreemarkerPrettyTimeMethodTest {
    Map<Locale, String> expections = new HashMap<Locale, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(Locale.ENGLISH, "1 day ago");
            put(Locale.GERMAN, "vor 1 Tag");
            put(Locale.FRENCH, "il y a 1 jour");
            put(Locale.ITALIAN, "1 giorno fa");
            put(Locale.CHINESE, "1 ? ?");
            put(Locale.JAPANESE, "1??");
            put(Locale.KOREAN, "1? ?");
        }
    };

    @Test
    public void testThatJavaUtilDateWorks() throws Exception {
        test(new SimpleDate(new Date(getYesterdaysMillis()), SimpleDate.DATE));
    }

    @Test
    public void testThatJavaSqlDateWorks() throws Exception {
        test(new SimpleDate(new java.sql.Date(getYesterdaysMillis())));
    }

    @Test
    public void testThatJavaSqlTimeWorks() throws Exception {
        test(new SimpleDate(new Time(getYesterdaysMillis())));
    }

    @Test
    public void testThatJavaSqlTimestampWorks() throws Exception {
        test(new SimpleDate(new Timestamp(getYesterdaysMillis())));
    }
}

