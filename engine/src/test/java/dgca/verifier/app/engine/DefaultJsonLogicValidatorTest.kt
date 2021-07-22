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
 *  Created by osarapulov on 7/8/21 12:08 PM
 */

package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

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
 * Created by osarapulov on 08.07.21 12:08
 */
class DefaultJsonLogicValidatorTest {
 private val jsonLogicValidator = DefaultJsonLogicValidator()

    @Test(expected = RuntimeException::class)
    fun testException() {
        assertNull(jsonLogicValidator.isDataValid(jacksonObjectMapper().readValue("{}"), jacksonObjectMapper().readValue("{}")))
    }

    @Test
    fun testPassed() {
        assertEquals(true, jsonLogicValidator.isDataValid(jacksonObjectMapper().readValue("{\"var\": \"res\"}"), jacksonObjectMapper().readValue("{\"res\": true}")))
    }

    @Test
    fun testFailed() {
        assertEquals(false, jsonLogicValidator.isDataValid(jacksonObjectMapper().readValue("{\"var\": \"res\"}"), jacksonObjectMapper().readValue("{\"res\": false}")))
    }
}