/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.beans;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link PropertyMatches}.
 *
 * @author Stephane Nicoll
 */
public class PropertyMatchesTests {
    @Test
    public void simpleBeanPropertyTypo() {
        PropertyMatches matches = PropertyMatches.forProperty("naem", PropertyMatchesTests.SampleBeanProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("name"));
    }

    @Test
    public void complexBeanPropertyTypo() {
        PropertyMatches matches = PropertyMatches.forProperty("desriptn", PropertyMatchesTests.SampleBeanProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), emptyArray());
    }

    @Test
    public void unknownBeanProperty() {
        PropertyMatches matches = PropertyMatches.forProperty("unknown", PropertyMatchesTests.SampleBeanProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), emptyArray());
    }

    @Test
    public void severalMatchesBeanProperty() {
        PropertyMatches matches = PropertyMatches.forProperty("counter", PropertyMatchesTests.SampleBeanProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter1"));
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter2"));
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter3"));
    }

    @Test
    public void simpleBeanPropertyErrorMessage() {
        PropertyMatches matches = PropertyMatches.forProperty("naem", PropertyMatchesTests.SampleBeanProperties.class);
        String msg = matches.buildErrorMessage();
        Assert.assertThat(msg, containsString("naem"));
        Assert.assertThat(msg, containsString("name"));
        Assert.assertThat(msg, containsString("setter"));
        Assert.assertThat(msg, not(containsString("field")));
    }

    @Test
    public void complexBeanPropertyErrorMessage() {
        PropertyMatches matches = PropertyMatches.forProperty("counter", PropertyMatchesTests.SampleBeanProperties.class);
        String msg = matches.buildErrorMessage();
        Assert.assertThat(msg, containsString("counter"));
        Assert.assertThat(msg, containsString("counter1"));
        Assert.assertThat(msg, containsString("counter2"));
        Assert.assertThat(msg, containsString("counter3"));
    }

    @Test
    public void simpleFieldPropertyTypo() {
        PropertyMatches matches = PropertyMatches.forField("naem", PropertyMatchesTests.SampleFieldProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("name"));
    }

    @Test
    public void complexFieldPropertyTypo() {
        PropertyMatches matches = PropertyMatches.forField("desriptn", PropertyMatchesTests.SampleFieldProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), emptyArray());
    }

    @Test
    public void unknownFieldProperty() {
        PropertyMatches matches = PropertyMatches.forField("unknown", PropertyMatchesTests.SampleFieldProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), emptyArray());
    }

    @Test
    public void severalMatchesFieldProperty() {
        PropertyMatches matches = PropertyMatches.forField("counter", PropertyMatchesTests.SampleFieldProperties.class);
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter1"));
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter2"));
        Assert.assertThat(matches.getPossibleMatches(), hasItemInArray("counter3"));
    }

    @Test
    public void simpleFieldPropertyErrorMessage() {
        PropertyMatches matches = PropertyMatches.forField("naem", PropertyMatchesTests.SampleFieldProperties.class);
        String msg = matches.buildErrorMessage();
        Assert.assertThat(msg, containsString("naem"));
        Assert.assertThat(msg, containsString("name"));
        Assert.assertThat(msg, containsString("field"));
        Assert.assertThat(msg, not(containsString("setter")));
    }

    @Test
    public void complexFieldPropertyErrorMessage() {
        PropertyMatches matches = PropertyMatches.forField("counter", PropertyMatchesTests.SampleFieldProperties.class);
        String msg = matches.buildErrorMessage();
        Assert.assertThat(msg, containsString("counter"));
        Assert.assertThat(msg, containsString("counter1"));
        Assert.assertThat(msg, containsString("counter2"));
        Assert.assertThat(msg, containsString("counter3"));
    }

    private static class SampleBeanProperties {
        private String name;

        private String description;

        private int counter1;

        private int counter2;

        private int counter3;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCounter1() {
            return counter1;
        }

        public void setCounter1(int counter1) {
            this.counter1 = counter1;
        }

        public int getCounter2() {
            return counter2;
        }

        public void setCounter2(int counter2) {
            this.counter2 = counter2;
        }

        public int getCounter3() {
            return counter3;
        }

        public void setCounter3(int counter3) {
            this.counter3 = counter3;
        }
    }

    private static class SampleFieldProperties {
        private String name;

        private String description;

        private int counter1;

        private int counter2;

        private int counter3;
    }
}

