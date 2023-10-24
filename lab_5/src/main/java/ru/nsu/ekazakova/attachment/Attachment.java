package ru.nsu.ekazakova.attachment;


import java.util.Objects;

public abstract class Attachment {
    private final AttachmentType attachmentType;

    public Attachment( AttachmentType attachmentType) {
        this.attachmentType = Objects.requireNonNull(attachmentType, "Attachment type cant be null");
    }

    public AttachmentType getType() {
        return attachmentType;
    }
}
