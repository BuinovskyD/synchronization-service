package com.intech.rkn.ksim.synchronization_service;

import com.intech.rkn.ksim.synchronization_service.config.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(Configuration.class)
@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}
}
