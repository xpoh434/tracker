package me.tracker.repositories;

import java.util.Date;
import java.util.List;

import me.tracker.PriceRange;
import me.tracker.db.jpa.entities.Price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PriceRepository extends JpaRepository<Price, Long>{
	List<Price> findBySymbol(String symbol);
	@Query(value ="select new me.tracker.PriceRange(min(mid),max(mid),min(vol),max(vol)) from Price where symbol = :symbol and time > :since and vol > 0 and mid > 0")
	PriceRange findPriceVolRangeSince(@Param("symbol") String symbol, @Param("since") Date since);
	@Query(value ="select p from Price p where symbol = :symbol and time = (select max(time) from Price where symbol = :symbol)")
	List<Price> findlatestPriceBySymbol(@Param("symbol") String symbol);
}
