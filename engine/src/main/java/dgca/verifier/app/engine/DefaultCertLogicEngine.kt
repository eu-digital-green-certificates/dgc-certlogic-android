package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import dgca.verifier.app.engine.data.ExternalParameter
import dgca.verifier.app.engine.data.Rule

/*-
 * ---license-start
 * eu-digital-green-certificates / dgc-certlogic-android
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 *
 * Created by osarapulov on 11.06.21 11:10
 */
class DefaultCertLogicEngine(
    private val jsonLogicValidator: JsonLogicValidator,
    private val schema: String, private val rules: List<Rule>
) : CertLogicEngine {
    private val objectMapper = ObjectMapper()

    companion object {
        private const val EXTERNAL_KEY = "external"
        private const val HCERT_KEY = "hcert"
    }

    init {
        objectMapper.findAndRegisterModules()
    }

    private fun prepareData(
        externalParameter: ExternalParameter,
        payload: String
    ): ObjectNode = objectMapper.createObjectNode().apply {
        this.putPOJO(EXTERNAL_KEY, externalParameter)
        this.put(HCERT_KEY, payload)
    }

    override fun validate(
        externalParameter: ExternalParameter,
        payload: String
    ): List<ValidationResult> {
        val validationResults = mutableListOf<ValidationResult>()
        rules.forEach {
            val ruleJsonNode: JsonNode = objectMapper.readValue(it.logic)
            val dataJsonNode = prepareData(externalParameter, payload)
            val isValid = jsonLogicValidator.isDataValid(ruleJsonNode, dataJsonNode)
            val res = when {
                isValid -> Result.PASSED
                else -> Result.FAIL
            }
            validationResults.add(
                ValidationResult(
                    it,
                    res,
                    null
                )
            )
        }
        return validationResults
    }
}