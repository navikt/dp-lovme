package no.nav.dagpenger.lovme.helpers

import no.nav.dagpenger.lovme.kafka.Topic
import no.nav.helse.rapids_rivers.testsupport.TestRapid

class TestTopic : Topic<String, String> {
    private val messages = mutableListOf<Pair<String?, String>>()
    val inspektør get() = TestRapid.RapidInspector(messages.toList())

    override fun publiser(melding: String) {
        messages.add(null to melding)
    }

    override fun publiser(nøkkel: String, melding: String) {
        messages.add(nøkkel to melding)
    }
}
