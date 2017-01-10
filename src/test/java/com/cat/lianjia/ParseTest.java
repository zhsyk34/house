package com.cat.lianjia;

import com.cat.dao.HouseDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/spring/spring-dao.xml", "classpath:/spring/spring-service.xml"})
public class ParseTest {

	@Resource
	private HouseDao houseDao;

	@Test
	public void start() throws Exception {
		new Parse(houseDao).start();
	}
}