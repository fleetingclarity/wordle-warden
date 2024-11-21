package dev.fleetingclarity.wordlewarden.scores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WordleScore {
    String userId;
    String username;
    String messageId;
    int score;
    int puzzleNumber;
    LocalDate puzzleDate;
}
