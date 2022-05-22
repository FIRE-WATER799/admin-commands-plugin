package salatosik;

import java.io.File;
import java.util.Timer;

import arc.Events;
import arc.util.*;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.mod.*;
import salatosik.util.DatabasePlayersSystem;
import salatosik.util.DatabaseTimerTask;
import salatosik.commands.client.ClientAdminCommands;
import salatosik.commands.server.ServerAdminCommands;
import salatosik.filters.chatfilers.MutedPlayerFilter;
import salatosik.handlers.PlayerAction;

public class AdminCommands extends Plugin {

    private Timer databaseTimer = new Timer();

    // xD
    public static void main(String[] args) {
        System.out.println("Hi my name is HUUUUUUUUUUUUUUUUUUAAAAAAAAAAAHHHHHH");
    }

    // called when game initializes
    @Override
    public void init() {
        
        // init database and searching problems, if database not loaded - generate exception
        File dir = new File("config/mods/admin-commands-databases");
        if(!dir.exists()) {
            dir.mkdir();
            DatabasePlayersSystem.init("config/mods/admin-commands-databases/database.db");

        } else DatabasePlayersSystem.init("config/mods/admin-commands-databases/database.db");

        if(!DatabasePlayersSystem.getDatabaseStatus()) {
            try { throw new DatabaseException("Database not loaded!"); } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

        // schedule task for change values in database
        databaseTimer.scheduleAtFixedRate(new DatabaseTimerTask(), 1000, 60000);

        // register events
        Events.on(EventType.PlayerJoin.class, PlayerAction::playerJoined);

        // register chat filter
        Vars.netServer.admins.addChatFilter(MutedPlayerFilter::init);
    }

    // register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler) {

        // bans commands
        handler.register("admtimeban", "<playername/playeruuid> <year> <month> <date> <hour> <minute>",
            "bans the player on set dates", ServerAdminCommands::banPlayer);

        handler.register("admunban", "<playername/playeruuid>", "unban the player", ServerAdminCommands::unbanPlayer);

        // mute commands
        handler.register("admmute", "<playername/playeruuid> <year> <month> <date> <hour> <minute>", 
            "mute the player on set dates", ServerAdminCommands::mutePlayer);
        
        handler.register("admunmute", "<playername/playeruuid>", "unmute the player", ServerAdminCommands::unmutePlayer);

        // other commands
        handler.register("admcurrentdate", "getting current date", ServerAdminCommands::currentDate);
        handler.register("admplayerstats", "<playername/playeruuid>", "returns the status of the player's ban and mut", ServerAdminCommands::playerStats);
    }

    // register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler) {

        // bans commands
        handler.<Player>register("admtimeban", "<playername/playeruuid> <year> <month> <date> <hour> <minute>",
            "bans the player on set dates", ClientAdminCommands::banPlayer);

        handler.<Player>register("admtimeunban", "<playername/playeruuid>", "unban the player", ClientAdminCommands::unbanPlayer);

        // mute commands
        handler.<Player>register("admmute", "<playername/playeruuid> <year> <month> <date> <hour> <minute>", 
            "mute the player on set dates", ClientAdminCommands::mutePlayer);
        
        handler.<Player>register("admunmute", "<playername/playeruuid>", "unmute the player", ClientAdminCommands::unmutePlayer);

        // other commands
        handler.<Player>register("admcurrentdate", "getting current date", ClientAdminCommands::currentDate);
        handler.<Player>register("admplayeruuid", "<playername>", "returns the player UUID", ClientAdminCommands::playerId);
        handler.<Player>register("admplayerstats", "<playername/playeruuid>", "returns the status of the player's ban and mut", ClientAdminCommands::playerStats);
    }
}
