package salatosik.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import mindustry.game.EventType;
import salatosik.util.ConfigLoader;
import salatosik.util.DatabasePlayersSystem;

public class PlayerAction {
    private static SimpleDateFormat formater = ConfigLoader.getTimeZoneFormatter();

    public static void playerJoined(EventType.PlayerJoin event) {
        boolean playerInDatabase = DatabasePlayersSystem.searchId(event.player.con().uuid);

        if(!playerInDatabase) {
            DatabasePlayersSystem.createNewPlayer(event.player.con().uuid, 0, 0);

        } else if(playerInDatabase) {

            long banTime = DatabasePlayersSystem.getByPlayerId(event.player.con().uuid, "bantime");

            if(banTime != 0) {
                event.player.con().kick("[red]You have been blocked on the server!\n[green]Until the end of the block: [yellow]" +
                    formater.format(new Date(banTime)), 100);
            }
        }
    }
}
