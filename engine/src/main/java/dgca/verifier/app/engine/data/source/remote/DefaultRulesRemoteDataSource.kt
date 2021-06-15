package dgca.verifier.app.engine.data.source.remote

import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.engine.data.Rule
import java.util.*

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
 * Created by osarapulov on 13.06.21 16:54
 */
class DefaultRulesRemoteDataSource : RulesRemoteDataSource {
    companion object {
        private const val TEMP_RULE = "{\n" +
                "  \"Identifier\": \"GR-CZ-0001\",\n" +
                "  \"Version\": \"1.0.0\",\n" +
                "  \"SchemaVersion\": \"1.0.0\",\n" +
                "  \"Engine\": \"CERTLOGIC\",\n" +
                "  \"EngineVersion\": \"2.0.1\",\n" +
                "  \"Type\": \"Vaccination\",\n" +
                "  \"CertificateType\": \"CERTLOGIC\",\n" +
                "  \"CountryCode\": \"AT\",\n" +
                "  \"Description\": [\n" +
                "    {\n" +
                "      \"lang\": \"en\",\n" +
                "      \"desc\": \"The Field “Doses” MUST contain number 2 OR 2/2.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"ValidFrom\": \"2021-05-27T07:46:40Z\",\n" +
                "  \"ValidTo\": \"2021-06-01T07:46:40Z\",\n" +
                "  \"AffectedFields\": [\n" +
                "    \"dt\",\n" +
                "    \"nm\"\n" +
                "  ],\n" +
                "  \"Logic\": \"{\\\"and\\\":[{\\\">=\\\":[{\\\"var\\\":\\\"dt\\\"},2012]},{\\\">=\\\":[{\\\"var\\\":\\\"nm\\\"},13]}]}\"\n" +
                "}"
    }

    override fun getRules(): List<Rule> {
        val rule: Rule = ObjectMapper().readValue(TEMP_RULE, Rule::class.java)
        return Collections.singletonList(rule)
    }
}