package com.wonderprints.isomorphic.example.actions;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class Action {
    @NonNull
    private String type;
}
