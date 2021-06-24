package com.radicle.mesh.crowdfund.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.radicle.mesh.crowdfund.api.model.BackerData;
import com.radicle.mesh.crowdfund.api.model.CrowdfundTarget;
import com.radicle.mesh.crowdfund.api.model.DonationData;
import com.radicle.mesh.payments.service.PaymentRepository;
import com.radicle.mesh.payments.service.domain.Payment;

@Service
public class CrowdfundServiceImpl implements CrowdfundService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PaymentRepository paymentRepository;

	public CrowdfundTarget getCrowdfundTarget(String projectId) {
		AggregationOperation group = Aggregation.group("paymentType").sum("amountMoney.amount").as("total");
		Aggregation aggregation = Aggregation.newAggregation(group);
		List<DonationData> dd = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Payment.class), DonationData.class).getMappedResults();
		CrowdfundTarget ct = new CrowdfundTarget();
		ct.setDonationData(dd);
		ct.setBackerData(getUniqueBackers());
 		return ct;
	}
	
	private BackerData getUniqueBackers() {
		GroupOperation groupOperation = Aggregation.group("username");
		CountOperation countOperation = Aggregation.count().as("total");
		Aggregation aggregation = Aggregation.newAggregation(groupOperation, countOperation);

		BackerData result = mongoTemplate.aggregate(aggregation, "backer", BackerData.class)
		        .getUniqueMappedResult();
		return result;
	}

	public CrowdfundTarget crowdfundTotalsManual(@PathVariable String projectId) {
		List<Payment> payments = paymentRepository.findByPaymentType("square");
		float sum = 0;
		for (Payment payment : payments) {
			sum += payment.getAmountMoney().getAmount() / 100;
		}
		DonationData dd = new DonationData();
		dd.setPaymentType("square");
		dd.setTotal(sum);
		List<DonationData> dds = new ArrayList();
		dds.add(dd);
		CrowdfundTarget ct = new CrowdfundTarget();
		ct.setDonationData(dds);
 		return ct;
	}
}