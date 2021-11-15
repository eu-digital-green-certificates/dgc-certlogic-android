package dgca.verifier.app.engine.data.source.local

interface EnginePreferences {
    fun setLastCountriesSync(millis: Long)

    fun getLastCountriesSync(): Long

    fun getSelectedCountryIsoCode(): String?
}