/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.gson2;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.json.AbstractJsonMapperTest;
import org.jdbi.v3.postgres.PostgresDbRule;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Rule;
import org.junit.Test;


public class TestGson2Plugin extends AbstractJsonMapperTest {
    @Rule
    public JdbiRule db = PostgresDbRule.rule();

    @Test
    public void typeCanBeOverridden() {
        db.getJdbi().useHandle(( h) -> {
            h.createUpdate("create table users(usr json)").execute();
            Gson gson = new GsonBuilder().registerTypeAdapter(.class, new org.jdbi.v3.gson2.SuperUserAdapter()).registerTypeAdapter(.class, new org.jdbi.v3.gson2.SubUserAdapter()).create();
            h.getConfig(.class).setGson(gson);
            // declare that the subuser should be mapped as a superuser
            h.createUpdate("insert into users(usr) values(:user)").bindByType("user", new org.jdbi.v3.gson2.SubUser(), QualifiedType.of(.class).with(.class)).execute();
            org.jdbi.v3.gson2.User subuser = h.createQuery("select usr from users").mapTo(QualifiedType.of(.class).with(.class)).findOnly();
            assertThat(subuser.name).describedAs("instead of being bound via getClass(), the object was bound according to the type param").isEqualTo("super");
        });
    }

    public static class User {
        private final String name;

        public User(String name) {
            this.name = name;
        }
    }

    private static class SuperUser {}

    private static class SubUser extends TestGson2Plugin.SuperUser {}

    private static class SuperUserAdapter extends TypeAdapter<TestGson2Plugin.SuperUser> {
        @Override
        public void write(JsonWriter out, TestGson2Plugin.SuperUser user) throws IOException {
            out.beginObject().name("name").value("super").endObject();
        }

        @Override
        public TestGson2Plugin.SuperUser read(JsonReader in) {
            throw new UnsupportedOperationException();
        }
    }

    private static class SubUserAdapter extends TypeAdapter<TestGson2Plugin.SubUser> {
        @Override
        public void write(JsonWriter out, TestGson2Plugin.SubUser user) throws IOException {
            out.beginObject().name("name").value("sub").endObject();
        }

        @Override
        public TestGson2Plugin.SubUser read(JsonReader in) {
            throw new UnsupportedOperationException();
        }
    }
}

