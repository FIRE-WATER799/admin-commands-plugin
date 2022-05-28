package salatosik.commands.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import arc.util.Log;
import mindustry.Vars;
import mindustry.net.NetConnection;
import salatosik.util.ConfigLoader;
import salatosik.util.DatabasePlayersSystem;

public class ServerAdminCommands {

    private static SimpleDateFormat formater = ConfigLoader.getTimeZoneFormatter();

    private static Calendar timeConsructor(String[] arguments) {
        Calendar calendar = ConfigLoader.getTimeZoneCalendar();
        List<Integer> dateList = new ArrayList<>();

        try {

            for(int i = 1; i < arguments.length; i++) {
                if(arguments[i].equals("#")) {
                    switch(i) {
                        case 1: dateList.add(calendar.get(Calendar.YEAR));
                        break;

                        case 2: dateList.add(calendar.get(Calendar.MONTH));
                        break;

                        case 3: dateList.add(calendar.get(Calendar.DATE));
                        break;

                        case 4: dateList.add(calendar.get(Calendar.HOUR_OF_DAY));
                        break;

                        case 5: dateList.add(calendar.get(Calendar.MINUTE));
                        break;
                    }

                } else {
                    dateList.add(Integer.parseInt(arguments[i]));
                }
            }

        } catch(NumberFormatException exception) {
            Log.info("Invalid argument! Please write numbers or symvol '#' to insert the current date");
            return null;

        } catch(Exception exception) {
            Log.info("Invalid argument! Maybe you entered too big a number");
            return null;
        }

        calendar.set(dateList.get(0), dateList.get(1), dateList.get(2), 
            dateList.get(3), dateList.get(4), 0);
        
        return calendar;
    }

    public static void currentDate(String[] args) {
        Log.info(formater.format(ConfigLoader.getTimeZoneCalendar().getTime()));
    }

    public static void banPlayer(String[] args) {
        Calendar calendar = timeConsructor(args);
        Long banTime;
        
        banTime = calendar.getTime().getTime();

        if(calendar != null || banTime != null) {

            for(NetConnection net: Vars.net.getConnections()) {

                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;
                    String playerName = net.player.name();

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        if(DatabasePlayersSystem.getByPlayerId(uuid, "bantime") != 0) {
                            Log.info("The player has already been banned");

                        } else {
                            DatabasePlayersSystem.replaceWherePlayerId(uuid, "bantime", banTime);

                            net.player.kick("[red]Вас заблокували через консоль серверу!\nДо кінця бану: [yellow][time]"
                                .replace("[time]", formater.format(calendar.getTime())), 100);

                            Log.info("The [player] was banned.".replace("[player]", playerName));
                        }

                        return;
                    }
                }
            }

            if(DatabasePlayersSystem.searchId(args[0])) {
                if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") != 0) {
                    Log.info("The player has already been banned");

                } else {
                    DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", banTime);

                    for(NetConnection net: Vars.net.getConnections()) {
                        if(net.player.con().uuid.equals(args[0])) {
                            net.player.kick("[red]Вас заблокували через консоль серверу до [yellow][time]"
                            .replace("[time]", formater.format(calendar.getTime())), 100);
                        }
                    }

                    Log.info("The player was banned");
                }

                return;
            }
        
            Log.info("Player is not found.");

        }
    }

    public static void unbanPlayer(String[] args) {        
        for(NetConnection net: Vars.net.getConnections()) {
            String uuid = net.player.con().uuid;

            if(net.player.name().equals(args[0])) {
                if(DatabasePlayersSystem.searchId(uuid)) {
                    if(DatabasePlayersSystem.getByPlayerId(uuid, "bantime") <= 0) {
                        Log.info("The player is not banned");
                        return;
    
                    } else {
                        Log.info("The player is unbaned");
                        DatabasePlayersSystem.replaceWherePlayerId(uuid, "bantime", 0);
                        return;
                    }
                }
            }
        }

        if(DatabasePlayersSystem.searchId(args[0])) {
            if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") <= 0) {
                Log.info("The player is not banned");
                return;

            } else {
                Log.info("The player is unbaned");
                DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", 0);
                return;
            }
        }

        Log.info("Player is not found.");

    }


    public static void mutePlayer(String[] args) {
        Calendar calendar = timeConsructor(args);
        Long mutetime = calendar.getTime().getTime();


        if(calendar != null || mutetime != null) {

            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        if(DatabasePlayersSystem.getByPlayerId(uuid, "mutetime") > 0) {
                            Log.info("The player is already muted!");
                            return;

                        } else {
                            DatabasePlayersSystem.replaceWherePlayerId(uuid, "mutetime", mutetime);
                            Log.info("Player has muted!");

                            net.player.sendMessage("[red]Вас заглушили через консоль!\n" +
                                "[green]До кінця приглушення: [yellow]" + formater.format(calendar.getTime()));

                            return;
                        }
                    }
                }
            }

            if(DatabasePlayersSystem.searchId(args[0])) {
                if(DatabasePlayersSystem.getByPlayerId(args[0], "mutetime") > 0) {
                    Log.info("The player is already muted!");
                    return;

                } else {
                    DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", mutetime);

                    for(NetConnection net: Vars.net.getConnections()) {
                        if(net.player.con().uuid.equals(args[0])) {
                            net.player.sendMessage("[red]Вас заглушили через консоль!\n" +
                                "[green]До кінця приглушення: [yellow]" + formater.format(calendar.getTime()));
                        }
                    }

                    Log.info("Player has muted!");
                    return;
                }
            }

            Log.info("Player not found");
        }
    }

    public static void unmutePlayer(String[] args) {
        if(DatabasePlayersSystem.searchId(args[0])) {
            long muteValue = DatabasePlayersSystem.getByPlayerId(args[0], "mutetime");

            if(muteValue == 0) {
                Log.info("Player not muted!");
                return;

            } else {
                DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", 0);
                Log.info("Player has unmuted!");

                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.name().equals(args[0])) {
                        net.player.sendMessage("[green]Вас розглушили і ви можете писати в чат!");
                    }
                }

                return;
            }
        }

        for(NetConnection net: Vars.net.getConnections()) {
            if(DatabasePlayersSystem.searchId(net.player.con().uuid)) {
                DatabasePlayersSystem.replaceWherePlayerId(net.player.con().uuid, "mutetime", 0);

                Log.info("Player has unmuted!");
                net.player.sendMessage("[green]Вас розглушили і ви можете писати в чат!");
                return;
            }
        }

        Log.info("Player not found");
    }

    public static void playerStats(String[] args) {
        if(DatabasePlayersSystem.searchId(args[0])) {
            long banTime = DatabasePlayersSystem.getByPlayerId(args[0], "bantime");
            long muteTime = DatabasePlayersSystem.getByPlayerId(args[0], "mutetime");

            if(banTime == 0) Log.info("Ban time: no ban status");
            else Log.info("Ban time: " + formater.format(new Date(banTime)));

            if(muteTime == 0) Log.info("Mute time: no mute status");
            else Log.info("Mute time: " + formater.format(new Date(muteTime)));

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.con().uuid.equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    long banTime = DatabasePlayersSystem.getByPlayerId(uuid, "bantime");
                    long muteTime = DatabasePlayersSystem.getByPlayerId(uuid, "mutetime");

                    if(banTime == 0) Log.info("Ban time: no ban status");
                    else Log.info("Ban time: " + formater.format(new Date(banTime)));

                    if(muteTime == 0) Log.info("Mute time: no mute status");
                    else Log.info("Mute time: " + formater.format(new Date(muteTime)));

                    break;
                }
            }
        }
    }
}
