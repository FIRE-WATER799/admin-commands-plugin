# Information
Some time ago (no) I had no idea to create a new plugin, and I asked CergC
so that he could suggest an idea, he answered me so that I could create a plug-in for blocking and jamming!
And you know what? I didn't know the database but I got together and wrote it all (who am I contacting ..)
## Особливості
- The plugin works on a database **sqlite3**
- Even when the plugin is turned off conditionally time will go

## Команди
- Traditionally, to avoid conflicts between team names, I added the opening word ``adm``
- You can also enter dates in the commands where you need to enter ``#`` so the plugin will take the present
the value of an element. For example there is a time ``2022 5 22 21 40`` of course that it quickly gets bored back this can be solved quickly by writing ``# # # 21 40`` comes out as ``2022 5 22 21 40``.

# console commands
- ``admtimeban <playername/UUID> <year> <month> <date> <hour> <minute>`` - blocked the player at the 
specified time

- ``admtimeunban <playername/UUID>`` - unlocks the player
- ``admmute <playername/UUID> <year> <month> <date> <hour> <minute>`` - drowns the player at a given time
- ``admunmute <playername/UUID>`` - deafens the player
- ``admcurrentdate`` - gives full dates from the time you wrote the command
- ``admplayerstats <playername/UUID>`` - gives player statistics (blocking and muting time)

# client commands
to all **clients** commands have access only to the admin
- ``admtimeban <playername/UUID> <year> <month> <date> <hour> <minute>`` - blocks the player 
at the specified time

- ``admtimeunban <playername/UUID>`` - unlocks the player
- ``admmute <playername/UUID> <year> <month> <date> <hour> <minute>`` - drowns the player at a given time
- ``admunmute <playername/UUID>`` - deafens the player
- ``admcurrentdate`` - gives full dates from the time you wrote the command
- ``admplayeruuid <playername>`` - gives **UUID** player 
(exactly the identifier that is recorded in the database)

- ``admplayerstats <playername/UUID>`` - gives player statistics in the table (block and mut time)

# Compilation
You need to compile it to start using my plugin

## requirements
- installed **Maven** on your computer
- loaded **zip** source code archive

## compilation process
- open a command prompt in an unzipped folder **zip** archive (if not unzipped unzip)
- write it down ``mvn install``
- at the end of the assembly **jar** the file on the command line should write 
*build succes* and create a folder *target*

- in the folder **target** find **.jar** a file that will have an approximate name 
*admin-commands-plugin-1.0-jar-with-dependencies.jar* - this is a plugin that you can use for your server
