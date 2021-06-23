package dgca.verifier.app.engine.data.source.remote

import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.CertificateType
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.Type
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.apache.commons.io.IOUtils
import org.junit.Test
import java.io.InputStream
import java.nio.charset.Charset

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
 * Created by osarapulov on 17.06.21 15:50
 */
class RuleRemoteMapperTest {
    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    private fun fetchRuleRemote(): RuleRemote {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, RuleRemote::class.java)
    }

    @Test
    fun fromRuleRemote() {
        val ruleRemote: RuleRemote = fetchRuleRemote()
        val rule: Rule = ruleRemote.toRule()
        assertEquals(ruleRemote.identifier, rule.identifier)
        assertEquals(Type.valueOf(ruleRemote.type.toUpperCase()), rule.type)
        assertEquals(ruleRemote.version, rule.version)
        assertEquals(ruleRemote.schemaVersion, rule.schemaVersion)
        assertEquals(ruleRemote.engine, rule.engine)
        assertEquals(ruleRemote.engineVersion, rule.engineVersion)
        assertEquals(
            CertificateType.valueOf(ruleRemote.certificateType.toUpperCase()),
            rule.certificateType
        )
        assertEquals(ruleRemote.descriptions.size, rule.descriptions.size)
        assertEquals(ruleRemote.validFrom, rule.validFrom)
        assertEquals(ruleRemote.validTo, rule.validTo)
        assertEquals(ruleRemote.affectedString, rule.affectedString)
        assertEquals(ruleRemote.logic, rule.logic)
        assertEquals(ruleRemote.countryCode, rule.countryCode)
    }

    @Test
    fun fromDescriptionRemote() {
        val descRemote = DescriptionRemote("lang", "desc")
        val desc = descRemote.toDescriptions()

        assertEquals(descRemote.lang, desc.lang)
        assertEquals(descRemote.desc, desc.desc)
    }

    @Test
    fun fromDescriptionsRemote() {
        val descsRemote = listOf(DescriptionRemote("lang", "desc"))

        val descs = descsRemote.toDescriptions()
        descsRemote.forEachIndexed { index, descRemote ->
            assertTrue(descs.containsKey(descRemote.lang))
            assertEquals(descRemote.desc, descs[descRemote.lang])
        }
    }
}