package me.tracker.repositories;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import me.tracker.PriceRange;
import me.tracker.config.TestConfig;
import me.tracker.config.WebAppConfig;
import me.tracker.db.jpa.entities.Price;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebAppConfig.class, TestConfig.class})
@ActiveProfiles({"test", "jpa"})
public class PriceRepositoryTest {
	
	@Inject
	PriceRepository priceRepo;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	@Transactional
	@Rollback
	public void testFindMinPrice() {
		Price p = new Price();
		p.setAsk(new BigDecimal("1"));
		p.setBid(new BigDecimal("1"));
		p.setSymbol("TEST");
		p.setTime(new Date());
		p.setVol(new BigDecimal("3"));
		p.setMid(new BigDecimal("2"));
		
		p = priceRepo.save(p);
		
		p = new Price();
		p.setAsk(new BigDecimal("1"));
		p.setBid(new BigDecimal("2"));
		p.setSymbol("TEST");
		p.setTime(new Date());
		p.setVol(new BigDecimal("4"));
		p.setMid(new BigDecimal("1"));
		
		p = priceRepo.save(p);
		
		Date since = new DateTime().minusMinutes(1).toDate();
		PriceRange r = priceRepo.findPriceVolRangeSince("TEST", since);
		assertEquals(1,r.getMinMid().intValue());
		assertEquals(2,r.getMaxMid().intValue());
		assertEquals(3,r.getMinVol().intValue());
		assertEquals(4,r.getMaxVol().intValue());
		
	}
	
	@Test
	@Transactional
	@Rollback
	public void testFindBySymbol() {
		Price p = new Price();
		p.setAsk(new BigDecimal("1"));
		p.setBid(new BigDecimal("1"));
		p.setSymbol("UTEST");
		p.setTime(new Date());
		p.setVol(new BigDecimal("1"));
		
		p = priceRepo.save(p);
		
		assertNotNull(p.getId());
		
		System.out.println(p);
		
		List<Price> prices = priceRepo.findBySymbol("UTEST");
		
		assertTrue(prices.size() == 1);
		assertEquals(p.getId(),prices.get(0).getId());
	}

}
