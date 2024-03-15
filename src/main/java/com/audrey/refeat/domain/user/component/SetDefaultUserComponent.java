package com.audrey.refeat.domain.user.component;

import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetDefaultUserComponent {
    private final UserInfoRepository userInfoRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        System.out.println("contextRefreshedEvent");
        if (userInfoRepository.findById(1L).isEmpty())
            userInfoRepository.save(UserInfo
                    .builder()
                    .id(1L)
                    .nickname("Refeat")
                    .email("Refeat@gmail.com")
                    .profileImage("https://d1ko5ecn45u2gs.cloudfront.net/profile/Refeat.png")
                    .build());
        //do whatever
    }
}
