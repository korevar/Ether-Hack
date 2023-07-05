package EtherHack.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;

/**
 * Класс, отвечащий за инъекцию в игровые файлы
 */

@UtilityClass
public class Patch {
    private static final Map<String, ClassNode> classNodeMap = new HashMap<>();

    /**
     * Проверяет, присутствует ли указанная аннотация в файле .class.
     *
     * @param filePath      Относительный путь к файлу .class, который нужно проверить.
     * @param gameFolder    Название игровой папки с .class файлами
     * @return true, если аннотация присутствует в классе, false в противном случае.
     */
    public static boolean isInjectedAnnotationPresent(String filePath, String gameFolder) {
        Path currentPath = Paths.get("").toAbsolutePath();
        Path classPath = Paths.get(currentPath.toString(), gameFolder, filePath);
        try (FileInputStream fileInputStream = new FileInputStream(classPath.toString())) {
            ClassReader reader = new ClassReader(fileInputStream);
            final boolean[] isAnnotationFound = {false};

            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.equals("LEtherHack/annotations/Injected;")) {
                                isAnnotationFound[0] = true;
                            }
                            return super.visitAnnotation(descriptor, visible);
                        }
                    };
                }
            }, 0);

            return isAnnotationFound[0];
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Добавление аннотации Injected к изменяемому методу
     * @param classNode     Узел всех классов bytecode'a
     * @param methodName    Название изменяемого метода
     */
    private void addInjectAnnotation(ClassNode classNode, String methodName){
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
     * Вставляет указанный хук в случае вызова события LuaEventManager.triggerEvent() с указанным именем события.
     *
     * @param method            метод, в который будет вставлен хук
     * @param eventName         имя события, после вызова которого будет вставлен хук
     * @param hook              хук, который будет вставлен после вызова события
     * @param insertBefore      вставлять инструкцию до или после данного хука
     */
    public static void insertHookForEventTrigger(MethodNode method, String eventName, MethodInsnNode hook, boolean insertBefore) {
        AbstractInsnNode insn = method.instructions.getFirst();
        while (insn != null) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.name.equals("triggerEvent") && methodInsn.desc.equals("(Ljava/lang/String;)V")) {
                    if (methodInsn.getPrevious() instanceof LdcInsnNode) {
                        LdcInsnNode ldcInsn = (LdcInsnNode) methodInsn.getPrevious();
                        if (ldcInsn.cst instanceof String && ldcInsn.cst.equals(eventName)) {
                            // Вставка нового вызова перед найденным вызовом
                            if (insertBefore){
                                method.instructions.insertBefore(methodInsn, hook);
                            } else{
                                method.instructions.insert(methodInsn, hook);
                            }
                            break;
                        }
                    }
                }
            }
            insn = insn.getNext();
        }
    }

    /**
     * Метод для инъекции аннотаций и внесения изменений в заданный метод класса.
     * Первоначально, он читает байт-код класса и преобразует его в ClassNode для дальнейшей обработки.
     * Затем добавляет аннотацию к указанному методу и использует предоставленное действие Consumer<MethodNode>
     * для внесения изменений в этот метод. После внесения всех изменений, метод записывает измененный байт-код
     * обратно в .class файл.
     *
     * @param className    Название класса, в который будет произведена инъекция.
     * @param methodName   Название метода, в который будет добавлена аннотация и произведены изменения.
     * @param isStatic     Является ли искомый метод статическим или нет
     * @param modifyMethod Действие, которое будет применено к MethodNode для внесения изменений в метод.
     *                     Это действие принимает один параметр - MethodNode, представляющий метод, в который
     *                     вносятся изменения.
     */
    public void injectIntoClass(String className, String methodName, boolean isStatic, Consumer<MethodNode> modifyMethod) {
        Logger.print("Injection into a game file '" + className + "'" + " in method: '" + methodName + "'");

        try {
            // Чтение класса или извлечение из Map, если он уже был прочитан
            ClassNode classNode = classNodeMap.getOrDefault(className, new ClassNode());
            if (!classNodeMap.containsKey(className)) {
                ClassReader classReader = new ClassReader(className);
                classReader.accept(classNode, 0);
            }

            // Поиск метода, добавление аннотации и внесение изменений
            for (MethodNode method : classNode.methods) {
                if (method.name.equals(methodName) && Modifier.isStatic(method.access) == isStatic) {
                    // Добавляем аннотацию
                    addInjectAnnotation(classNode, methodName);
                    modifyMethod.accept(method);
                }
            }

            // Сохранение измененного класса в Map
            classNodeMap.put(className, classNode);

        } catch (IOException e) {
            Logger.print("An error occurred during injection into '" + className + "': " + e.getMessage());
        }
    }

    /**
     * Применение изменений в .class файлы
     */
    public void saveModifiedClasses() {
        for (Map.Entry<String, ClassNode> entry : classNodeMap.entrySet()) {
            String className = entry.getKey();
            ClassNode classNode = entry.getValue();

            // Запись изменений
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            byte[] modifiedClass = classWriter.toByteArray();

            // Запись измененного класса обратно в файл
            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
                fos.write(modifiedClass);
            } catch (IOException e) {
                Logger.print("An error occurred while writing the modified class '" + className + "': " + e.getMessage());
            }
        }
    }
}
