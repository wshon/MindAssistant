package MindAssistant.io;

import arc.files.Fi;
import arc.util.Log;
import arc.util.Strings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static mindustry.Vars.modDirectory;

/**
 * @author wshon
 */
public class MindSettings {
    protected final static byte typeBool = 0, typeInt = 1, typeLong = 2, typeFloat = 3, typeString = 4;
    protected HashMap<String, Object> values = new HashMap<>();
    protected boolean modified;
    protected boolean hasErrored;
    protected boolean loaded = false;

    public void init() {
        try {
            loadValues();
        } catch (Throwable error) {
            hasErrored = true;
        }
        //if loading failed, it still counts
        loaded = true;
    }

    public Fi getDataDirectory() {
        Fi fi = modDirectory.child("MinerTools");
        fi.mkdirs();
        return fi;
    }

    public Fi getSettingsFile() {
        return getDataDirectory().child("settings.bin");
    }

    public Fi getBackupSettingsFile() {
        return getDataDirectory().child("settings_backup.bin");
    }

    public Fi getLatestBackupSettingsFile() {
        return getDataDirectory().child("settings_backup_latest.bin");
    }

    public synchronized void loadValues() {
        //don't load settings files if neither of them exist
        if (!getSettingsFile().exists() && !getBackupSettingsFile().exists()) {
            writeLog("No settings files found: " + getSettingsFile().absolutePath() + " and " + getBackupSettingsFile().absolutePath());
            return;
        }

        try {
            loadValues(getSettingsFile());
            writeLog("Loaded " + values.size() + " values");

            //back up the save file, as the values have now been loaded successfully
            getSettingsFile().copyTo(getBackupSettingsFile());
            writeLog("Backed up " + getSettingsFile() + " to " + getBackupSettingsFile() + " (" + getSettingsFile().length() + " bytes)");
        } catch (Throwable e) {
            Log.err("Failed to load base settings file, attempting to load backup.", e);
            writeLog("Failed to load base file " + getSettingsFile() + ":\n" + Strings.getStackTrace(e));

            try {
                //attempt to load *latest* backup, which is updated more regularly but likely to be corrupt
                loadValues(getLatestBackupSettingsFile());
                //copy to normal settings file for future use
                getLatestBackupSettingsFile().copyTo(getSettingsFile());
                Log.info("Loaded latest backup settings file.");
                writeLog("Loaded latest backup settings file after load failure. Length: " + getLatestBackupSettingsFile().length());
            } catch (Throwable e2) {
                writeLog("Failed to load latest backup file " + getLatestBackupSettingsFile() + ":\n" + Strings.getStackTrace(e2));
                Log.err("Failed to load latest backup settings file.", e2);

                try {
                    //attempt to load the old, reliable backup
                    loadValues(getBackupSettingsFile());
                    //copy to normal settings file for future use
                    getBackupSettingsFile().copyTo(getSettingsFile());
                    Log.info("Loaded backup settings file.");
                    writeLog("Loaded backup settings file after load failure. Length: " + getBackupSettingsFile().length());
                } catch (Throwable e3) {
                    writeLog("Failed to load backup file " + getSettingsFile() + ":\n" + Strings.getStackTrace(e3));
                    Log.err("Failed to load backup settings file.", e3);
                }
            }
        }
    }

    public synchronized void loadValues(Fi file) throws IOException {
        try (DataInputStream stream = new DataInputStream(file.read(8192))) {
            int amount = stream.readInt();
            //current theory: when corruptions happen, the only things written to the stream are a bunch of zeroes
            //try to anticipate this case and throw an exception when 0 values are written
            if (amount <= 0) throw new IOException("0 values are not allowed.");
            for (int i = 0; i < amount; i++) {
                String key = stream.readUTF();
                byte type = stream.readByte();

                switch (type) {
                    case typeBool:
                        values.put(key, stream.readBoolean());
                        break;
                    case typeInt:
                        values.put(key, stream.readInt());
                        break;
                    case typeLong:
                        values.put(key, stream.readLong());
                        break;
                    case typeFloat:
                        values.put(key, stream.readFloat());
                        break;
                    case typeString:
                        values.put(key, stream.readUTF());
                        break;
                }
            }
            //make sure all data was read - this helps with potential corruption
            int end = stream.read();
            if (end != -1) {
                throw new IOException("Trailing settings data; expected EOF, but got: " + end);
            }
        }
    }

    /**
     * Saves all entries from {@link #values} into the correct location.
     */
    public synchronized void saveValues() {
        Fi file = getSettingsFile();

        try (DataOutputStream stream = new DataOutputStream(file.write(false, 8192))) {
            stream.writeInt(values.size());

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                stream.writeUTF(entry.getKey());

                Object value = entry.getValue();

                if (value instanceof Boolean) {
                    stream.writeByte(typeBool);
                    stream.writeBoolean((Boolean) value);
                } else if (value instanceof Integer) {
                    stream.writeByte(typeInt);
                    stream.writeInt((Integer) value);
                } else if (value instanceof Long) {
                    stream.writeByte(typeLong);
                    stream.writeLong((Long) value);
                } else if (value instanceof Float) {
                    stream.writeByte(typeFloat);
                    stream.writeFloat((Float) value);
                } else if (value instanceof String) {
                    stream.writeByte(typeString);
                    stream.writeUTF((String) value);
                }
            }

        } catch (Throwable e) {
            //file is now corrupt, delete it
            file.delete();
            throw new RuntimeException("Error writing preferences: " + file, e);
        }

        writeLog("Saving " + values.size() + " values; " + file.length() + " bytes");

//        executor.submit(() -> {
//            //back up to latest file in another thread
//            file.copyTo(getLatestBackupSettingsFile());
//        });
    }

    void writeLog(String text) {
        try {
            Fi log = getDataDirectory().child("settings.log");
            log.writeString("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "] " + text + "\n", true);
        } catch (Throwable t) {
            Log.err("Failed to write settings log", t);
        }
    }

    public synchronized void put(String name, Object object) {
        if (object instanceof Float || object instanceof Integer || object instanceof Boolean || object instanceof Long || object instanceof String) {
            values.put(name, object);
            modified = true;
        } else {
            throw new IllegalArgumentException("Invalid object stored: " + (object == null ? null : object.getClass()) + ".");
        }
    }

    public void put(String name, Object obj, boolean isDef, boolean forceSave) {
        if (get(name, null) != null && isDef) return;
        put(name, obj);
        if (forceSave) saveValues();
    }

    public Object get(String name, Object def) {
        return values.getOrDefault(name, def);
    }

    public int getInt(String name) {
        return (int) get(name, 0);
    }

    public int getInt(String name, int def) {
        return (int) get(name, def);
    }

    public boolean getBool(String name) {
        return (boolean) get(name, false);
    }

    public boolean getBool(String name, boolean def) {
        return (boolean) get(name, def);
    }

    public float getFloat(String name) {
        return (float) get(name, 0f);
    }

    public float getFloat(String name, float def) {
        return (float) get(name, def);
    }

    public String getString(String name) {
        return (String) get(name, "");
    }

    public String getString(String name, float def) {
        return (String) get(name, def);
    }
}
