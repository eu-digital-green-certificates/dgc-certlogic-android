package dgca.verifier.app.engine.data.source.remote

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
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
 * Created by osarapulov on 11.06.21 11:03
 */
data class RuleRemote(
    @JsonProperty("IDENTIFIER")
    val identifier: String,
    @JsonProperty("TYPE")
    val type: String,
    @JsonProperty("VERSION")
    val version: String,
    @JsonProperty("SCHEMAVERSION")
    val schemaVersion: String,
    @JsonProperty("ENGINE")
    val engine: String,
    @JsonProperty("ENGINEVERSION")
    val engineVersion: String,
    @JsonProperty("CERTIFICATETYPE")
    val certificateType: String,
    @JsonProperty("DESCRIPTION")
    val descriptions: List<DescriptionRemote>,
    @JsonProperty("VALIDFROM")
    val validFrom: ZonedDateTime,
    @JsonProperty("VALIDTO")
    val validTo: ZonedDateTime,
    @JsonProperty("AFFECTEDFIELDS")
    val affectedString: List<String>,
    @JsonProperty("LOGIC")
    val logic: JsonNode,
    @JsonProperty("COUNTRY")
    val countryCode: String
)