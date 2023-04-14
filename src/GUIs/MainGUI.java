package GUIs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import enumerations.*; 

public class MainGUI extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private JButton button;
	private JRadioButton Desktop;
	private JRadioButton CF;
	private JRadioButton User;
	private JTextField keyword;
	private JPanel TextPanel;
	private JPanel CheckBoxPanel;
	private JPanel MainPanel;

	public MainGUI() {
		
		setTitle("Selected Keyword and Path");
        
	    Desktop = new JRadioButton("Desktop");
	    CF = new JRadioButton("CF");
	    User = new JRadioButton("User");
	    
	    CheckBoxPanel = new JPanel();
	    CheckBoxPanel.setLayout(new GridLayout(3,1));
	    CheckBoxPanel.add(User);
	    CheckBoxPanel.add(CF);
	    CheckBoxPanel.add(Desktop);
	       
	    button = new JButton("Display Files");
	    button.addActionListener(this);
	    keyword = new JTextField(20);
	    
	    TextPanel = new JPanel();
	    TextPanel.setLayout(new GridLayout(2,1));
	    TextPanel.add(keyword);
	    TextPanel.add(button);
	    
	    MainPanel = new JPanel();
	    MainPanel.setLayout(new FlowLayout());
	    MainPanel.add(TextPanel);
	    MainPanel.add(CheckBoxPanel);
	    
	    add(MainPanel);
	       
	    setSize(300, 300);
	    setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String pattern = keyword.getText();
		if(Desktop.isSelected()) {
			new FileDisplayer(Path.DESKTOP, pattern);
		}else if(CF.isSelected()) {
			new FileDisplayer(Path.CF, pattern);
		}else if(User.isSelected()) {
			new FileDisplayer(Path.USERS, pattern);
		}
	}
}
