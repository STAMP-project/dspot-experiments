package eu.stamp_project.dci.selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 20/09/18
 */
public class CommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandExecutor.class);

    public static void runCmd(String shaParent, String cwd, String outputPathFolder) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(
                    "git diff " + shaParent,
                    new String[]{},
                    new File(cwd));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (final FileWriter fileWriter = new FileWriter(outputPathFolder, false)) {
            new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines()
                    .forEach(line -> {
                        try {
                            //LOGGER.info("{}", line);
                            fileWriter.write(line + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runCmd(String cmd) {
        LOGGER.info("Executing cmd {}", cmd);
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
            new ThreadToReadInputStream(System.out, p.getInputStream()).start();
            new ThreadToReadInputStream(System.err, p.getErrorStream()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ThreadToReadInputStream extends Thread {

        private final PrintStream output;
        private final InputStream input;

        ThreadToReadInputStream(PrintStream output, InputStream input) {
            this.output = output;
            this.input = input;
        }

        @Override
        public synchronized void start() {
            int read;
            try {
                while ((read = this.input.read()) != -1) {
                    this.output.print((char) read);
                }
            } catch (Exception ignored) {
                //ignored
            } finally {
                this.interrupt();
            }
        }
    }

}
