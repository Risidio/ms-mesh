package com.radicle.mesh.email.api;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "TransacEmail")
public class TransacEmail {
    private String fromEmail;
    private String fromName;

    private List<String> to;
    private List<String> cc;

    private String subject;
    private String body;

    private String attachmentName;
    private String attachmentContent;

    //Getters and Setters omitted for brevity

}
