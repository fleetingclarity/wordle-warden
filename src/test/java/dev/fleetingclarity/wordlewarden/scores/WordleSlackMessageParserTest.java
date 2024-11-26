package dev.fleetingclarity.wordlewarden.scores;

import com.slack.api.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class WordleSlackMessageParserTest {

    @Test
    void testParsesSuccessfulScores() {
        final Message message = new Message();
        final int expectedScore = 3;
        message.setText(String.format("Wordle 1,234 %d/6", expectedScore));
        message.setTs(String.valueOf(Instant.now().getEpochSecond()));
        WordleSlackMessageParser cut = new WordleSlackMessageParser();
        WordleScore result = cut.parse(message);
        Assertions.assertEquals(expectedScore, result.getScore());
    }

    @Test
    void testParsesFailedScores() {
        final Message message = new Message();
        final int expectedScore = 7;
        message.setText("Wordle 1,234 X/6");
        message.setTs(String.valueOf(Instant.now().getEpochSecond()));
        WordleSlackMessageParser cut = new WordleSlackMessageParser();
        WordleScore result = cut.parse(message);
        Assertions.assertEquals(expectedScore, result.getScore());

        // lowercase is not typical, but we should handle it anyway
        message.setText("Wordle 1,234 x/6");
        result = cut.parse(message);
        Assertions.assertEquals(expectedScore, result.getScore());
    }
}
