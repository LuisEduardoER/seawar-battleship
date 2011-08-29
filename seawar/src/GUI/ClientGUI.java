package GUI;

import java.applet.Applet;

import client.Cliente;

import Comunicacao.MessageSender;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.Rectangle;

import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.CardLayout;
import java.awt.FlowLayout;

public class ClientGUI extends Applet{

	/**
	 * 
	 */
	MessageSender enviador;
	Cliente client;
	private static final long serialVersionUID = 1L;
	private Panel pnlTabDefesa = null;
	private Panel pnlTabuleiroAtaque = null;
	private JButton Conectar = null;
	private JButton Desconectar = null;
	private JPanel pnlTabuleiros = null;
	private JTextArea txtAreaServer = null;
	private JLabel lblTabuleiroDefesa = null;
	private JLabel lblTabuleiroAtaque = null;
	public ClientGUI(){
		super();
		
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setLayout(null);
		this.setSize(653, 495);

		this.add(getConectar(), null);
		this.add(getDesconectar(), null);
		this.add(getPnlTabuleiros(), null);
		this.add(getTxtAreaServer(), null);
	}

	/**
	 * This method initializes pnlTabDefesa	
	 * 	
	 * @return java.awt.Panel	
	 */
	private Panel getPnlTabDefesa() {
		if (pnlTabDefesa == null) {
			pnlTabDefesa = new Panel();
			pnlTabDefesa.setLayout(new FlowLayout());
			pnlTabDefesa.setBounds(new Rectangle(11, 108, 290, 242));
		}
		return pnlTabDefesa;
	}

	/**
	 * This method initializes pnlTabuleiroAtaque	
	 * 	
	 * @return java.awt.Panel	
	 */
	private Panel getPnlTabuleiroAtaque() {
		if (pnlTabuleiroAtaque == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			pnlTabuleiroAtaque = new Panel();
			pnlTabuleiroAtaque.setBounds(new Rectangle(315, 108, 310, 241));
			pnlTabuleiroAtaque.setLayout(gridLayout);
		}
		return pnlTabuleiroAtaque;
	}

	/**
	 * This method initializes Conectar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getConectar() {
		if (Conectar == null) {
			Conectar = new JButton();
			Conectar.setBounds(new Rectangle(6, 6, 107, 26));
			Conectar.setText("Conectar");
			Conectar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String login = JOptionPane.showInputDialog("Digite o login");
					String senha = JOptionPane.showInputDialog("Digite sua senha");
					
					client.conectar(login,senha);
				}
			});
		}
		return Conectar;
	}

	/**
	 * This method initializes Desconectar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDesconectar() {
		if (Desconectar == null) {
			Desconectar = new JButton();
			Desconectar.setBounds(new Rectangle(121, 7, 111, 25));
			Desconectar.setEnabled(false);
			Desconectar.setText("Desconectar");
		}
		return Desconectar;
	}

	/**
	 * This method initializes pnlTabuleiros	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlTabuleiros() {
		if (pnlTabuleiros == null) {
			lblTabuleiroAtaque = new JLabel();
			lblTabuleiroAtaque.setBounds(new Rectangle(313, 86, 311, 22));
			lblTabuleiroAtaque.setText("Tabuleiro Ataque:");
			lblTabuleiroDefesa = new JLabel();
			lblTabuleiroDefesa.setBounds(new Rectangle(9, 87, 291, 21));
			lblTabuleiroDefesa.setText("Tabuleiro Defesa:");
			pnlTabuleiros = new JPanel();
			pnlTabuleiros.setLayout(null);
			pnlTabuleiros.setBounds(new Rectangle(6, 134, 641, 351));
			pnlTabuleiros.setEnabled(false);
			pnlTabuleiros.setVisible(false);
			pnlTabuleiros.add(getPnlTabDefesa(), null);
			pnlTabuleiros.add(getPnlTabuleiroAtaque(), null);
			pnlTabuleiros.add(lblTabuleiroDefesa, null);
			pnlTabuleiros.add(lblTabuleiroAtaque, null);
		}
		return pnlTabuleiros;
	}

	/**
	 * This method initializes txtAreaServer	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getTxtAreaServer() {
		if (txtAreaServer == null) {
			txtAreaServer = new JTextArea();
			txtAreaServer.setBounds(new Rectangle(9, 39, 628, 83));
			txtAreaServer.setLineWrap(true);
			txtAreaServer.setText("Aqui você receberá as mensagens do servidor");
			txtAreaServer.setBackground(new Color(245, 244, 244));
			txtAreaServer.setEditable(false);
		}
		return txtAreaServer;
	}

}  //  @jve:decl-index=0:visual-constraint="9,17"
