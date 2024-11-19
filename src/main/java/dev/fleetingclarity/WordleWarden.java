package dev.fleetingclarity;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jakarta_jetty.SlackAppServer;
import com.slack.api.model.event.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordleWarden {
    private static final Logger log = LoggerFactory.getLogger(WordleWarden.class);

    public static void main(String[] args) throws Exception {
        log.info("Configuring WordleWarden...");
        EnvLoader.loadEnv();
        log.info("Running database migrations...");
        DatabaseMigration.runMigrations(DatabaseConfig.getDataSource());

        log.info("Setting up other dependencies...");
        final String botToken = EnvLoader.getEnvOrProperty("WW_SLACK_BOT_TOKEN");
        final String targetChannelName = EnvLoader.getEnvOrProperty("WW_TARGET_CHANNEL");
        final SlackClient slackClient = new SlackClient(botToken);
        final WordleSlackMessageParser parser = new WordleSlackMessageParser();
        final WordleScoreDao dao = new WordleScoreDao(DatabaseConfig.getDataSource());
        final SlackChannelScanner scanner = new SlackChannelScanner(slackClient, parser, dao);

        log.info("Scanning past messages...");
        scanner.scanChannel(targetChannelName);

        log.info("Starting slack event handling...");
        final App slack = new App();
        var signingSecret = DatabaseConfig.getEnvOrProperty("WW_SLACK_SIGNING_SECRET");
        slack.config().setSigningSecret(signingSecret);

        slack.event(MessageEvent.class, (payload, ctx) -> {
            WordleScore score = parser.parse(payload.getEvent());
            //todo extract to service, duplicates logic in SlackChannelScanner
            if (score != null && dao.shouldSaveScore(score)) {
                score.setUsername(slackClient.getUsernameById(score.userId));
                dao.saveScore(score);
            }
            return ctx.ack();
        });

        SlackAppServer server = new SlackAppServer(slack, 8888);
        server.start();
    }
}
