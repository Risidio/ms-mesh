package com.radicle.mesh.api.model.stxbuffer;

public enum ReadOnlyFunctionNames {

	GET_TOKEN_BY_INDEX("get-token-by-index"),
	GET_TOKEN_BY_HASH("get-token-by-hash"),
	GET_BASE_TOKEN_URI("get-base-token-uri"),
	GET_MINT_COUNTER("get-mint-counter"),
	GET_APP("get-app"),
	GET_CONTRACT_DATA("get-contract-data"),
	GET_OFFER_AT_INDEX("get-offer-at-index"),
	GET_BID_AT_INDEX("get-bid-at-index"),
	GET_TRANSFER_AT_INDEX("get-transfer-at-index"),
	GET_APP_COUNTER("get-app-counter");

	private String name;
	
	private ReadOnlyFunctionNames(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
