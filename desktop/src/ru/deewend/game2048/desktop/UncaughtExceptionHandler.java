package ru.deewend.game2048.desktop;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

final class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final String[] splashes = {
            ":(((", "I'm very sorry", "Ugh", "My bad", "I feel sad now :(", "I didn't expect that...",
            "It isn't cool"
    };

    @Override
    public void uncaughtException(final Thread thread, final Throwable e) {
        final String date = new Date().toString();
        final String crashReport;

        {
            final StringBuilder crashReportBuilder = new StringBuilder();
            crashReportBuilder.append("===== CRASH REPORT =====\n");
            crashReportBuilder.append("// ").append(splashes[(int) (Math.random() * splashes.length)])
                    .append("\n\n");
            crashReportBuilder.append("Date: ").append(date).append("\n");
            crashReportBuilder.append("Stacktrace:\n\n");

            {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);

                e.printStackTrace(pw);

                crashReportBuilder.append(sw.toString()).append("\n");
            }

            crashReportBuilder.append("Available processors: ").append(Runtime.getRuntime().availableProcessors())
                    .append("\n");
            crashReportBuilder.append("Free memory: ").append(Runtime.getRuntime().freeMemory() / 1_000_000)
                    .append(" MB\n");
            crashReportBuilder.append("Total memory: ").append(Runtime.getRuntime().totalMemory() / 1_000_000)
                    .append(" MB\n");
            crashReportBuilder.append("Max memory: ").append(Runtime.getRuntime().maxMemory() / 1_000_000)
                    .append(" MB");

            crashReport = crashReportBuilder.toString();
        }

        final File crashReportFile;
        final File crashReportsDirectory = new File("2048" + File.separator + "crash_reports");

        if (!crashReportsDirectory.isDirectory() && !crashReportsDirectory.mkdir()) {
            printCrashReport(crashReport);

            return;
        }

        {
            int prefix = 0;

            final String modifiedDate = date
                    .replaceAll(" ", "_")
                    .replaceAll(":", "-");
            String crashReportFileName = "2048" + File.separator + "crash_reports" + File.separator +
                    "crash_report-" + modifiedDate + ".txt";

            File tmp;
            while ((tmp = new File(crashReportFileName)).exists() && prefix > -1)
                crashReportFileName = "crash_report-" + modifiedDate + "_" + (++prefix);

            crashReportFile = tmp;
        }

        if (crashReportFile.exists()) {
            printCrashReport(crashReport);

            return;
        }

        try {
            if (!crashReportFile.createNewFile())
                throw new RuntimeException("Unable to create the crash report file!");

            final PrintWriter writer = new PrintWriter(crashReportFile);
            writer.print(crashReport);
            writer.close();

            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().edit(crashReportFile);
        } catch (final Throwable t) {
            t.printStackTrace();

            printCrashReport(crashReport);
        }
    }

    private static void printCrashReport(final String crashReport) {
        System.out.println("Unable to save the crash report. Printing it here...");
        System.out.print(crashReport);
    }
}