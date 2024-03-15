package com.audrey.refeat.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Disabled // 테스트를 실행하지 않음
@AutoConfigureMockMvc
@Transactional
public class BaseIntegrationTest { // 상속을 위한 클래스

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
