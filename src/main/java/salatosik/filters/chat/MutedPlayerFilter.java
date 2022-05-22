package salatosik.filters.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import mindustry.gen.Player;
import salatosik.util.DatabasePlayersSystem;

public class MutedPlayerFilter {

    private static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String init(Player player, String text) {
        if(DatabasePlayersSystem.getByPlayerId(player.con().uuid, "mutetime") > 0) {

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date(DatabasePlayersSystem.getByPlayerId(player.con().uuid, "mutetime")));

            player.sendMessage("[red]Ви замючені!\n[green]До кінця муту: [yellow]" +
                formater.format(calendar.getTime()) + " []хвилин!");

            return text.replace(text, "[yellow]щось бурмутить... (замючений)");

        } else { return text; }
    }
}
