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
package org.jdbi.v3.sqlobject;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Rule;
import org.junit.Test;


public class TestRegisteredGenericReturnAndParam {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    @Test
    public void testRegisterGenericRowMapperAnnotationWorks() {
        testFoodToppingRestrictions(new TestRegisteredGenericReturnAndParam.GyroProvider(), 1);
        testFoodToppingRestrictions(new TestRegisteredGenericReturnAndParam.BurritoProvider(), 2);
    }

    public interface FoodProvider<T, R, DAO extends TestRegisteredGenericReturnAndParam.Food<T, R>> {
        Class<DAO> getDao();

        T getTopping();

        R getRestriction();
    }

    public class GyroProvider implements TestRegisteredGenericReturnAndParam.FoodProvider<String, String, TestRegisteredGenericReturnAndParam.Gyro> {
        @Override
        public Class<TestRegisteredGenericReturnAndParam.Gyro> getDao() {
            return TestRegisteredGenericReturnAndParam.Gyro.class;
        }

        @Override
        public String getTopping() {
            return "yogurt";
        }

        @Override
        public String getRestriction() {
            return "vegetarian";
        }
    }

    public class BurritoProvider implements TestRegisteredGenericReturnAndParam.FoodProvider<String, Integer, TestRegisteredGenericReturnAndParam.Burrito> {
        @Override
        public Class<TestRegisteredGenericReturnAndParam.Burrito> getDao() {
            return TestRegisteredGenericReturnAndParam.Burrito.class;
        }

        @Override
        public String getTopping() {
            return "hot sauce";
        }

        @Override
        public Integer getRestriction() {
            return 3;
        }
    }

    @RegisterRowMapper(TestRegisteredGenericReturnAndParam.StringToppingMapper.class)
    public interface Gyro extends TestRegisteredGenericReturnAndParam.Food<String, String> {
        @SqlQuery("select id, name from something where id = :id and char_length(:str) > 5")
        @Override
        List<TestRegisteredGenericReturnAndParam.Topping<String>> getToppings(@Bind("id")
        int id, @Bind("str")
        String restrictions);
    }

    @RegisterRowMapper(TestRegisteredGenericReturnAndParam.StringToppingMapper.class)
    public interface Burrito extends TestRegisteredGenericReturnAndParam.Food<String, Integer> {
        @SqlQuery("select id, name from something where id = :id and :int + 1 > 0")
        @Override
        List<TestRegisteredGenericReturnAndParam.Topping<String>> getToppings(@Bind("id")
        int id, @Bind("int")
        Integer restrictions);
    }

    public interface Food<T, R> {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insertTopping(@Bind("id")
        int id, @Bind("name")
        T name);

        List<TestRegisteredGenericReturnAndParam.Topping<T>> getToppings(int id, R restrictions);
    }

    public static class Topping<T> {
        public T value;

        public Topping(T value) {
            this.value = value;
        }
    }

    public static class StringToppingMapper implements RowMapper<TestRegisteredGenericReturnAndParam.Topping<String>> {
        @Override
        public TestRegisteredGenericReturnAndParam.Topping<String> map(ResultSet r, StatementContext ctx) throws SQLException {
            return new TestRegisteredGenericReturnAndParam.Topping<>(r.getString("name"));
        }
    }
}

