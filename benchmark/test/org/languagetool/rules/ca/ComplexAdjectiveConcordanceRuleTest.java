/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Jaume Ortol?
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.ca;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class ComplexAdjectiveConcordanceRuleTest {
    private ComplexAdjectiveConcordanceRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        // de l'altra m?s neguit?s
        // per primera vegada documentat
        // en alguns casos documentat
        // en tot cas poc honesta
        // amb la mirada de cadascun dels homes clavada en la de l'adversari
        // de fer una torre o fortificaci? bo i al?legant que aix?
        // a confondre en un mateix amor amics i enemics
        // es van posar en cam? prove?ts de presents
        /* d'una banda tossut i, de l'altra, del tot inepte
        principis mascle i femella de la foscor//els elements reproductors mascle
        i femella// les formigues mascle i femella
         */
        /* multiwords: en aparen?a, en ess?ncia,per ess?ncia, amb exc?s,en rep?s,
        amb rapidesa, en algun grau, per molt de temps altres vegades estacionat,
        en molts casos subordinada?, era al principi instintiva, de moment
        imperfectament conegudes de llarg menys perfectes, ?s de totes passades
        exactament interm?dia, ?s, en conjunt, gaireb? interm?dia en cert grau
        paral?lela en algun grau en grau lleuger menys distintes han estat de fet
        exterminades
         */
        // (en especial si hi ha un adverbi entremig: en algun grau m?s distintes
        // assertCorrect("Es van somriure l'una a l'altra encara dretes, suades i panteixants,");
        // assertCorrect("una combinaci? de dos o m?s metalls obtinguda generalment");
        assertCorrect("La ra? sol allunyar-se dels extrems");
        assertCorrect("L'URL introdu?t");
        assertCorrect("Som els m?s antisistema");
        assertCorrect("En un entorn de prova segur");
        assertCorrect("Amb un termini d'execuci? de nou mesos aproximadament.");
        assertCorrect("les causes per primera vegada explicades");
        assertCorrect("per les causes explicades fa molt dif?cil");
        assertCorrect("a Fran?a mateix");
        assertCorrect("tenen moltes m?s dificultats");
        assertCorrect("l'endeutament que gener? fou for?a elevat");
        assertCorrect("el text de m?s ?mplia i persistent influ?ncia");
        assertCorrect("el text de m?s ?mplia influ?ncia");
        assertCorrect("Ell i jo som una altra vegada partidaris del rei");
        assertCorrect("despres de la revolta contra el poder pontifici iniciada a Bolonya");
        assertCorrect("-Aix?, ?viatges sola? -va dir");
        assertCorrect("El riu passa engorjat en aquest sector ");
        assertCorrect("i ronda amagat pels carrers");
        assertCorrect("Setmanari il?lustrat d'art, literatura i actualitats fundat a Barcelona");
        assertCorrect("Entitat oficial de cr?dit a mitj? i llarg termini constitu?da l'any 1920");
        assertCorrect("edificacions superposades d'?poca romana republicanes i imperials");
        assertCorrect("Fou un dels primers barris de barraques sorgit despr?s del 1939");
        assertCorrect("i el premi a la investigaci? m?dica atorgat per la Funadaci?");
        assertCorrect("no arriben als 300 mm de pluja anuals");
        assertCorrect("un dibuix de colors vius d'un noi ben plantat i una noia preciosa drets");
        assertCorrect("de la captura i l'assassinat recents");
        assertCorrect("la captura i l'assassinat recents");// desambiguar "la captura"

        assertCorrect("era la defensa i seguretat m?tues");
        assertCorrect("la revolta contra el poder pontifici iniciada a Bolonya");// desambiguar "la revolta"

        assertCorrect("donar estocades sense ordre ni concert mal dirigides");
        assertCorrect("trobarien un dels nanos mort de fred");
        assertCorrect("aquest text, el m?s antic de l'obra fins ara conegut");
        assertCorrect("va reaccionar, la molt astuta, aix?");
        assertCorrect("S?n de barba i cabellera blanques");
        assertCorrect("per a dur a terme tota la lectura i escriptura requerides");
        assertCorrect("al cam? va trobar una branca de roure s?lida");
        assertCorrect("Tesis doctorals");
        assertCorrect("Va veure una cara rosada i arrugada, una boca sense dents oberta");
        assertCorrect("la vista en el magn?fic ocell de potes i bec vermells");
        assertCorrect("-Male?t ximple! -va exclamar Tom");
        assertCorrect("amb alguns motllurats de guixeria classicitzants");
        assertCorrect("amb alguns motllurats de guixeria classicitzant");
        assertCorrect("amb alguns motllurats de guixeria retallats");
        assertCorrect("amb alguns motllurats de guixeria retallada");
        assertCorrect("a confondre en un mateix amor amics i enemics");
        assertCorrect("En l'eix esquerra-dreta.");
        assertCorrect("podrien tamb? esdevenir correlacionades");
        assertCorrect("Cada polinomi en forma expandida");
        assertCorrect("El 1967 una partida de liberals rebel al govern");
        assertCorrect("El 1640 una junta de nobles reunida a Lisboa");
        assertCorrect("amb una expressi? de dolor i de por barrejats.");
        assertCorrect("un tram m?s tou, amb morfologia i color diferents.");
        assertCorrect("Especialment en mat?ria de policia i finan?ament auton?mics");
        assertCorrect("Especialment en mat?ria de policia i just?cia auton?miques");
        assertCorrect("l'obra de Boeci amb espontane?tat i vigor notables");
        assertCorrect("tenen en canvi altres parts de llur estructura certament molt anormals:");
        assertCorrect("constitueix l'?nica comunitat aut?noma amb menys aturats");
        assertCorrect("durant tot l'any, i del sud-est, m?s notoris a la primavera");
        assertCorrect("amb la veu i el posat cada cop m?s agressius");
        assertCorrect("l'experi?ncia sensitiva i la ra?, degudament combinades.");
        assertCorrect("a la infermeria, d'all? m?s interessat");
        assertCorrect("el record, i absolutament fascinats");
        assertCorrect("no s'atorguen drets de visita tret que ho consenta el progenitor");
        assertCorrect("La meua filla viu amb mi la major part del temps");
        assertCorrect("que en l'actualitat viu a la ciutat de Santa Cruz");
        assertCorrect("s?n submarines i la nostra gent viu al fons del mar.");
        assertCorrect("la meitat mascles i la meitat femelles");
        assertCorrect("?s for?a amarg");
        assertCorrect("Era poder?s, for?a estrabul?lat");
        assertCorrect("S?n for?a desconegudes");
        assertCorrect("Zeus, for?a cansat de tot");
        assertCorrect("un car?cter fix, per molt extraordin?ria que sigui la manera");
        assertCorrect("una quantitat copiosa de llavors olioses");
        assertCorrect("que cri? sense variaci?, per molt lleugers que fossin");
        assertCorrect("Bernab? i Saule, un cop acomplerta la seva missi? a Jerusalem");
        assertCorrect("Bernab? i Saule, un colp acomplerta la seva missi? a Jerusalem");
        assertIncorrect("Bernab? i Saule, el colp acomplerta la seva missi? a Jerusalem");
        assertCorrect("Bernab? i Saule, una vegada acomplert el seu viatge a Jerusalem");
        assertCorrect("Bernab? i Saule, una volta acomplert el seu viatge a Jerusalem");
        assertCorrect("he passat una nit i un dia sencers a la deriva");
        assertCorrect("L'olor dels teus perfums, m?s agradable que tots els b?lsams.");
        assertCorrect("La part superior esquerra");
        assertCorrect("I s?, la crisi ser? llarga, molt llarga, potser eterna.");
        assertCorrect("El rei ha trobat l'excusa i l'explicaci? adequada.");
        // assertCorrect("t? una manera de jugar aquestes gires tan femenina");
        assertCorrect("des de la tradicional divisi? en dos regnes establida per Linnaeus");
        assertCorrect("aquestes activitats avui residuals donada ja la manca de territori");
        assertCorrect("instruments de c?lcul basats en boles anomenats yupana.");
        assertCorrect("El rei ha trobat l'excusa i l'explicaci? adequades.");
        assertCorrect("Copa del m?n femenina.");
        assertCorrect("Batalla entre asteques i espanyols coneguda com la Nit Trista.");
        assertCorrect("?s un informe sobre la cultura japonesa realitzat per enc?rrec de l'ex?rcit d'Estats Units.");
        assertCorrect("Les perspectives de futur immediat.");
        assertCorrect("Les perspectives de futur immediates.");
        assertCorrect("la t?cnica i l'art cinematogr?fiques.");
        assertCorrect("la t?cnica i l'art cinematogr?fic.");
        assertCorrect("la t?cnica i l'art cinematogr?fics.");
        assertCorrect("la t?cnica i l'art cinematogr?fica.");
        assertCorrect("Les perspectives i el futur immediats.");
        assertCorrect("Un punt de densitat i gravetat infinites.");
        assertCorrect("De la literatura i la cultura catalanes.");
        assertCorrect("Es fa segons regles de lectura constants i regulars.");
        assertCorrect("Les meitats dreta i esquerra de la mand?bula.");
        assertCorrect("Els per?odes cl?ssic i medieval.");
        // assertCorrect("Els costats superior i laterals.");
        assertCorrect("En una mol?cula de glucosa i una de fructosa unides.");
        // Should be Incorrect, but it is impossible to detect
        assertCorrect("?ndex de desenvolupament hum? i qualitat de vida elevat");
        assertCorrect("?ndex de desenvolupament hum? i qualitat de vida elevats");
        assertCorrect("?ndex de desenvolupament hum? i qualitat de vida elevada");
        assertCorrect("La massa, el radi i la lluminositat llistats per ell.");
        assertCorrect("La massa, el radi i la lluminositat llistada per ell.");
        assertCorrect("L'origen de l'?bac est? literalment perdut en el temps.");
        assertCorrect("L'origen ha esdevingut literalment perdut en el temps.");
        assertCorrect("En efecte, hi ha consideracions racistes, llavors for?a comunes");
        assertCorrect("el personatge canvi? f?sicament: m?s alt i prim que el seu germ?");
        assertCorrect("un a baix i un altre a dalt identificat amb el s?mbol");
        assertCorrect("un a baix i un altre a dalt identificats amb el s?mbol");
        assertCorrect("El tabaquisme ?s l'addicci? al tabac provocada per components.");
        assertCorrect("El \"treball\" en q\u00fcesti\u00f3, normalment associat a un lloc de treball pagat");
        assertCorrect("una organitzaci? paramilitar de protecci? civil t?picament catalana");
        assertCorrect("un Do dues octaves m?s alt que l'anterior");
        assertCorrect("s?n pr?cticament dos graus m?s baixes");
        assertCorrect("?s unes vint vegades m?s gran que l'espermatozou.");
        assertCorrect("?s unes 20 vegades m?s gran que l'espermatozou.");
        assertCorrect("eren quatre vegades m?s alts");
        assertCorrect("eren uns fets cada volta m?s inexplicables");
        assertCorrect("El castell est? totalment en ru?nes i completament cobert de vegetaci?.");
        assertCorrect("han estat tant elogiades per la cr?tica teatral, com pol?miques");
        assertCorrect("Del segle XVIII per? reconstru?da recentment");
        // assertCorrect("vivien a la casa paterna, mig confosos entre els criats.");
        assertCorrect("La ind?stria, tradicionalment dedicada al t?xtil i ara molt diversificada,");
        assertCorrect("oficialment la comarca[2] del Moian?s, molt reivindicada");
        assertCorrect("En l'actualitat est? del tot despoblada de resid?ncia permanent.");
        assertCorrect("amb la terra repartida entre diversos propietaris, b? que encara poc poblada");
        assertCorrect("al capdamunt de les Costes d'en Quintanes, sota mateix del Tur?");
        assertCorrect("el Moviment per l?Autodeterminaci? cors");
        assertCorrect("amb una taula de logaritmes davant meu.");
        assertCorrect("la denominaci? valenci? per a la llengua pr?pia");
        assertCorrect("Com m?s petita ?s l'obertura de diafragma, m?s grans s?n la profunditat de camp i la profunditat");
        assertCorrect("es movien mitjan?ant filferros, tot projectant ombres");
        assertCorrect("sota les grans persianes de color verd recalcades");
        assertCorrect("que seria en pocs anys for?a hegem?nica a Catalunya");
        assertCorrect("Era un home for?a misteri?s");
        // errors:
        assertIncorrect("Fran?a mateix ho necessita.");
        assertIncorrect("recull de llegendes i can?ons populars en part inventats per ell");
        assertIncorrect("amb dos conjunts territorial diferents entre si");
        assertIncorrect("per mitj? de g?metes haploides obtingudes per meiosi");
        assertIncorrect("?s tan ple d'urg?ncies, tan ple de desitj?s materials");
        assertIncorrect("Tesis doctoral");
        assertIncorrect("vaig posar mans a l'obra: a dins de casa mateix vaig cavar un sot per enterrar");
        assertIncorrect("amb alguns motllurats de guixeria retallat");
        assertIncorrect("amb alguns motllurats de guixeria retallades");
        assertIncorrect("Aquella va ser la seva pe?a mestre.");
        assertIncorrect("La petici? de tramitar el cas per lesions dolosa.");
        // policia i just?cia s?n m?s usualment femenins, encara que poden ser masculins
        assertIncorrect("Especialment en mat?ria de policia i just?cia auton?mics");
        assertIncorrect("amb rigor i honor barrejades.");
        assertIncorrect("hi ha hagut una certa recuperaci? (3,2%), efecte en part de la descongesti? madrilenya cap a les prov?ncies lim?trofs de Toledo i Guadalajara.");
        assertIncorrect("Son molt boniques");
        // assertIncorrect("La casa destrossat"); ambigu
        assertIncorrect("pantalons curt o llargs");
        assertIncorrect("sota les grans persianes de color verd recalcada");
        assertIncorrect("sota les grans persianes de color verd recalcat");
        assertIncorrect("sota les grans persianes de color verd recalcats");
        assertIncorrect("S?n unes corbes de llum complexos.");
        assertIncorrect("fets moltes vegades inexplicable.");
        assertIncorrect("eren uns fets cada volta m?s inexplicable");
        assertIncorrect("Unes explotacions ramaderes porcina.");
        // assertIncorrect("amb un rendiment del 5,62%, m?s alta que el 5,44%");
        // assertIncorrect("un a baix i un altre a dalt identificada amb el s?mbol");
        // assertIncorrect("un a baix i un altre a dalt identificades amb el s?mbol");
        // assertIncorrect("En efecte, hi ha consideracions, llavors for?a comuns");
        assertIncorrect("En efecte, hi ha consideracions llavors for?a comuns");
        // assertIncorrect("En efecte, hi ha consideracions racistes, llavors for?a comuns");
        assertIncorrect("amb una alineaci? impr?piament habituals");
        assertIncorrect("amb una alineaci? poc habituals");
        assertIncorrect("amb una alineaci? molt poc habituals");
        // assertIncorrect("Era un home for?a misteriosa"); -> permet
        // "en pocs anys for?a hegem?nica"
        assertIncorrect("Era un home for?a misteriosos");
        assertIncorrect("El rei ha trobat l'excusa perfecte.");
        assertIncorrect("El rei ha trobat l'excusa i l'explicaci? adequats.");
        assertIncorrect("El rei ha trobat l'excusa i l'explicaci? adequat.");
        assertIncorrect("Les perspectives de futur immediata.");
        assertIncorrect("Les perspectives de futur immediats.");
        assertIncorrect("la llengua i la cultura catalans.");
        assertIncorrect("En una mol?cula de glucosa i una de fructosa units.");
        assertIncorrect("Un punt de densitat i gravetat infinits.");
        assertIncorrect("?ndex de desenvolupament hum? i qualitat de vida elevades.");
        // Should be Incorrect, but it is impossible to detect
        // assertIncorrect("?ndex de desenvolupament hum? i qualitat de vida elevat");
        assertIncorrect("La massa, el radi i la lluminositat llistat per ell.");
        assertIncorrect("La massa, el radi i la lluminositat llistades per ell.");
    }
}

