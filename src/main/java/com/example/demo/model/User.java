package com.example.demo.model;

import lombok.*;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    public final String user_id;

    public final String name;
    public final String password;
}
