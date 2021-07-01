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
 *  Created by osarapulov on 6/30/21 3:25 PM
 */

package dgca.verifier.app.engine.domain.rules

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.UTC_ZONE_ID
import dgca.verifier.app.engine.data.CertificateType
import dgca.verifier.app.engine.data.RuleCertificateType
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.Type
import dgca.verifier.app.engine.data.source.rules.RulesRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
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
 * Created by osarapulov on 30.06.21 15:25
 */
@RunWith(MockitoJUnitRunner::class)
internal class DefaultGetRulesUseCaseTest {
    private val acceptanceCountryIso = "de"
    private val invalidationCountryIso = "at"

    @Mock
    lateinit var rulesRepository: RulesRepository

    private lateinit var getRulesUseCase: GetRulesUseCase

    @Before
    fun setUp() {
        getRulesUseCase = DefaultGetRulesUseCase(rulesRepository)
    }

    @Test
    fun shouldGetLatestRules() {
        val newVersion = "2.0.0"
        val oldVersion = "1.0.0"
        val oldAcceptanceRule = mockRule(
            type = Type.ACCEPTANCE,
            countryCode = acceptanceCountryIso,
            version = oldVersion
        )
        val newAcceptanceRule = mockRule(
            type = Type.ACCEPTANCE,
            countryCode = acceptanceCountryIso,
            version = newVersion
        )
        val acceptanceRules = listOf(oldAcceptanceRule, newAcceptanceRule)

        val oldInvalidationRule = mockRule(
            type = Type.INVALIDATION,
            countryCode = invalidationCountryIso,
            version = oldVersion
        )
        val newInvalidationRule = mockRule(
            type = Type.INVALIDATION,
            countryCode = invalidationCountryIso,
            version = newVersion
        )
        val invalidationRules = listOf(oldInvalidationRule, newInvalidationRule)

        doReturn(acceptanceRules).`when`(rulesRepository)
            .getRulesBy(eq(acceptanceCountryIso), any(), eq(Type.ACCEPTANCE), any())
        doReturn(invalidationRules).`when`(rulesRepository)
            .getRulesBy(eq(invalidationCountryIso), any(), eq(Type.INVALIDATION), any())
        val actual = getRulesUseCase.invoke(
            acceptanceCountryIso,
            invalidationCountryIso,
            CertificateType.VACCINATION
        )

        assertEquals(2, actual.size)
        assertEquals(newAcceptanceRule, actual[0])
        assertEquals(newInvalidationRule, actual[1])
    }

    @Test
    fun shouldGetLatestRulesWithRegion() {
        val region = "region"
        var acceptanceRule = mockRule(
            type = Type.ACCEPTANCE,
            countryCode = acceptanceCountryIso
        )
        val invalidationRule = mockRule(
            type = Type.INVALIDATION,
            countryCode = invalidationCountryIso
        )

        doReturn(listOf(acceptanceRule)).`when`(rulesRepository)
            .getRulesBy(eq(acceptanceCountryIso), any(), eq(Type.ACCEPTANCE), any())
        doReturn(listOf(invalidationRule)).`when`(rulesRepository)
            .getRulesBy(eq(invalidationCountryIso), any(), eq(Type.INVALIDATION), any())
        var actual = getRulesUseCase.invoke(
            acceptanceCountryIso,
            invalidationCountryIso,
            CertificateType.VACCINATION,
            region
        )

        assertEquals(1, actual.size)
        assertEquals(invalidationRule, actual[0])

        acceptanceRule = acceptanceRule.copy(region = region)
        doReturn(listOf(acceptanceRule)).`when`(rulesRepository)
            .getRulesBy(eq(acceptanceCountryIso), any(), eq(Type.ACCEPTANCE), any())

        actual = getRulesUseCase.invoke(
            acceptanceCountryIso,
            invalidationCountryIso,
            CertificateType.VACCINATION,
            region
        )

        assertEquals(2, actual.size)
        assertEquals(acceptanceRule, actual[0])
        assertEquals(invalidationRule, actual[1])
    }

    private fun mockRule(
        identifier: String = "identifier",
        type: Type,
        version: String = "1.0.0",
        schemaVersion: String = "1.0.0",
        engine: String = "engine",
        engineVersion: String = "1.0.0",
        ruleCertificateType: RuleCertificateType = RuleCertificateType.GENERAL,
        descriptions: Map<String, String> = emptyMap(),
        validFrom: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(UTC_ZONE_ID),
        validTo: ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(UTC_ZONE_ID),
        affectedString: List<String> = emptyList(),
        logic: JsonNode = ObjectMapper().createObjectNode(),
        countryCode: String,
        region: String? = null
    ): Rule = Rule(
        identifier,
        type,
        version,
        schemaVersion,
        engine,
        engineVersion,
        ruleCertificateType,
        descriptions,
        validFrom,
        validTo,
        affectedString,
        logic,
        countryCode,
        region
    )
}