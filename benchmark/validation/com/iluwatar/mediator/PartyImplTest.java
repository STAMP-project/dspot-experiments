/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.mediator;


import Action.GOLD;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Date: 12/19/15 - 10:00 PM
 *
 * @author Jeroen Meulemeester
 */
public class PartyImplTest {
    /**
     * Verify if a member is notified when it's joining a party. Generate an action and see if the
     * other member gets it. Also check members don't get their own actions.
     */
    @Test
    public void testPartyAction() {
        final PartyMember partyMember1 = Mockito.mock(PartyMember.class);
        final PartyMember partyMember2 = Mockito.mock(PartyMember.class);
        final PartyImpl party = new PartyImpl();
        party.addMember(partyMember1);
        party.addMember(partyMember2);
        Mockito.verify(partyMember1).joinedParty(party);
        Mockito.verify(partyMember2).joinedParty(party);
        party.act(partyMember1, GOLD);
        Mockito.verifyZeroInteractions(partyMember1);
        Mockito.verify(partyMember2).partyAction(GOLD);
        Mockito.verifyNoMoreInteractions(partyMember1, partyMember2);
    }
}

