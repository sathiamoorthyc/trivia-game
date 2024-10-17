package org.trivia.game.model;

import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class SubmitAnswerRequestResponse {

    @Transient
    private String answer;
    @Transient
    private String result;
}
