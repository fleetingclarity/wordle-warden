package dev.fleetingclarity.wordlewarden.scores;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.MessageEvent;

public class MessageEventHandler {

    private WordleSlackMessageParser parser;
    private SlackClient slackClient;
    private WordleScoreDao dao;

    public MessageEventHandler(WordleSlackMessageParser parser, SlackClient slackClient, WordleScoreDao dao) {
        this.parser = parser;
        this.slackClient = slackClient;
        this.dao = dao;
    }

    public Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
        WordleScore score = parser.parse(payload.getEvent());
        //todo extract to service, duplicates logic in SlackChannelScanner
        if (score != null && dao.shouldSaveScore(score)) {
            slackClient.setScoreUsername(score);
            dao.saveScore(score);
        }
        return ctx.ack();
    }
}
