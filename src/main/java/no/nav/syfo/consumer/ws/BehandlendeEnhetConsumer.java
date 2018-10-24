package no.nav.syfo.consumer.ws;

import lombok.extern.slf4j.Slf4j;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSArbeidsfordelingKriterier;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSGeografi;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSTema;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Optional;

import static no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSEnhetsstatus.AKTIV;

@Component
@Slf4j
public class BehandlendeEnhetConsumer {

    private final PersonV3 personV3;
    private final ArbeidsfordelingV1 arbeidsfordelingV1;

    @Inject
    public BehandlendeEnhetConsumer(PersonV3 personV3, ArbeidsfordelingV1 arbeidsfordelingV1) {
        this.personV3 = personV3;
        this.arbeidsfordelingV1 = arbeidsfordelingV1;
    }

    public String hentBehandlendeEnhet(String fnr) {
        String geografiskTilknytning = hentGeografiskTilknytning(fnr);

        try {
            return arbeidsfordelingV1.finnBehandlendeEnhetListe(new WSFinnBehandlendeEnhetListeRequest()
                    .withArbeidsfordelingKriterier(new WSArbeidsfordelingKriterier()
                            .withGeografiskTilknytning(new WSGeografi().withValue(geografiskTilknytning))
                            .withTema(new WSTema().withValue("SYK"))))
                    .getBehandlendeEnhetListe()
                    .stream()
                    .filter(wsOrganisasjonsenhet -> AKTIV.equals(wsOrganisasjonsenhet.getStatus()))
                    .map(WSOrganisasjonsenhet::getEnhetId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Fant ingen aktiv enhet for " + geografiskTilknytning));
        } catch (FinnBehandlendeEnhetListeUgyldigInput e) {
            log.error("Feil ved henting av brukers forvaltningsenhet", e);
            throw new RuntimeException("Feil ved henting av brukers forvaltningsenhet", e);
        } catch (RuntimeException e) {
            log.error("Klarte ikke å hente behandlende enhet!", e);
            throw new RuntimeException(e);
        }
    }

    public String hentGeografiskTilknytning(String fnr) {
        try {
            return Optional.of(personV3.hentGeografiskTilknytning(
                    new HentGeografiskTilknytningRequest()
                            .withAktoer(new PersonIdent().withIdent(new NorskIdent().withIdent(fnr)))))
                    .map(HentGeografiskTilknytningResponse::getGeografiskTilknytning)
                    .map(GeografiskTilknytning::getGeografiskTilknytning)
                    .orElseThrow(() -> new RuntimeException("Kunne ikke hente geografisk tilknytning"));
        } catch (HentGeografiskTilknytningSikkerhetsbegrensing | HentGeografiskTilknytningPersonIkkeFunnet e) {
            log.error("Feil ved henting av geografisk tilknytning", e);
            throw new RuntimeException("Feil ved henting av geografisk tilknytning", e);
        }
    }

}
