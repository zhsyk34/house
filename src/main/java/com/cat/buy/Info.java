package com.cat.buy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Info {
	private String link;
	private BigDecimal amount;
	private BigDecimal area;
	private BigDecimal price;
	private String location;
	private String floor;
	private String type;
}
