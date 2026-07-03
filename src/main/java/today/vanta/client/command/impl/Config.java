package today.vanta.client.command.impl;

import today.vanta.Vanta;
import today.vanta.client.command.Command;
import today.vanta.client.file.impl.ConfigFile;
import today.vanta.util.game.player.ChatUtil;
import today.vanta.util.system.FileUtil;
import today.vanta.util.system.OperatingSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Config extends Command {
    public Config() {
        super("Config", "Manage your configs.");
        aliases = new String[]{"config", "cfg"};
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1 || args.length > 2) {
            ChatUtil.error("Usage: config <load/save/list/folder> [config name]");
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "load": {
                if (args.length != 2) {
                    ChatUtil.error("Usage: config load <config name>");
                    return;
                }

                String configName = args[1];
                new ConfigFile(configName).load();
                ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Loaded config &e{}&f!", configName);
                break;
            }
            case "save": {
                if (args.length == 1) {
                    Vanta.instance.fileStorage.defaultConfig.save();
                    ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Saved default config!");
                } else {
                    String configName = args[1];
                    new ConfigFile(configName).save();
                    ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Saved config &e{}&f!", configName);
                }
                break;
            }
            case "list": {
                Path configsPath = FileUtil.getPath().resolve("configs");

                if (!Files.exists(configsPath)) {
                    ChatUtil.error("Configs folder does not exist!");
                    return;
                }

                ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Configs:");

                try (Stream<Path> stream = Files.list(configsPath)) {
                    stream.filter(Files::isRegularFile)
                            .map(path -> path.getFileName().toString())
                            .forEach(name -> ChatUtil.sendNoLineNoPrefix(" &7- &f{}", name));
                } catch (IOException e) {
                    ChatUtil.error("Failed to list configs!");
                    Vanta.instance.logger.error("Failed to list configs", e);
                }
                break;
            }
            case "folder": {
                Path configsPath = FileUtil.getPath().resolve("configs");
                OperatingSystem.getOperatingSystem().open(configsPath.toFile());
                ChatUtil.sendNoLine(ChatUtil.Prefix.INFO, "Opened configs folder!");
                break;
            }
            default: {
                ChatUtil.error("Unknown subcommand &e{}&f!", subCommand);
                ChatUtil.error("Usage: config <load/save/list/folder> [config name]");
            }
        }
    }

    @Override
    public String[] getArgs() {
        return new String[]{
                "config load <name>",
                "config save <name>",
                "config save",
                "config list",
                "config folder"
        };
    }
}