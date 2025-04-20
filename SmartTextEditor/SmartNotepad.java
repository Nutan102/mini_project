import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class SmartNotepad extends JFrame {

    private JTextArea textArea;
    private JLabel statusLabel;
    private File currentFile = null;
    private boolean darkMode = false;

    public SmartNotepad() {
        setTitle("Smart Notepad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ✅ Set icon image (make sure icon.png is in the same folder or in resources)
        ImageIcon icon = new ImageIcon(getClass().getResource("icon.png")); // No slash if in same package
        setIconImage(icon.getImage());

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Words: 0 | Characters: 0");
        add(statusLabel, BorderLayout.SOUTH);

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem toggleThemeItem = new JMenuItem("Toggle Dark Mode");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        viewMenu.add(toggleThemeItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // Listeners
        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> dispose());
        toggleThemeItem.addActionListener(e -> toggleDarkMode());

        textArea.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateStatus();
            }
        });

        // Auto-save every 5 seconds
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                autoSave();
            }
        }, 5000, 5000);

        updateTheme();
    }

    private void newFile() {
        textArea.setText("");
        currentFile = null;
        setTitle("Smart Notepad");
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(reader, null);
                setTitle("Smart Notepad - " + currentFile.getName());
            } catch (IOException ex) {
                showError("Failed to open file");
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
                if (!currentFile.getName().endsWith(".txt")) {
                    currentFile = new File(currentFile.getAbsolutePath() + ".txt");
                }
            } else {
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            textArea.write(writer);
            setTitle("Smart Notepad - " + currentFile.getName());
        } catch (IOException ex) {
            showError("Failed to save file");
        }
    }

    private void autoSave() {
        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(writer);
                System.out.println("Auto-saved to: " + currentFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Auto-save failed");
            }
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        updateTheme();
    }

    private void updateTheme() {
        if (darkMode) {
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
            textArea.setCaretColor(Color.WHITE);
            statusLabel.setBackground(Color.DARK_GRAY);
            statusLabel.setForeground(Color.WHITE);
        } else {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            textArea.setCaretColor(Color.BLACK);
            statusLabel.setBackground(Color.LIGHT_GRAY);
            statusLabel.setForeground(Color.BLACK);
        }
    }

    private void updateStatus() {
        String text = textArea.getText();
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int charCount = text.length();
        statusLabel.setText("Words: " + wordCount + " | Characters: " + charCount);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SmartNotepad().setVisible(true);
        });
    }
}
