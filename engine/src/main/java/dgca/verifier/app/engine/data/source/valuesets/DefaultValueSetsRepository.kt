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
 * Created by osarapulov on 25.06.21 15:49
 */

package dgca.verifier.app.engine.data.source.valuesets

import dgca.verifier.app.engine.data.ValueSet
import dgca.verifier.app.engine.data.ValueSetIdentifier
import dgca.verifier.app.engine.data.source.local.valuesets.ValueSetsLocalDataSource
import dgca.verifier.app.engine.data.source.remote.valuesets.ValueSetsRemoteDataSource
import dgca.verifier.app.engine.data.source.remote.valuesets.toValueSet
import dgca.verifier.app.engine.data.source.remote.valuesets.toValueSetIdentifier

class DefaultValueSetsRepository(
    private val remoteDataSource: ValueSetsRemoteDataSource,
    private val localDataSource: ValueSetsLocalDataSource
) : ValueSetsRepository {

    override suspend fun preLoad(url: String) {
        val valueSetsIdentifiersRemote = remoteDataSource.getValueSetsIdentifiers(url).map { it.toValueSetIdentifier() }
        val valueSetsIdentifiersLocal = localDataSource.getValueSetIdentifiers()

        val added = valueSetsIdentifiersRemote - valueSetsIdentifiersLocal
        val removed = valueSetsIdentifiersLocal - valueSetsIdentifiersRemote

        localDataSource.removeValueSetsBy(removed.map { it.id })

        val valueSets = mutableListOf<ValueSet>()
        added.forEach {
            val valueSetRemote = remoteDataSource.getValueSet("$url/${it.hash}")
            if (valueSetRemote != null) {
                valueSets.add(valueSetRemote.toValueSet())
            }
        }

        if (valueSets.isNotEmpty()) {
            localDataSource.addValueSets(added, valueSets)
        }
    }

    override suspend fun getValueSets(): List<ValueSet> = localDataSource.getValueSets()

    override suspend fun getValueSetIdentifiers(): List<ValueSetIdentifier> = localDataSource.getValueSetIdentifiers()
}