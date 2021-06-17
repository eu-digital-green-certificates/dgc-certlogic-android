package dgca.verifier.app.engine.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
 * Created by osarapulov on 16.06.21 9:00
 */
@Dao
abstract class RulesDao {
    @Query("SELECT * from rules")
    abstract fun getAll(): List<RuleLocal>

    @Transaction
    @Query("SELECT * FROM rules WHERE :verificationClock BETWEEN validFrom AND validTo")
    abstract fun getRulesWithDescriptionsBy(verificationClock: ZonedDateTime): List<RuleWithDescriptionsLocal>

    @Insert
    abstract fun insertRule(rule: RuleLocal): Long

    @Insert
    abstract fun insertDescriptions(descriptions: Collection<DescriptionLocal>)

    fun insertAll(vararg rulesWithDescriptions: RuleWithDescriptionsLocal) {
        rulesWithDescriptions.forEach { ruleWithDescriptionsLocal ->
            val rule = ruleWithDescriptionsLocal.rule
            val descriptions = ruleWithDescriptionsLocal.descriptions
            val ruleId = insertRule(rule)
            val descriptionsToBeInserted = mutableListOf<DescriptionLocal>()
            descriptions.forEach { descriptionLocal ->
                descriptionsToBeInserted.add(
                    descriptionLocal.copy(
                        ruleContainerId = ruleId
                    )
                )
            }
            insertDescriptions(descriptionsToBeInserted)
        }
    }
}