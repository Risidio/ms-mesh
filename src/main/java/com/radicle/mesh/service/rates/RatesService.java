package com.radicle.mesh.service.rates;

import java.util.List;

import com.radicle.mesh.service.rates.domain.BinanceRate;
import com.radicle.mesh.service.rates.domain.TickerRate;


public interface RatesService
{
	public BinanceRate findLatestBinanceRate();

	public BinanceRate findLatestBinanceEthRate();

	public TickerRate findLatestTickerRate();

	List<BinanceRate> findBinanceRatesByCloseTime(Integer limit);

	public List<TickerRate> findTickerRatesByCloseTime(Integer limit);
}
