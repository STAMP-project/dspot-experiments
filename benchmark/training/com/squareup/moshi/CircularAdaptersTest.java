/**
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.moshi;


import Util.NO_ANNOTATIONS;
import com.squareup.moshi.internal.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Set;
import org.junit.Test;


public final class CircularAdaptersTest {
    static class Team {
        final String lead;

        final CircularAdaptersTest.Project[] projects;

        public Team(String lead, CircularAdaptersTest.Project... projects) {
            this.lead = lead;
            this.projects = projects;
        }
    }

    static class Project {
        final String name;

        final CircularAdaptersTest.Team[] teams;

        Project(String name, CircularAdaptersTest.Team... teams) {
            this.name = name;
            this.teams = teams;
        }
    }

    @Test
    public void circularAdapters() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CircularAdaptersTest.Team> teamAdapter = moshi.adapter(CircularAdaptersTest.Team.class);
        CircularAdaptersTest.Team team = new CircularAdaptersTest.Team("Alice", new CircularAdaptersTest.Project("King", new CircularAdaptersTest.Team("Charlie", new CircularAdaptersTest.Project("Delivery", null))));
        assertThat(teamAdapter.toJson(team)).isEqualTo(("{\"lead\":\"Alice\",\"projects\":[{\"name\":" + "\"King\",\"teams\":[{\"lead\":\"Charlie\",\"projects\":[{\"name\":\"Delivery\"}]}]}]}"));
        CircularAdaptersTest.Team fromJson = teamAdapter.fromJson(("{\"lead\":\"Alice\",\"projects\":[{\"name\":" + "\"King\",\"teams\":[{\"lead\":\"Charlie\",\"projects\":[{\"name\":\"Delivery\"}]}]}]}"));
        assertThat(fromJson.lead).isEqualTo("Alice");
        assertThat(fromJson.projects[0].name).isEqualTo("King");
        assertThat(fromJson.projects[0].teams[0].lead).isEqualTo("Charlie");
        assertThat(fromJson.projects[0].teams[0].projects[0].name).isEqualTo("Delivery");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    public @interface Left {}

    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    public @interface Right {}

    static class Node {
        final String name;

        @CircularAdaptersTest.Left
        final CircularAdaptersTest.Node left;

        @CircularAdaptersTest.Right
        final CircularAdaptersTest.Node right;

        Node(String name, CircularAdaptersTest.Node left, CircularAdaptersTest.Node right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        CircularAdaptersTest.Node plusPrefix(String prefix) {
            return new CircularAdaptersTest.Node((prefix + (name)), left, right);
        }

        CircularAdaptersTest.Node minusPrefix(String prefix) {
            if (!(name.startsWith(prefix)))
                throw new IllegalArgumentException();

            return new CircularAdaptersTest.Node(name.substring(prefix.length()), left, right);
        }
    }

    /**
     * This factory uses extensive delegation. Each node delegates to this for the left and right
     * subtrees, and those delegate to the built-in class adapter to do most of the serialization
     * work.
     */
    static class PrefixingNodeFactory implements JsonAdapter.Factory {
        @Override
        public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations, Moshi moshi) {
            if (type != (CircularAdaptersTest.Node.class))
                return null;

            final String prefix;
            if (Util.isAnnotationPresent(annotations, CircularAdaptersTest.Left.class)) {
                prefix = "L ";
            } else
                if (Util.isAnnotationPresent(annotations, CircularAdaptersTest.Right.class)) {
                    prefix = "R ";
                } else {
                    return null;
                }

            final JsonAdapter<CircularAdaptersTest.Node> delegate = moshi.nextAdapter(this, CircularAdaptersTest.Node.class, NO_ANNOTATIONS);
            return nullSafe();
        }
    }

    @Test
    public void circularAdaptersAndAnnotations() throws Exception {
        Moshi moshi = new Moshi.Builder().add(new CircularAdaptersTest.PrefixingNodeFactory()).build();
        JsonAdapter<CircularAdaptersTest.Node> nodeAdapter = moshi.adapter(CircularAdaptersTest.Node.class);
        CircularAdaptersTest.Node tree = new CircularAdaptersTest.Node("C", new CircularAdaptersTest.Node("A", null, new CircularAdaptersTest.Node("B", null, null)), new CircularAdaptersTest.Node("D", null, new CircularAdaptersTest.Node("E", null, null)));
        assertThat(nodeAdapter.toJson(tree)).isEqualTo(("{" + ((("\"left\":{\"name\":\"L A\",\"right\":{\"name\":\"R B\"}}," + "\"name\":\"C\",") + "\"right\":{\"name\":\"R D\",\"right\":{\"name\":\"R E\"}}") + "}")));
        CircularAdaptersTest.Node fromJson = nodeAdapter.fromJson(("{" + ((("\"left\":{\"name\":\"L A\",\"right\":{\"name\":\"R B\"}}," + "\"name\":\"C\",") + "\"right\":{\"name\":\"R D\",\"right\":{\"name\":\"R E\"}}") + "}")));
        assertThat(fromJson.name).isEqualTo("C");
        assertThat(fromJson.left.name).isEqualTo("A");
        assertThat(fromJson.left.right.name).isEqualTo("B");
        assertThat(fromJson.right.name).isEqualTo("D");
        assertThat(fromJson.right.right.name).isEqualTo("E");
    }
}

