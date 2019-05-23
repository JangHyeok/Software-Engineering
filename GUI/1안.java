import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.border.*;

class 테스트 extends JFrame
{
	JPanel panel;


	JTextArea jTextArea1;
	JScrollPane jTextArea1_scroll;
	JTextField jTextField2;
	JButton textin;

	public 테스트()
	{
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		ImageIcon frameIcon = new ImageIcon("C:\\Users\\JHmulti\\Desktop\\인디언포커 아이콘 복사.png");
		setIconImage(frameIcon.getImage());

		setTitle("인디언포커");
		setBounds(118, 97, 1280, 720);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel = new JPanel();
		panel.setLayout(null);

		makeComponent();

		getContentPane().add(panel, BorderLayout.CENTER);
	}

	public void makeComponent()
	{
		jTextArea1 = new JTextArea();
		jTextArea1_scroll = new JScrollPane(jTextArea1);
		jTextArea1.setText("jTextArea1");
		jTextArea1.setFont(new Font("Dialog.plain", 0, 12));
		jTextArea1.setForeground(new Color(-13421773));
		jTextArea1.setBackground(new Color(-1));
		jTextArea1_scroll.setBounds(934, 506, 300, 110);
		panel.add(jTextArea1_scroll);

		jTextField2 = new JTextField();
		jTextField2.setText("jTextField2");
		jTextField2.setFont(new Font("Dialog.plain", 0, 12));
		jTextField2.setForeground(new Color(-13421773));
		jTextField2.setBackground(new Color(-1));
		jTextField2.setBounds(933, 619, 240, 40);
		panel.add(jTextField2);

		textin = new JButton();
		textin.setText("입력");
		textin.setFont(new Font("Dialog.plain", 0, 12));
		textin.setIcon(new ImageIcon(""));
		textin.setForeground(new Color(-13421773));
		textin.setBounds(1174, 619, 60, 40);
		panel.add(textin);
	}

	public static void main(String[] args)
	{
		테스트 GUI_Interface = new 테스트();
		GUI_Interface.setVisible(true);
	}
}
