package com.audrey.refeat.domain.chat.dto.response;

import com.audrey.refeat.domain.chat.entity.Chat;

import java.util.List;

public record ChatListResponseDto(
        Long chat_id,
        String content,
        Long user_id,
        String user_profile,
        String created_at,
        List<ChatReferenceResponseDto> references,
        List<Long> mentions,
        String Image
) {

    public static List<ChatListResponseDto> fromChatList(List<Chat> chatList) {
        return chatList.stream()
                .map(chat -> new ChatListResponseDto(
                        chat.getId(),
                        chat.getContent(),
                        chat.getUser().getId(),
                        chat.getUser().getProfileImage(),
                        chat.getCreatedAt().toString(),
                        chat.getReferenceList(),
                        chat.getMentionList(),
                        chat.getImage()
                ))
                .toList();
    }

    private static List<Position> getPosition(List<List<Integer>> positionList){
        List<Position> positions = new java.util.ArrayList<>(List.of());
        for (List<Integer> position : positionList){
            positions.add(new Position(position.get(0), position.get(1), position.get(2), position.get(3)));
        }
        return positions;
    }

    private record Position(
            Integer left_x,
            Integer top_y,
            Integer right_x,
            Integer bottom_y
    ){}
}
