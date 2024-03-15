package com.audrey.refeat.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Integer indexNumber;
    @Column
    private UUID documentId;
    @Column(columnDefinition = "TEXT")
    private String chunk;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat")
    private Chat chat;


}
