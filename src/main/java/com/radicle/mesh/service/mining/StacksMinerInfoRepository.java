package com.radicle.mesh.service.mining;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.radicle.mesh.service.mining.domain.GroupedWinnerDistribution;
import com.radicle.mesh.service.mining.domain.StacksMinerInfo;

@Repository
public interface StacksMinerInfoRepository extends MongoRepository<StacksMinerInfo, String> {

	@Aggregation(value="{$group : { _id: '$actual_win', count: {$sum: 1}, totalBurned: {$sum: '$miner_burned' }, totalBlocksMined: {$sum: '$total_mined' }}}, {$sort: {count: -1}}")
	List<GroupedWinnerDistribution> groupByActualWins();

}
