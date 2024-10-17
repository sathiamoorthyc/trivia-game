package org.trivia.game.openTDb;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenTDbResponse {
    private Integer response_code;
    private OpenTDbResult[] results;
}
