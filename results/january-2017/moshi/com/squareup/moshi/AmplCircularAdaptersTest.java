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


public final class AmplCircularAdaptersTest {
    static class Team {
        final java.lang.String lead;

        final com.squareup.moshi.AmplCircularAdaptersTest.Project[] projects;

        public Team(java.lang.String lead, com.squareup.moshi.AmplCircularAdaptersTest.Project... projects) {
            this.lead = lead;
            this.projects = projects;
        }
    }

    static class Project {
        final java.lang.String name;

        final com.squareup.moshi.AmplCircularAdaptersTest.Team[] teams;

        Project(java.lang.String name, com.squareup.moshi.AmplCircularAdaptersTest.Team... teams) {
            this.name = name;
            this.teams = teams;
        }
    }

    @org.junit.Test
    public void circularAdapters() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplCircularAdaptersTest.Team> teamAdapter = moshi.adapter(com.squareup.moshi.AmplCircularAdaptersTest.Team.class);
        com.squareup.moshi.AmplCircularAdaptersTest.Team team = new com.squareup.moshi.AmplCircularAdaptersTest.Team("Alice", new com.squareup.moshi.AmplCircularAdaptersTest.Project("King", new com.squareup.moshi.AmplCircularAdaptersTest.Team("Charlie", new com.squareup.moshi.AmplCircularAdaptersTest.Project("Delivery", null))));
        org.assertj.core.api.Assertions.assertThat(teamAdapter.toJson(team)).isEqualTo(("{\"lead\":\"Alice\",\"projects\":[{\"name\":" + "\"King\",\"teams\":[{\"lead\":\"Charlie\",\"projects\":[{\"name\":\"Delivery\"}]}]}]}"));
        com.squareup.moshi.AmplCircularAdaptersTest.Team fromJson = teamAdapter.fromJson(("{\"lead\":\"Alice\",\"projects\":[{\"name\":" + "\"King\",\"teams\":[{\"lead\":\"Charlie\",\"projects\":[{\"name\":\"Delivery\"}]}]}]}"));
        org.assertj.core.api.Assertions.assertThat(fromJson.lead).isEqualTo("Alice");
        org.assertj.core.api.Assertions.assertThat(fromJson.projects[0].name).isEqualTo("King");
        org.assertj.core.api.Assertions.assertThat(fromJson.projects[0].teams[0].lead).isEqualTo("Charlie");
        org.assertj.core.api.Assertions.assertThat(fromJson.projects[0].teams[0].projects[0].name).isEqualTo("Delivery");
    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    public @interface Left {    }

    @java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.squareup.moshi.JsonQualifier
    public @interface Right {    }

    static class Node {
        final java.lang.String name;

        @com.squareup.moshi.AmplCircularAdaptersTest.Left
        final com.squareup.moshi.AmplCircularAdaptersTest.Node left;

        @com.squareup.moshi.AmplCircularAdaptersTest.Right
        final com.squareup.moshi.AmplCircularAdaptersTest.Node right;

        Node(java.lang.String name, com.squareup.moshi.AmplCircularAdaptersTest.Node left, com.squareup.moshi.AmplCircularAdaptersTest.Node right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        com.squareup.moshi.AmplCircularAdaptersTest.Node plusPrefix(java.lang.String prefix) {
            return new com.squareup.moshi.AmplCircularAdaptersTest.Node((prefix + (name)), left, right);
        }

        com.squareup.moshi.AmplCircularAdaptersTest.Node minusPrefix(java.lang.String prefix) {
            if (!(name.startsWith(prefix)))
                throw new java.lang.IllegalArgumentException();
            
            return new com.squareup.moshi.AmplCircularAdaptersTest.Node(name.substring(prefix.length()), left, right);
        }
    }

    /**
     * This factory uses extensive delegation. Each node delegates to this for the left and right
     * subtrees, and those delegate to the built-in class adapter to do most of the serialization
     * work.
     */
    static class PrefixingNodeFactory implements com.squareup.moshi.JsonAdapter.Factory {
        @java.lang.Override
        public com.squareup.moshi.JsonAdapter<?> create(java.lang.reflect.Type type, java.util.Set<? extends java.lang.annotation.Annotation> annotations, com.squareup.moshi.Moshi moshi) {
            if (type != (com.squareup.moshi.AmplCircularAdaptersTest.Node.class))
                return null;
            
            final java.lang.String prefix;
            if (com.squareup.moshi.Util.isAnnotationPresent(annotations, com.squareup.moshi.AmplCircularAdaptersTest.Left.class)) {
                prefix = "L ";
            }else
                if (com.squareup.moshi.Util.isAnnotationPresent(annotations, com.squareup.moshi.AmplCircularAdaptersTest.Right.class)) {
                    prefix = "R ";
                }else {
                    return null;
                }
            
            final com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplCircularAdaptersTest.Node> delegate = moshi.nextAdapter(this, com.squareup.moshi.AmplCircularAdaptersTest.Node.class, com.squareup.moshi.Util.NO_ANNOTATIONS);
            return new com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplCircularAdaptersTest.Node>() {
                @java.lang.Override
                public void toJson(com.squareup.moshi.JsonWriter writer, com.squareup.moshi.AmplCircularAdaptersTest.Node value) throws java.io.IOException {
                    delegate.toJson(writer, value.plusPrefix(prefix));
                }

                @java.lang.Override
                public com.squareup.moshi.AmplCircularAdaptersTest.Node fromJson(com.squareup.moshi.JsonReader reader) throws java.io.IOException {
                    com.squareup.moshi.AmplCircularAdaptersTest.Node result = delegate.fromJson(reader);
                    return result.minusPrefix(prefix);
                }
            }.nullSafe();
        }
    }

    @org.junit.Test
    public void circularAdaptersAndAnnotations() throws java.lang.Exception {
        com.squareup.moshi.Moshi moshi = new com.squareup.moshi.Moshi.Builder().add(new com.squareup.moshi.AmplCircularAdaptersTest.PrefixingNodeFactory()).build();
        com.squareup.moshi.JsonAdapter<com.squareup.moshi.AmplCircularAdaptersTest.Node> nodeAdapter = moshi.adapter(com.squareup.moshi.AmplCircularAdaptersTest.Node.class);
        com.squareup.moshi.AmplCircularAdaptersTest.Node tree = new com.squareup.moshi.AmplCircularAdaptersTest.Node("C", new com.squareup.moshi.AmplCircularAdaptersTest.Node("A", null, new com.squareup.moshi.AmplCircularAdaptersTest.Node("B", null, null)), new com.squareup.moshi.AmplCircularAdaptersTest.Node("D", null, new com.squareup.moshi.AmplCircularAdaptersTest.Node("E", null, null)));
        org.assertj.core.api.Assertions.assertThat(nodeAdapter.toJson(tree)).isEqualTo(("{" + ((("\"left\":{\"name\":\"L A\",\"right\":{\"name\":\"R B\"}}," + "\"name\":\"C\",") + "\"right\":{\"name\":\"R D\",\"right\":{\"name\":\"R E\"}}") + "}")));
        com.squareup.moshi.AmplCircularAdaptersTest.Node fromJson = nodeAdapter.fromJson(("{" + ((("\"left\":{\"name\":\"L A\",\"right\":{\"name\":\"R B\"}}," + "\"name\":\"C\",") + "\"right\":{\"name\":\"R D\",\"right\":{\"name\":\"R E\"}}") + "}")));
        org.assertj.core.api.Assertions.assertThat(fromJson.name).isEqualTo("C");
        org.assertj.core.api.Assertions.assertThat(fromJson.left.name).isEqualTo("A");
        org.assertj.core.api.Assertions.assertThat(fromJson.left.right.name).isEqualTo("B");
        org.assertj.core.api.Assertions.assertThat(fromJson.right.name).isEqualTo("D");
        org.assertj.core.api.Assertions.assertThat(fromJson.right.right.name).isEqualTo("E");
    }
}

