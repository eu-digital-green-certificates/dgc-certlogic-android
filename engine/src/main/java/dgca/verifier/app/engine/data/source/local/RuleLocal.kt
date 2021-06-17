package dgca.verifier.app.engine.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
 * Created by osarapulov on 16.06.21 8:26
 */
@Entity(tableName = "rules")
data class RuleLocal(
    @PrimaryKey(autoGenerate = true)
    val ruleId: Int = 0,
    @ColumnInfo(name = "identifier") val identifier: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "version") val version: String,
    @ColumnInfo(name = "schema_version") val schemaVersion: String,
    @ColumnInfo(name = "engine") val engine: String,
    @ColumnInfo(name = "engine_version") val engineVersion: String,
    @ColumnInfo(name = "certificate_type") val certificateType: String,
    @ColumnInfo(name = "valid_from") val validFrom: Long,
    @ColumnInfo(name = "valid_to") val validTo: Long,
//    @ColumnInfo(name = "affected_fields") val affectedString: List<String>,
    @ColumnInfo(name = "logic") val logic: String,
    @ColumnInfo(name = "country_code") val countryCode: String,
)
