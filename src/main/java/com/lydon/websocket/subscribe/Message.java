package com.lydon.websocket.subscribe;

import lombok.*;

/**
 * author liuyd 2020/03/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String to;
    private Long date;
    private String from;
    private String content;
}
