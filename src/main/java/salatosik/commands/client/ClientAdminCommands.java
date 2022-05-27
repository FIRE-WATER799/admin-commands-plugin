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
                    if(i == 1) {
                        dateList.add(calendar.get(Calendar.YEAR));

                    } else if(i == 2) {
                        dateList.add(calendar.get(Calendar.MONTH));

                    } else if(i == 3) {
                        dateList.add(calendar.get(Calendar.DATE));

                    } else if(i == 4) {
                        dateList.add(calendar.get(Calendar.HOUR_OF_DAY));

                    } else if(i == 5) {
                        dateList.add(calendar.get(Calendar.MINUTE));
                    }

                } else {
                    dateList.add(Integer.parseInt(arguments[i]));
                }
            }

        } catch(NumberFormatException exception) {
            player.sendMessage("[yellow]Invalid argument! Please write numbers or a '#' to insert the current date");
            return null;

        } catch(Exception exception) {
            player.sendMessage("[yellow]Invalid argument! You may have entered too large a number");
            return null;
        }

        calendar.set(dateList.get(0), dateList.get(1), dateList.get(2), 
            dateList.get(3), dateList.get(4), 0);
        
        return calendar;
    }

    public static void currentDate(String[] args, Player player) {
        player.sendMessage("[green]Time: [yellow]" + formater.format(ConfigLoader.getTimeZoneCalendar().getTime()));
    }

    public static void playerId(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]the command is available only to admins!");
            return;
        }
        
        for(NetConnection net: Vars.net.getConnections()) {
            if(net.player.name().equals(args[0])) {
                if(DatabasePlayersSystem.searchId(net.player.con().uuid)) {

                    player.sendMessage("[red]UUID [green]player [yellow][player]: [red][uuid]"
                        .replace("[player]", net.player.name())
                        .replace("[uuid]", net.player.con().uuid));

                } else {
                    player.sendMessage("[yellow]player not found.");
                }

                break;
            }
        }
    }

    private static String statsBuilder(String playerName, long banTime, long muteTime) {
        String text = "[green]Player: [yellow]" + playerName + "[]\n";

        if(banTime == 0) text += "Block time: [yellow]no blocked[]\n";
        else text += "Block time: [yellow]" + formater.format(new Date(banTime)) + "[]\n";

        if(muteTime == 0) text += "Mute time: [yellow]no muted";
        else text += "Chat block time: [yellow]" + formater.format(new Date(muteTime));

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

            player.sendMessage("[yellow]Player not found!");

        } else {
            player.sendMessage("[red]the command is only available to admins!");
        }
    }

    public static void banPlayer(String[] args, Player player) {
        Calendar calendar = timeConsructor(args, player);

        if(!player.admin) {
            player.sendMessage("[red]the command is only available to admins!");
            return;

        } else if(calendar != null) {
            if(DatabasePlayersSystem.searchId(args[0])) {

                if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") > 0) {
                    player.sendMessage("[yellow]Player has blocked");
                    return;

                } else {
                    DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", calendar.getTime().getTime());
                    
                    for(NetConnection net: Vars.net.getConnections()) {
                        if(net.player.con().uuid.equals(args[0])) {
                            net.kick("[red]You blocked by admin" + player.name + "\nTo end block: [yellow]" + 
                                formater.format(calendar.getTime()), 100);

                            break;
                        }
                    }

                    player.sendMessage("[green]player has blocked!");
                    return;
                }

            } else {
                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.name().equals(args[0])) {
                        String uuid = net.player.con().uuid;

                        if(DatabasePlayersSystem.searchId(uuid)) {
                            DatabasePlayersSystem.replaceWherePlayerId(uuid, "bantime", 
                                calendar.getTime().getTime());
                            
                            net.kick("[red]You baned by admin [yellow]" + player.name +"\n[green]To end block: [yellow]" + 
                                formater.format(calendar.getTime()), 100);

                            player.sendMessage("[green]Player has blocked!");
                            return;
                        }
                    }
                }
            }

            player.sendMessage("[yellow]the player not found");
        }
    }

    public static void unbanPlayer(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]The command is available only to admins!");

        } else if(DatabasePlayersSystem.searchId(args[0])) {
            if(DatabasePlayersSystem.getByPlayerId(args[0], "bantime") == 0) {
                player.sendMessage("[yellow]The player is not currently blocked");
                return;
            }

            DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", 0);
            player.sendMessage("[green]Player successfully unblocked!");
            return;

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        DatabasePlayersSystem.replaceWherePlayerId(args[0], "bantime", 0);
                        player.sendMessage("[green]Player successfully unblocked!");
                    }

                    return;
                }
            }
        }

        player.sendMessage("[yellow]Player not found");
    }

    public static void mutePlayer(String[] args, Player player) {
        Calendar calendar = timeConsructor(args, player);

        if(!player.admin) {
            player.sendMessage("[red]Player successfully unblocked!");
            return;

        } else if(DatabasePlayersSystem.searchId(args[0])) {
            if(DatabasePlayersSystem.getByPlayerId(args[0], "mutetime") > 0) {
                player.sendMessage("[green]The player is already muted.");
                return;
            }

            DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", calendar.getTime().getTime());
            player.sendMessage("[green]The player was successfully muted!");

            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.con().uuid.equals(args[0])) {
                    Call.infoMessage(net.player.con(), "[red]You were muffled by the administrator[yellow]" + player.name() +
                            "\nUntil the end of the order: " + formater.format(calendar.getTime()));

                    return;
                }
            }

        } else {
            for(NetConnection net: Vars.net.getConnections()) {
                if(net.player.name().equals(args[0])) {
                    String uuid = net.player.con().uuid;

                    if(DatabasePlayersSystem.searchId(uuid)) {
                        if(DatabasePlayersSystem.getByPlayerId(uuid, "mutetime") > 0) {
                            player.sendMessage("[green]The player is already muted.");
                            return;
                        }

                        DatabasePlayersSystem.replaceWherePlayerId(uuid, "mutetime", calendar.getTime().getTime());
                        player.sendMessage("[green]The player was successfully muted!");

                        Call.infoMessage(net.player.con(), "[red]You were muffled by the administrator [yellow]" + player.name() +
                            "\nUntil the end of the order: " + formater.format(calendar.getTime()));
                        
                        return;
                    }
                }
            }
        }

        player.sendMessage("[yellow]Player not found.");
    }

    public static void unmutePlayer(String[] args, Player player) {
        if(!player.admin) {
            player.sendMessage("[red]The team is available only to admins!");

        } else if(DatabasePlayersSystem.searchId(args[0])) {

            if(DatabasePlayersSystem.getByPlayerId(args[0], "mutetime") == 0) {
                player.sendMessage("[yellow]The player does not have the status of muted!");
                return;

            } else {
                DatabasePlayersSystem.replaceWherePlayerId(args[0], "mutetime", 0);
                player.sendMessage("[green]The player was successfully silenced!");

                for(NetConnection net: Vars.net.getConnections()) {
                    if(net.player.con().uuid.equals(args[0])) {
                        Call.infoMessage(net.player.con(), "[green]You were unmuted by the administrator [yellow]" +
                            player.name() + "\n[green]Now you can chat again!");

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
                            player.sendMessage("[yellow]The player does not have the status of muted!");
                            return;
                        }

                        DatabasePlayersSystem.replaceWherePlayerId(uuid, "mutetime", 0);
                        player.sendMessage("[green]The player was successfully muted!");

                        Call.infoMessage(net.player.con(), "[green]You were unmuted by the administrator [yellow]" +
                            player.name() + "\n[green]Now you can chat again!");
                        
                        return;
                    }
                }
            }
        }

        player.sendMessage("[yellow]Player not found.");
    }
}
