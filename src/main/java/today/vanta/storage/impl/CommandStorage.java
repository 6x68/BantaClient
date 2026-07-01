package today.vanta.storage.impl;

import today.vanta.client.command.Command;
import today.vanta.client.command.impl.*;
import today.vanta.storage.Storage;

import java.util.Arrays;

public class CommandStorage extends Storage<Command> {
    public Command context;

    @Override
    public void subscribe() {
        super.subscribe();

        list.add(new Help());
        list.add(new Toggle());
        list.add(new Clear());
        list.add(new Bind());

        this.context = null;
    }

    public Command getCommand(String input) {
        return this.list.stream()
                .filter(cmd ->
                        cmd.name.equalsIgnoreCase(input) ||
                                (cmd.aliases != null && Arrays.stream(cmd.aliases)
                                        .anyMatch(a -> a.equalsIgnoreCase(input)))
                )
                .findFirst()
                .orElse(null);
    }
}
