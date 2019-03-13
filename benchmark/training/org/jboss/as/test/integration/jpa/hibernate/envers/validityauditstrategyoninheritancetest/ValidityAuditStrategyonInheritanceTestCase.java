/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jpa.hibernate.envers.validityauditstrategyoninheritancetest;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.jpa.hibernate.envers.SLSBAuditInheritance;
import org.jboss.as.test.integration.jpa.hibernate.envers.SoccerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test Auditing on inherited attributes using Validity Audit strategy
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class ValidityAuditStrategyonInheritanceTestCase {
    private static final String ARCHIVE_NAME = "jpa_TestValidityAuditStrategyonInheritance";

    @ArquillianResource
    private static InitialContext iniCtx;

    /* Ensure that auditing works for inherited attributes */
    @Test
    public void testValidityStrategyonInheritance() throws Exception {
        SLSBAuditInheritance slsb = ValidityAuditStrategyonInheritanceTestCase.lookup("SLSBAuditInheritance", SLSBAuditInheritance.class);
        SoccerPlayer socplayer = slsb.createSoccerPlayer("LEONARDO", "MESSI", "SOCCER", "REAL MADRID");
        socplayer.setFirstName("Christiano");
        socplayer.setLastName("Ronaldo");
        socplayer.setGame("FOOTBALL");
        // update Player
        socplayer = slsb.updateSoccerPlayer(socplayer);
        SoccerPlayer val = slsb.retrieveSoccerPlayerbyId(socplayer.getId());
        Assert.assertNotNull(val);
        Assert.assertEquals("LEONARDO", val.getFirstName());
        Assert.assertEquals("MESSI", val.getLastName());
        Assert.assertNull(val.getGame());
    }
}

