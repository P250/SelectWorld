package me.johnny.selectworld;

import me.johnny.selectworld.commands.CreativeServerCommand;
import me.johnny.selectworld.commands.LobbyServerCommand;
import me.johnny.selectworld.commands.SurvivalServerCommand;
import me.johnny.selectworld.events.PlayerEnterLobbyListener;
import me.johnny.selectworld.events.PlayerLeaveWorldListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public final class SelectWorld extends JavaPlugin {

    /**
     * Actual enum name (SURVIVAL, CREATIVE) represents the world name on disk.
     */
    public enum WorldTypes {

        SURVIVAL,
        CREATIVE,
        LOBBY;

    }

    private Connection databaseConn;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(new PlayerLeaveWorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEnterLobbyListener(this), this);
        getCommand("creative").setExecutor(new CreativeServerCommand(this));
        getCommand("survival").setExecutor(new SurvivalServerCommand(this));
        getCommand("lobby").setExecutor(new LobbyServerCommand(this));

        try {

            initDB();

        } catch (SQLException ex) {
            ex.printStackTrace();
            getLogger().severe("There was an error creating the SQLite database file. Try deleting the current file and restarting the plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            getLogger().severe("There was an error creating the SQLite database file. Try deleting the current file and restarting the plugin...");
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
            getLogger().severe("This should not be possible... the plugin could not find a JDBC driver for SQLite. Please report this...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {

    }

    private void initDB() throws IOException, SQLException, ClassNotFoundException {

            getDataFolder().mkdir(); // required to create the plugins/SelectWorld folder!
            Class.forName("org.sqlite.JDBC");
            this.databaseConn = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder() + "\\playerlocations.db");

            final String worldPlayerLocationScheme = "CREATE TABLE IF NOT EXISTS`WorldPlayerLocation` (\n" +
                    "\t`uuid` VARCHAR(36) NOT NULL,\n" +
                    "\t`worldName` VARCHAR(16) NOT NULL,\n" +
                    "\t`worldX` DOUBLE NOT NULL,\n" +
                    "\t`worldY` DOUBLE NOT NULL,\n" +
                    "\t`worldZ` INT NOT NULL,\n" +
                    "\t`uuidYaw` FLOAT NOT NULL,\n" +
                    "\t`uuidPitch` FLOAT NOT NULL,\n" +
                    "\tPRIMARY KEY (`uuid`, `worldName`)\n" +
                    ")";

            databaseConn.prepareStatement(worldPlayerLocationScheme).executeUpdate();
    }

    private boolean uuidExistsInTable(WorldTypes worldTableName, UUID uuid) throws SQLException {
        return getPlayerLocationFromTable(worldTableName, uuid) != null;
    }

    public void updatePlayerLocation(WorldTypes worldType, UUID uuid, String worldName, double x, double y, double z, float yaw, float pitch) throws SQLException {
        boolean uuidExists = uuidExistsInTable(worldType, uuid);
        if (uuidExists) {
            PreparedStatement removePlayerEntryStatement = databaseConn.prepareStatement("DELETE FROM WorldPlayerLocation WHERE uuid=? AND worldName=?");
            removePlayerEntryStatement.setObject(1, uuid);
            removePlayerEntryStatement.setString(2, worldType.name().toLowerCase());
            removePlayerEntryStatement.executeUpdate();
        }

        String updatePlayerLocationString = "INSERT INTO WorldPlayerLocation VALUES (?,?,?,?,?,?,?)";
        PreparedStatement updatePlayerLocationStatement = databaseConn.prepareStatement(updatePlayerLocationString);
        updatePlayerLocationStatement.setObject(1, uuid);
        updatePlayerLocationStatement.setString(2, worldName);
        updatePlayerLocationStatement.setDouble(3, x);
        updatePlayerLocationStatement.setDouble(4, y);
        updatePlayerLocationStatement.setDouble(5, z);
        updatePlayerLocationStatement.setFloat(6, yaw);
        updatePlayerLocationStatement.setFloat(7, pitch);
        updatePlayerLocationStatement.executeUpdate();
    }

    public Location getPlayerLocationFromTable(WorldTypes worldTableName, UUID uuid) throws SQLException {
        PreparedStatement checkUuidInTableStatement = databaseConn.prepareStatement("SELECT * FROM WorldPlayerLocation WHERE uuid=? AND worldName=?");
        checkUuidInTableStatement.setString(1, uuid.toString());
        checkUuidInTableStatement.setString(2, worldTableName.name().toLowerCase());
        ResultSet result = checkUuidInTableStatement.executeQuery();

        // returns false if there are no valid rows (what we want to check for....)
        if (!result.next()) {
            return null;
        }

        // todo debug
        String worldName = result.getString(2);
        double worldX = result.getDouble(3);
        double worldY = result.getDouble(4);
        double worldZ = result.getDouble(5);
        float yaw = result.getFloat(6);
        float pitch = result.getFloat(7);

        return new Location(Bukkit.getWorld(worldName), worldX, worldY, worldZ, yaw, pitch);

    }

}
