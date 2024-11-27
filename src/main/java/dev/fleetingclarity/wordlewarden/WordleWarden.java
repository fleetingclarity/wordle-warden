package dev.fleetingclarity.wordlewarden;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jakarta_jetty.SlackAppServer;
import com.slack.api.model.event.MessageEvent;
import dev.fleetingclarity.wordlewarden.audit.AuditTrailDao;
import dev.fleetingclarity.wordlewarden.audit.AuditTrailService;
import dev.fleetingclarity.wordlewarden.audit.AuditTrailServiceFactory;
import dev.fleetingclarity.wordlewarden.commands.groupstats.GroupStatsCommandHandler;
import dev.fleetingclarity.wordlewarden.commands.groupstats.GroupStatsDao;
import dev.fleetingclarity.wordlewarden.commands.submissions.UserSubmissionsCommandHandler;
import dev.fleetingclarity.wordlewarden.commands.submissions.UserSubmissionsDao;
import dev.fleetingclarity.wordlewarden.commands.userstats.UserStatsCommandHandler;
import dev.fleetingclarity.wordlewarden.commands.userstats.UserStatsDao;
import dev.fleetingclarity.wordlewarden.scores.*;
import org.jetbrains.annotations.NotNull;
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

        final AuditTrailDao auditTrailDao = new AuditTrailDao(DatabaseConfig.getDataSource());
        final AuditTrailService auditTrailService = AuditTrailServiceFactory.create(auditTrailDao);

        final CommandHandlerRegistry commandHandlerRegistry = getCommandHandlerRegistry(auditTrailService);

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

    private static CommandHandlerRegistry getCommandHandlerRegistry(final AuditTrailService auditTrailService) {
        final CommandHandlerRegistry commandHandlerRegistry = new CommandHandlerRegistry(auditTrailService);
        final GroupStatsDao groupStatsDao = new GroupStatsDao(DatabaseConfig.getDataSource());
        final GroupStatsCommandHandler groupStatsCommandHandler = new GroupStatsCommandHandler(groupStatsDao);
        final UserSubmissionsDao userSubmissionsDao = new UserSubmissionsDao(DatabaseConfig.getDataSource());
        final UserSubmissionsCommandHandler userSubmissionsCommandHandler = new UserSubmissionsCommandHandler(userSubmissionsDao);
        final UserStatsDao userStatsDao = new UserStatsDao(DatabaseConfig.getDataSource());
        final UserStatsCommandHandler userStatsCommandHandler = new UserStatsCommandHandler(userStatsDao);
        commandHandlerRegistry.register("group-stats", groupStatsCommandHandler);
        commandHandlerRegistry.register("submissions", userSubmissionsCommandHandler);
        commandHandlerRegistry.register("user-stats", userStatsCommandHandler);
        return commandHandlerRegistry;
    }
}
