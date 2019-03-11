/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.documentation.generator;


import Generator.EMULATED_SWAGGER_VERSION;
import ServerVersion.VERSION;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.graylog2.shared.rest.documentation.generator.Generator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class GeneratorTest {
    static ObjectMapper objectMapper;

    @Test
    public void testGenerateOverview() throws Exception {
        Generator generator = new Generator("org.graylog2.rest.resources", GeneratorTest.objectMapper);
        Map<String, Object> result = generator.generateOverview();
        Assert.assertEquals(VERSION.toString(), result.get("apiVersion"));
        Assert.assertEquals(EMULATED_SWAGGER_VERSION, result.get("swaggerVersion"));
        Assert.assertNotNull(result.get("apis"));
        Assert.assertTrue(((((List) (result.get("apis"))).size()) > 0));
    }

    @Test
    public void testGenerateForRoute() throws Exception {
        Generator generator = new Generator("org.graylog2.rest.resources", GeneratorTest.objectMapper);
        Map<String, Object> result = generator.generateForRoute("/system", "http://localhost:12900/");
    }
}

