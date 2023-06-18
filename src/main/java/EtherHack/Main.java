package EtherHack;

import EtherHack.utils.Console;

/**
 * Главный класс EtherHack - точка входа
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            Console.Print("You must specify one of the '--install' or '--uninstall' flags");
            return;
        }

        GamePatcher gamePatcher = new GamePatcher();
        switch (args[0]) {
            case "--install" -> gamePatcher.PatchGame();
            case "--uninstall" -> gamePatcher.RestoreFiles();
            default -> Console.Print("Unknown flag " + "'" + args[0] + "'");
        }
    }
}