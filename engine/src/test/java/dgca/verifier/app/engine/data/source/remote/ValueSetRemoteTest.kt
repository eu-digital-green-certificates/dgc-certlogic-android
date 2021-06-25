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
 *  Created by osarapulov on 6/25/21 3:34 PM
 */

package dgca.verifier.app.engine.data.source.remote

import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.source.remote.valuesets.ValueSetRemote
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
 * Created by osarapulov on 25.06.21 15:34
 */
class ValueSetRemoteTest {
    companion object {
        const val VALUE_SET_JSON_FILE_NAME = "valueset.json"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Test
    fun shouldMapValueSet() {
        val valueSetIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(VALUE_SET_JSON_FILE_NAME)
        val valueSetJson = IOUtils.toString(valueSetIs, Charset.defaultCharset())
        val valueSet = objectMapper.readValue(valueSetJson, ValueSetRemote::class.java)
        return
    }
}