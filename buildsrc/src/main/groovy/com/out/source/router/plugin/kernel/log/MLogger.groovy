package com.out.source.router.plugin.kernel.log

import com.android.SdkConstants
import com.android.utils.ILogger

class MLogger implements ILogger {
    private final MLogger.Level mLevel

    private boolean isLogPrint = true

    MLogger(MLogger.Level level) {
        if (level == null) {
            throw new IllegalArgumentException("level cannot be null")
        } else {
            this.mLevel = level
        }
    }

    MLogger.Level getLevel() {
        return this.mLevel
    }

    void setLogPrint(boolean isLogPrint){

        this.isLogPrint = isLogPrint
    }
    void error(Throwable t, String errorFormat, Object... args) {

        if(!isLogPrint){

            return
        }
        if (errorFormat != null) {
            String msg = String.format("Error: " + errorFormat, args)
            this.printMessage(msg, System.err)
        }

        if (t != null) {
            System.err.println("Error: " + t.getMessage())
        }

    }

    void warning(String warningFormat, Object... args) {
        if(!isLogPrint){

            return
        }
        if (this.mLevel.mLevel <= MLogger.Level.WARNING.mLevel) {
            String msg = String.format("Warning: " + warningFormat, args)
            this.printMessage(msg, System.out)
        }
    }

    void info(String msgFormat, Object... args) {
        if(!isLogPrint){

            return
        }
        if (this.mLevel.mLevel <= MLogger.Level.INFO.mLevel) {
            String msg = String.format(msgFormat, args)
            this.printMessage(msg, System.out)
        }
    }

    void verbose(String msgFormat, Object... args) {
        if(!isLogPrint){

            return
        }
        if (this.mLevel.mLevel <= MLogger.Level.VERBOSE.mLevel) {
            String msg = String.format(msgFormat, args)
            this.printMessage(msg, System.out)
        }
    }

    private void printMessage(String msg, PrintStream stream) {
        if(!isLogPrint){

            return
        }
        if (SdkConstants.CURRENT_PLATFORM == 2 && !msg.endsWith("\r\n") && msg.endsWith("\n")) {
            msg = msg.substring(0, msg.length() - 1)
        }
        msg = ":log:" + msg
        stream.print(msg)
        if (!msg.endsWith("\n")) {
            stream.println()
        }

    }

    static enum Level {
        VERBOSE(0),
        INFO(1),
        WARNING(2),
        ERROR(3)

        private final int mLevel

        private Level(int level) {
            this.mLevel = level
        }
    }
}
