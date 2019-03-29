package com.out.source.router.visitor;
import com.out.source.router.transform.extension.RouterExtension;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

/**
 * Created by yangjian on 2018/3/1.
 */

public class MethodInjectVisitor extends MethodVisitor {

    private RouterExtension mExt;

    private List<String> mNeedToRouters;

    public MethodInjectVisitor(int i, MethodVisitor methodVisitor, List<String> mNeedToRouters ,RouterExtension ext) {
        super(i, methodVisitor);
        this.mExt = ext;
        this.mNeedToRouters = mNeedToRouters;
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 1, maxLocals);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN && mNeedToRouters != null &&
                mNeedToRouters.size() > 0) {
            for(String s : mNeedToRouters){
                mv.visitLdcInsn(Type.getType("L" + s + ";"));
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/source/sdk/prouter/PRouter", "register", "(Ljava/lang/Class;)V", false);
            }
            mExt.onLog("插入成功");
        }
        super.visitInsn(opcode);
    }
}
