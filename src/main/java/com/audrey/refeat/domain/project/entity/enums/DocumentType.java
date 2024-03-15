package com.audrey.refeat.domain.project.entity.enums;

public enum DocumentType {
    WEB,
    PDF;

    @Override
    public String toString(){
        return name();
    }
}
