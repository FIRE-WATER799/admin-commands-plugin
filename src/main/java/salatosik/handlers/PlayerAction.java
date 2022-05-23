package salatosik.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import mindustry.game.EventType;
import salatosik.util.DatabasePlayersSystem;

public class PlayerAction {
    public static void playerJoined(EventType.PlayerJoin event) {
        boolean playerInDatabase = DatabasePlayersSystem.searchId(event.player.con().uuid);

        if(!playerInDatabase) {
            DatabasePlayersSystem.createNewPlayer(event.player.con().uuid, 0, 0);

        } else if(playerInDatabase) {

            long banTime = DatabasePlayersSystem.getByPlayerId(event.player.con().uuid, "bantime");
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if(banTime != 0) {
                event.player.con().kick("[red]Ви були заблуковані на сервері!\n[green]Час зняття наказу: [yellow]" +
                    formater.format(new Date(banTime)), 100);
            }
        }
    }
}
