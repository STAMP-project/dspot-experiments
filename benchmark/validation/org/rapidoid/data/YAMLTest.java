package org.rapidoid.data;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.test.TestCommons;
import org.rapidoid.test.TestIO;
import org.rapidoid.u.U;


/**
 *
 *
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class YAMLTest extends TestCommons {
    private final TypeReference<List<User>> personList = new TypeReference<List<User>>() {};

    @Test
    public void parseMap() {
        String yaml = new String(TestIO.loadRes("test.yaml"));
        Map<String, Object> data = YAML.parseMap(yaml);
        eq(data, U.map("aa", 1, "bb", "2am", "cc", U.map("x", true, "z", false)));
    }

    @Test
    public void parseBeans() {
        String yaml = new String(TestIO.loadRes("persons.yaml"));
        List<User> persons = YAML.parse(yaml, personList);
        eq(persons.size(), 2);
        User p1 = persons.get(0);
        eq(p1.id, 123);
        eq(p1.name, "John Doe");
        eq(p1.age, 50);
        User p2 = persons.get(1);
        eq(p2.name, "Highlander");
        eq(p2.age, 900);
    }
}

