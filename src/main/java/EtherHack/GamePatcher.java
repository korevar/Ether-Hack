package EtherHack;

import EtherHack.utils.Console;
import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Класс, отвечающий за установку и удаление чита из кодов игры
 */
public class GamePatcher {

    private final List<String> backupFilesList = Arrays.asList("GameWindow.class", "core/Core.class", "network/GameServer.class");

    /**
     * Создание бэкап файла по заданному пути
     * @param originalPath путь до файда .class
     */
    private void CreateBackupFile(Path originalPath) {
        try {
            Path backupPath = Paths.get(originalPath.toString() + ".bkup");

            if (Files.exists(backupPath)) {
                Console.Print("Backup of the file already exists. Skipping backup.");
                return;
            }

            Files.copy(originalPath, backupPath);
        } catch (IOException e) {
            Console.Print("Error while creating backup file: " + e.getMessage());
        }
    }

    /**
     * Создание бэкап файлов перед внесением изменений
     */
    public void BackupFiles(){
        Console.Print("Creating backup files...");

        try {
            for (int i = 0; i < backupFilesList.size(); i++) {
                String iteration = "[" + (i+1) + "/" + backupFilesList.size() + "]";
                Console.Print( "Creating a backup file '" + backupFilesList.get(i) + "' " + iteration);

                Path currentPath = Paths.get("").toAbsolutePath();
                Path backupFilePath = Paths.get(currentPath.toString(), "zombie", backupFilesList.get(i));

                if (Files.exists(backupFilePath)) {
                    CreateBackupFile(backupFilePath);
                } else {
                    Console.Print(backupFilesList.get(i) + " file not found.");
                }
            }

        } catch (Exception e) {
            Console.Print("Error while backing up files: " + e.getMessage());
        }
    }


    /**
     * Добавление аннотации Injected к изменяемому методу
     * @param classNode Узел всех классов bytecode'a
     * @param methodName Название изменяемого метода
     */
    private void AddAnnotation(ClassNode classNode, String methodName){
        // Поиск и добавление аннотации в метод
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName)) {
                if (method.visibleAnnotations == null) {
                    method.visibleAnnotations = new LinkedList<>();
                }
                AnnotationNode annotationNode = new AnnotationNode("LEtherHack/annotations/Injected;");
                method.visibleAnnotations.add(annotationNode);
            }
        }
    }

    /**
     * Распаковка файлов чита в директорию игры
     */
    public void ExtractEtherFiles() throws UnsupportedEncodingException {
        Console.Print("Unzipping EtherHack files...");

        // Получение пути к текущему исполняемому JAR-файлу
        String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        // Декодирование пути
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);


        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jarPath))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            // Удаление существующих папок
            deleteExistingDirectories();

            while (entry != null) {
                String entryName = entry.getName();

                // Проверка, является ли папка из белого списка
                if (isAllowedDirectory(entryName)) {
                    // Создание выходного потока для разархивации файла
                    File outputFile = new File(entryName);
                    if (entry.isDirectory()) {
                        outputFile.mkdirs();
                    } else {
                        outputFile.getParentFile().mkdirs();
                        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zipInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                        }
                    }
                }

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            Console.Print("An error occurred while unzipping EtherHack files: " + e.getMessage());
        }
    }

    /**
     * Удаление папок внутри директории, название которых совпадает с разархивированными
     */
    private void deleteExistingDirectories() {
        String[] directoriesToDelete = {"EtherHack", "Class50", "lombok"};
        String currentDirectoryPath = System.getProperty("user.dir");

        for (String directory : directoriesToDelete) {
            File directoryToDelete = new File(currentDirectoryPath, directory);
            if (directoryToDelete.exists()) {
                try {
                    FileUtils.deleteDirectory(directoryToDelete);
                    Console.Print("Deleted existing directory: " + directory);
                } catch (IOException e) {
                    Console.Print("An error occurred while deleting the existing directory: " + directory + ", " + e.getMessage());
                }
            }
        }
    }

    /**
     * Провера на принадлежность папки в текущем .jar к белому списку
     * @param path Путь до папки
     * @return true - является, false - нет
     */
    private boolean isAllowedDirectory(String path) {
        String[] allowedDirectories = {"EtherHack", "com", "Class50", "lombok"};
        for (String directory : allowedDirectories) {
            if (path.startsWith(directory + "/") || path.equals(directory)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Внедрение в файл игрового окна
     */
    public void PatchGameWindow() {
        Console.Print("Embedding a game window into a file");

        String className = "zombie/GameWindow";
        String methodName = "InitDisplay";
        String oldTitle = "Project Zomboid";
        String newTitle = "Project Zomboid by EtherHack";

        try {
            // Чтение класса
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(className);
            classReader.accept(classNode, 0);

            // Добавляем аннотацию
            AddAnnotation(classNode, methodName);

            // Поиск и замена строки в методах
            for (MethodNode method : classNode.methods) {
                if (method.name.equals(methodName)) {
                    for (AbstractInsnNode insn : method.instructions.toArray()) {
                        if (insn instanceof LdcInsnNode) {
                            LdcInsnNode ldcInsnNode = (LdcInsnNode) insn;
                            if (ldcInsnNode.cst.equals(oldTitle)) {
                                ldcInsnNode.cst = newTitle;
                            }
                        }
                    }

                    // Создание нового вызова метода
                    MethodInsnNode initEtherMain = new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "EtherHack/cheat/EtherMain",
                            "getInstance",
                            "()LEtherHack/cheat/EtherMain;",
                            false
                    );

                    // Вставка нового вызова в конец метода
                    method.instructions.insert(initEtherMain);
                }
            }

            // Запись изменений
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            byte[] modifiedClass = classWriter.toByteArray();

            // Запись измененного класса обратно в файл
            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
                fos.write(modifiedClass);
            }

        } catch (IOException e) {
            Console.Print("An error occurred while embedding the game window: " + e.getMessage());
        }
    }

    /**
     * Внедрение в файл игрового ядра
     */
    public void PatchGameCore() {
        Console.Print("Embedding a game core into a file");

        String className = "zombie/core/Core";
        String methodName = "EndFrameUI";

        try {
            // Чтение класса
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(className);
            classReader.accept(classNode, 0);

            // Добавляем аннотацию
            AddAnnotation(classNode, methodName);

            // Поиск и вставка вызова в начало метода
            for (MethodNode method : classNode.methods) {
                if (method.name.equals(methodName)) {
                    // Добавление операции загрузки ссылки на 'this'
                    VarInsnNode loadThis = new VarInsnNode(Opcodes.ALOAD, 0);
                    method.instructions.insert(loadThis);

                    // Создание нового вызова метода
                    MethodInsnNode newCall = new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "EtherHack/hooks/GameCoreHook",
                            "Call",
                            "(Lzombie/core/Core;)V",
                            false
                    );

                    // Вставка нового вызова после загрузки 'this'
                    method.instructions.insert(loadThis, newCall);
                }
            }

            // Запись изменений
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            byte[] modifiedClass = classWriter.toByteArray();

            // Запись измененного класса обратно в файл
            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
                fos.write(modifiedClass);
            }
        } catch (IOException e) {
            Console.Print("An error occurred while embedding the game core: " + e.getMessage());
        }
    }


    /**
     * Внедрение в файл игровоой сетевой логики
     */
    public void PatchGameNetwork() {
        Console.Print("Embedding a game network into a file");
        //TODO: Do...
    }

    /**
     * Патчинг игровых bytecode файлов игры
     * для реализации собственного фунционала
     */
    public void PatchGame() throws UnsupportedEncodingException {
        Console.Print("Preparing to install the EtherHack...");

        BackupFiles();

        Console.Print("Preparation for implementation in game files...");

        PatchGameWindow();

        PatchGameCore();

        PatchGameNetwork();

        ExtractEtherFiles();

        Console.Print("The cheat installation is complete, you can enter the game!");
    }

    /**
     * Восстановление оригинальных файлов игры
     */
    public void RestoreFiles() {
        Console.Print("Restoring files...");

        try {
            for (int i = 0; i < backupFilesList.size(); i++) {
                String fileName = backupFilesList.get(i);
                String iteration = "[" + (i + 1) + "/" + backupFilesList.size() + "]";

                Path currentPath = Paths.get("").toAbsolutePath();
                Path originalPath = Paths.get(currentPath.toString(), "zombie", fileName);
                Path backupPath = Paths.get(originalPath.toString() + ".bkup");

                if (Files.exists(backupPath)) {
                    Console.Print("Restoring the file '" + fileName + "' " + iteration);

                    if (Files.exists(originalPath)) {
                        Files.delete(originalPath);
                    }

                    Files.move(backupPath, originalPath);

                } else {
                    Console.Print("Backup file '" + fileName + ".bkup' not found. Skipping restore.");
                }
            }

            deleteExistingDirectories();

            Console.Print("Files restoration completed.");

        } catch (Exception e) {
            Console.Print("Error while restoring files: " + e.getMessage());
        }
    }
}
