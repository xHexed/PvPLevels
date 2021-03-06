package me.MathiasMC.PvPLevels.data;

import me.MathiasMC.PvPLevels.PvPLevels;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.Date;

public class Database {

    private final PvPLevels plugin;

    private Connection connection;

    public Database(final PvPLevels plugin) {
        this.plugin = plugin;
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    connection = get();
                    if (plugin.isDebug()) { plugin.getTextUtils().debug("[Database] Lost connection, getting new connection"); }
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
    }

    private Connection get() {
        try {
            if (plugin.getFileUtils().config.getBoolean("mysql.use")) {
                plugin.getTextUtils().info("[Database] ( Connected ) ( MySQL )");
                Class.forName("com.mysql.jdbc.Driver");
                return DriverManager.getConnection("jdbc:mysql://" + plugin.getFileUtils().config.getString("mysql.host") + ":" + plugin.getFileUtils().config.getString("mysql.port") + "/" + plugin.getFileUtils().config.getString("mysql.database"), plugin.getFileUtils().config.getString("mysql.username"), plugin.getFileUtils().config.getString("mysql.password"));
            } else {
                plugin.getTextUtils().info("[Database] ( Connected ) ( SQLite )");
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getTextUtils().exception(e.getStackTrace(), e.getMessage());
            return null;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private boolean check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = get();
            if (connection == null || connection.isClosed()) {
                return false;
            }
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `players` (`uuid` char(36) PRIMARY KEY, `group` text(255), `kills` bigint(255), `deaths` bigint(255), `xp` bigint(255), `level` bigint(255), `killstreak` bigint(255), `killstreak_top` bigint(255), `multiplier` text(255), `lastseen` DATETIME);");
        }
        return true;
    }

    public boolean set() {
        try {
            return check();
        } catch (SQLException e) {
            plugin.getTextUtils().exception(e.getStackTrace(), e.getMessage());
            return false;
        }
    }

    public void insert(final String uuid) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM players WHERE uuid= '" + uuid + "';");
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, `group`, kills, deaths, xp, level, killstreak, killstreak_top, multiplier, lastseen) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.setString(2, "default");
                            preparedStatement.setLong(3, 0L);
                            preparedStatement.setLong(4, 0L);
                            preparedStatement.setLong(5, 0L);
                            preparedStatement.setLong(6, plugin.getStartLevel());
                            preparedStatement.setLong(7, 0L);
                            preparedStatement.setLong(8, 0L);
                            preparedStatement.setString(9, "0.0 0 0");
                            preparedStatement.setTimestamp(10, new Timestamp(new Date().getTime()));
                            preparedStatement.executeUpdate();
                            if (plugin.isDebug()) { plugin.getTextUtils().debug("[Database] Inserted ( " + uuid + " )"); }
                        }
                    } catch (SQLException exception) {
                        plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                        if (preparedStatement != null)
                            try {
                                preparedStatement.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void delete(final String uuid) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM players WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM players WHERE uuid = ?");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.executeUpdate();
                            plugin.removePlayerConnect(uuid);
                            if (plugin.isDebug()) { plugin.getTextUtils().debug("[Database] Deleting ( " + uuid + " )"); }
                        }
                    } catch (SQLException exception) {
                        plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                        if (preparedStatement != null)
                            try {
                                preparedStatement.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void setValues(final String uuid, final String group, final long kills, final long deaths, final long xp, final long level, final long killstreak, final long killstreak_top, final String multiplier, final Timestamp timestamp) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM players WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("UPDATE players SET `group` = ?, kills = ?, deaths = ?, xp = ?, level = ?, killstreak = ?, killstreak_top = ?, multiplier = ?, lastseen = ? WHERE uuid = ?");
                            preparedStatement.setString(1, group);
                            preparedStatement.setLong(2, kills);
                            preparedStatement.setLong(3, deaths);
                            preparedStatement.setLong(4, xp);
                            preparedStatement.setLong(5, level);
                            preparedStatement.setLong(6, killstreak);
                            preparedStatement.setLong(7, killstreak_top);
                            preparedStatement.setString(8, multiplier);
                            preparedStatement.setTimestamp(9, timestamp);
                            preparedStatement.setString(10, uuid);
                            preparedStatement.executeUpdate();
                            if (plugin.isDebug()) { plugin.getTextUtils().debug("[Database] Setting values for ( " + uuid + " )"); }
                        }
                    } catch (SQLException exception) {
                        plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                        if (preparedStatement != null)
                            try {
                                preparedStatement.close();
                            } catch (SQLException exception) {
                                plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                            }
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public String[] getValues(String uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM players WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                if (plugin.isDebug()) { plugin.getTextUtils().debug("[Database] Getting new data for ( " + uuid + " )"); }
                return new String[]{ resultSet.getString("group"), String.valueOf(resultSet.getLong("kills")), String.valueOf(resultSet.getLong("deaths")), String.valueOf(resultSet.getLong("xp")), String.valueOf(resultSet.getLong("level")), String.valueOf(resultSet.getLong("killstreak")), String.valueOf(resultSet.getLong("killstreak_top")), resultSet.getString("multiplier"), String.valueOf(resultSet.getTimestamp("lastseen")) };
            }
        } catch (SQLException exception) {
            plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException exception) {
                    plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                }
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException exception) {
                    plugin.getTextUtils().exception(exception.getStackTrace(), exception.getMessage());
                }
        }
        return new String[] { "default", String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(plugin.getStartLevel()), String.valueOf(0L), String.valueOf(0L), "0.0 0 0", String.valueOf(new Timestamp(new Date().getTime())) };
    }
}