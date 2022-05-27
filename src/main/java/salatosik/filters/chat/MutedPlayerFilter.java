package salatosik.filters.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mindustry.gen.Player;
import salatosik.util.ConfigLoader;
import salatosik.util.DatabasePlayersSystem;

public class MutedPlayerFilter {

    private static SimpleDateFormat formater = ConfigLoader.getTimeZoneFormatter();

    public static String init(Player player, String text) {
        if(DatabasePlayersSystem.getByPlayerId(player.con().uuid, "mutetime") > 0) {

            Calendar calendar = ConfigLoader.getTimeZoneCalendar();
            calendar.setTime(new Date(DatabasePlayersSystem.getByPlayerId(player.con().uuid, "mutetime")));

            player.sendMessage("[red]Ви заглушені!\n[green]До кінця приглушення: [yellow]" +
                formater.format(calendar.getTime()));

            return text.replace(text, "[yellow]щось бурмутить... [red](заглушений)");

        } else { return text; }
    }
}
