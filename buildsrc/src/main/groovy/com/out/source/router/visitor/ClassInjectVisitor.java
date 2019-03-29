package com.out.source.router.visitor;
import com.out.source.router.transform.extension.RouterExtension;
import com.source.sdk.annotaion.IActivity;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.AnnotationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjian on 2018/3/1.
 */

public class ClassInjectVisitor extends ClassVisitor {

    public String mClassName = "";

    private RouterExtension mExt;

    private boolean isNeedInjectActivity = false;

    public List<String> mNeedToRouters;

    public ClassInjectVisitor(int i, ClassVisitor classVisitor,RouterExtension ext) {
        super(i, classVisitor);
        this.mExt = ext;
    }

    public ClassInjectVisitor(int i, ClassVisitor classVisitor,List<String> mNeedToRouters,RouterExtension ext) {
        super(i, classVisitor);
        this.mExt = ext;
        this.mNeedToRouters = mNeedToRouters;
        ext.onLog("插入个数为 " + (mNeedToRouters == null ? 0 : mNeedToRouters.size()));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        String annotaionActivity = changeSeparator(s);
        if(annotaionActivity.contains(IActivity.class.getSimpleName())){
            isNeedInjectActivity = true;
        }
        return super.visitAnnotation(s, b);
    }

    public boolean isNeedInjectActivity(){

        return isNeedInjectActivity;
    }
    @Override
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath typePath, String s, boolean b) {
        return super.visitTypeAnnotation(i, typePath, s, b);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if(mNeedToRouters != null && mNeedToRouters.size() > 0 &&
               "()V".equals(desc) && "init".equals(name)){
            mv = new MethodInjectVisitor(Opcodes.ASM4, mv,mNeedToRouters,mExt);
        }
        return mv;
    }

    private String changeSeparator(String path){

        if(path != null && !"".equals(path)){

            path.replaceAll("/", ".");
        }

        return path;
    }
}
