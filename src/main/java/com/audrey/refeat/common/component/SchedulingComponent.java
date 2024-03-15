package com.audrey.refeat.common.component;

import com.audrey.refeat.common.component.RequestComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulingComponent {

    private final RequestComponent requestComponent;

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void healthCheck() {
        if (!requestComponent.healthCheck()) {
            log.error("AI 서버가 정상적으로 동작하지 않습니다.");
        }
    }
}
