/*
 *  ---license-start
 *  eu-digital-green-certificates / dgca-verifier-app-android
 *  ---
 *  Copyright (C) 2021 T-Systems International GmbH and all other contributors
 *  ---
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ---license-end
 *
 *  Created by osarapulov on 6/14/21 12:46 PM
 */

package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.*
import dgca.verifier.app.engine.data.ExternalParameter
import dgca.verifier.app.engine.data.source.remote.RuleRemote
import dgca.verifier.app.engine.data.source.remote.toRules
import org.apache.commons.io.IOUtils
import org.junit.Ignore
import org.junit.jupiter.api.Test
import java.io.InputStream
import java.nio.charset.Charset
import java.time.ZonedDateTime

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
 * Created by osarapulov on 14.06.21 12:46
 */
internal class DefaultCertLogicEngineTest {
    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
        const val HCERT_JSON_FILE_NAME = "hcert.json"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    fun testValidate() {
        val schema = "1.0.0"
        val countryIsoCode = "de"
        val jsonLogicValidator = DefaultJsonLogicValidator()
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        val ruleRemote: RuleRemote = objectMapper.readValue(ruleJson, RuleRemote::class.java)
        val hcertExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(HCERT_JSON_FILE_NAME)
        val hcertJson = IOUtils.toString(hcertExampleIs, Charset.defaultCharset())
        val rulesRemote: List<RuleRemote> = listOf(ruleRemote)
        val rules = rulesRemote.toRules()
        val certLogicEngine = DefaultCertLogicEngine(jsonLogicValidator)
        val externalParameter =
            ExternalParameter(
                ZonedDateTime.now(),
                emptyMap(),
                countryIsoCode,
                ZonedDateTime.now(),
                ZonedDateTime.now()
            )
        val results = certLogicEngine.validate(schema, "", rules, externalParameter, hcertJson)
    }
}
