/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.fayder.restcountries;


import eu.fayder.restcountries.domain.BaseCountry;
import java.util.List;
import org.junit.Test;


public class EmptyDataTest {
    List<BaseCountry> countries;

    @Test
    public void emptyBorders() throws Exception {
        System.out.println("- Empty Borders");
        for (BaseCountry c : countries) {
            if (((c.getBorders()) == null) || (c.getBorders().isEmpty())) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyAreas() throws Exception {
        System.out.println("- Empty Areas");
        for (BaseCountry c : countries) {
            if ((c.getArea()) == null) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyGini() throws Exception {
        System.out.println("- Empty Gini");
        for (BaseCountry c : countries) {
            if ((c.getGini()) == null) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyNumericCode() throws Exception {
        System.out.println("- Empty Numeric Code");
        for (BaseCountry c : countries) {
            if ((c.getNumericCode()) == null) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyPopulation() throws Exception {
        System.out.println("- Empty Population");
        for (BaseCountry c : countries) {
            if ((c.getPopulation()) == 0) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyRegion() throws Exception {
        System.out.println("- Empty Region");
        for (BaseCountry c : countries) {
            if (((c.getRegion()) == null) || (c.getRegion().isEmpty())) {
                System.out.println(c.getName());
            }
        }
    }

    @Test
    public void emptyTimezones() throws Exception {
        System.out.println("- Empty Timezones");
        for (BaseCountry c : countries) {
            if (((c.getTimezones()) == null) || (c.getTimezones().isEmpty())) {
                System.out.println(c.getName());
            } else {
                for (String timezone : c.getTimezones()) {
                    if (!(timezone.contains("UTC"))) {
                        System.out.println(c.getName());
                    }
                }
            }
        }
    }
}

