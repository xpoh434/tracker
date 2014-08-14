package me.tracker.db.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-03-16T13:33:03.743+0800")
@StaticMetamodel(Price.class)
public class Price_ {
	public static volatile SingularAttribute<Price, String> id;
	public static volatile SingularAttribute<Price, String> symbol;
	public static volatile SingularAttribute<Price, Date> time;
	public static volatile SingularAttribute<Price, BigDecimal> bid;
	public static volatile SingularAttribute<Price, BigDecimal> vol;
	public static volatile SingularAttribute<Price, BigDecimal> ask;
	public static volatile SingularAttribute<Price, BigDecimal> mid;
}
