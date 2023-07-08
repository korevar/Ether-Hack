package EtherHack;

import EtherHack.utils.Logger;
import EtherHack.utils.Info;
import EtherHack.utils.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Класс, отвечающий за установку и удаление чита из кодов игры
 */
public class GamePatcher {

    /**
     * Список всех файлов, подлежащих инъекции
     */
    private final String[] patchFiles = new String[]{
            "GameWindow.class", "inventory/ItemContainer.class", "ui/UIManager.class"
    };

    /**
     * Название игровой папки с .class файлами
     */
    private final String gameClassFolder = "zombie";

    /**
     * Папки и файлы, которые нужно экспортировать в корневую директорию игры
     */
    private final String[] whiteListPathEtherFiles = new String[]{
            "com/zwitserloot", "Class50", "EtherHack", "lombok", "org/objectweb"
    };

    /**
     * Экспортирование файлов EtherHack в корневую директорию игры
     */
    public void extractEtherHack() {
        try {
            // Получаем местоположение запущенного JAR файла
            String jarFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

            // Открываем текущий JAR-файл
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                // Получаем все записи из JAR-файла
                Enumeration<JarEntry> entries = jarFile.entries();

                // Получаем текущую директорию
                Path currentDirectory = Paths.get(System.getProperty("user.dir"));

                // Проходим по каждой записи в JAR-файле
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    // Проверяем, что запись находится в белом списке
                    if (isInWhitelist(entry.getName())) {
                        // Создаем путь для извлечения файла
                        Path extractPath = currentDirectory.resolve(entry.getName());

                        // Если запись - директория, создаем пустую директорию
                        if (entry.isDirectory()) {
                            Files.createDirectories(extractPath);
                        } else {
                            // Если запись - файл, копируем его содержимое
                            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                Files.copy(inputStream, extractPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }

            Logger.print("Extraction completed successfully");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     *Проверяет, находится ли запись в белом списке.
     * @param entryName путь и имя записи в JAR-файле
     * @return {@code true}, если запись находится в белом списке, иначе {@code false}
     */
    private boolean isInWhitelist(String entryName) {
        // Проверяем, что запись находится в белом списке
        for (String whitelistEntry : whiteListPathEtherFiles) {
            if (entryName.startsWith(whitelistEntry)) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Удаление всех экспортированных файлов EtherHack из директории игры
     */
    public void uninstallEtherHackFiles() {
        Logger.print("Deleting all EtherHack files...");
        try {
            // Получаем текущую директорию
            Path currentDirectory = Paths.get(System.getProperty("user.dir"));

            // Проходим по каждому элементу в белом списке
            for (String deletePath : whiteListPathEtherFiles) {
                Path targetPath = currentDirectory.resolve(deletePath);

                // Удаляем папки и файлы, если они существуют
                if (Files.exists(targetPath)) {
                    if (Files.isDirectory(targetPath)) {
                        deleteDirectory(targetPath);
                    } else {
                        Files.delete(targetPath);
                    }
                }
            }

            Logger.print("Deletion EtherHack files completed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    *
     * Рекурсивно удаляет папку и ее содержимое.
     * @param directoryPath путь к удаляемой папке
     */
    private void deleteDirectory(Path directoryPath) throws IOException {
        // Рекурсивное удаление папки и ее содержимого
        Files.walk(directoryPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Создает резервные копии игровых файлов, если они еще не существуют.
     * Файлы будут сохранены с расширением .bkup в той же папке, что и оригиналы.
     */
    public void backupGameFiles() {
        Path currentPath = Paths.get("").toAbsolutePath();

        for (int i = 0; i < patchFiles.length; i++) {
            String iteration = "[" + (i + 1) + "/" + patchFiles.length + "]";
            Logger.print("Creating a backup file '" + patchFiles[i] + "' " + iteration);

            Path originalFilePath = Paths.get(currentPath.toString(), gameClassFolder, patchFiles[i]);

            if (Files.exists(originalFilePath)) {
                try {
                    Path backupFilePath = Paths.get(originalFilePath.toString() + ".bkup");

                    if (Files.exists(backupFilePath)) {
                        Logger.print("Backup of the file already exists. Skipping backup.");
                    } else {
                        Files.copy(originalFilePath, backupFilePath);
                    }
                } catch (IOException e) {
                    Logger.print("Error while creating backup file: " + e.getMessage());
                }
            } else {
                Logger.print(patchFiles[i] + " file not found.");
            }
        }

        Logger.print("Backups of game files have been completed!");
    }

    /**
     * Внедрение в файл игрового окна
     */
    public void patchGameWindow() {
        Patch.injectIntoClass("zombie/GameWindow", "InitDisplay",true, method -> {
            String oldTitle = "Project Zomboid";
            String newTitle = "Project Zomboid" + Info.CHEAT_WINDOW_TITLE_SUFFIX;

            // Замена строки в методе
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn instanceof LdcInsnNode ldcInsnNode) {
                    if (ldcInsnNode.cst.equals(oldTitle)) {
                        ldcInsnNode.cst = newTitle;
                    }
                }
            }
            // Внедрение вызова EtherMain.getInstance().initEther()
            InsnList initEtherInstructions = new InsnList();
            initEtherInstructions.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/Ether/EtherMain",
                    "getInstance",
                    "()LEtherHack/Ether/EtherMain;",
                    false
            ));
            initEtherInstructions.add(new MethodInsnNode(
                    Opcodes.INVOKEVIRTUAL,
                    "EtherHack/Ether/EtherMain",
                    "initEther",
                    "()V",
                    false
            ));
            method.instructions.insert(initEtherInstructions);
        });

        Patch.injectIntoClass("zombie/GameWindow", "init", true, method -> {
            AbstractInsnNode lastInsn = method.instructions.getLast();
            if (lastInsn.getOpcode() == Opcodes.RETURN) {
                InsnList initEtherInstructions = new InsnList();
                initEtherInstructions.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "EtherHack/Ether/EtherLogo",
                        "getInstance",
                        "()LEtherHack/Ether/EtherLogo;",
                        false
                ));
                initEtherInstructions.add(new MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        "EtherHack/Ether/EtherLogo",
                        "initLogoState",
                        "()V",
                        false
                ));
                method.instructions.insertBefore(lastInsn, initEtherInstructions);
            } else {
                throw new IllegalStateException("Cannot find RETURN instruction in the method");
            }
        });

    }

    /**
     * Внедрение в файлы игровых предметов
     */
    public void patchItemContainer() {
        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getWeight",false, method -> {
            InsnList newInstructions = new InsnList();

            // Вставка проверки "this.parent instanceof IsoPlayer"
            newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));  // загрузить 'this'
            newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "zombie/inventory/ItemContainer", "parent", "Lzombie/characters/IsoGameCharacter;"));  // получить 'parent'
            newInstructions.add(new TypeInsnNode(Opcodes.INSTANCEOF, "zombie/characters/IsoPlayer"));  // проверить, является ли 'parent' экземпляром 'IsoPlayer'

            LabelNode ifLabel = new LabelNode();
            newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, ifLabel));  // если 'parent' не является экземпляром 'IsoPlayer', перейти к метке 'ifLabel'

            // Вставка проверки "EtherMain.getInstance().etherAPI.isWeightBypass"
            newInstructions.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/Ether/EtherMain",
                    "getInstance",
                    "()LEtherHack/Ether/EtherMain;",
                    false
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherMain",
                    "etherAPI",
                    "LEtherHack/Ether/EtherAPI;"
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherAPI",
                    "isUnlimitedCarry",
                    "Z"
            ));

            LabelNode returnLabel = new LabelNode();
            newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));  // если 'isInfiniteWeight' == 0 (false), перейти к метке 'returnLabel'

            // Вставка "return 0;"
            newInstructions.add(new InsnNode(Opcodes.ICONST_0));
            newInstructions.add(new InsnNode(Opcodes.IRETURN));

            // Метки для продолжения выполнения оригинального метода, если условие if не выполнено
            newInstructions.add(ifLabel);
            newInstructions.add(returnLabel);

            // Вставить новые инструкции в начало метода
            method.instructions.insert(newInstructions);
        });

        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getCapacityWeight",false, method -> {
            InsnList newInstructions = new InsnList();

            // Вставка проверки "EtherMain.getInstance().etherAPI.isWeightBypass"
            newInstructions.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/Ether/EtherMain",
                    "getInstance",
                    "()LEtherHack/Ether/EtherMain;",
                    false
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherMain",
                    "etherAPI",
                    "LEtherHack/Ether/EtherAPI;"
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherAPI",
                    "isUnlimitedCarry",
                    "Z"
            ));

            LabelNode returnLabel = new LabelNode();
            newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));  // если 'isInfiniteWeight' == 0 (false), перейти к метке 'returnLabel'

            // Вставка "return 0;"
            newInstructions.add(new InsnNode(Opcodes.FCONST_0));
            newInstructions.add(new InsnNode(Opcodes.FRETURN));

            // Метки для продолжения выполнения оригинального метода, если условие if не выполнено
            newInstructions.add(returnLabel);

            // Вставить новые инструкции в начало метода
            method.instructions.insert(newInstructions);
        });

        Patch.injectIntoClass("zombie/inventory/ItemContainer", "getContentsWeight",false, method -> {
            InsnList newInstructions = new InsnList();

            // Вставка проверки "EtherMain.getInstance().etherAPI.isWeightBypass"
            newInstructions.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/Ether/EtherMain",
                    "getInstance",
                    "()LEtherHack/Ether/EtherMain;",
                    false
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherMain",
                    "etherAPI",
                    "LEtherHack/Ether/EtherAPI;"
            ));
            newInstructions.add(new FieldInsnNode(
                    Opcodes.GETFIELD,
                    "EtherHack/Ether/EtherAPI",
                    "isUnlimitedCarry",
                    "Z"
            ));

            LabelNode returnLabel = new LabelNode();
            newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, returnLabel));  // если 'isInfiniteWeight' == 0 (false), перейти к метке 'returnLabel'

            // Вставка "return 0;"
            newInstructions.add(new InsnNode(Opcodes.FCONST_0));
            newInstructions.add(new InsnNode(Opcodes.FRETURN));

            // Метки для продолжения выполнения оригинального метода, если условие if не выполнено
            newInstructions.add(returnLabel);

            // Вставить новые инструкции в начало метода
            method.instructions.insert(newInstructions);
        });
    }


    /**
     * Внедрение в файл UIManager
     */
    public void patchUIManager() {
        Patch.injectIntoClass("zombie/ui/UIManager", "render", true, method -> {
            // Создание нового вызова метода PreRender
            MethodInsnNode PreRenderHook = new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/hooks/OnUIElementPreRenderHook",
                    "call",
                    "()V",
                    false
            );

            // Создание нового вызова метода PostRender
            MethodInsnNode PostRenderHook = new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "EtherHack/hooks/OnUIElementPostRenderHook",
                    "call",
                    "()V",
                    false
            );

            // Поиск вызова LuaEventManager.triggerEvent("OnPreUIDraw")
            Patch.insertHookForEventTrigger(method, "OnPreUIDraw", PreRenderHook , true);

            // Поиск вызова LuaEventManager.triggerEvent("OnPostUIDraw")
            Patch.insertHookForEventTrigger(method, "OnPostUIDraw", PostRenderHook, false);
        });
    }

    /**
     * Проверяет, содержит ли хотя бы один из заданных файлов аннотацию @Injected.
     * @return true, если аннотация @Injected найдена хотя бы в одном файле. false в противном случае.
     */
    public boolean checkInjectedAnnotations() {
        return Arrays.stream(patchFiles)
                .anyMatch(filePath -> Patch.isInjectedAnnotationPresent(filePath, gameClassFolder));
    }

    /**
     * Проверяет наличие игровой папки и определенных файлов внутри.
     * @return true, если игровая папка и все требуемые файлы присутствуют. false в противном случае.
     */
    public boolean isGameFolder() {
        Path gameFolderPath = Paths.get(gameClassFolder);

        // Проверяем, существует ли папка игры
        if (Files.exists(gameFolderPath) && Files.isDirectory(gameFolderPath)) {
            // Если папка существует, проверяем наличие всех необходимых файлов
            return Arrays.stream(patchFiles)
                    .allMatch(fileName -> Files.exists(gameFolderPath.resolve(fileName)));
        }

        return false;
    }

    /**
     * Патчинг игровых bytecode файлов игры
     * для реализации собственного фунционала
     */
    public void patchGame() {
        Logger.printCredits();

        Logger.print("Preparing to install the EtherHack...");

        if (!isGameFolder()){
            Logger.print("No game files were found in this directory. Place the cheat in the root folder of the game");
            return;
        };

        Logger.print("Checking for injections in game files");

        if (checkInjectedAnnotations()) {
            Logger.print("Signs of interference were found in the game files. If you have installed this cheat before, run it with the '--uninstall' flag. Otherwise, check the integrity of the game files via Steam");
            return;
        }
        Logger.print("No signs of injections were found. Preparing for backup...");

        backupGameFiles();

        Logger.print("Preparation for injection into game file...");

        patchGameWindow();

        patchUIManager();

        patchItemContainer();

        Patch.saveModifiedClasses();

        Logger.print("Extracting EtherHack files to the current directory");

        extractEtherHack();

        Logger.print("The cheat installation is complete, you can enter the game!");
    }

    /**
     * Восстановление оригинальных файлов игры
     */
    public void restoreFiles() {
        Logger.print("Restoring files...");

        Path currentPath = Paths.get("").toAbsolutePath();

        for (int i = 0; i < patchFiles.length; i++) {
            String fileName = patchFiles[i];
            String iteration = "[" + (i + 1) + "/" + patchFiles.length + "]";
            Logger.print("Restoring the file '" + fileName + "' " + iteration);

            Path originalFilePath = Paths.get(currentPath.toString(), gameClassFolder, patchFiles[i]);
            Path backupFilePath = Paths.get(originalFilePath.toString() + ".bkup");

            if (Files.exists(backupFilePath)) {
                try {
                    if (Files.exists(originalFilePath)) {
                        Files.delete(originalFilePath);
                    }

                    Files.move(backupFilePath, originalFilePath);
                } catch (IOException e) {
                    Logger.print("Error when restoring the game file '" + fileName +"': " + e.getMessage());
                }
            } else {
                Logger.print("Backup file '" + fileName + ".bkup' not found. Skipping restore");
            }
        }

        uninstallEtherHackFiles();

        Logger.print("Files restoration completed!");
    }

}
