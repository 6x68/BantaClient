package today.vanta.client.file;

import com.google.gson.JsonObject;
import today.vanta.Vanta;
import today.vanta.storage.impl.FileStorage;
import today.vanta.util.system.FileUtil;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class File {
    public final String name;

    public File(String name) {
        this.name = name;
        Vanta.instance.fileStorage.list.add(this);
    }

    public final void save() {
        try {
            Path path = FileUtil.getPath().resolve(name);

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                FileStorage.GSON.toJson(writeJson(), writer);
            }

            Vanta.instance.logger.info("Saved {}", name);
        } catch (IOException e) {
            Vanta.instance.logger.error("Failed to save {}", name, e);
        }
    }

    public final void load() {
        try {
            Path path = FileUtil.getPath().resolve(name);

            if (!Files.exists(path)) {
                save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject json = FileStorage.GSON.fromJson(reader, JsonObject.class);
                readJson(json != null ? json : new JsonObject());
            }

            Vanta.instance.logger.info("Loaded {}", name);
        } catch (IOException e) {
            Vanta.instance.logger.error("Failed to load {}", name, e);
        }
    }

    protected abstract JsonObject writeJson();

    protected abstract void readJson(JsonObject json);
}