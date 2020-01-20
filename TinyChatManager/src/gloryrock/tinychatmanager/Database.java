package gloryrock.tinychatmanager;

import gloryrock.tinychatmanager.user.UserData;
import java.util.UUID;
import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import gloryrock.tinychatmanager.messages.Messages;
import java.sql.DriverManager;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import java.sql.Connection;

public class Database
{
    private String host;
    private String database;
    private String username;
    private String tablePrefix;
    private String password;
    private Connection connection;
    private int port;

    Database()
    {
        this.host = FileManager.getConfig().getFileData().getString(ConfigData.Values.SQL_HOST.toString());
        this.database = FileManager.getConfig().getFileData().getString(ConfigData.Values.SQL_DATABASE.toString());
        this.username = FileManager.getConfig().getFileData().getString(ConfigData.Values.SQL_USERNAME.toString());
        this.password = FileManager.getConfig().getFileData().getString(ConfigData.Values.SQL_PASSWORD.toString());
        this.tablePrefix = FileManager.getConfig().getFileData().getString(ConfigData.Values.SQL_TABLE_PREFIX.toString());
        this.port = FileManager.getConfig().getFileData().getInt(ConfigData.Values.SQL_PORT.toString());
        if (this.tablePrefix == null || this.tablePrefix.isEmpty())
        {
            this.tablePrefix = "";
        }
        else if (!this.tablePrefix.endsWith("_"))
        {
            this.tablePrefix += "_";
        }
        this.connect();
    }

    public String getTablePrefix()
    {
        return this.tablePrefix;
    }

    public Connection getConnection()
    {
        return this.connection;
    }

    private void connect()
    {
        synchronized (this)
        {
            try
            {
                if (this.connection != null && !this.connection.isClosed())
                    return;

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
                this.createTables();
            }
            catch (SQLSyntaxErrorException exception)
            {
                Messages.log("§cDatabase '" + this.database + "' does not exist!");
            }
            catch (SQLException exception)
            {
                Messages.log("§cAccess denied for user '" + this.username + "'@'" + this.host + "'");
                Messages.log("§cPlease check if the sql server is running and you entered the right username and password.");
            }
            catch (ClassNotFoundException exception)
            {
                Messages.log("§cYour installation does not support sql!");
            }
        }
    }

    public void update(String statement)
    {
        try
        {
            if (this.connection.isClosed())
                this.connect();

            statement = statement.replace("%p%", this.getTablePrefix());
            final Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(statement);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    public ResultSet getValue(String statement)
    {
        try
        {
            if (this.connection.isClosed())
                this.connect();

            statement = statement.replace("%p%", this.getTablePrefix());
            final Statement stmt = this.connection.createStatement();
            return stmt.executeQuery(statement);
        }
        catch (SQLException exception)
        {
            Messages.log("§cCouldn't get value from statement '" + statement + "'!");
            Messages.log("§c" + exception.getMessage());
            exception.printStackTrace();
            return null;
        }
    }

    private void createTables()
    {
        this.update("CREATE TABLE IF NOT EXISTS `%p%users` ( `uuid` CHAR(36) NOT NULL, `group` VARCHAR(64) NULL DEFAULT NULL, `force_group` BOOLEAN NULL DEFAULT NULL, `subgroup` VARCHAR(64) NULL DEFAULT NULL, `custom_prefix` VARCHAR(128) NULL DEFAULT NULL, `custom_suffix` VARCHAR(128) NULL DEFAULT NULL, `gender` VARCHAR(32) NULL DEFAULT NULL, `chat_color` CHAR(2) NULL DEFAULT NULL, `chat_formatting` CHAR(2) NULL DEFAULT NULL, PRIMARY KEY (`uuid`) ) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;");
        this.update("CREATE TABLE IF NOT EXISTS `%p%groups` ( `group` VARCHAR(64) NOT NULL, `prefix` VARCHAR(128) default NULL null, `suffix` VARCHAR(128) default NULL null, `chat_color` CHAR(2) default NULL null, `chat_formatting` CHAR(2) default NULL null, `join_msg` VARCHAR(255) default NULL null, `quit_msg` VARCHAR(255) default NULL null, PRIMARY KEY (`group`) )ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;");
        this.update("CREATE TABLE IF NOT EXISTS `%p%subgroups` ( `group` VARCHAR(64) NOT NULL, `prefix` VARCHAR(128) default NULL null, `suffix` VARCHAR(128) default NULL null, PRIMARY KEY (`group`) ) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;");
    }

    public PreparedStatement prepareStatement(final String sql)
    {
        try
        {
            return this.getConnection().prepareStatement(sql);
        }
        catch (SQLException exception)
        {
            return null;
        }
    }

    private void uploadGroups() throws SQLException
    {
        final FileConfiguration data = FileManager.getGroups().getFileData();
        final Set<String> groups = (Set<String>) data.getConfigurationSection("groups").getKeys(false);
        for (final String groupName : groups)
        {
            try
            {
                final String sql = "INSERT INTO `" + this.getTablePrefix() + "groups`(`group`) VALUES (?)";
                final PreparedStatement stmt = this.prepareStatement(sql);
                stmt.setString(1, groupName);
                stmt.executeUpdate();
                Messages.log("§7Uploaded group '" + groupName + "' to database!");
            }
            catch (SQLIntegrityConstraintViolationException ex)
            {
            }
            final String sql = "UPDATE `" + this.getTablePrefix() + "groups` SET `prefix`= ?,`suffix`= ?,`chat_color`= ?,`chat_formatting`= ?,`join_msg`= ?,`quit_msg`= ? WHERE `group` = ?";
            final PreparedStatement stmt = this.prepareStatement(sql);
            final String prefix = data.getString("groups." + groupName + ".prefix");
            if (prefix != null)
            {
                stmt.setString(1, prefix);
            }
            else
            {
                stmt.setNull(1, 12);
            }
            final String suffix = data.getString("groups." + groupName + ".suffix");
            if (suffix != null)
            {
                stmt.setString(2, suffix);
            }
            else
            {
                stmt.setNull(2, 12);
            }
            final String chatcolor = data.getString("groups." + groupName + ".chatcolor");
            if (chatcolor != null && chatcolor.length() >= 2)
            {
                stmt.setString(3, chatcolor.substring(1, 2));
            }
            else
            {
                stmt.setNull(3, 12);
            }
            final String chatformatting = data.getString("groups." + groupName + ".chatformatting");
            if (chatformatting != null && chatformatting.length() >= 2)
            {
                stmt.setString(4, chatformatting.substring(1, 2));
            }
            else
            {
                stmt.setNull(4, 12);
            }
            final String joinMessage = data.getString("groups." + groupName + ".join-msg");
            if (joinMessage != null)
            {
                stmt.setString(5, joinMessage);
            }
            else
            {
                stmt.setNull(5, 12);
            }
            final String quitMessage = data.getString("groups." + groupName + ".quit-msg");
            if (quitMessage != null)
            {
                stmt.setString(6, quitMessage);
            }
            else
            {
                stmt.setNull(6, 12);
            }
            stmt.setString(7, groupName);
            stmt.executeUpdate();
        }
    }

    private void uploadSubgroups() throws SQLException
    {
        final FileConfiguration data = FileManager.getGroups().getFileData();
        final ConfigurationSection mainSection = data.getConfigurationSection("subgroups");
        if (mainSection == null)
        {
            return;
        }
        final Set<String> groups = (Set<String>) mainSection.getKeys(false);
        for (final String groupName : groups)
        {
            try
            {
                final PreparedStatement stmt = this.prepareStatement("INSERT INTO `" + this.getTablePrefix() + "subgroups`(`group`) VALUES (?)");
                stmt.setString(1, groupName);
                stmt.executeUpdate();
                Messages.log("§7Uploaded subgroup '" + groupName + "' to database!");
            }
            catch (SQLIntegrityConstraintViolationException exception)
            {
            }
            final String sql = "UPDATE `" + this.getTablePrefix() + "subgroups` SET `prefix`= ?,`suffix`= ? WHERE `group` = ?";
            final PreparedStatement stmt2 = this.prepareStatement(sql);
            final String prefix = data.getString("subgroups." + groupName + ".prefix");
            if (prefix != null)
            {
                stmt2.setString(1, prefix);
            }
            else
            {
                stmt2.setNull(1, 12);
            }
            final String suffix = data.getString("subgroups." + groupName + ".suffix");
            if (suffix != null)
            {
                stmt2.setString(2, suffix);
            }
            else
            {
                stmt2.setNull(2, 12);
            }
            stmt2.setString(3, groupName);
            stmt2.executeUpdate();
        }
    }

    private void uploadUsers() throws SQLException
    {
        final File dirUsers = new File(FileManager.getPluginFolder() + "/user");
        final File[] listOfFiles = dirUsers.listFiles();
        if (listOfFiles != null)
        {
            for (final File listOfFile : listOfFiles)
            {
                if (listOfFile.isFile())
                {
                    final UUID uuid = UUID.fromString(listOfFile.getName().replace(".yml", ""));
                    final UserData userData = new UserData(uuid);
                    final String groupName = userData.getFileData().getString("group");
                    final String subgroupName = userData.getFileData().getString("subgroup");
                    final String chatColor = userData.getFileData().getString("chat-color");
                    final String chatFormatting = userData.getFileData().getString("chat-formatting");
                    final String cstmPrefix = userData.getFileData().getString("custom-prefix");
                    final String cstmSuffix = userData.getFileData().getString("custom-suffix");
                    final boolean forceGroup = userData.getFileData().getBoolean("force-group");
                    final String sql = "INSERT INTO `" + this.getTablePrefix() + "users`(`uuid`, `group`, `force_group`, `subgroup`, `chat_color`, `chat_formatting`) VALUES (?,?,?,?,?,?,?)";
                    final PreparedStatement stmt = this.prepareStatement(sql);
                    stmt.setString(1, uuid.toString());
                    stmt.setString(2, groupName);
                    stmt.setBoolean(3, forceGroup);
                    stmt.setString(4, subgroupName);
                    stmt.setString(5, cstmPrefix);
                    stmt.setString(6, cstmSuffix);
                    stmt.setString(8, chatColor);
                    stmt.setString(9, chatFormatting);
                    try
                    {
                        stmt.executeUpdate();
                    }
                    catch (SQLIntegrityConstraintViolationException ex)
                    {
                    }
                }
            }
        }
    }

    public void migrateData() throws SQLException
    {
        final long startTime = System.currentTimeMillis();
        Messages.log("§cMigrating data to SQL...");
        Messages.log("§7loading files...");
        FileManager.load();
        Messages.log("§7creating tables...");
        this.createTables();
        Messages.log("§7uploading groups...");
        this.uploadGroups();
        Messages.log("§7uploading subgroups...");
        this.uploadSubgroups();
        Messages.log("§7uploading users...");
        this.uploadUsers();
        final long ms = System.currentTimeMillis() - startTime;
        Messages.log("§aMigration took " + ms + " ms!");
    }
}
