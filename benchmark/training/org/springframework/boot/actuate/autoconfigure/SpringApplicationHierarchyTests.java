/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure;


import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Test for application hierarchies created using {@link SpringApplicationBuilder}.
 *
 * @author Dave Syer
 */
public class SpringApplicationHierarchyTests {
    private ConfigurableApplicationContext context;

    @Test
    public void testParent() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringApplicationHierarchyTests.Child.class);
        builder.parent(SpringApplicationHierarchyTests.Parent.class);
        this.context = builder.run("--server.port=0", "--management.metrics.use-global-registry=false");
    }

    @Test
    public void testChild() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SpringApplicationHierarchyTests.Parent.class);
        builder.child(SpringApplicationHierarchyTests.Child.class);
        this.context = builder.run("--server.port=0", "--management.metrics.use-global-registry=false");
    }

    @EnableAutoConfiguration(exclude = { ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class, CassandraAutoConfiguration.class, CassandraDataAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, Neo4jDataAutoConfiguration.class, Neo4jRepositoriesAutoConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, FlywayAutoConfiguration.class, JestAutoConfiguration.class, MetricsAutoConfiguration.class }, excludeName = { "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration" })
    public static class Child {}

    @EnableAutoConfiguration(exclude = { ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class, CassandraAutoConfiguration.class, CassandraDataAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, Neo4jDataAutoConfiguration.class, Neo4jRepositoriesAutoConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, FlywayAutoConfiguration.class, JestAutoConfiguration.class, MetricsAutoConfiguration.class }, excludeName = { "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration" })
    public static class Parent {}
}

