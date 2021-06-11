package dgca.verifier.app.engine

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
class Rule(
    val identifier: String,
    val type: String,
    val version: String,
    val schemaVersion: String,
    val engine: String,
    val engineVersion: String,
    val certificateType: String,
    val description: Array<Description>,
    val validFrom: String,
    val validTo: String,
    val affectedString: Array<String>,
    val logic: String,
    val countryCode: String,
)