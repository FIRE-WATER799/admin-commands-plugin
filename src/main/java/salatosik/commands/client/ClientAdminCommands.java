package salatosik.commands.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.net.NetConnection;
import salatosik.util.ConfigLoader;
import salatosik.util.DatabasePlayersSystem;

public class ClientAdminCommands {
    private static SimpleDateFormat formater = ConfigLoader.getTimeZoneFormatter();

    private static Calendar timeConsructor(String[] arguments, Player player) {
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
            player.sendMessage("[yellow]Недійсний аргумент! Будь-ласка, напишіть цифри або символ '#', щоб вставити поточну дату");
            return null;

        } catch(Exception exception) {
            player.sendMessage("[yellow]Недійсний аргумент! Можливо, ви ввели занадто велику цифру");
            return null;
        }

        calendar.set(dateList.get(0), dateList.get(1), dateList.get(2), 
            dateList.get(3), dateList.get(4), 0);
        
        return calendar;
    }

    public static void currentDate(String[] args, Player player) {
        player.sendMessage("[green]Час: [yellow]" + formater.format(ConfigLoader.getTimeZoneCalendar().getTime()));
    }

    public static void playerId(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]Команда доступна лише адмінам!");
            return;
        }
        
        for(NetConnection net: Vars.net.getConnections()) {
            if(net.player.name().equals(args[0])) {
                if(DatabasePlayersSystem.searchId(net.player.con().uuid)) {

                    player.sendMessage("[red]UUID [green]гравця [yellow][player]: [red][uuid]"
                        .replace("[player]", net.player.name())
                        .replace("[uuid]", net.player.con().uuid));

                } else {
                    player.sendMessage("[yellow]Гравця не знайдено.");
                }

                break;
            }
        }
    }

    private static String statsBuilder(String playerName, long banTime, long muteTime) {
        String text = "[green]Гравець: [yellow]" + playerName + "[]\n";

        if(banTime == 0) text += "Час блокування: [yellow]не заблокований[]\n";
        else text += "Час блокування: [yellow]" + formater.format(new Date(banTime)) + "[]\n";

        if(muteTime == 0) text += "Час приглушення: [yellow]не приглушений";
        else text += "Час блокування чату: [yellow]" + formater.format(new Date(muteTime));

        return text;
    }

    public static void playerStats(String[] args, Player player) {
        if(player.admin) {
            if(DatabasePlayersSystem.searchId(args[0])) {
                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.con().uuid.equals(args[0])) {
                        long banTime = DatabasePlayersSystem.getByPlayerId(args[0], "bantime");
                        long muteTime = DatabasePlayersSystem.getByPlayerId(args[0], "mutetime");
                        
                        Call.infoMessage(player.con(), statsBuilder(net.player.name(), banTime, muteTime));
                        return;
                    }
                }

            } else {
                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.name().equals(args[0])) {
                        if(DatabasePlayersSystem.searchId(net.player.con().uuid)) {
                            long banTime = DatabasePlayersSystem.getByPlayerId(args[0], "bantime");
                            long muteTime = DatabasePlayersSystem.getByPlayerId(args[0], "mutetime");

                            Call.infoMessage(player.con(), statsBuilder(net.player.name(), banTime, muteTime));
                            return;
                        }
                    }
                }
            }

            player.sendMessage("[yellow]Гравця не знайдено");

        } else {
            player.sendMessage("[red]Команда доступна лише адмінам!");
        }
    }

    public static void banPlayer(String[] args, Player player) {
        Calendar calendar = timeConsructor(args, player);

        if(!player.admin) {
            player.sendMessage("[red]Команда доступна лише адмінам!");
            return;

        } else if(calendar != null) {
            if(DatabasePlayersSystem.searchId(args[0])) {

                if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") > 0) {
                    player.sendMessage("[yellow]Гравець вже заблокований");
                    return;

                } else {
                    DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", calendar.getTime().getTime());
                    
                    for(NetConnection net: Vars.net.getConnections()) {
                        if(net.player.con().uuid.equals(args[0])) {
                            net.kick("[red]Вас заблокував адмін" + player.name + "\nДо кінця блокування: [yellow]" + 
                                formater.format(calendar.getTime()), 100);

                            break;
                        }
                    }

                    player.sendMessage("[green]Гравець заблокований!");
                    return;
                }

            } else {
                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.name().equals(args[0])) {
                        String uuid = net.player.con().uuid;

                        if(DatabasePlayersSystem.searchId(uuid)) {
                            DatabasePlayersSystem.replaceWherePlayerId(uuid, "bantime", 
                                calendar.getTime().getTime());
                            
                            net.kick("[red]Вас забанив адмін [yellow]" + player.name +"\n[green]До кінця блокування: [yellow]" + 
                                formater.format(calendar.getTime()), 100);

                            player.sendMessage("[green]Гравець заблокований!");
                            return;
                        }
                    }
                }
            }

            player.sendMessage("[yellow]Гравця не знайдено");
        }
    }

    public static void unbanPlayer(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]Команда доступна лише адмінам!");

        } else if(DatabasePlayersSystem.searchId(args[0])) {
            if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") == 0) {
                player.sendMessage("[yellow]Гравець на даний момент не заблокований");
                return;
            }

            DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", 0);
            player.sendMessage("[green]Гравець успішно розблокований!");
            return;

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", 0);
                        player.sendMessage("[green]Гравець успішно розблокований!");
                    }

                    return;
                }
            }
        }

        player.sendMessage("[yellow]Гравець не знайдений!");
    }

    public static void mutePlayer(String[] args, Player player) {
        Calendar calendar = timeConsructor(args, player);

        if(!player.admin) {
            player.sendMessage("[red]Команда доступна лише адмінам!");
            return;

        } else if(DatabasePlayersSystem.searchId(args[0])) {
            if(DatabasePlayersSystem.getByPlayerId(args[0], "mutetime") > 0) {
                player.sendMessage("[green]Гравець вже приглушений.");
                return;
            }

            DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", calendar.getTime().getTime());
            player.sendMessage("[green]Гравець успішно приглушений!");

            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.con().uuid.equals(args[0])) {
                    Call.infoMessage(net.player.con(), "[red]Тебе приглушив адмін [yellow]" + player.name() +
                            "\nДо кінця наказу: " + formater.format(calendar.getTime()));

                    return;
                }
            }

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        if(DatabasePlayersSystem.getByPlayerId(uuid, "mutetime") > 0) {
                            player.sendMessage("[green]Гравець вже приглушений.");
                            return;
                        }

                        DatabasePlayersSystem.replaceWherePlayerId(uuid, "mutetime", calendar.getTime().getTime());
                        player.sendMessage("[green]Гравець успішно приглушений!");

                        Call.infoMessage(net.player.con(), "[red]Тебе приглушив адмін [yellow]" + player.name() +
                            "\nДо кінця наказу: " + formater.format(calendar.getTime()));
                        
                        return;
                    }
                }
            }
        }

        player.sendMessage("[yellow]Гравець не знайдений.");
    }

    public static void unmutePlayer(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]Команда доступна лише адмінам!");

        } else if(DatabasePlayersSystem.searchId(args[0])) {

            if(DatabasePlayersSystem.getByPlayerId(args[0], "mutetime") == 0) {
                player.sendMessage("[yellow]Гравець не має статусу приглушеного!");
                return;

            } else {
                DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", 0);
                player.sendMessage("[green]Гравець успішно розглушений!");

                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.con().uuid.equals(args[0])) {
                        Call.infoMessage(net.player.con(), "[green]Вас розглушив адмін [yellow]" +
                            player.name() + "\n[green]Тепер ви знову можете писати в чат!");

                        break;
                    }
                }

                return;
            }

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        if(DatabasePlayersSystem.getByPlayerId(uuid, "mutetime") == 0) {
                            player.sendMessage("[yellow]Гравець не має статусу приглушеного!");
                            return;
                        }

                        DatabasePlayersSystem.replaceWherePlayerId(uuid, "mutetime", 0);
                        player.sendMessage("[green]Гравець успішно розглушений!");

                        Call.infoMessage(net.player.con(), "[green]Вас розглушив адмін [yellow]" +
                            player.name() + "\n[green]Тепер ви знову можете писати в чат!");
                        
                        return;
                    }
                }
            }
        }

        player.sendMessage("[yellow]Гравець не знайдений.");
    }
}
