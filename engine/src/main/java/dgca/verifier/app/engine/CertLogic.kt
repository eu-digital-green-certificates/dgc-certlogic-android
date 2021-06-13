package dgca.verifier.app.engine

import dgca.verifier.app.engine.data.Rule

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
 * Created by osarapulov on 11.06.21 11:10
 */
class CertLogic(private val schema: String, private val rules: Array<Rule>) {

    fun validate(external: ExternalParameter, payload: String): ValidationResult {
        TODO("TO be implemented")
    }

    // Get List of Rules for Country by Code
    private fun getListOfRulesFor(countryCode: String): Array<Rule> {
        return rules.filter { it.countryCode == countryCode }.toTypedArray()
    }

    // Parce Rules from Data or JSON String
    fun getRules(jsonString: String): Array<Rule> {
        TODO("TO be implemented")
    }

    fun getRule(jsonString: String): Rule? {
        TODO("TO be implemented")
    }
}