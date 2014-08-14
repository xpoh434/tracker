package me.tracker;

import java.math.BigDecimal;

public class PriceRange {
	@Override
	public String toString() {
		return "PriceRange [minMid=" + minMid + ", maxMid=" + maxMid
				+ ", minVol=" + minVol + ", maxVol=" + maxVol + "]";
	}
	BigDecimal minMid;
	BigDecimal maxMid;
	BigDecimal minVol;
	BigDecimal maxVol;
	public PriceRange(BigDecimal minMid, BigDecimal maxMid, BigDecimal minVol,
			BigDecimal maxVol) {
		super();
		this.minMid = minMid;
		this.maxMid = maxMid;
		this.minVol = minVol;
		this.maxVol = maxVol;
	}
	public BigDecimal getMinMid() {
		return minMid;
	}
	public BigDecimal getMaxMid() {
		return maxMid;
	}
	public BigDecimal getMinVol() {
		return minVol;
	}
	public BigDecimal getMaxVol() {
		return maxVol;
	}
}
