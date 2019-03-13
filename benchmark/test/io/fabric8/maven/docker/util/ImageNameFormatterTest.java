/**
 * Copyright 2016 Roland Huss
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
package io.fabric8.maven.docker.util;


import java.util.Date;
import java.util.Properties;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Injectable;
import mockit.Tested;
import org.apache.maven.project.MavenProject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 07/06/16
 */
public class ImageNameFormatterTest {
    @Injectable
    private MavenProject project;

    @Injectable
    private Date now = new Date();

    @Tested
    private ImageNameFormatter formatter;

    @Test
    public void simple() throws Exception {
        MatcherAssert.assertThat(formatter.format("bla"), CoreMatchers.equalTo("bla"));
    }

    @Test
    public void invalidFormatChar() throws Exception {
        try {
            formatter.format("bla %z");
            Assert.fail();
        } catch (IllegalArgumentException exp) {
            System.out.println(exp);
            MatcherAssert.assertThat("Doesnt match", exp.getMessage(), Matchers.containsString("%z"));
        }
    }

    @Test
    public void defaultUserName() throws Exception {
        final String[] data = new String[]{ "io.fabric8", "fabric8", "io.FABRIC8", "fabric8", "io.fabric8.", "fabric8", "io.fabric8", "fabric8", "fabric8....", "fabric8", "io.fabric8___", "fabric8__" };
        for (int i = 0; i < (data.length); i += 2) {
            final int finalI = i;
            new Expectations() {
                {
                    project.getProperties();
                    result = new Properties();
                    project.getGroupId();
                    result = data[finalI];
                }
            };
            String value = formatter.format("%g");
            MatcherAssert.assertThat(("Idx. " + (i / 2)), value, CoreMatchers.equalTo(data[(i + 1)]));
        }
    }

    @Test
    public void artifact() throws Exception {
        new Expectations() {
            {
                project.getArtifactId();
                result = "Docker....Maven.....Plugin";
            }
        };
        MatcherAssert.assertThat(formatter.format("--> %a <--"), CoreMatchers.equalTo("--> docker.maven.plugin <--"));
    }

    @Test
    public void tagWithProperty() throws Exception {
        new Expectations() {
            {
                Properties props = new Properties();
                props.put("docker.image.tag", "1.2.3");
                project.getProperties();
                result = props;
            }
        };
        MatcherAssert.assertThat(formatter.format("%t"), CoreMatchers.equalTo("1.2.3"));
        new FullVerifications() {
            {
            }
        };
    }

    @Test
    public void tag() throws Exception {
        new Expectations() {
            {
                project.getArtifactId();
                result = "docker-maven-plugin";
                project.getGroupId();
                result = "io.fabric8";
                project.getVersion();
                result = "1.2.3-SNAPSHOT";
                project.getProperties();
                result = new Properties();
            }
        };
        MatcherAssert.assertThat(formatter.format("%g/%a:%l"), CoreMatchers.equalTo("fabric8/docker-maven-plugin:latest"));
        MatcherAssert.assertThat(formatter.format("%g/%a:%v"), CoreMatchers.equalTo("fabric8/docker-maven-plugin:1.2.3-SNAPSHOT"));
        MatcherAssert.assertThat(formatter.format("%g/%a:%t").matches(".*snapshot-[\\d-]+$"), CoreMatchers.is(true));
    }

    @Test
    public void nonSnapshotArtifact() throws Exception {
        new Expectations() {
            {
                project.getArtifactId();
                result = "docker-maven-plugin";
                project.getGroupId();
                result = "io.fabric8";
                project.getVersion();
                result = "1.2.3";
                project.getProperties();
                result = new Properties();
            }
        };
        MatcherAssert.assertThat(formatter.format("%g/%a:%l"), CoreMatchers.equalTo("fabric8/docker-maven-plugin:1.2.3"));
        MatcherAssert.assertThat(formatter.format("%g/%a:%v"), CoreMatchers.equalTo("fabric8/docker-maven-plugin:1.2.3"));
        MatcherAssert.assertThat(formatter.format("%g/%a:%t"), CoreMatchers.equalTo("fabric8/docker-maven-plugin:1.2.3"));
    }

    @Test
    public void groupIdWithProperty() throws Exception {
        new Expectations() {
            {
                Properties props = new Properties();
                props.put("docker.image.user", "this.it..is");
                project.getProperties();
                result = props;
            }
        };
        MatcherAssert.assertThat(formatter.format("%g/name"), CoreMatchers.equalTo("this.it..is/name"));
        new FullVerifications() {
            {
            }
        };
    }

    private final class GroupIdExpectations extends Expectations {
        GroupIdExpectations(String groupId) {
        }
    }
}

