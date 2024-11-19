package dev.fleetingclarity;

import com.slack.api.model.Conversation;
import com.slack.api.model.ConversationType;
import com.slack.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SlackChannelScanner {
    private static final Logger log = LoggerFactory.getLogger(SlackChannelScanner.class);
    private final SlackClient slackClient;
    private final WordleSlackMessageParser parser;
    private final WordleScoreDao dao;

    public SlackChannelScanner(final SlackClient slackClient,
                               final WordleSlackMessageParser parser,
                               final WordleScoreDao dao) {
        this.slackClient = slackClient;
        this.parser = parser;
        this.dao = dao;
    }

    public void scanChannel(final String channelName) throws IOException {
        try {
            List<Conversation> channels = slackClient.listChannels(ConversationType.PRIVATE_CHANNEL);
            Conversation targetChannel = channels.stream()
                    .filter(channel -> channel.getName().equalsIgnoreCase(channelName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Channel '%s' not found", channelName)));

            //todo: should be able to join on own, and handle if already joined
            //slackClient.joinChannel(targetChannel.getId());

            List<Message> messages = slackClient.getAllChannelHistory(targetChannel.getId());
            for (Message message : messages) {
                WordleScore score = parser.parse(message);
                if (score != null && !dao.shouldSaveScore(score.getMessageId())) {
                    score.setUsername(slackClient.getUsernameById(score.userId));
                    dao.saveScore(score);
                }
            }
        } catch (Exception e) {
            log.error("Unable to parse wordle score from message", e);
        }
    }
}
