import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * SOFTENG 206 Assignment 2 2018
 * Name Sayer allows the user to enter a name and record how that name should be said.
 * They can then listen to any names previously saved.
 * Names should only contain letters, hyphens, spaces and underscores.
 *
 * @author Eric Pedrido
 */

public class NameSayer extends JFrame {

    private final String SEARCH_TEXT = "Search for a Creation";
    protected EmbeddedMediaPlayerComponent _mediaPlayer;
    private JButton _create;
    private JButton _play;
    private JButton _delete;
    private JButton _reRecord;
    private JLabel _title;
    protected JLabel _emptyList;
    protected JPanel _tablePanel;
    private JTextField _search;
    private JPanel _mainPanel;
    protected JPanel _playPanel;
    protected ListTableModel _model;
    protected JTable _list;
    private TableRowSorter<ListTableModel> _rowSorter;

    /**
     * Launches the GUI and creates a folder where creations will be saved if one does not already exist.
     *
     * @see #NameSayer()
     */
    public static void main(String[] args) {
        new NameSayer();

        // Create the creations folder upon start-up if it does not already exist
        if (!new File("Creations").isDirectory()) {
            SwingWorkerThread thread = new SwingWorkerThread("mkdir Creations");
            thread.execute();
        }
    }

    /**
     * Constructs the main <code>NameSayer</code> JFrame and
     * initialises all of the components within the JFrame of the home screen.
     * Also does any set up necessary for any component to improve the GUI visually.
     */
    public NameSayer() {
        super("Name Sayer");
        SwingUtilities.invokeLater(() -> {
            // Set the look and feel to one that works on all platforms.
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            // Initialise the fields and any other component being used
            _mediaPlayer = new EmbeddedMediaPlayerComponent();
            _create = new JButton("Create");
            _delete = new JButton("Delete");
            _play = new JButton("Play");
            _reRecord = new JButton("Re-record");
            _title = new JLabel("Name sayer", SwingConstants.CENTER);
            _emptyList = new JLabel("There are no creations", SwingConstants.CENTER);
            _search = new JTextField(SEARCH_TEXT);
            _model = new ListTableModel();
            fillTable();
            _list = new JTable(_model);
            _rowSorter = new TableRowSorter<>(_model);
            _tablePanel = new JPanel();
            _mainPanel = new JPanel();
            _playPanel = new JPanel();
            JPanel buttonPanel = new JPanel();

            // Setup the components
            _search.setForeground(Color.GRAY);
            _list.setRowSorter(_rowSorter);
            _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _title.setFont(new Font("Sans Serif", Font.BOLD, 20));
            _emptyList.setFont(new Font("Sans Serif", Font.PLAIN, 20));
            _emptyList.setForeground(Color.GRAY);
            _tablePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
            _mainPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
            _mainPanel.setBackground(Color.BLACK);

            // Setup the event handlers for the components
            createHandler();
            playHandler();
            deleteHandler();
            searchHandler();
            rerecordHandler();
            tableHandler();

            // Set the layouts for the JFrame and the JPanels
            setLayout(new BorderLayout());
            buttonPanel.setLayout(new FlowLayout());
            _playPanel.setLayout(new BorderLayout());
            _mainPanel.setLayout(new BorderLayout());
            _tablePanel.setLayout(new BorderLayout());
            _tablePanel.setPreferredSize(new Dimension(250, _tablePanel.getHeight()));

            // Add all components to their JPanels
            _playPanel.add(_mediaPlayer, BorderLayout.CENTER);
            buttonPanel.add(_create);
            buttonPanel.add(_play);
            buttonPanel.add(_delete);
            buttonPanel.add(_reRecord);
            _tablePanel.add(_search, BorderLayout.NORTH);
            // Decide whether to display the JTable or a text saying there are no creations depending on if there are
            // creations to display
            if (_list.getRowCount() <= 0) {
                _delete.setEnabled(false);
                _play.setEnabled(false);
                _reRecord.setEnabled(false);
                _tablePanel.add(_emptyList, BorderLayout.CENTER);
            } else {
                _tablePanel.add(_list, BorderLayout.CENTER);
            }
            _mainPanel.add(_playPanel, BorderLayout.CENTER);
            _mainPanel.add(_tablePanel, BorderLayout.WEST);

            // Add the JPanels to the JFrame
            add(_title, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.SOUTH);
            add(_mainPanel, BorderLayout.CENTER);

            // Configure the JFrame's properties
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(670, 401);
            setResizable(false);
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }

    /**
     * Finds all the creations within the Creations folder and populates the <code>_list</code> JTable.
     * A creation in this situation is just an mp4 file that is not <video>temp,file.mp4</video>.
     * This is fine in the context of names because a name with a comma is an invalid name.
     */
    private void fillTable() {
        File directory = new File("Creations");
        File[] list = directory.listFiles((dir, name) -> name.endsWith(".mp4"));
        String name;

        if (list != null) {
            for (File creations : list) {
                name = creations.getName();
                if (!name.equals("temp,file.mp4")) {
                    _model.addName(name.substring(0, name.lastIndexOf('.')));
                }
            }
        }

    }

    /**
     * Handles interactions done with the <code>_list</code>, namely, if a creation has been selected.
     * If a creation has indeed been selected, then the
     * <code>_play</code> <code>_delete</code> <code>NameSayer#_reRecord</code> JButtons are set to be visible.
     * Otherwise, those options are unavailable.
     * This is because the functionality of those buttons require a creation be selected first.
     *
     * @see #playHandler()
     * @see #deleteHandler()
     * @see #rerecordHandler()
     */
    private void tableHandler() {
        _list.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (_list.getSelectedRow() != -1) {
                _reRecord.setEnabled(true);
                _delete.setEnabled(true);
                _play.setEnabled(true);
            } else {
                _reRecord.setEnabled(false);
                _delete.setEnabled(false);
                _play.setEnabled(false);
            }
        });
    }

    /**
     * Handles interactions with the <code>_search</code> JTextField above the <code>_list</code> JTable.
     * This allows the user to enter a name, and only creations with matching characters
     * will populate the JTable. This filtering happens in real-time to the user typing.
     *
     * When focus is gained to the JTextField, the text will text shown will become blank
     * and set to black, allowing the user to enter the desired creation name. When focus
     * is lost, the text will return to its idle state, gray and asking the user to search.
     */
    private void searchHandler() {
        _search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (_search.getText().equals(SEARCH_TEXT)) {
                    _search.setText("");
                    _search.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (_search.getText().isEmpty()) {
                    _search.setForeground(Color.GRAY);
                    _search.setText(SEARCH_TEXT);
                }
            }
        });

        // Filter the rows of the JTable to only show rows with names containing the letters entered
        _search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String input = _search.getText();

                // Do not filter if the text is empty or it the search bar is in its idle state
                if (input.trim().length() == 0 || input.equals(SEARCH_TEXT)) {
                    _rowSorter.setRowFilter(null);
                } else {
                    _rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + input));
                }
            }

            // The same as functionality as the insertUpdate method, except it works when deleting characters also
            @Override
            public void removeUpdate(DocumentEvent e) {
                String input = _search.getText();

                if (input.trim().length() == 0 || input.equals(SEARCH_TEXT)) {
                    _rowSorter.setRowFilter(null);
                } else {
                    _rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + input));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    /**
     * Plays the selected creation on an embedded VLCJ media player when the <code>_play</code> JButton is pressed.
     * Also, sets the <code>_playPanel</code> visibility to be <code>true</code> if it is not already.
     * The visibility is set to <code>false</code> when a creation is deleted in order to
     * work around a bug where the media player continues to display the creation even after
     * the creation has been deleted.
     *
     * Refer to {@link #deleteHandler()} and {@link RecordingFrame#buttonHandlers()} to see when this can happen.
     */
    private void playHandler() {
        _play.addActionListener((ActionEvent e) -> {
            if (!_playPanel.isVisible()) {
                _playPanel.setVisible(true);
            }

            // Play the creation selected on the JTable
            _mediaPlayer.getMediaPlayer().playMedia("Creations/" + _list.getValueAt(_list.getSelectedRow(),
                    _list.getSelectedColumn()) + ".mp4");
        });
    }

    /**
     * Creates a new <code>CreateFrame</code> object where all the interactions regarding creating
     * a creation are handled in a new window.
     *
     * @see CreateFrame
     */
    private void createHandler() {
        _create.addActionListener((ActionEvent e) -> {
            new CreateFrame();
        });
    }

    /**
     * Prompts the user to confirm their decision of the deletion of the selected creation.
     * If the user decides to delete a creation, a bash command deletes the creation in a background
     * thread and the <code>_list</code> JTable is notified of its deletion.
     * The <code>_playPanel</code> is also set to be invisible due to a bug where the VLCJ media player
     * would still show a creation even after it is deleted.
     *
     * If the creation deleted is the last item on the JTable, the JTable is removed from the <code>_tablePanel</code>
     * and replaced with an <code>_emptyList</code> JLabel.
     *
     * Refer to {@link #playHandler()} to see when <code>_playPanel</code> is set back to visible.
     * Refer to {@link #record(String)} to see when the JTable can be added back to the <code>_tablePanel</code>,
     * replacing the JLabel.
     */
    private void deleteHandler() {
        _delete.addActionListener((ActionEvent e) -> {
            String name = _list.getValueAt(_list.getSelectedRow(), _list.getSelectedColumn()).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this creation? (This cannot be undone)",
                    "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            // Confirm that the user wants to delete the selected creation
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove the creation from the system
                _model.removeName(name);
                SwingWorkerThread thread = new SwingWorkerThread("rm Creations/\"" + name + "\".mp4");
                thread.execute();

                // Ensure that a video of the now deleted creation is not still on the _playPanel
                _playPanel.setVisible(false);

                // Remove the JTable if the creation deleted was the last creation
                if (_list.getRowCount() < 1) {
                    _tablePanel.remove(_list);
                    _tablePanel.add(_emptyList, BorderLayout.CENTER);
                    validate();
                    repaint();
                }
            }
        });
    }

    /**
     * Invokes <code>#overwrite(String)</code> which handles when the user wishes to overwrite a creation.
     * The <param>name</param> is that of the currently selected creation.
     *
     * Refer to {@link #overwrite(String)} to see how a creation is overwritten.
     */
    private void rerecordHandler() {
        _reRecord.addActionListener((ActionEvent e) -> {
            overwrite(_list.getValueAt(_list.getSelectedRow(), _list.getSelectedColumn()).toString());
        });
    }

    /**
     * Creates a <code>RecordingFrame</code> object,
     * which handles all the interactions regarding the recording of a new creation, in a new window.
     *
     * Also adds the creation to the <code>_list</code> JTable prematurely to assure the user
     * that the creation has been registered into the system.
     * This is mainly to make the UI feel more responsive.
     *
     * If there were previously no creations, and a new one is created, then the JTable is added back into
     * the <code>_tablePanel</code>, removing the <code>_emptyList</code> JLabel.
     *
     * Refer to {@link #deleteHandler()} and {@link RecordingFrame#buttonHandlers()}
     * to see where the <code>_tablePanel</code> is removed and <code>_emptyList</code> replaces it.
     *
     * @param name  The name of the creation being recorded.
     *
     * @see RecordingFrame
     */
    protected void record(String name) {
        // Add the new creation to the JTable and allow the user to record for their creation
        _model.addName(name);
        new RecordingFrame(name);

        // If this is the first creation, then replace the _emptyList JLabel with the _list JTable
        if (_list.getRowCount() > 0 && !_list.isAncestorOf(_tablePanel)) {
            _tablePanel.remove(_emptyList);
            _tablePanel.add(_list, BorderLayout.CENTER);
            validate();
            repaint();
        }
    }

    /**
     * Prompts the user to confirm their decision to overwrite the creation (which essentially deletes it).
     * Upon confirmation, the creation is deleted and a new creation with the same name is created.
     *
     * @param name  The name of the creation being overwritten
     */
    protected void overwrite(String name) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you wish to overwrite this creation? (This cannot be undone)",
                "Confirm overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        // Delete the creation and record a new one if the user confirms
        if (confirm == JOptionPane.YES_OPTION) {
            _model.removeName(name);
            SwingWorkerThread thread = new SwingWorkerThread("rm Creations/\"" + name + "\".mp4");
            thread.execute();
            record(name);
        }
    }

    /**
     *  A JFrame window containing a JTextField where the user enters their desired creation name.
     *  If the creation name already exists, then a pop-up is created,
     *  asking if the user wishes overwrite that existing creation with the same name.
     *  If the name contains invalid characters, no characters, or just spaces (the same as no characters),
     *  then a pop-up is created preventing the creation of such.
     *
     *  @author Eric Pedrido
     */
    private class CreateFrame {

        private JFrame _frame;
        private JTextField _name = new JTextField();
        private JButton _create = new JButton("Create");
        private JButton _cancel = new JButton("Cancel");

        /**
         * Construct a separate JFrame from the main menu of <code>NameSayer</code>
         * that is solely responsible for handling the creation of a new creation.
         *
         * @see NameSayer
         */
        protected CreateFrame() {
            // Initialise JPanels and JFrame
            _frame = new JFrame("Create a creation");
            JPanel mainPanel = new JPanel();
            JPanel buttonPanel = new JPanel();

            // Set the layout for the JFrame and JPanels
            setLayout(new BorderLayout());
            mainPanel.setLayout(new FlowLayout());
            buttonPanel.setLayout(new FlowLayout());

            _name.setPreferredSize(new Dimension(250, 30));

            // setup the components event handlers
            buttonHandler();
            textHandler();

            // Add components the JPanels
            mainPanel.add(new JLabel("Creation name: "));
            mainPanel.add(_name);
            buttonPanel.add(_create);
            buttonPanel.add(_cancel);

            // Add the JPanels to the JFrame
            _frame.add(mainPanel, BorderLayout.CENTER);
            _frame.add(buttonPanel, BorderLayout.SOUTH);

            // Configure the JFrames properties
            _frame.pack();
            _frame.setVisible(true);
            _frame.setLocationRelativeTo(null);
            _frame.setSize(300, 130);
        }

        /**
         * Handles all interactions with all buttons.
         * If <code>_cancel</code> is pressed, then the <code>_frame</code> JFrame is disposed.
         * Otherwise if <code>_create</code> is pressed, the process for creating a creation is invoked.
         *
         * Refer to {@link #create()} to see how a creation is made.
         */
        private void buttonHandler() {
            _create.addActionListener((ActionEvent e) -> create());
            _cancel.addActionListener((ActionEvent e) -> _frame.dispose());
        }

        /**
         * Allows the user to press <button>Enter</button> instead of having to click the <code>_create</code> JButton.
         * The option to click the JButton is still there, but the same functionality can be achieved
         * by pressing <buttton>Enter</buttton> instead.
         * This is for the ease of use of the user.
         *
         * @see #create()
         */
        private void textHandler() {
            _name.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    // Create the creation when Enter is pressed instead of pressing the _create JButton
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                        create();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });
        }

        /**
         *  Checks that the name entered in the <code>_name</code> JTextField contains only valid characters
         *  and is not only spaces or empty.
         *  Also ensures that creations cannot have the same name,
         *  asking the user to overwrite if a duplicate is detected.
         *
         *  If the name meets all the criteria for a valid creation,
         *  then a <code>RecordingFrame</code> window will appear.
         *
         * @see RecordingFrame
         */
        private void create() {
            String input = _name.getText();

            // Check that the entered name contains only valid characters
            if (input.matches("^[ A-Za-z0-9_-]*$")) {
                // Check that the entered name contains SOME characters that are not spaces.
                if (input.matches("^[ ]*$") || input.equals("")) {
                    JOptionPane.showMessageDialog(_frame,
                            "Warning: A creation must contain 1 or more characters.",
                            "WARNING", JOptionPane.WARNING_MESSAGE);
                }
                // Check if a creation with that name already exists and offer to overwrite it if it does
                else if (_model.contains(input)) {
                    int overwrite = JOptionPane.showConfirmDialog(_frame,
                            "Warning: A creation with this name already exists. Would you like to overwrite it?",
                            "WARNING",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    // Confirm the users choice to overwrite
                    if (overwrite == JOptionPane.YES_OPTION) {
                        overwrite(input);
                        _frame.dispose();
                    }
                }
                // Record the creation once is confirmed to meet all the criteria
                else {
                    record(input);
                    _frame.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(_frame,
                        "Warning: Your creation contains invalid characters. Please try again.",
                        "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * A JFrame window which presents the user with the option to Record their new creation or Cancel.
     * If the user decides to record, then a JProgressBar will load for 5 seconds, indicating to the user
     * the duration remaining in their recording. After the recording has finished, the user has options
     * to Re-record, Listen, or Save that recording. At any time if the user decides to cancel, then
     * their creation will not be saved and the name will be removed from the JTable.
     *
     * @author Eric Pedrido
     */
    public class RecordingFrame {

        private Timer _timer;
        private int _count = 0;
        private JProgressBar _progress;
        private JFrame _frame;
        private JLabel _text = new JLabel("Recording progress: ");
        private JButton _record;
        private JButton _reRecord;
        private JButton _listen;
        private JButton _save;
        private JButton _cancel;
        private JPanel _mainPanel;
        private JPanel _buttonPanel;
        private String _name;

        /**
         * Constructs a separate JFrame window from the <code>CreateFrame</code> window.
         * Also does any setup required for any components.
         *
         * @param name  The name of the Creation.
         *
         * @see CreateFrame
         */
        public RecordingFrame(String name) {
            // Initialise Timer
            _timer = new Timer(50, (ActionEvent e) -> {
                if (_count <= 100) {
                    _progress.setValue(++_count);
                }
            });

            // Initialise components including JFrame
            _frame = new JFrame("Recording");
            _name = name;
            _mainPanel = new JPanel();
            _buttonPanel = new JPanel();
            _record = new JButton("Record");
            _reRecord = new JButton("Re-record");
            _save = new JButton("Save");
            _listen = new JButton("Listen");
            _cancel = new JButton("Cancel");
            _progress = new JProgressBar(0, 100);

            // Disable all the buttons so that the user cannot press any of them before a recording is done
            _reRecord.setEnabled(false);
            _listen.setEnabled(false);
            _save.setEnabled(false);


            // Set the layouts of the JFrame and JPanels
            _frame.setLayout(new BorderLayout());
            _mainPanel.setLayout(new BorderLayout());
            _buttonPanel.setLayout(new FlowLayout());

            // Setup the progress bar to be initialised to the correct value (zero)
            _progress.setValue(_count);

            // Setup the event handlers for the components
            progressHandler();
            buttonHandlers();

            // Add the components to their JPanels
            _mainPanel.add(_progress, BorderLayout.CENTER);
            _mainPanel.add(_text, BorderLayout.NORTH);
            _buttonPanel.add(_record);
            _buttonPanel.add(_reRecord);
            _buttonPanel.add(_listen);
            _buttonPanel.add(_save);
            _buttonPanel.add(_cancel);

            // Add the JPanels to the JFrame
            _frame.add(_mainPanel, BorderLayout.CENTER);
            _frame.add(_buttonPanel, BorderLayout.SOUTH);

            // Configure the properties of the JFrame
            _frame.pack();
            _frame.setSize(new Dimension(450, 100));
            _frame.setVisible(true);
            _frame.setLocationRelativeTo(null);

            // Delete any pre-existing temp files upon opening the window and also upon closing.
            _frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    if (new File("Creations/recording.wav").exists()) {
                        SwingWorkerThread thread1 = new SwingWorkerThread("rm Creations/recording.wav");
                        thread1.execute();
                    }
                    if (new File("Creations/temp,file.mp4").exists()) {
                        SwingWorkerThread thread2 = new SwingWorkerThread("rm Creations/temp,file.mp4");
                        thread2.execute();
                    }
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    close();
                }
            });
        }

        /**
         * Handles interactions with all of the JButtons.
         *
         * If <code>_record</code> is pressed, then a new user recording and a video is generated through bash commands.
         *
         * If <code>RecordingFrame#_reRecord</code> is pressed, then the JProgressBar is reset and
         * the previous recording is deleted, to be replaced by a new one.
         * The JButtons <code>RecordingFrame#_reRecord</code> <code>_listen</code> <code>_save</code> are disabled.
         *
         * If <code>_listen</code> is pressed, then the recording will be played back to the user so the user can decide
         * whether or not they are satisfied. The user can re-record as many times as desired.
         *
         * If <code>_save</code> is pressed, then the recording and video will be combined through a bash command,
         * and saved under the name "<code>_name</code>.mp4"
         *
         * If <code>_cancel</code> is pressed, then the creation is deleted from the <code>NameSayer#_list</code> JTable
         * and the <code>RecordingFrame</code> window is close. If the creation is the last creation on the JTable,
         * then it is removed from the <code>_tablePanel</code> and replaced with <code>_emptyList</code>, along with
         * the <code>NameSayer#_playPanel</code> being set to invisible to work around a bug where
         * the VLCJ media player keeps showing a creation even if it has been deleted.
         *
         * Refer to {@link #close()} to see how the window is closed.
         * Refer to {@link #record(String)} to see when the JTable can be added back to the <code>_tablePanel</code>
         * replacing <code>_emptyList</code>.
         * Refer to {@link NameSayer#playHandler()} to see where the <code>_playPanel</code> is set back to visible
         */
        private void buttonHandlers() {
            _record.addActionListener((ActionEvent e) -> {
                // Start the timer for the JProgressBar
                _timer.start();
                _record.setEnabled(false);

                // Make a new recording and video with the name showing through a bash command on a worker thread
                SwingWorkerThread thread = new SwingWorkerThread(
                        "ffmpeg -f alsa -i default -t 5 Creations/recording.wav ; " +
                                "ffmpeg -f lavfi -i color=c=white:s=320x240:d=5 -vf " +
                                "\"drawtext=fontfile =/path/to/font.ttf:fontsize=20:fontcolor=black:" +
                                "x=(w-text_w)/2:y=(h-text_h)/2:text=" + _name + "\" Creations/temp,file.mp4");
                thread.execute();
            });

            _reRecord.addActionListener((ActionEvent e) -> {
                // Restart the Timer and JProgressBar
                _text.setText("Recording progress: ");
                _progress.setValue(_count);
                _timer.start();

                // Remove the current recording and make a new one through a bash command on a worker thread
                SwingWorkerThread thread = new SwingWorkerThread(
                        "rm Creations/recording.wav ; " +
                                "ffmpeg -f alsa -i default -t 5 Creations/recording.wav 2> /dev/null");
                thread.execute();

                // Disable all buttons while recording is underway
                _listen.setEnabled(false);
                _reRecord.setEnabled(false);
                _save.setEnabled(false);
            });

            _listen.addActionListener((ActionEvent e) -> {
                File path = new File("Creations/recording.wav");

                try {
                    // Initialise the audio clip and its line listener
                    Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
                    clip.addLineListener((LineEvent event) -> {
                        // Stop the audio playback once it is finished
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });

                    // Start the playback of the recording
                    clip.open(AudioSystem.getAudioInputStream(path));
                    clip.start();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
                    e1.printStackTrace();
                }

            });

            _save.addActionListener((ActionEvent e) -> {
                // Combine the files into a single mp4 with the creation name through a bash command on a worker thread
                SwingWorkerThread thread = new SwingWorkerThread("ffmpeg -i Creations/temp,file.mp4 -i " +
                        "Creations/recording.wav -c:v copy -c:a aac -strict -2 Creations/$\"" + _name + "\".mp4");
                thread.execute();

                _frame.dispose();
            });

            _cancel.addActionListener((ActionEvent e) -> {
                // Remove the new creation from the JTable and if it's the last creation, then remove the JTable
                _model.removeName(_name);
                if (_list.getRowCount() < 1) {
                    _tablePanel.remove(_list);
                    _tablePanel.add(_emptyList, BorderLayout.CENTER);
                    _playPanel.setVisible(false);

                    validate();
                    repaint();
                }
                close();
            });
        }

        /**
         * Deletes the temp files created to create the creation.
         * The temp files are <audio>recording.wav</audio> and <video>temp,file.mp4</video>
         *
         * Also disposes of the <code>RecordingFrame#_frame</code> JFrame.
         */
        private void close() {
            SwingWorkerThread thread = new SwingWorkerThread(
                    "rm Creations/recording.wav ; rm Creations/temp,file.mp4");
            thread.execute();
            _frame.dispose();
        }

        /**
         * Handles how the <code>_progress</code> JProgressBar behaves during certain events.
         * Namely, how it behaves when it is done.
         *
         * When it is done, the <code>_timer</code> is stopped and the <code>_listen</code> <code>_save</code>
         * <code>RecordingFrame#_reRecord</code> JButtons are set to be visible, while <code>_record</code>
         * is set to be invisible.
         *
         * <code>_count</code> is also set to zero to allow for a re-recording.
         */
        private void progressHandler() {
            _progress.addChangeListener((ChangeEvent e) -> {
                if (_progress.getValue() >= 100) {
                    _timer.stop();
                    _text.setText("Recording complete. Please select an option below");

                    // Adjust buttons to a Post-Recording state
                    _record.setEnabled(false);
                    _listen.setEnabled(true);
                    _reRecord.setEnabled(true);
                    _save.setEnabled(true);

                    _frame.validate();
                    _frame.repaint();

                    _count = 0;
                }
            });
        }
    }
}