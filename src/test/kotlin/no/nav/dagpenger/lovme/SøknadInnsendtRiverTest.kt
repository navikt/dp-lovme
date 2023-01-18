package no.nav.dagpenger.lovme

import no.nav.dagpenger.lovme.helpers.TestTopic
import no.nav.dagpenger.lovme.tjenester.SøknadInnsendtRiver
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SøknadInnsendtRiverTest {
    private val topic = TestTopic()
    private val rapid by lazy {
        TestRapid().apply {
            SøknadInnsendtRiver(this, topic)
        }
    }

    @AfterEach
    fun cleanUp() {
        rapid.reset()
    }

    @Test
    fun `skal publisere opprettet`() {
        rapid.sendTestMessage(tilstandEndret("Innsendt", "Dagpenger").toJson())
        rapid.sendTestMessage(tilstandEndret("Innsendt", "Innsending").toJson())

        with(topic.inspektør) {
            assertEquals(1, size)
            assertEquals("søknad_mottatt", field(0, "@event_name").asText())
            assertTrue(message(0).has("ident"))
            assertTrue(message(0).has("siste_dag_i_arbeid"))
        }
    }
}

private fun tilstandEndret(tilstand: String, prosessnavn: String) = JsonMessage.newMessage(
    "søknad_endret_tilstand",
    listOfNotNull(
        "ident" to "12312312312",
        "søknad_uuid" to UUID.randomUUID(),
        "forrigeTilstand" to "Opprettet",
        "gjeldendeTilstand" to tilstand,
        "prosessnavn" to prosessnavn
    ).toMap()
)
