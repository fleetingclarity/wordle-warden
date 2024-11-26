package dev.fleetingclarity.wordlewarden.scores;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsJoinResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.Conversation;
import com.slack.api.model.ConversationType;
import com.slack.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SlackClient {
    private static final Logger log = LoggerFactory.getLogger(SlackClient.class);
    private final Slack slack;
    private final String botToken;

    public SlackClient(String botToken) {
        this.slack = Slack.getInstance();
        this.botToken = botToken;
    }

    public List<Conversation> listChannels(final ConversationType type) throws IOException, SlackApiException {
        ConversationsListResponse res = slack.methods(botToken).conversationsList(req -> req
                .excludeArchived(true)
                .types(List.of(type))
                .limit(100)
        );

        if (!res.isOk()) {
            throw new RuntimeException("Failed to list channels: " + res.getError());
        }

        return res.getChannels();
    }

    public List<Message> getAllChannelHistory(final String channelId) throws IOException, SlackApiException {
        final List<Message> allMessages = new ArrayList<>();
        final AtomicReference<String> nextCursor = new AtomicReference<>(null);

        do {
            ConversationsHistoryResponse res = slack.methods(botToken)
                    .conversationsHistory(req -> req
                            .channel(channelId)
                            .cursor(nextCursor.get())
                            .limit(100));
            if (!res.isOk()) {
                throw new RuntimeException(String.format("Failed to get all history for channel '%s': %s", channelId, res.getError()));
            }

            allMessages.addAll(res.getMessages());

            nextCursor.set(res.getResponseMetadata() != null
                    ? res.getResponseMetadata().getNextCursor()
                    : null);
        } while (nextCursor.get() != null && !nextCursor.get().isEmpty());

        return allMessages;
    }

    public void setScoreUsername(final WordleScore score) {
        try {
            UsersInfoResponse res = slack.methods(botToken).usersInfo(req -> req.user(score.getUserId()));
            if (res.isOk() && res.getUser() != null) {
                score.setUsername(res.getUser().getRealName());
            } else {
                log.warn("Unable to find details of user '{}'", score.getUserId());
                score.setUsername("");
            }
        } catch (IOException | SlackApiException e) {
            log.error("Error querying for userDetails of userId={}", score.getUserId(), e);
        }
    }
}
