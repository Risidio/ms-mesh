package com.radicle.mesh.crowdfund.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Service;

import com.radicle.mesh.crowdfund.api.model.BackerData;
import com.radicle.mesh.crowdfund.api.model.CrowdfundTarget;
import com.radicle.mesh.payments.api.model.ProjectPaymentTotals;
import com.radicle.mesh.payments.service.PaymentService;

@Service
public class CrowdfundServiceImpl implements CrowdfundService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PaymentService paymentService;

	public CrowdfundTarget getCrowdfundTarget(String projectId) {
//		AggregationOperation group = Aggregation.group("paymentType").sum("amountMoney.amount").as("total");
//		Aggregation aggregation = Aggregation.newAggregation(group);
//		List<DonationData> dd = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Payment.class), DonationData.class).getMappedResults();
		ProjectPaymentTotals projectPaymentTotals = paymentService.getProjectPaymentTotals(projectId);
		CrowdfundTarget ct = new CrowdfundTarget();
		ct.setProjectPaymentTotals(projectPaymentTotals);
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
}