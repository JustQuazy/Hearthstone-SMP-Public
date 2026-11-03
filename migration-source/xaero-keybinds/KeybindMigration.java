package hearthstone.migration;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class KeybindMigration implements PreLaunchEntrypoint {
    private static final Map<String, String> BINDINGS = new LinkedHashMap<>();
    static {
        BINDINGS.put("key_key.friends", "key.keyboard.unknown");
        BINDINGS.put("key_gui.xaero_open_map", "key.keyboard.o");
        BINDINGS.put("key_gui.xaero_waypoints_key", "key.keyboard.u");
        BINDINGS.put("key_gui.xaero_new_waypoint", "key.keyboard.i");
        BINDINGS.put("key_gui.xaero_instant_waypoint", "key.keyboard.unknown");
        BINDINGS.put("key_iris.keybind.toggleShaders", "key.keyboard.k");
    }
    public void onPreLaunch() {
        try { apply(FabricLoader.getInstance().getGameDir()); }
        catch (IOException e) { throw new IllegalStateException("Hearthstone keybinding migration failed; existing settings were not intentionally discarded", e); }
    }
    public static void apply(Path root) throws IOException {
        applyChange(root, BINDINGS, "2026-11-03-xaero-keybinds", "2026-11-03-options-before-xaero.txt");
        applyChange(root, Map.of("key_gui.xaero_minimap_settings", "key.keyboard.unknown"),
                "2026-11-03-minimap-settings-key", "2026-11-03-options-before-minimap-key.txt");
    }
    private static void applyChange(Path root, Map<String, String> bindings, String migration, String backupName) throws IOException {
        Path state = root.resolve("hearthstone-migrations");
        Path marker = state.resolve(migration + ".done");
        if (Files.exists(marker)) return;
        Files.createDirectories(state);
        Path options = root.resolve("options.txt");
        if (!Files.exists(options)) {
            Files.writeString(marker, "Skipped: no existing options; fresh pack defaults apply.\n", StandardOpenOption.CREATE_NEW);
            return;
        }
        byte[] original = Files.readAllBytes(options);
        String text = new String(original, StandardCharsets.UTF_8);
        String newline = text.contains("\r\n") ? "\r\n" : "\n";
        ArrayList<String> output = new ArrayList<>();
        Set<String> done = new HashSet<>();
        for (String line : text.split("\r?\n", -1)) {
            int colon = line.indexOf(':');
            String key = colon < 0 ? "" : line.substring(0, colon);
            if (bindings.containsKey(key)) {
                if (done.add(key)) output.add(key + ":" + bindings.get(key));
            } else output.add(line);
        }
        while (!output.isEmpty() && output.get(output.size() - 1).isEmpty()) output.remove(output.size() - 1);
        for (var e : bindings.entrySet()) if (!done.contains(e.getKey())) output.add(e.getKey() + ":" + e.getValue());
        Path backup = state.resolve(backupName);
        if (!Files.exists(backup)) Files.write(backup, original, StandardOpenOption.CREATE_NEW);
        Path tmp = Files.createTempFile(root, ".hearthstone-options-", ".tmp");
        try {
            Files.writeString(tmp, String.join(newline, output) + newline, StandardCharsets.UTF_8);
            try { Files.move(tmp, options, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) { Files.move(tmp, options, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(tmp); }
        Files.writeString(marker, "Applied once; original options backed up beside this marker.\n", StandardOpenOption.CREATE_NEW);
    }
}
