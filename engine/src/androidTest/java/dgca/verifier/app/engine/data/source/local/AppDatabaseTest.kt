package dgca.verifier.app.engine.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.Rule
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
    private lateinit var db: AppDatabase
    private val objectMapper = ObjectMapper().apply { this.findAndRegisterModules() }

    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
    }

    private fun fetchRule(): Rule {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, Rule::class.java)
    }


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
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
    fun testInsertAll() {
        val ruleRemote = fetchRule()
        val expected = ruleRemote.toLocal().copy(ruleId = 1)
        rulesDao.insertAll(expected)
        val actual = rulesDao.getAll()

        assertTrue(actual.size == 1)
        assertEquals(expected, actual[0])
    }

    @Test
    @Throws(Exception::class)
    fun testGetRulesBy() {
        val ruleRemote = fetchRule()
        val expected = ruleRemote.toLocal().copy(ruleId = 1)
        rulesDao.insertAll(expected)

        assertTrue(rulesDao.getRulesBy(ruleRemote.validTo.plusDays(1)).isEmpty())

        val actual = rulesDao.getRulesBy(ruleRemote.validTo.minusMinutes(1))

        assertTrue(actual.size == 1)
        assertEquals(expected, actual[0])
    }
}