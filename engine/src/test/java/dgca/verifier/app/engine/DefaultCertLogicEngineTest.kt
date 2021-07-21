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
import dgca.verifier.app.engine.data.CertificateType
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
import org.mockito.kotlin.doThrow
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
        const val STANDARD_VERSION = "1.0.0"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Mock
    lateinit var affectedFieldsDataRetriever: AffectedFieldsDataRetriever

    @Mock
    lateinit var jsonLogicValidator: JsonLogicValidator

    private lateinit var certLogicEngine: CertLogicEngine

    @Before
    fun setUp() {
        certLogicEngine = DefaultCertLogicEngine(affectedFieldsDataRetriever, jsonLogicValidator)

        doReturn(true).`when`(jsonLogicValidator).isDataValid(any(), any())
        doReturn("").`when`(affectedFieldsDataRetriever).getAffectedFieldsData(any(), any(), any())
    }

    @Test
    fun testInconsistentOpenVersions() {
        val schemaVersion = "2.0.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(schemaVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                "3.0.0",
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testInconsistentFailedVersions() {
        val schemaVersion = "2.2.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(schemaVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                "2.1.0",
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testConsistentVersions() {
        val schemaVersion = "2.0.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(schemaVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.PASSED,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                "2.1.0",
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testInconsistentEngine() {
        val engineVersion = "2.0.0"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(engineVersion = engineVersion)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testInconsistentEngineVersion() {
        val customEngine = "CUSTOMENGINE"
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(engine = customEngine)).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testEngineVersionIsGreater() {
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(engineVersion = "0.7.5")).toRules()
        val externalParameter = mockExternalParameter()
        doReturn(true).`when`(jsonLogicValidator).isDataValid(any(), any())

        assertEquals(
            Result.PASSED,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testEngineVersionIsLower() {
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote(engineVersion = "1.7.5")).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    @Test
    fun testValidatedWithException() {
        doThrow(RuntimeException()).`when`(jsonLogicValidator).isDataValid(any(), any())
        val hcertJson = mockHcertJson()
        val rules = listOf(mockRuleRemote()).toRules()
        val externalParameter = mockExternalParameter()

        assertEquals(
            Result.OPEN,
            certLogicEngine.validate(
                CertificateType.VACCINATION,
                STANDARD_VERSION,
                rules,
                externalParameter,
                hcertJson
            )
                .first().result
        )
    }

    private fun mockHcertJson(): String {
        val hcertExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(HCERT_JSON_FILE_NAME)
        return IOUtils.toString(hcertExampleIs, Charset.defaultCharset())
    }

    private fun mockRuleRemote(
        schemaVersion: String = STANDARD_VERSION,
        engine: String = "CERTLOGIC",
        engineVersion: String = STANDARD_VERSION
    ): RuleRemote {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, RuleRemote::class.java)
            .copy(schemaVersion = schemaVersion, engine = engine, engineVersion = engineVersion)
    }

    private fun mockExternalParameter(
        kid: String = "kid",
        countryIsoCode: String = "de"
    ): ExternalParameter = ExternalParameter(
        validationClock = ZonedDateTime.now(),
        valueSets = emptyMap(),
        countryCode = countryIsoCode,
        exp = ZonedDateTime.now(),
        iat = ZonedDateTime.now(),
        issuerCountryCode = countryIsoCode,
        kid = kid,
        region = ""
    )
}
