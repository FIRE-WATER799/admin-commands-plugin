package salatosik.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date(banTime));

                event.player.con().kick("[red]Ви були забанені на сервері!\n[green]Дата розбану: [yellow]" +
                    formater.format(calendar.getTime()), 100);
            }
        }
    }
}
