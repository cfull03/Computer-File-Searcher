package GUIs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import enumerations.OSPath;

/**
 * The {@code MainGUI} class provides a graphical user interface for selecting a file path
 * and entering a keyword to search for files in the specified path. The user can choose
 * between three predefined paths: Desktop, CF, or User.
 */
public class MainGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final JButton displayButton;
	private final JRadioButton desktopButton;
	private final JRadioButton cfButton;
	private final JRadioButton userButton;
	private final JTextField keywordField;
	private final JPanel textPanel;
	private final JPanel radioPanel;
	private final JPanel mainPanel;

	/**
	 * Constructs the main GUI window for the application.
	 * Allows users to input a keyword and select a path to display matching files.
	 */
	public MainGUI() {
		// Set up the main window
		setTitle("Keyword and Path Selector");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 300);
		setLayout(new BorderLayout());

		// Initialize components
		desktopButton = new JRadioButton("Desktop");
		cfButton = new JRadioButton("CF");
		userButton = new JRadioButton("User");

		// Group radio buttons to allow only one selection at a time
		ButtonGroup group = new ButtonGroup();
		group.add(desktopButton);
		group.add(cfButton);
		group.add(userButton);

		// Create panel for radio buttons
		radioPanel = new JPanel();
		radioPanel.setLayout(new GridLayout(3, 1));
		radioPanel.setBorder(BorderFactory.createTitledBorder("Select Path"));
		radioPanel.add(userButton);
		radioPanel.add(cfButton);
		radioPanel.add(desktopButton);

		// Initialize keyword input field and button
		keywordField = new JTextField(20);
		displayButton = new JButton("Display Files");
		displayButton.addActionListener(this);

		// Create panel for keyword input and button
		textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(2, 1));
		textPanel.setBorder(BorderFactory.createTitledBorder("Enter Keyword"));
		textPanel.add(keywordField);
		textPanel.add(displayButton);

		// Combine panels into the main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		mainPanel.add(textPanel);
		mainPanel.add(radioPanel);

		// Add main panel to the frame
		add(mainPanel, BorderLayout.CENTER);

		// Make the GUI visible
		setVisible(true);
	}

	/**
	 * Handles the button click event.
	 * When the "Display Files" button is clicked, retrieves the selected path and keyword,
	 * then opens a {@link FileDisplayer} window with the specified parameters.
	 *
	 * @param e The {@link ActionEvent} triggered by clicking the button.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String pattern = keywordField.getText();

		if (desktopButton.isSelected()) {
			new FileDisplayer(OSPath.DESKTOP, pattern);
		} else if (cfButton.isSelected()) {
			new FileDisplayer(OSPath.CF, pattern);
		} else if (userButton.isSelected()) {
			new FileDisplayer(OSPath.USERS, pattern);
		} else {
			JOptionPane.showMessageDialog(this, "Please select a path!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
