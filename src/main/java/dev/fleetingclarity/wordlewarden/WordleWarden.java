package dev.fleetingclarity.wordlewarden;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jakarta_jetty.SlackAppServer;
import com.slack.api.model.event.MessageEvent;
import dev.fleetingclarity.wordlewarden.commands.groupstats.GroupStatsCommandHandler;
import dev.fleetingclarity.wordlewarden.commands.groupstats.GroupStatsDao;
import dev.fleetingclarity.wordlewarden.scores.*;
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

        final MessageEventHandler messageEventHandler = new MessageEventHandler(parser, slackClient, dao);

        final CommandHandlerRegistry commandHandlerRegistry = new CommandHandlerRegistry();
        final GroupStatsDao groupStatsDao = new GroupStatsDao(DatabaseConfig.getDataSource());
        final GroupStatsCommandHandler groupStatsCommandHandler = new GroupStatsCommandHandler(groupStatsDao);
        commandHandlerRegistry.register("group-stats", groupStatsCommandHandler);

        log.info("Scanning past messages...");
        scanner.scanChannel(targetChannelName);

        log.info("Starting slack event handling...");
        final App slack = new App();
        var signingSecret = DatabaseConfig.getEnvOrProperty("WW_SLACK_SIGNING_SECRET");
        slack.config().setSigningSecret(signingSecret);

        slack.event(MessageEvent.class, messageEventHandler::handle);
        slack.command("/ww", commandHandlerRegistry::handle);

        SlackAppServer server = new SlackAppServer(slack, 8888);
        server.start();
    }
}
