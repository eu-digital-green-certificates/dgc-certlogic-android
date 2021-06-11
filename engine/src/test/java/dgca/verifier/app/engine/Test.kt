package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
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
 * Created by osarapulov on 11.06.21 12:51
 */
class Test {
    companion object {
        const val RULE_JSON_FILE_NAME = "rule.json"
    }

    @Test
    fun test() {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(RULE_JSON_FILE_NAME)
        val str = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        val rule: Rule = ObjectMapper().readValue(str, Rule::class.java)
        val logic: JsonNode = ObjectMapper().readValue(rule.logic)
        return
    }
}