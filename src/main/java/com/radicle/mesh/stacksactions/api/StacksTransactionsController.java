package com.radicle.mesh.stacksactions.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.radicle.mesh.stacksactions.service.StacksTransactionRepository;
import com.radicle.mesh.stacksactions.service.domain.StacksTransaction;

@RestController
@EnableAsync
@EnableScheduling
public class StacksTransactionsController {

	private static final Logger logger = LogManager.getLogger(StacksTransactionsController.class);
	@Autowired
	private StacksTransactionRepository stacksTransactionRepository;

	@PostMapping(value = "/v2/transaction")
	public StacksTransaction registerTransaction(@RequestBody StacksTransaction stacksTransaction) {
		StacksTransaction st = stacksTransactionRepository.findByTxId(stacksTransaction.getTxId());
		logger.info("Registering tx: " + stacksTransaction.getTxId());
		if (st != null) {
			stacksTransaction.setId(st.getId());
		}
		stacksTransactionRepository.save(stacksTransaction);
		return stacksTransaction;
	}

	@PutMapping(value = "/v2/transaction")
	public StacksTransaction updateTransaction(HttpServletRequest request, @RequestBody StacksTransaction stacksTransaction) {
		StacksTransaction st = stacksTransactionRepository.findByTxId(stacksTransaction.getTxId());
		if (st == null) {
			st = stacksTransactionRepository.save(st);
		}
		st.setTxStatus(stacksTransaction.getTxStatus());
		stacksTransactionRepository.save(st);
		return st;
	}

	@GetMapping(value = "/v2/transaction/{txId}")
	public StacksTransaction fetchTransactions(@PathVariable String txId) {
		return stacksTransactionRepository.findByTxId(txId);
	}

	@GetMapping(value = "/v2/transactions/{contractId}/{nftIndex}")
	public List<StacksTransaction> fetchTransactions(@PathVariable String contractId, @PathVariable Long nftIndex) {
		return stacksTransactionRepository.findByContractIdAndNftIndex(contractId, nftIndex);
	}

	@GetMapping(value = "/v2/transactionsByNftIndexAndTxStatus/{contractId}/{nftIndex}/{txStatus}")
	public List<StacksTransaction> fetchTransactionsByStatus(@PathVariable String contractId, @PathVariable Long nftIndex, @PathVariable String txStatus) {
		return stacksTransactionRepository.findByContractIdAndNftIndexAndTxStatus(contractId, nftIndex, txStatus);
	}

	@GetMapping(value = "/v2/transactionsByTxStatus/{contractId}/{txStatus}")
	public List<StacksTransaction> fetchTransactionsByStatus(@PathVariable String contractId, @PathVariable String txStatus) {
		return stacksTransactionRepository.findByContractIdAndTxStatus(contractId, txStatus);
	}

	@GetMapping(value = "/v2/transactionsByAssetHash/{contractId}/{assetHash}")
	public List<StacksTransaction> fetchTransactions(@PathVariable String contractId, @PathVariable String assetHash) {
		return stacksTransactionRepository.findByContractIdAndAssetHash(contractId, assetHash);
	}

	@GetMapping(value = "/v2/transactionsByFunctionName/{contractId}/{functionName}")
	public List<StacksTransaction> fetchTransactionsByFunction(@PathVariable String contractId, @PathVariable String functionName) {
		return stacksTransactionRepository.findByContractIdAndAssetHash(contractId, functionName);
	}

}
