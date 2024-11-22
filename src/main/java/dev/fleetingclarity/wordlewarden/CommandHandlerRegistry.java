package dev.fleetingclarity.wordlewarden;

import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import dev.fleetingclarity.wordlewarden.commands.CommandResponse;
import dev.fleetingclarity.wordlewarden.commands.NoOpCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class CommandHandlerRegistry {
    private static final Logger log = LoggerFactory.getLogger(CommandHandlerRegistry.class);
    private final HashMap<String, CommandHandler> handlers = new HashMap<>();
    private final CommandHandler DEFAULT = new NoOpCommandHandler();

    public void register(final String command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public CommandHandler getHandler(final String command) {
        return handlers.get(command);
    }

    public Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
        final String text = req.getPayload().getText().trim();
        final String[] parts = text.split("\\s+", 2);
        final String subcommand = parts[0].toLowerCase();
        final String args = parts.length > 1 ? parts[1] : "";

        CommandHandler handler = handlers.getOrDefault(subcommand, DEFAULT);
        final CommandResponse response = handler.handle(args);
        try {
            ctx.respond(r -> r.text(response.getText())
                    .responseType(response.isEphemeral() ? "ephemeral" : "in_channel"));
        } catch (IOException e) {
            log.error("Unable to respond to slack command", e);
        }
        return ctx.ack();
    }
}
