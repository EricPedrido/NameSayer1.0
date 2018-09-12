import javax.swing.SwingWorker;

/**
 * A SwingWorker thread which allows for bash command to be run as
 * processes in the background to avoid blocking the GUI.
 *
 * Overrides some methods from {@link SwingWorker}
 *
 * @author Eric Pedrido
 */
public class SwingWorkerThread extends SwingWorker<Void, Void> {

    private String _cmd;

    /**
     * Constructs the SwingWorker
     *
     * @param cmd  The command to be executed on bash
     */
    public SwingWorkerThread(String cmd) {
        _cmd = cmd;
    }

    /**
     * Runs the <code>_cmd</code> bash command on a processes in a worker thread.
     *
     * @return  Nothing, as the purpose of this method is to execute an external command.
     * @throws Exception  Any exception of the process does not finish.
     */
    @Override
    protected Void doInBackground() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", _cmd);

        Process process = pb.start();
        process.waitFor();
        process.exitValue();

        return null;
    }
}
