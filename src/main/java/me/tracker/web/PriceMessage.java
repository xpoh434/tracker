package me.tracker.web;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import me.tracker.db.jpa.entities.Price;

@XmlRootElement
public class PriceMessage implements Serializable {
	
	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public BigDecimal getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}

	public BigDecimal getVolChange() {
		return volChange;
	}

	public void setVolChange(BigDecimal volChange) {
		this.volChange = volChange;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4667709048307913412L;

	private Price price;
	
	private BigDecimal priceChange;

	private BigDecimal volChange;
	
	@Override
	public String toString() {
		return "PriceMessage [price=" + price + ", priceChange=" + priceChange
				+ ", volChange=" + volChange + "]";
	}

}
