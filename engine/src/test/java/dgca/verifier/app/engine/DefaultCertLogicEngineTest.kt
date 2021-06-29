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

import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.ExternalParameter
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
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
@RunWith(MockitoJUnitRunner::class)
internal class DefaultCertLogicEngineTest {
    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
        const val HCERT_JSON_FILE_NAME = "hcert.json"
        private const val JSON_SCHEMA = "{}"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Mock
    lateinit var jsonLogicValidator: JsonLogicValidator

    private lateinit var certLogicEngine: CertLogicEngine

    @Before
    fun setUp() {
        certLogicEngine = DefaultCertLogicEngine(jsonLogicValidator)

        doReturn(true).`when`(jsonLogicValidator).isDataValid(any(), any())
    }

    @Test
    fun testInconsistentOpenVersions() {
        val ruleVersion = "2.0.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(ruleVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate("1.0.0", JSON_SCHEMA, rules, externalParameter, hcertJson)
                .first().result
        )

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate("3.0.0", JSON_SCHEMA, rules, externalParameter, hcertJson)
                .first().result
        )
    }

    @Test
    fun testInconsistentFailedVersions() {
        val ruleVersion = "2.2.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(ruleVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.FAIL,
            certLogicEngine.validate("2.1.0", JSON_SCHEMA, rules, externalParameter, hcertJson)
                .first().result
        )
    }

    @Test
    fun testConsistentVersions() {
        val ruleVersion = "2.0.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(ruleVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.PASSED,
            certLogicEngine.validate("2.1.0", JSON_SCHEMA, rules, externalParameter, hcertJson)
                .first().result
        )
    }

    private fun mockHcertJson(): String {
        val hcertExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(HCERT_JSON_FILE_NAME)
        return IOUtils.toString(hcertExampleIs, Charset.defaultCharset())
    }

    private fun mockRuleRemote(version: String = "1.0.0"): RuleRemote {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, RuleRemote::class.java).copy(version = version)
    }

    private fun mockExternalParameter(
        kid: String = "kid",
        countryIsoCode: String = "de"
    ): ExternalParameter = ExternalParameter(
        kid,
        ZonedDateTime.now(),
        emptyMap(),
        countryIsoCode,
        ZonedDateTime.now(),
        ZonedDateTime.now()
    )
}
