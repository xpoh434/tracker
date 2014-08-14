package me.tracker;

import static org.junit.Assert.*;

import javax.inject.Inject;

import me.tracker.config.TestConfig;
import me.tracker.config.WebAppConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebAppConfig.class, TestConfig.class})
@ActiveProfiles("test")
public class StockUpdateSchedulerTest {
	
	@Inject
	StockUpdateScheduler scheduler;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidate() {
		assertFalse(scheduler.validate("9999.HK"));
		assertTrue(scheduler.validate("0001.HK"));
	}


}
