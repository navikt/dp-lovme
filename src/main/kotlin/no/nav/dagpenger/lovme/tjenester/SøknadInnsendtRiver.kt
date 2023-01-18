package no.nav.dagpenger.lovme.tjenester

import com.fasterxml.jackson.databind.JsonNode
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.lovme.kafka.Topic
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.time.LocalDate
import java.util.UUID

internal typealias LovmeTopic = Topic<String, String>

internal val lovme_dagpenger_soknad_mottatt_topic by stringType

internal class SøknadInnsendtRiver(
    rapidsConnection: RapidsConnection,
    private val lovme: LovmeTopic
) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "søknad_endret_tilstand")
                it.requireKey("søknad_uuid", "ident")
                it.requireValue("gjeldendeTilstand", "Innsendt")
                it.requireValue("prosessnavn", "Dagpenger")
            }
        }.register(this)
    }

    private companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val søknadId = packet["søknad_uuid"].asUUID()

        withLoggingContext("søknad_uuid" to søknadId.toString()) {
            val endretTilstand = SøknadInnsendtMelding(packet)

            logger.info { "Informerer LovMe om endringer" }
            lovme.publiser(endretTilstand.nøkkel, endretTilstand.melding.toJson())
        }
    }
}

class SøknadInnsendtMelding(packet: JsonMessage) {
    private val søknadId = packet["søknad_uuid"].asUUID()
    private val ident = packet["ident"].asText()
    val nøkkel = ident.toString()
    val melding = JsonMessage.newMessage(
        "søknad_mottatt",
        mapOf(
            "søknadId" to søknadId,
            "ident" to ident,
            "siste_dag_i_arbeid" to LocalDate.now()
        )
    )
}

private fun JsonNode.asUUID(): UUID = this.asText().let { UUID.fromString(it) }
