# For upstream certlogic.kt as it does not have a consumer-rules mechanism (non Android project)
-keep class eu.ehn.dcc.certlogic.** { *; }
# See https://github.com/eu-digital-green-certificates/dgc-certlogic-android/pull/44
-keep class dgca.verifier.app.** { *; }