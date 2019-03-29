package com.out.source.router.plugin.kernel.transfrom.utils;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.out.source.router.plugin.kernel.extension.BasePluginExtension;
import com.out.source.router.plugin.kernel.transfrom.BaseTransfrom;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by yangjian on 2018/6/25.
 */

public class TransformUtils {

    public static void transform(BaseTransfrom baseTransfrom, TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        BasePluginExtension extension = baseTransfrom.getExt();
        List<String> packgets = extension.getPackgets();
        if (baseTransfrom.isEnablePrepar()) {
            List<DirectoryInput> dirPathList = new ArrayList<>();
            List<JarInput> jarPathList = new ArrayList<>();
            for (TransformInput input : inputs) {
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                    dirPathList.add(directoryInput);
                }
                for (JarInput jarInput : input.getJarInputs()) {
                    jarPathList.add(jarInput);
                }
            }
            baseTransfrom.onPrepareDirAndClass(dirPathList, jarPathList);

            for (DirectoryInput directoryInput : dirPathList) {

                onDirectoryInput(directoryInput, packgets, baseTransfrom, outputProvider);
            }
            for (JarInput jarInput : jarPathList) {
                onJarInput(jarInput, packgets, baseTransfrom, outputProvider);
            }
        } else {
            for (TransformInput input : inputs) {
                for (DirectoryInput directoryInput : input.getDirectoryInputs()) {

                    onDirectoryInput(directoryInput, packgets, baseTransfrom, outputProvider);
                }
                for (JarInput jarInput : input.getJarInputs()) {
                    onJarInput(jarInput, packgets, baseTransfrom, outputProvider);
                }
            }
        }

    }


    private static void onDirectoryInput(DirectoryInput directoryInput, List<String> packgets, BaseTransfrom baseTransfrom, TransformOutputProvider outputProvider) throws IOException {
        File file = directoryInput.getFile();

        if (packgets == null || packgets.size() <= 0) {
            baseTransfrom.onLog("ext packgets is null");
        }
        List<String> dirList = new ArrayList<>();
        injectDir(file.getAbsolutePath(), packgets, dirList, baseTransfrom, false);
        injectClass(dirList, baseTransfrom);
        File dest = outputProvider.getContentLocation(directoryInput.getName(),
                directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
        FileUtils.copyDirectory(directoryInput.getFile(), dest);
    }

    private static void onJarInput(JarInput jarInput, List<String> packgets, BaseTransfrom baseTransfrom, TransformOutputProvider outputProvider) throws IOException {
        String jarPath = jarInput.getFile().getAbsolutePath();
        String projectName = baseTransfrom.getProject().getRootProject().getName();
        if (jarPath.contains(projectName)) {
            if (packgets == null || packgets.size() <= 0) {
                baseTransfrom.onLog("ext packgets is null");
            }
            injectJar(jarPath, packgets, baseTransfrom);
        }
        String jarName = jarInput.getName();
        String md5Name = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath());
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4);
        }
        File dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
        dest.getParentFile().mkdirs();
        dest.createNewFile();
        FileUtils.copyFile(jarInput.getFile(), dest);
    }


    public static void injectDir(String path, List<String> packgets, List<String> dirList, BaseTransfrom baseTransfrom, boolean isPrepare) {
        if (packgets == null || packgets.size() <= 0) {
            return;
        }
        File dir = new File(path);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                String filePath = file.getAbsolutePath();
                if (file.isDirectory()) {
                    if (!filePath.contains("android")) {
                        injectDir(filePath, packgets, dirList, baseTransfrom, isPrepare);
                    }
                } else {
                    if (filePath.endsWith(".class") && !filePath.contains("R$") &&
                            !filePath.contains("R.class") && !filePath.contains("BuildConfig.class") &&
                            checkPackget(packgets, filePath) && baseTransfrom.isInjectFile(filePath)) {
                        dirList.add(filePath);
                        if (isPrepare)
                            baseTransfrom.onPrepareClass(filePath);
                    }
                }
            }
        }
    }


    public static boolean checkPackget(List<String> packgets, String filePath) {
        String separator = File.separator;
        for (String packget : packgets) {
            if (!packget.contains(separator)) {
                filePath = filePath.replaceAll("\\\\", "/");
            }
            if (filePath.contains(packget)) {
                return true;
            }
        }
        return false;
    }


    public static void injectClass(List<String> injectNames, BaseTransfrom transfrom) {

        if (injectNames == null || injectNames.size() <= 0) {
            return;
        }
        for (String enterName : injectNames) {
            try {
                File file = new File(enterName);
                File unClassFile = new File(file.getParent(), file.getName() + ".opt");
                FileOutputStream classOutputStream = new FileOutputStream(unClassFile);

                FileInputStream in = new FileInputStream(enterName);
                byte[] data = transfrom.onModifyClass(in, enterName);
                if (data != null) {
                    classOutputStream.write(data);
                } else {
                    classOutputStream.write(IOUtils.toByteArray(in));
                }
//                if (file.exists()) {
//                    try {
//                        FileUtils.forceDelete(file);
//                    } catch (IOException e) {
//                        //  e.printStackTrace();
//                    }
//                }

                if (data != null) {
                    try {
                        FileUtils.copyFile(unClassFile, file);
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                }

                classOutputStream.close();
                in.close();
                if (unClassFile.exists()) {
                    try {
                        FileUtils.forceDelete(unClassFile);
                    } catch (IOException e) {
                        //  e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                //  e.printStackTrace();
            }
        }
    }

    public static void injectJar(String path, List<String> packgets, BaseTransfrom baseTransfrom) throws FileNotFoundException {
        if (packgets == null || packgets.size() <= 0) {
            return;
        }
        if (path.endsWith(".jar")) {
            //获取jar文件md5
            File allClassesJar = new File(path);
            File bakJar = new File(allClassesJar.getParent(), allClassesJar.getName() + ".bak");
            if (bakJar.exists()) {
                try {
                    bakJar.delete();
                } catch (Exception e) {

                }
            }
            try {
                FileUtils.copyFile(allClassesJar, bakJar);
            } catch (IOException e) {
                // e.printStackTrace();
            }
            String md5AllClassesJar = getMd5ByFile(allClassesJar);
            File unifyRJar = new File(allClassesJar.getParent(), md5AllClassesJar + ".jar.opt");
            if (!unifyRJar.exists()) {
                //清除md5值不同的jar.opt
                File parentDir = allClassesJar.getParentFile();
                File[] subFiles = parentDir.listFiles();
                for (int i = 0; i < subFiles.length; i++) {
                    File subFile = subFiles[i];
                    if (subFile.getName().endsWith(".jar.opt")) {
                        subFile.delete();
                    }
                }
                try {
                    modifyJar(bakJar, unifyRJar, packgets, baseTransfrom);
                } catch (IOException e) {
                    //     e.printStackTrace();
                }
            }
            //unifyRJar to allClassesJar
//            if (allClassesJar.exists()) {
//                try {
//                    FileUtils.forceDelete(allClassesJar);
//                } catch (IOException e) {
//                    //     e.printStackTrace();
//                }
//            }
            try {
                FileUtils.copyFile(unifyRJar, allClassesJar);
            } catch (IOException e) {
                //    e.printStackTrace();
            }
        }
    }

    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        InputStream stream = new FileInputStream(file);
        try {
            value = DigestUtils.md5Hex(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return value;
    }

    private static void modifyJar(File allClassesJar, File unifyRJar, List<String> packgets, BaseTransfrom baseTransfrom) throws IOException {
        JarFile jarFile = new JarFile(allClassesJar);
        Enumeration enumeration = jarFile.entries();
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(unifyRJar));
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            modifyJarClass(jarFile, jarEntry, packgets, jarOutputStream, baseTransfrom);
        }
        jarOutputStream.close();
        jarFile.close();
    }

    private static void modifyJarClass(JarFile jarFile, JarEntry jarEntry,
                                       List<String> packgets, JarOutputStream jarOutputStream,
                                       BaseTransfrom baseTransfrom) throws IOException {
        String entryName = jarEntry.getName();
        ZipEntry zipEntry = new ZipEntry(entryName);
        InputStream inputStream = jarFile.getInputStream(jarEntry);
        if (!entryName.contains("android") && entryName.endsWith(".class") && checkPackget(packgets, entryName) && baseTransfrom.isInjectFile(entryName)) {
            jarOutputStream.putNextEntry(zipEntry);
            byte[] data = baseTransfrom.onModifyClass(inputStream, entryName);
            if (data != null) {
                jarOutputStream.write(data);
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream));
            }
        } else {
            jarOutputStream.putNextEntry(zipEntry);
            jarOutputStream.write(IOUtils.toByteArray(inputStream));
        }
        inputStream.close();
        jarOutputStream.closeEntry();
    }
}
