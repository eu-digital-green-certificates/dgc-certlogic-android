package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import eu.ehn.dcc.certlogic.evaluate
import org.junit.Ignore
import org.junit.jupiter.api.Test

internal class CertLogicTests {
    private val objectMapper = jacksonObjectMapper().apply { findAndRegisterModules() }

    fun test() {
        val ruleJson = "{\n" +
                "  \"after\": [\n" +
                "    {\"plusTime\" : [\"2021-06-15T11:48:18.855Z\", 1, \"day\"]},\n" +
                "    { \"var\":\"time\"}\n" +
                "  ]\n" +
                "}"
        var rule: ObjectNode = objectMapper
            .readValue(ruleJson)
//        (rule.get("after").get(0).get("plusTime") as ArrayNode).set(0, JsonDateTime.fromString(((rule.get("after").get(0).get("plusTime") as ArrayNode).get(0) as TextNode).textValue()))
        var data: ObjectNode =
            objectMapper.readValue("{\"time\": \"2021-06-16T11:47:18.855Z\"}")
        val res = evaluate(rule, data)
        return
    }
}