package dev.fleetingclarity;

import com.slack.api.model.Message;
import com.slack.api.model.event.MessageEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordleSlackMessageParser {
    private static final Pattern SCORE_PATTERN = Pattern.compile("Wordle ([\\d,]+) (\\d)/6");
    private static final int failureScore = 7;

    public WordleScore parse(Message message) {
        Matcher matcher = SCORE_PATTERN.matcher(message.getText());
        if (matcher.find()) {
            int puzzleNumber = parsePuzzleNumber(matcher.group(1));
            int score = parseScore(matcher.group(2));

            LocalDate puzzleDate = parseLocalDateFromSlackTimestamp(message.getTs());
            return new WordleScore(
                    message.getUser(),
                    message.getUsername(),
                    message.getClientMsgId(),
                    score,
                    puzzleNumber,
                    puzzleDate
            );
        }
        return null;
    }

    public WordleScore parse(MessageEvent messageEvent) {
        Message message = new Message();
        message.setText(messageEvent.getText());
        message.setUser(messageEvent.getUser());
        message.setUsername("");
        message.setClientMsgId(messageEvent.getClientMsgId());
        message.setTs(messageEvent.getTs());
        return parse(message);
    }

    public boolean hasWordleScore(final String messageText) {
        return SCORE_PATTERN.matcher(messageText).find();
    }

    private int parsePuzzleNumber(final String val) {
        try {
            var sanitizedVal = val.replace(",", "");
            return Integer.parseInt(sanitizedVal);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseScore(final String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            // a failed puzzle will have "x/6" rather than a valid integer
            return failureScore;
        }
    }

    private LocalDate parseLocalDateFromSlackTimestamp(final String ts) {
        long epochSeconds = Long.parseLong(ts.split("\\.")[0]);
        return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
