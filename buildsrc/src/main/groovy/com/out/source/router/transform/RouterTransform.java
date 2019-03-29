package com.out.source.router.transform;
import com.out.source.router.plugin.kernel.transfrom.BaseTransfrom;
import com.out.source.router.plugin.kernel.transfrom.utils.TransformUtils;
import com.out.source.router.transform.extension.RouterExtension;
import com.out.source.router.visitor.ClassInjectVisitor;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjian on 2018/6/25.
 */

public class RouterTransform extends BaseTransfrom<RouterExtension> {


    private List<String> mNeedToRouters = new ArrayList<>();

    public RouterTransform(Project project, RouterExtension ext, boolean isAppPlugin) {
        super(project, ext, isAppPlugin);
    }

    @Override
    public String getName() {
        return "RouterTransform";
    }

    @Override
    public byte[] onModifyClass(InputStream inputStream, String enterName) throws IOException {
        if (enterName.contains(getExt().getInjectClass())) {
            getExt().onLog("插入的类为 enterName : " + enterName);
            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassInjectVisitor cv = new ClassInjectVisitor(Opcodes.ASM4, cw, mNeedToRouters, getExt());
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
        return null;
    }

    @Override
    public boolean isInjectFile(String pathFile) {
        return true;
    }

    @Override
    public boolean isEnablePrepar() {
        return true;
    }

    @Override
    public void onPrepareClass(String classPath) {
        super.onPrepareClass(classPath);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(classPath);
            checkFile(inputStream, classPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void checkFile(InputStream inputStream, String pathFile) {
        if (!pathFile.contains("android") &&
                pathFile.endsWith(".class") &&
                TransformUtils.checkPackget(getExt().getPackgets(),
                        pathFile)
                && !pathFile.contains(getExt().getInjectClass())) {
            try {
                ClassReader cr = new ClassReader(inputStream);
                ClassWriter cw = new ClassWriter(cr, 0);
                ClassInjectVisitor cv = new ClassInjectVisitor(Opcodes.ASM4, cw, getExt());
                cr.accept(cv, 0);
                if (cv.isNeedInjectActivity()) {
                    getExt().onLog("插入的资源为 mClassName : " + cv.mClassName);
                    mNeedToRouters.add(cv.mClassName);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
