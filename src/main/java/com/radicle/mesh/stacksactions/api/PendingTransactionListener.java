package com.radicle.mesh.stacksactions.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.radicle.mesh.stacksactions.service.StacksTransactionRepository;
import com.radicle.mesh.stacksactions.service.domain.StacksTransaction;

@Configuration
@EnableScheduling
public class PendingTransactionListener {

	@Autowired 	private StacksTransactionRepository stacksTransactionRepository;
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	List<StacksTransaction> previous;

	/**
	 * check for new NFT every 5 seconds
	 * @throws JsonProcessingException
	 */
	@Scheduled(fixedDelay=5000)
	public void transactionListener() throws JsonProcessingException {
		// List<StacksTransaction> changed = getChanged();
		List<StacksTransaction> transactions = stacksTransactionRepository.findByTxStatus("pending");
		simpMessagingTemplate.convertAndSend("/queue/transaction-news", transactions);
	}
	
//	private List<StacksTransaction> getChanged() {
//		List<StacksTransaction> changed = new ArrayList<StacksTransaction>();
//		List<StacksTransaction> transactions = stacksTransactionRepository.findByTxStatus("pending");
//		if (previous == null || previous.size() == 0) {
//			previous = transactions;
//			return previous;
//		}
//		for (StacksTransaction st1 : previous) {
//			Optional<StacksTransaction> st2 = stacksTransactionRepository.findById(st1.getId());
//			if (st1.getTxId().equals(st2.getTxId())) {
//				if (st1.getTxStatus().equals("pending") && !st2.getTxStatus().equals("pending")) {
//					changed.add(st2);
//				}
//			}
//		}
//		previous = transactions;
//		return changed;
//	}
}
