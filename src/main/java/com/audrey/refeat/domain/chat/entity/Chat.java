package com.audrey.refeat.domain.chat.entity;

import com.audrey.refeat.domain.chat.dto.response.ChatListResponseDto;
import com.audrey.refeat.domain.chat.dto.response.ChatReferenceResponseDto;
import com.audrey.refeat.domain.project.entity.ColumnValue;
import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.user.entity.UserInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Entity
@Getter
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String contentRefined;
    @Column
    private String mention;
    @Column
    private String reference;
    @Column
    private LocalDateTime createdAt;
    @Column
    private String image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private UserInfo user;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Reference> references;

    @Builder
    public Chat(Long id, String name, String content, String mention, String reference, Project project, UserInfo user, String image, String contentRefined) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.contentRefined = contentRefined;
        this.mention = mention;
        this.reference = reference;
        this.project = project;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.image = image;
    }

    public List<Long> getMentionList() {
        if (mention == null || mention.equals("")) {
            return List.of();
        }
        return Stream.of(mention.split(","))
                .map(Long::parseLong)
                .toList();
    }

    public List<ChatReferenceResponseDto> getReferenceList() {

        if (references == null || references.isEmpty()) {
            return List.of();
        }
        return references.stream()
                .map(ref -> new ChatReferenceResponseDto(
                        ref.getIndexNumber(),
                        ref.getDocumentId().toString(),
                        ref.getChunk()
                ))
                .toList();
    }

    public void setImage(String image) {
        this.image = image;
    }

}
