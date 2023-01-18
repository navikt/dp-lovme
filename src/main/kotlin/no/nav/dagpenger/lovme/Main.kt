package no.nav.dagpenger.lovme

import no.nav.dagpenger.lovme.kafka.AivenConfig
import no.nav.dagpenger.lovme.kafka.KafkaTopic.Companion.jsonTopic
import no.nav.dagpenger.lovme.tjenester.SøknadInnsendtRiver
import no.nav.dagpenger.lovme.tjenester.lovme_dagpenger_soknad_mottatt_topic
import no.nav.helse.rapids_rivers.RapidApplication
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.Properties

private val aivenKafka: AivenConfig = AivenConfig.default

fun main() {
    val env = System.getenv()
    val beskjedTopic by lazy {
        jsonTopic(
            createProducer(aivenKafka.producerConfig(stringProducerConfig)),
            config[lovme_dagpenger_soknad_mottatt_topic]
        )
    }

    RapidApplication.create(env) { _, rapidsConnection ->
        SøknadInnsendtRiver(rapidsConnection, beskjedTopic)
    }.start()
}

private fun <K, V> createProducer(producerConfig: Properties = Properties()) =
    KafkaProducer<K, V>(producerConfig).also { producer ->
        Runtime.getRuntime().addShutdownHook(
            Thread {
                producer.flush()
                producer.close()
            }
        )
    }
