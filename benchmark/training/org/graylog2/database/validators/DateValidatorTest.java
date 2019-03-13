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
package org.graylog2.database.validators;


import java.util.Date;
import org.graylog2.plugin.database.validators.Validator;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class DateValidatorTest {
    @Test
    public void testValidate() throws Exception {
        Validator v = new DateValidator();
        Assert.assertFalse(v.validate(null).passed());
        Assert.assertFalse(v.validate(9001).passed());
        Assert.assertFalse(v.validate("").passed());
        Assert.assertFalse(v.validate(new Date()).passed());
        // Only joda datetime.
        Assert.assertTrue(v.validate(new org.joda.time.DateTime(DateTimeZone.UTC)).passed());
        // Only accepts UTC.
        Assert.assertFalse(v.validate(new org.joda.time.DateTime(DateTimeZone.forID("+09:00"))).passed());
    }
}

