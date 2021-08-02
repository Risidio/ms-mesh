package com.radicle.mesh.api.model.stxbuffer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.radicle.mesh.stacks.service.ClarityDeserialiser;
import com.radicle.mesh.stacks.service.ClarityType;
import com.radicle.mesh.stacks.service.ReadOnlyFunctionNames;
import com.radicle.mesh.stacks.service.domain.Token;

class ClarityDeserialiserTest {
	//{"okay":true,"result":"0x07070c0000000c0a626964436f756e74657201000000000000000000000000000000000a626964486973746f7279090e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000010c6f66666572436f756e7465720100000000000000000000000000000000066f666665727309056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000000000001062696464696e672d656e642d74696d6501000000000000000000000178c644e4780d696e6372656d656e742d73747801000000000000000000000000000000000b726573657276652d7374780100000000000000000000000002faf0801073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000309746f6b656e496e666f0a0c000000060a61737365742d686173680200000020bd890ccc7427d47b4b86b734c919feb1b64ce088271e315964c32f0dd10efd04046461746501000000000000000000000000000001270765646974696f6e01000000000000000000000000000000010d676169612d757365726e616d6502000000106d696b652e706572736f6e616c2e69640c6d61782d65646974696f6e7301000000000000000000000000000000050f7365726965732d6f726967696e616c01000000000000000000000000000000010f7472616e73666572436f756e7465720100000000000000000000000000000000127472616e73666572486973746f72794d6170090b7472616e736665724d617009"}
	//{"okay":true,"result":"0x07070c0000000c0a626964436f756e74657201000000000000000000000000000000000a626964486973746f7279090e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000020c6f66666572436f756e7465720100000000000000000000000000000000066f666665727309056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000000000001062696464696e672d656e642d74696d6501000000000000000000000178c64663480d696e6372656d656e742d73747801000000000000000000000000000000000b726573657276652d73747801000000000000000000000000047868c01073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000309746f6b656e496e666f0a0c000000060a61737365742d6861736802000000202522cec483346f41c9458823328a56b02f80c3d98a93213e3af7a951db16ed59046461746501000000000000000000000000000002220765646974696f6e01000000000000000000000000000000010d676169612d757365726e616d6502000000106d696b652e706572736f6e616c2e69640c6d61782d65646974696f6e7301000000000000000000000000000000050f7365726965732d6f726967696e616c01000000000000000000000000000000020f7472616e73666572436f756e7465720100000000000000000000000000000000127472616e73666572486973746f72794d6170090b7472616e736665724d617009"}
	private String getTokenByIndexJson2 = "{\"okay\":true,\"result\":\"0x07070c0000000c0a626964436f756e74657201000000000000000000000000000000000a626964486973746f7279090e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000030c6f66666572436f756e7465720100000000000000000000000000000001066f66666572730a0c000000050861636365707465640406616d6f756e740100000000000000000000000003dfd240096d6164652d6461746501000000000000000000000178c64ab8a8076f666665726572051a1f2735d7587c165cef01199309acc969dfef35ef0a73616c652d6379636c650100000000000000000000000000000001056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000000000001062696464696e672d656e642d74696d6501000000000000000000000178c64ab8a80d696e6372656d656e742d73747801000000000000000000000000000000000b726573657276652d7374780100000000000000000000000003dfd2401073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000309746f6b656e496e666f0a0c000000060a61737365742d686173680200000020739754de23d76f0f78fadcc68dcd306d8499c7c6e7c4c890fc572475587699370464617465010000000000000000000000000000023a0765646974696f6e01000000000000000000000000000000010d676169612d757365726e616d6502000000106d696b652e706572736f6e616c2e69640c6d61782d65646974696f6e7301000000000000000000000000000000050f7365726965732d6f726967696e616c01000000000000000000000000000000030f7472616e73666572436f756e7465720100000000000000000000000000000000127472616e73666572486973746f72794d6170090b7472616e736665724d617009\"}";
	
	// token with beneficiaries
	// 0x07070c000000090f62656e6566696369617279446174610a0c00000002096164647265737365730b0000000a051a1f2735d7587c165cef01199309acc969dfef35ef051a29e515caaf0cc046b90e4a7df4a2a0167496317b051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc051a16ead603dcb0895f7ba61a954ca68585c7f9afcc067368617265730b0000000a010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca00010000000000000000000000003b9aca000a626964436f756e74657201000000000000000000000000000000000e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000000c6f66666572436f756e7465720100000000000000000000000000000000056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000000000001062696464696e672d656e642d74696d6501000000000000000000000000001baf800d696e6372656d656e742d73747801000000000000000000000000000000000b726573657276652d73747801000000000000000000000000000000001073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000309746f6b656e496e666f0a0c000000060a61737365742d686173680200000020bd890ccc7427d47b4b86b734c919feb1b64ce088271e315964c32f0dd10efd0404646174650100000000000000000000000000000c5c0765646974696f6e01000000000000000000000000000000010d676169612d757365726e616d65020000001972616469636c655f6172742e69642e626c6f636b737461636b0c6d61782d65646974696f6e7301000000000000000000000000000000640f7365726965732d6f726967696e616c01000000000000000000000000000000000f7472616e73666572436f756e7465720100000000000000000000000000000000
	private String getAppCounterJson = "{\"okay\":true,\"result\":\"0x070000000000000000000000000000000001\"}";
	private String getAppCounter = "0x070000000000000000000000000000000001";
	private String getAppJson = "{\"okay\":true,\"result\":\"0x070c000000040f6170702d636f6e74726163742d6964020000003553543145535943474a42355a354e4248533339585043373050474331345741514b3558584e515944572e6c6f6f70626f6d622d7631056f776e6572020000001972616469636c655f6172742e69642e626c6f636b737461636b0673746174757300000000000000000000000000000000000d73746f726167652d6d6f64656c0000000000000000000000000000000000\"}";
	private String getApp = "0x070c000000040f6170702d636f6e74726163742d6964020000003553543145535943474a42355a354e4248533339585043373050474331345741514b3558584e515944572e6c6f6f70626f6d622d7631056f776e6572020000001972616469636c655f6172742e69642e626c6f636b737461636b0673746174757300000000000000000000000000000000000d73746f726167652d6d6f64656c0000000000000000000000000000000000";
	private String getBaseTokenUriJson = "{\"okay\":true,\"result\":\"0x020000002c68747470733a2f2f6c6f6f70626f6d622e7269736964696f2e636f6d2f696e6465782f76312f61737365742f\"}";
	private String getBaseTokenUri = "0x020000002c68747470733a2f2f6c6f6f70626f6d622e7269736964696f2e636f6d2f696e6465782f76312f61737365742f";
	private String getTokenByIndexJson = "{\"okay\":true,\"result\":\"0x07070c000000080a626964436f756e74657201000000000000000000000000000000000e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000000c6f66666572436f756e7465720100000000000000000000000000000000056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000042c1d801062696464696e672d656e642d74696d6501000000000000000000000000001badf00d696e6372656d656e742d73747801000000000000000000000000009896800b726573657276652d7374780100000000000000000000000005f5e1001073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000209746f6b656e496e666f0a0c000000050a61737365742d686173680200000020a3fa2d1e8631d5aa6d91e35eecfc003c202e1f5cbf3478128715a3b2c16b9014046461746501000000000000000000000000000000110765646974696f6e01000000000000000000000000000000010c6d61782d65646974696f6e7301000000000000000000000000000000640f7365726965732d6f726967696e616c01000000000000000000000000000000000f7472616e73666572436f756e7465720100000000000000000000000000000000\"}";
	private String getTokenByIndex = "0x07070c000000080a626964436f756e74657201000000000000000000000000000000000e65646974696f6e436f756e7465720100000000000000000000000000000001086e6674496e64657801000000000000000000000000000000000c6f66666572436f756e7465720100000000000000000000000000000000056f776e6572051a1f2735d7587c165cef01199309acc969dfef35ef0873616c65446174610a0c000000060a616d6f756e742d73747801000000000000000000000000042c1d801062696464696e672d656e642d74696d6501000000000000000000000000001badf00d696e6372656d656e742d73747801000000000000000000000000009896800b726573657276652d7374780100000000000000000000000005f5e1001073616c652d6379636c652d696e64657801000000000000000000000000000000010973616c652d74797065010000000000000000000000000000000209746f6b656e496e666f0a0c000000050a61737365742d686173680200000020a3fa2d1e8631d5aa6d91e35eecfc003c202e1f5cbf3478128715a3b2c16b9014046461746501000000000000000000000000000000110765646974696f6e01000000000000000000000000000000010c6d61782d65646974696f6e7301000000000000000000000000000000640f7365726965732d6f726967696e616c01000000000000000000000000000000000f7472616e73666572436f756e7465720100000000000000000000000000000000";
	static ClarityDeserialiser cd = new ClarityDeserialiser();
	ClarityType ct = null;

	@BeforeAll
	static void setup() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		cd.setMapper(mapper);
	}
	
	@Test
	void test_getTokenByIndex() {
		try {
			ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_INDEX;
			Map<String, Object> data = cd.deserialise(fname.getName(), getTokenByIndexJson);
			Token token = Token.fromMap(0, (Map)data.get(fname.getName()), "contractId");
			assertTrue(data.size() == 1);
			Map<String, Object> nm = (Map)data.get(fname.getName());
			assertTrue( nm.size() == 8);
			ct = (ClarityType)nm.get("owner");
			assertTrue( ((String)ct.getValueHex()).equals("1f2735d7587c165cef01199309acc969dfef35ef"));
			Map<String, Object> tokenInfo = (Map)nm.get("tokenInfo");
			ct = (ClarityType)tokenInfo.get("asset-hash");
			assertTrue( ((String)ct.getValueHex()).equals("a3fa2d1e8631d5aa6d91e35eecfc003c202e1f5cbf3478128715a3b2c16b9014"));
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

	@Test
	void test_getTokenByIndex2() {
		try {
			ReadOnlyFunctionNames fname = ReadOnlyFunctionNames.GET_TOKEN_BY_INDEX;
			Map<String, Object> data = cd.deserialise(fname.getName(), getTokenByIndexJson2);
			Token token = Token.fromMap(0, (Map)data.get(fname.getName()), "contractId");
			assertTrue(data.size() == 1);
			Map<String, Object> nm = (Map)data.get(fname.getName());
			assertTrue( nm.size() == 8);
			ct = (ClarityType)nm.get("owner");
			assertTrue( ((String)ct.getValueHex()).equals("1f2735d7587c165cef01199309acc969dfef35ef"));
			Map<String, Object> tokenInfo = (Map)nm.get("tokenInfo");
			ct = (ClarityType)tokenInfo.get("asset-hash");
			assertTrue( ((String)ct.getValueHex()).equals("a3fa2d1e8631d5aa6d91e35eecfc003c202e1f5cbf3478128715a3b2c16b9014"));
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

	@Test
	void test_getBaseTokenUri() {
		try {
			Map<String, Object> data = cd.deserialise("getBaseTokenUri", getBaseTokenUriJson);
			assertTrue(data.size() == 1);
			ct = (ClarityType)data.get("getBaseTokenUri");
			assertTrue( ct.getType() == 2);
			assertTrue( ((String)ct.getValue()).equals("https://loopbomb.risidio.com/index/v1/asset/"));
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

	@Test
	void test_getAppCounter() {
		try {
			Map<String, Object> data = cd.deserialise("getAppCounter", getAppCounterJson);
			assertTrue(data.size() == 1);
			ct = (ClarityType)data.get("getAppCounter");
			assertTrue( ct.getType() == 0);
			assertTrue( ((BigInteger)ct.getValue()).compareTo(BigInteger.ONE) == 0);
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

	@Test
	void test_getApp() {
		try {
			Map<String, Object> data = cd.deserialise("getApp", getAppJson);
			assertTrue(data.size() == 1);
			Map<String, Object> ctMap = (Map)data.get("getApp");
			assertTrue(ctMap.size() == 4);
			ct = (ClarityType)ctMap.get("owner");
			assertTrue( ((String)ct.getValue()).equals("radicle_art.id.blockstack"));
			ct = (ClarityType)ctMap.get("gaia-filename");
			assertTrue( ((String)ct.getValue()).equals("items_v003.json"));
			ct = (ClarityType)ctMap.get("app-contract-id");
			assertTrue( ((String)ct.getValue()).equals("ST1ESYCGJB5Z5NBHS39XPC70PGC14WAQK5XXNQYDW.loopbomb-v1"));
			ct = (ClarityType)ctMap.get("status");
			assertTrue( ((BigInteger)ct.getValue()).compareTo(BigInteger.ZERO) == 0);
			ct = (ClarityType)ctMap.get("storage-model");
			assertTrue( ((BigInteger)ct.getValue()).compareTo(BigInteger.ZERO) == 0);
		} catch (JsonMappingException e) {
			fail("Not expected", e);
		} catch (JsonProcessingException e) {
			fail("Not expected", e);
		}
	}

}
