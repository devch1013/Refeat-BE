package com.audrey.refeat;

import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class RefeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefeatApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logAtStart() {
        log.info("RefeatApplication started");
    }

}
