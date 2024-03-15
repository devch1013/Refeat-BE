package com.audrey.refeat.domain.chat.dto.response;

import java.util.List;

public record ChatListResponseWrapperDto(
        Boolean hasNext,
        List<ChatListResponseDto> chatList
) {
}
