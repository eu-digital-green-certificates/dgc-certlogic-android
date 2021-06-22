package dgca.verifier.app.engine.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.CertificateType
import dgca.verifier.app.engine.data.source.remote.RuleRemote
import dgca.verifier.app.engine.data.source.remote.toRule
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
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
 * Created by osarapulov on 16.06.21 9:11
 */
@RunWith(AndroidJUnit4::class)
internal class RulesDaoTest {
    private lateinit var rulesDao: RulesDao
    private lateinit var db: RulesDatabase
    private val objectMapper = ObjectMapper().apply { this.findAndRegisterModules() }

    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
    }

    private fun fetchRule(): RuleRemote {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, RuleRemote::class.java)
    }


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, RulesDatabase::class.java
        ).build()
        rulesDao = db.rulesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsert() {
        val ruleRemote = fetchRule().toRule()
        val expected: RuleWithDescriptionsLocal = ruleRemote.toRuleWithDescriptionLocal()
        rulesDao.insertAll(expected)

        assertTrue(
            rulesDao.getRulesWithDescriptionsBy(
                ruleRemote.countryCode,
                ruleRemote.validTo.plusDays(1),
                ruleRemote.type,
                ruleRemote.certificateType,
                CertificateType.GENERAL
            ).isEmpty()
        )

        val actual = rulesDao.getRulesWithDescriptionsBy(
            ruleRemote.countryCode,
            ruleRemote.validTo.minusMinutes(1),
            ruleRemote.type,
            ruleRemote.certificateType,
            CertificateType.GENERAL
        )

        assertTrue(actual.size == 1)
        assertEquals(expected.rule.copy(ruleId = 1), actual[0].rule)
        expected.descriptions.forEachIndexed { index, descriptionLocal ->
            assertEquals(
                descriptionLocal.copy(
                    descriptionId = (index + 1).toLong(),
                    ruleContainerId = 1
                ), actual[0].descriptions[index]
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testDelete() {
        val ruleRemote = fetchRule().toRule()
        val expected: RuleWithDescriptionsLocal = ruleRemote.toRuleWithDescriptionLocal()
        rulesDao.insertAll(expected)

        assertEquals(1, rulesDao.getAll().size)
        assertEquals(1, rulesDao.getDescriptionAll().size)

        rulesDao.deleteRulesBy(ruleRemote.identifier)

        assertEquals(0, rulesDao.getAll().size)
        assertEquals(0, rulesDao.getDescriptionAll().size)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteAllExcept() {
        val identifierFirst = "identifierFirst"
        val identifierSecond = "identifierSecond"
        val ruleFirst = fetchRule().toRule().copy(identifier = identifierFirst)
        val ruleSecond = fetchRule().toRule().copy(identifier = identifierSecond)
        val ruleWithDescriptionsLocalFirst: RuleWithDescriptionsLocal =
            ruleFirst.toRuleWithDescriptionLocal()
        val ruleWithDescriptionsLocalSecond: RuleWithDescriptionsLocal =
            ruleSecond.toRuleWithDescriptionLocal()

        rulesDao.insertAll(ruleWithDescriptionsLocalFirst, ruleWithDescriptionsLocalSecond)

        assertEquals(2, rulesDao.getAll().size)

        rulesDao.deleteAllExcept(arrayOf(identifierSecond))

        val rulesLocalActual = rulesDao.getAll()
        assertEquals(1, rulesLocalActual.size)
        assertEquals(identifierSecond, rulesLocalActual.first().identifier)
    }
}