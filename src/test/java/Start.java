import net.minecraft.client.main.Main;

import java.io.File;
import java.util.Arrays;

public class Start {
    public static void main(String[] args) {
        String os = System.getProperty("os.name").startsWith("Windows")
                ? "windows"
                : "linux";

        System.setProperty(
                "org.lwjgl.librarypath",
                new File(findNatives(), os).getAbsolutePath()
        );

        Main.main(concat(new String[]{"--version", "1.8.9", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static File findNatives() {
        File dir = new File(System.getProperty("user.dir"));

        while (dir != null) {
            File natives = new File(dir, "natives");
            if (natives.exists() && natives.isDirectory()) {
                return natives;
            }

            dir = dir.getParentFile();
        }

        throw new RuntimeException("Could not find natives directory");
    }
}
