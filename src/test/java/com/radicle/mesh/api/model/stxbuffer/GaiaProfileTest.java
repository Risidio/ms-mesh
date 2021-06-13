package com.radicle.mesh.api.model.stxbuffer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.radicle.mesh.stacks.model.stxbuffer.GaiaHubReader;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.AppsModel;
import com.radicle.mesh.stacks.model.stxbuffer.gaia.UserAppMaps;

class GaiaProfileTest {
	String profile = "{\n  \"radicle_art.id.blockstack\": {\n    \"owner_address\": \"13cuYFmbEnnL9CnRUNWRxVxSoVyc3QFKKd\", \n    \"profile\": {\n      \"@context\": \"http://schema.org\", \n      \"@type\": \"Person\", \n      \"api\": {\n        \"gaiaHubConfig\": {\n          \"url_prefix\": \"https://gaia.blockstack.org/hub/\"\n        }, \n        \"gaiaHubUrl\": \"https://hub.blockstack.org\"\n      }, \n      \"apps\": {\n        \"http://localhost:8080\": \"https://gaia.blockstack.org/hub/1LLWuf9bp7djMaJktYQEx1kDFSUJL62yfY/\", \n        \"http://localhost:8081\": \"https://gaia.blockstack.org/hub/13hGkw4i91AnKD1srgMXbDqCmh6pdDzBMQ/\", \n        \"http://localhost:8082\": \"https://gaia.blockstack.org/hub/1636wMc8VYcfUQzVLtNWiAGeSVKCGWn1yj/\", \n        \"http://localhost:8083\": \"https://gaia.blockstack.org/hub/1Gr8NEXV9pcTcqhtNP1DoLBCU1LPaoLjPS/\", \n        \"http://loopbomb.local\": \"https://gaia.blockstack.org/hub/1BHc8WrjdkZf9Q21AoB63zb3X5yLdcUcxi/\", \n        \"https://app.misthos.io\": \"https://gaia.blockstack.org/hub/1Q9U6Q8F4QxZCcWJuhidtbyDTJrqtffrtS/\", \n        \"https://banter.pub\": \"https://gaia.blockstack.org/hub/1GQfAYkfc8iDjUsqaaAKSBi5i45Srgwx9W\", \n        \"https://explorer.stacks.co\": \"https://gaia.blockstack.org/hub/1Jaz61cmwnr27WDhfyaZxgFJsZtrs3tvZu/\", \n        \"https://gifun.fun\": \"https://gaia.blockstack.org/hub/1Kvpqbp5qGRbNrym3LeCb9H6rWdJJqwsSP/\", \n        \"https://kit.st\": \"https://gaia.blockstack.org/hub/1AARB1uMnBMLVSehp8nWsaGKdZRJyJwock/\", \n        \"https://loopbomb.com\": \"https://gaia.blockstack.org/hub/1EKPiixoqur68bm4HHyLaUeent447pTVPo/\", \n        \"https://loopbomb.risidio.com\": \"https://gaia.blockstack.org/hub/1PDpCyHLemPFU3CUFEpGhXf72DG8Ti4ZMA/\", \n        \"https://pdrive.co\": \"https://gaia.blockstack.org/hub/1BknFUAKsrfsqdAConTVg57ap4MLjeJuaU/\", \n        \"https://radicle.art\": \"https://gaia.blockstack.org/hub/1CNcfHzg9nbC9PBUe6jFdWuYUSRP14scQ1/\", \n        \"https://speed-spend.netlify.app\": \"https://gaia.blockstack.org/hub/1PBwB72sbRhskqnhPnSiuYvfb6JNt5Ew9N/\", \n        \"https://test.loopbomb.com\": \"https://gaia.blockstack.org/hub/15KWDw1jxJy2xF7H3BdgPEPPLxpnNhh47w/\", \n        \"https://testnet-explorer.blockstack.org\": \"https://gaia.blockstack.org/hub/1NvXxAhzhY99MgBdBXpfHpVWNJqnKmz2bW/\"\n      }, \n      \"appsMeta\": {\n        \"http://localhost:8080\": {\n          \"publicKey\": \"035c4ea02f85cb4a9ef617662f5888e3bbefcbe3b2b0b76c9352f2629ea94d7176\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1LLWuf9bp7djMaJktYQEx1kDFSUJL62yfY/\"\n        }, \n        \"http://localhost:8081\": {\n          \"publicKey\": \"036aeb310f4d81ffde243fa3b603413ca2e1ea25e5cf61e94c26a7b1252bd525b1\", \n          \"storage\": \"https://gaia.blockstack.org/hub/13hGkw4i91AnKD1srgMXbDqCmh6pdDzBMQ/\"\n        }, \n        \"http://localhost:8082\": {\n          \"publicKey\": \"03464cbb5cc7062c9d74189f85aa4739bc113e467ff6283e23ca4256f515937a92\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1636wMc8VYcfUQzVLtNWiAGeSVKCGWn1yj/\"\n        }, \n        \"https://explorer.stacks.co\": {\n          \"publicKey\": \"0342956ac504d279b3c12ff14873924cf0999b345b4279c9b37d1cc71c6e000abe\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1Jaz61cmwnr27WDhfyaZxgFJsZtrs3tvZu/\"\n        }, \n        \"https://loopbomb.risidio.com\": {\n          \"publicKey\": \"03424ce45870b4d2aaff5228d46f6e34e5c5159c2d80d10f3e02cf494cecf215b0\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1PDpCyHLemPFU3CUFEpGhXf72DG8Ti4ZMA/\"\n        }, \n        \"https://speed-spend.netlify.app\": {\n          \"publicKey\": \"03a493a35cb9bb9d34c3bb2f991569d1a4249db7d68520c70835edb68a855da4ad\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1PBwB72sbRhskqnhPnSiuYvfb6JNt5Ew9N/\"\n        }, \n        \"https://test.loopbomb.com\": {\n          \"publicKey\": \"02d9764ff50aed11e8629c4195e9913946d3737b1b67ec885a0ef7577afe022044\", \n          \"storage\": \"https://gaia.blockstack.org/hub/15KWDw1jxJy2xF7H3BdgPEPPLxpnNhh47w/\"\n        }, \n        \"https://testnet-explorer.blockstack.org\": {\n          \"publicKey\": \"029de9d3722c8d2717b1a46111a26378219c536c985c6212609a7114c0463f765b\", \n          \"storage\": \"https://gaia.blockstack.org/hub/1NvXxAhzhY99MgBdBXpfHpVWNJqnKmz2bW/\"\n        }\n      }, \n      \"description\": \"\", \n      \"name\": \"\"\n    }, \n    \"public_key\": \"02824258eae3e560a3a8b6f7774e9e013906a4079898ac0ccaf4eae33da1fb78e7\", \n    \"verifications\": [\n      \"No verifications for non-id namespaces.\"\n    ], \n    \"zone_file\": {\n      \"$origin\": \"radicle_art.id.blockstack\", \n      \"$ttl\": 3600, \n      \"uri\": [\n        {\n          \"name\": \"_http._tcp\", \n          \"priority\": 10, \n          \"target\": \"https://gaia.blockstack.org/hub/13cuYFmbEnnL9CnRUNWRxVxSoVyc3QFKKd/profile.json\", \n          \"weight\": 1\n        }\n      ]\n    }\n  }\n}\n";
	static GaiaHubReader cd = new GaiaHubReader();
	static ObjectMapper mapper;

	@BeforeAll
	static void setup() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		cd.setMapper(mapper);
	}
	
	@Test
	void test_getAppsFromJsoProfile() {
		try {
			
			UserAppMaps uam = cd.parseApps(profile, "radicle_art.id.blockstack");

			AppsModel appsModel = uam.getApps().get("https://loopbomb.com");
			assertTrue(appsModel.getStorage().equals("https://gaia.blockstack.org/hub/1EKPiixoqur68bm4HHyLaUeent447pTVPo/"));
		
			appsModel = uam.getApps().get("https://loopbomb.risidio.com");
			assertTrue(appsModel.getStorage().equals("https://gaia.blockstack.org/hub/1PDpCyHLemPFU3CUFEpGhXf72DG8Ti4ZMA/"));
			
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

}
