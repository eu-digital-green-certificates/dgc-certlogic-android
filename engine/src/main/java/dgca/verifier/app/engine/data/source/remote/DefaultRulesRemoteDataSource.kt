package dgca.verifier.app.engine.data.source.remote

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Response

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
class DefaultRulesRemoteDataSource(private val rulesApiService: RulesApiService) :
    RulesRemoteDataSource {
    companion object {
        const val IDENTIFIER = "[\n" +
                "  {\n" +
                "    \"identifier\": \"VR-DE-1\",\n" +
                "    \"version\": \"1.0.0\",\n" +
                "    \"country\": \"DE\",\n" +
                "    \"hash\": \"6821d518570fe9f4417c482ff0d2582a7b6440f243a9034f812e0d71611b611f\"\n" +
                "  }\n" +
                "]"

        const val JSON = "{\n" +
                "  \"IDENTIFIER\": \"GR-CZ-0001\",\n" +
                "  \"VERSION\": \"1.0.0\",\n" +
                "  \"SCHEMAVERSION\": \"1.0.0\",\n" +
                "  \"ENGINE\": \"CERTLOGIC\",\n" +
                "  \"ENGINEVERSION\": \"2.0.1\",\n" +
                "  \"TYPE\": \"Acceptance\",\n" +
                "  \"CERTIFICATETYPE\": \"Vaccination\",\n" +
                "  \"COUNTRY\": \"at\",\n" +
                "  \"DESCRIPTION\": [\n" +
                "    {\n" +
                "      \"LANG\": \"en\",\n" +
                "      \"DESC\": \"The Field “Doses” MUST contain number 2 OR 2/2.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"VALIDFROM\": \"2021-05-27T07:46:40Z\",\n" +
                "  \"VALIDTO\": \"2030-06-01T07:46:40Z\",\n" +
                "  \"AFFECTEDFIELDS\": [\n" +
                "    \"dn\",\n" +
                "    \"sd\"\n" +
                "  ],\n" +
                "  \"LOGIC\": {\n" +
                "    \"and\": [\n" +
                "      {\n" +
                "        \">\": [\n" +
                "          {\n" +
                "            \"var\": \"hcert.v.0.dn\"\n" +
                "          },\n" +
                "          0\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \">=\": [\n" +
                "          {\n" +
                "            \"var\": \"hcert.v.0.dn\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"var\": \"hcert.v.0.sd\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}"
    }

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    override suspend fun getCountries(countriesUrl: String): List<String> {
        val countriesResponse: Response<List<String>> = rulesApiService.getCountries(countriesUrl)
        return countriesResponse.body() ?: listOf()
    }

    override suspend fun getRuleIdentifiers(rulesUrl: String): List<RuleIdentifierRemote> {
        if (true) return objectMapper.readValue(IDENTIFIER,
            object : TypeReference<List<RuleIdentifierRemote>>() {})
        val rulesResponse: Response<List<RuleIdentifierRemote>> =
            rulesApiService.getRuleIdentifiers(rulesUrl)
        return rulesResponse.body() ?: listOf()
    }

    override suspend fun getRules(rulesUrl: String): List<RuleRemote> {
        val rulesResponse: Response<List<RuleRemote>> = rulesApiService.getRules(rulesUrl)
        return rulesResponse.body() ?: listOf()
    }

    override suspend fun getRule(ruleUrl: String): RuleRemote? {
        if (true) return objectMapper.readValue(JSON, RuleRemote::class.java)
        val ruleResponse: Response<RuleRemote> = rulesApiService.getRule(ruleUrl)
        return ruleResponse.body()
    }
}