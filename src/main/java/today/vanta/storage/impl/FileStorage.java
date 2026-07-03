package today.vanta.storage.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import today.vanta.client.event.impl.game.PauseGameEvent;
import today.vanta.client.file.File;
import today.vanta.client.file.impl.AccountsFile;
import today.vanta.client.file.impl.ConfigFile;
import today.vanta.client.file.impl.ModulesFile;
import today.vanta.client.file.impl.ScreenFile;
import today.vanta.storage.Storage;
import today.vanta.util.game.events.EventListen;

public class FileStorage extends Storage<File> {
    public static final Gson GSON = new GsonBuilder().create();

    public ModulesFile modulesFile;
    public ScreenFile screenFile;
    public AccountsFile accountsFile;
    public ConfigFile defaultConfig;

    @Override
    public void subscribe() {
        modulesFile = new ModulesFile();
        screenFile = new ScreenFile();
        accountsFile = new AccountsFile();
        defaultConfig = new ConfigFile();

        modulesFile.load();
        screenFile.load();
        accountsFile.load();
        defaultConfig.load();

        super.subscribe();
    }

    @EventListen
    private void onPause(PauseGameEvent event) {
        saveAll();
    }

    public void saveAll() {
        list.forEach(File::save);
    }

    public void stop() {
        saveAll();
    }
}