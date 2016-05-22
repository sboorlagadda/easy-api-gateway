package io.easystack.apigateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ZuulServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class ZuulServerApplicationTests {
	
	@Value("${local.server.port}")
	private int port = 0;
	
	@Test
	public void contextLoads() {
		
	}
}
