package com.radicle.mesh.service.mining;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.mining.domain.GroupedStacksBlockWinner;
import com.radicle.mesh.service.mining.domain.StacksBlockWinner;

@Repository
public interface StacksBlockWinnerRepository extends MongoRepository<StacksBlockWinner, String> {

	@Aggregation(value="{$group : { _id: '$stx_address', count: {$sum: 1}, totalBurnFee: {$sum: '$burn_fee' }}}, {$sort: {count: 1, totalBurnFee: 1}}")
	List<GroupedStacksBlockWinner> groupByWinners();

	@Aggregation(value="{$group : { _id: '$stx_address', count: {$sum: 1}, totalBurnFee: {$sum: '$burn_fee' }}}, {$sort: {count: -1}}")
	List<GroupedStacksBlockWinner> groupByBurnFee();

	@Aggregation(value="{$group : { _id: '$stx_address', count: {$sum: 1}, totalBurnFee: {$sum: '$burn_fee' }}}, {$sort: {totalBurnFee: -1}}")
	List<GroupedStacksBlockWinner> groupByDistribution();

	// @Aggregation(pipeline = {"{$group : { _id: '$stx_address', count: {$sum: 1}, totalBurnFee: {$sum: '$burn_fee' }}}, {$group: {_id: '$count', totalBurned:{$sum: '$totalBurnFee'}, totalWins: {$sum: 1}}}, {$sort: {count: -1}}"})
	// List<Object> groupByDistribution();

}
