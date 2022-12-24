package com.example.demo.model;

import lombok.*;

@Data
@Getter
@Setter
@RequiredArgsConstructor
public class User {
    public final String firstName;
    public final String lastName;
    public final String password;
}
