package salatosik.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import mindustry.Vars;
import mindustry.net.NetConnection;

public class DatabaseTimerTask extends TimerTask {

    @Override
    public void run() {
        List<String> playerIdList = DatabasePlayersSystem.getListId();

        if(playerIdList != null) {
            for(String id: playerIdList) {
                if(DatabasePlayersSystem.searchId(id)) {
                    
                    long muteTime = DatabasePlayersSystem.getByPlayerId(id, "mutetime");
                    long banTime = DatabasePlayersSystem.getByPlayerId(id, "bantime");

                    if(DatabasePlayersSystem.getByPlayerId(id, "bantime") != 0) {

                        Calendar banFromDatabase = ConfigLoader.getTimeZoneCalendar();
                        banFromDatabase.setTime(new Date(banTime));

                        if(ConfigLoader.getTimeZoneCalendar().after(banFromDatabase)) {
                            DatabasePlayersSystem.replaceWherePlayerId(id, "bantime", 0);
                        }
                    }
                    
                    for(NetConnection net: Vars.net.getConnections()) {
                        if(net.player.con().uuid.equals(id)) {
                            if(DatabasePlayersSystem.getByPlayerId(id, "mutetime") != 0) {

                                Calendar muteFromDatabase = ConfigLoader.getTimeZoneCalendar();
                                muteFromDatabase.setTime(new Date(muteTime));

                                if(ConfigLoader.getTimeZoneCalendar().after(muteFromDatabase)) {
                                    net.player.sendMessage("[green]Час приглушення закінчився і ви знову можете писати.");
                                    DatabasePlayersSystem.replaceWherePlayerId(id, "mutetime", 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
