package dgca.verifier.app.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dgca.verifier.app.engine.data.CertificateType
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRule
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.InputStream
import java.nio.charset.Charset

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
 * Created by osarapulov on 01.07.21 13:57
 */
internal class DefaultAffectedFieldsDataRetrieverTest {
    private companion object {
        const val JSON_SCHEMA_FILE_NAME = "JSON_SCHEMA.json"
    }

    private lateinit var affectedFieldsDataRetriever: AffectedFieldsDataRetriever

    private val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    private val DATA_JSON = "{\n" +
            "  \"external\": {\n" +
            "    \"kid\": \"jd4FUVxAJjY=\",\n" +
            "    \"validationClock\": \"2021-07-14T11:23:18.463Z\",\n" +
            "    \"valueSets\": {\n" +
            "      \"country-2-codes\": [\n" +
            "        \"AD\",\n" +
            "        \"AE\",\n" +
            "        \"AF\",\n" +
            "        \"AG\",\n" +
            "        \"AI\",\n" +
            "        \"AL\",\n" +
            "        \"AM\",\n" +
            "        \"AO\",\n" +
            "        \"AQ\",\n" +
            "        \"AR\",\n" +
            "        \"AS\",\n" +
            "        \"AT\",\n" +
            "        \"AU\",\n" +
            "        \"AW\",\n" +
            "        \"AX\",\n" +
            "        \"AZ\",\n" +
            "        \"BA\",\n" +
            "        \"BB\",\n" +
            "        \"BD\",\n" +
            "        \"BE\",\n" +
            "        \"BF\",\n" +
            "        \"BG\",\n" +
            "        \"BH\",\n" +
            "        \"BI\",\n" +
            "        \"BJ\",\n" +
            "        \"BL\",\n" +
            "        \"BM\",\n" +
            "        \"BN\",\n" +
            "        \"BO\",\n" +
            "        \"BQ\",\n" +
            "        \"BR\",\n" +
            "        \"BS\",\n" +
            "        \"BT\",\n" +
            "        \"BV\",\n" +
            "        \"BW\",\n" +
            "        \"BY\",\n" +
            "        \"BZ\",\n" +
            "        \"CA\",\n" +
            "        \"CC\",\n" +
            "        \"CD\",\n" +
            "        \"CF\",\n" +
            "        \"CG\",\n" +
            "        \"CH\",\n" +
            "        \"CI\",\n" +
            "        \"CK\",\n" +
            "        \"CL\",\n" +
            "        \"CM\",\n" +
            "        \"CN\",\n" +
            "        \"CO\",\n" +
            "        \"CR\",\n" +
            "        \"CU\",\n" +
            "        \"CV\",\n" +
            "        \"CW\",\n" +
            "        \"CX\",\n" +
            "        \"CY\",\n" +
            "        \"CZ\",\n" +
            "        \"DE\",\n" +
            "        \"DJ\",\n" +
            "        \"DK\",\n" +
            "        \"DM\",\n" +
            "        \"DO\",\n" +
            "        \"DZ\",\n" +
            "        \"EC\",\n" +
            "        \"EE\",\n" +
            "        \"EG\",\n" +
            "        \"EH\",\n" +
            "        \"ER\",\n" +
            "        \"ES\",\n" +
            "        \"ET\",\n" +
            "        \"FI\",\n" +
            "        \"FJ\",\n" +
            "        \"FK\",\n" +
            "        \"FM\",\n" +
            "        \"FO\",\n" +
            "        \"FR\",\n" +
            "        \"GA\",\n" +
            "        \"GB\",\n" +
            "        \"GD\",\n" +
            "        \"GE\",\n" +
            "        \"GF\",\n" +
            "        \"GG\",\n" +
            "        \"GH\",\n" +
            "        \"GI\",\n" +
            "        \"GL\",\n" +
            "        \"GM\",\n" +
            "        \"GN\",\n" +
            "        \"GP\",\n" +
            "        \"GQ\",\n" +
            "        \"GR\",\n" +
            "        \"GS\",\n" +
            "        \"GT\",\n" +
            "        \"GU\",\n" +
            "        \"GW\",\n" +
            "        \"GY\",\n" +
            "        \"HK\",\n" +
            "        \"HM\",\n" +
            "        \"HN\",\n" +
            "        \"HR\",\n" +
            "        \"HT\",\n" +
            "        \"HU\",\n" +
            "        \"ID\",\n" +
            "        \"IE\",\n" +
            "        \"IL\",\n" +
            "        \"IM\",\n" +
            "        \"IN\",\n" +
            "        \"IO\",\n" +
            "        \"IQ\",\n" +
            "        \"IR\",\n" +
            "        \"IS\",\n" +
            "        \"IT\",\n" +
            "        \"JE\",\n" +
            "        \"JM\",\n" +
            "        \"JO\",\n" +
            "        \"JP\",\n" +
            "        \"KE\",\n" +
            "        \"KG\",\n" +
            "        \"KH\",\n" +
            "        \"KI\",\n" +
            "        \"KM\",\n" +
            "        \"KN\",\n" +
            "        \"KP\",\n" +
            "        \"KR\",\n" +
            "        \"KW\",\n" +
            "        \"KY\",\n" +
            "        \"KZ\",\n" +
            "        \"LA\",\n" +
            "        \"LB\",\n" +
            "        \"LC\",\n" +
            "        \"LI\",\n" +
            "        \"LK\",\n" +
            "        \"LR\",\n" +
            "        \"LS\",\n" +
            "        \"LT\",\n" +
            "        \"LU\",\n" +
            "        \"LV\",\n" +
            "        \"LY\",\n" +
            "        \"MA\",\n" +
            "        \"MC\",\n" +
            "        \"MD\",\n" +
            "        \"ME\",\n" +
            "        \"MF\",\n" +
            "        \"MG\",\n" +
            "        \"MH\",\n" +
            "        \"MK\",\n" +
            "        \"ML\",\n" +
            "        \"MM\",\n" +
            "        \"MN\",\n" +
            "        \"MO\",\n" +
            "        \"MP\",\n" +
            "        \"MQ\",\n" +
            "        \"MR\",\n" +
            "        \"MS\",\n" +
            "        \"MT\",\n" +
            "        \"MU\",\n" +
            "        \"MV\",\n" +
            "        \"MW\",\n" +
            "        \"MX\",\n" +
            "        \"MY\",\n" +
            "        \"MZ\",\n" +
            "        \"NA\",\n" +
            "        \"NC\",\n" +
            "        \"NE\",\n" +
            "        \"NF\",\n" +
            "        \"NG\",\n" +
            "        \"NI\",\n" +
            "        \"NL\",\n" +
            "        \"NO\",\n" +
            "        \"NP\",\n" +
            "        \"NR\",\n" +
            "        \"NU\",\n" +
            "        \"NZ\",\n" +
            "        \"OM\",\n" +
            "        \"PA\",\n" +
            "        \"PE\",\n" +
            "        \"PF\",\n" +
            "        \"PG\",\n" +
            "        \"PH\",\n" +
            "        \"PK\",\n" +
            "        \"PL\",\n" +
            "        \"PM\",\n" +
            "        \"PN\",\n" +
            "        \"PR\",\n" +
            "        \"PS\",\n" +
            "        \"PT\",\n" +
            "        \"PW\",\n" +
            "        \"PY\",\n" +
            "        \"QA\",\n" +
            "        \"RE\",\n" +
            "        \"RO\",\n" +
            "        \"RS\",\n" +
            "        \"RU\",\n" +
            "        \"RW\",\n" +
            "        \"SA\",\n" +
            "        \"SB\",\n" +
            "        \"SC\",\n" +
            "        \"SD\",\n" +
            "        \"SE\",\n" +
            "        \"SG\",\n" +
            "        \"SH\",\n" +
            "        \"SI\",\n" +
            "        \"SJ\",\n" +
            "        \"SK\",\n" +
            "        \"SL\",\n" +
            "        \"SM\",\n" +
            "        \"SN\",\n" +
            "        \"SO\",\n" +
            "        \"SR\",\n" +
            "        \"SS\",\n" +
            "        \"ST\",\n" +
            "        \"SV\",\n" +
            "        \"SX\",\n" +
            "        \"SY\",\n" +
            "        \"SZ\",\n" +
            "        \"TC\",\n" +
            "        \"TD\",\n" +
            "        \"TF\",\n" +
            "        \"TG\",\n" +
            "        \"TH\",\n" +
            "        \"TJ\",\n" +
            "        \"TK\",\n" +
            "        \"TL\",\n" +
            "        \"TM\",\n" +
            "        \"TN\",\n" +
            "        \"TO\",\n" +
            "        \"TR\",\n" +
            "        \"TT\",\n" +
            "        \"TV\",\n" +
            "        \"TW\",\n" +
            "        \"TZ\",\n" +
            "        \"UA\",\n" +
            "        \"UG\",\n" +
            "        \"UM\",\n" +
            "        \"US\",\n" +
            "        \"UY\",\n" +
            "        \"UZ\",\n" +
            "        \"VA\",\n" +
            "        \"VC\",\n" +
            "        \"VE\",\n" +
            "        \"VG\",\n" +
            "        \"VI\",\n" +
            "        \"VN\",\n" +
            "        \"VU\",\n" +
            "        \"WF\",\n" +
            "        \"WS\",\n" +
            "        \"YE\",\n" +
            "        \"YT\",\n" +
            "        \"ZA\",\n" +
            "        \"ZM\",\n" +
            "        \"ZW\"\n" +
            "      ],\n" +
            "      \"covid-19-lab-result\": [\n" +
            "        \"260415000\",\n" +
            "        \"260373001\"\n" +
            "      ],\n" +
            "      \"covid-19-lab-test-manufacturer-and-name\": [\n" +
            "        \"1833\",\n" +
            "        \"1232\",\n" +
            "        \"1468\",\n" +
            "        \"1304\",\n" +
            "        \"1822\",\n" +
            "        \"1815\",\n" +
            "        \"1736\",\n" +
            "        \"768\",\n" +
            "        \"1654\",\n" +
            "        \"2010\",\n" +
            "        \"1906\",\n" +
            "        \"1870\",\n" +
            "        \"1331\",\n" +
            "        \"1484\",\n" +
            "        \"1223\",\n" +
            "        \"1236\",\n" +
            "        \"1173\",\n" +
            "        \"1919\",\n" +
            "        \"1225\",\n" +
            "        \"1375\",\n" +
            "        \"1244\",\n" +
            "        \"1253\",\n" +
            "        \"1144\",\n" +
            "        \"1747\",\n" +
            "        \"1360\",\n" +
            "        \"1437\",\n" +
            "        \"1256\",\n" +
            "        \"1363\",\n" +
            "        \"1365\",\n" +
            "        \"1844\",\n" +
            "        \"1215\",\n" +
            "        \"1392\",\n" +
            "        \"1767\",\n" +
            "        \"1263\",\n" +
            "        \"1333\",\n" +
            "        \"1764\",\n" +
            "        \"1266\",\n" +
            "        \"1267\",\n" +
            "        \"1268\",\n" +
            "        \"1180\",\n" +
            "        \"1190\",\n" +
            "        \"1481\",\n" +
            "        \"1162\",\n" +
            "        \"1420\",\n" +
            "        \"1199\",\n" +
            "        \"308\",\n" +
            "        \"1271\",\n" +
            "        \"1341\",\n" +
            "        \"1097\",\n" +
            "        \"1606\",\n" +
            "        \"1604\",\n" +
            "        \"1489\",\n" +
            "        \"1490\",\n" +
            "        \"344\",\n" +
            "        \"345\",\n" +
            "        \"1319\",\n" +
            "        \"2017\",\n" +
            "        \"1769\",\n" +
            "        \"1574\",\n" +
            "        \"1218\",\n" +
            "        \"1114\",\n" +
            "        \"1466\",\n" +
            "        \"1934\",\n" +
            "        \"1443\",\n" +
            "        \"1246\",\n" +
            "        \"1763\",\n" +
            "        \"1278\",\n" +
            "        \"1456\",\n" +
            "        \"1884\",\n" +
            "        \"1296\",\n" +
            "        \"1295\",\n" +
            "        \"1343\"\n" +
            "      ],\n" +
            "      \"covid-19-lab-test-type\": [\n" +
            "        \"LP6464-4\",\n" +
            "        \"LP217198-3\"\n" +
            "      ],\n" +
            "      \"disease-agent-targeted\": [\n" +
            "        \"840539006\"\n" +
            "      ],\n" +
            "      \"sct-vaccines-covid-19\": [\n" +
            "        \"1119349007\",\n" +
            "        \"1119305005\",\n" +
            "        \"J07BX03\"\n" +
            "      ],\n" +
            "      \"vaccines-covid-19-auth-holders\": [\n" +
            "        \"ORG-100001699\",\n" +
            "        \"ORG-100030215\",\n" +
            "        \"ORG-100001417\",\n" +
            "        \"ORG-100031184\",\n" +
            "        \"ORG-100006270\",\n" +
            "        \"ORG-100013793\",\n" +
            "        \"ORG-100020693\",\n" +
            "        \"ORG-100010771\",\n" +
            "        \"ORG-100024420\",\n" +
            "        \"ORG-100032020\",\n" +
            "        \"Gamaleya-Research-Institute\",\n" +
            "        \"Vector-Institute\",\n" +
            "        \"Sinovac-Biotech\",\n" +
            "        \"Bharat-Biotech\"\n" +
            "      ],\n" +
            "      \"vaccines-covid-19-names\": [\n" +
            "        \"EU/1/20/1528\",\n" +
            "        \"EU/1/20/1507\",\n" +
            "        \"EU/1/21/1529\",\n" +
            "        \"EU/1/20/1525\",\n" +
            "        \"CVnCoV\",\n" +
            "        \"Sputnik-V\",\n" +
            "        \"Convidecia\",\n" +
            "        \"EpiVacCorona\",\n" +
            "        \"BBIBP-CorV\",\n" +
            "        \"Inactivated-SARS-CoV-2-Vero-Cell\",\n" +
            "        \"CoronaVac\",\n" +
            "        \"Covaxin\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"countryCode\": \"at\",\n" +
            "    \"exp\": \"2022-07-01T00:00Z\",\n" +
            "    \"iat\": \"2021-07-01T11:08:22Z\"\n" +
            "  },\n" +
            "  \"payload\": {\n" +
            "    \"v\": [\n" +
            "      {\n" +
            "        \"dn\": 1,\n" +
            "        \"ma\": \"ORG-100001699\",\n" +
            "        \"vp\": \"J07BX03\",\n" +
            "        \"dt\": \"2021-07-01\",\n" +
            "        \"co\": \"AT\",\n" +
            "        \"ci\": \"URN:UVCI:V1:DE:JYP9MNRWCMLK8B20328XRYUEV:\",\n" +
            "        \"mp\": \"EU/1/20/1507\",\n" +
            "        \"is\": \"asfadf\",\n" +
            "        \"sd\": 2,\n" +
            "        \"tg\": \"840539006\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"nam\": {\n" +
            "      \"fnt\": \"STANDARDISEDFAMILY\"\n" +
            "    },\n" +
            "    \"ver\": \"1.3.0\",\n" +
            "    \"dob\": \"\"\n" +
            "  }\n" +
            "}"

    private val RULE = "{\n" +
            "  \"Identifier\": \"VR-DE-0001\",\n" +
            "  \"Type\": \"Acceptance\",\n" +
            "  \"Country\": \"DE\",\n" +
            "  \"Version\": \"1.0.0\",\n" +
            "  \"SchemaVersion\": \"1.0.0\",\n" +
            "  \"Engine\": \"CERTLOGIC\",\n" +
            "  \"EngineVersion\": \"0.7.5\",\n" +
            "  \"CertificateType\": \"Vaccination\",\n" +
            "  \"Description\": [\n" +
            "    {\n" +
            "      \"lang\": \"en\",\n" +
            "      \"desc\": \"The vaccination schedule must be complete (e.g., 1/1, 2/2).\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"lang\": \"de\",\n" +
            "      \"desc\": \"Die Impfreihe muss vollständig sein (z.B. 1/1, 2/2).\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"lang\": \"fr\",\n" +
            "      \"desc\": \"La série vaccinale doit être complète (p. ex. 1/1, 2/2).\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"lang\": \"es\",\n" +
            "      \"desc\": \"La pauta de vacunación debe estar completa (por ejemplo, 1/1, 2/2).\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"lang\": \"it\",\n" +
            "      \"desc\": \"Il ciclo di vaccinazione deve essere stato completato (ad es. 1/1, 2/2).\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ValidFrom\": \"2021-07-03T00:00:00Z\",\n" +
            "  \"ValidTo\": \"2030-06-01T00:00:00Z\",\n" +
            "  \"AffectedFields\": [\n" +
            "    \"v.0\",\n" +
            "    \"v.0.dn\",\n" +
            "    \"v.0.sd\"\n" +
            "  ],\n" +
            "  \"Logic\": {\n" +
            "    \"if\": [\n" +
            "      {\n" +
            "        \"var\": \"payload.v.0\"\n" +
            "      },\n" +
            "      {\n" +
            "        \">=\": [\n" +
            "          {\n" +
            "            \"var\": \"payload.v.0.dn\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"var\": \"payload.v.0.sd\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      true\n" +
            "    ]\n" +
            "  }\n" +
            "}"

    @Before
    fun setUp() {
        val jsonNode = mockJsonSchema()
        affectedFieldsDataRetriever = DefaultAffectedFieldsDataRetriever(jsonNode, objectMapper)
    }

    @Test
    fun shouldTest() {
        val dataJsonNode: JsonNode = objectMapper.readTree(DATA_JSON)
        val rule: Rule = objectMapper.readValue<RuleRemote>(RULE).toRule()
        val affectedFieldsString = affectedFieldsDataRetriever.getAffectedFieldsData(
            rule,
            dataJsonNode,
            CertificateType.VACCINATION
        )
        assertEquals("\"Dose Number\": 1\n" +
                "\"Total Series of Doses\": 2\n", affectedFieldsString)
    }

    private fun mockJsonSchema(): JsonNode {
        val ruleExampleIs: InputStream =
            javaClass.classLoader!!.getResourceAsStream(JSON_SCHEMA_FILE_NAME)
        val ruleJson = IOUtils.toString(ruleExampleIs, Charset.defaultCharset())
        return objectMapper.readValue(ruleJson, JsonNode::class.java)
    }
}