package com.example.demo.entity;

import lombok.*;
import org.springframework.scheduling.support.SimpleTriggerContext;


@Getter
@Setter
@Builder
@RequiredArgsConstructor

public class PushPayload {
    private String alertTitle;
    private String alertBody;
    private String sound;
}
