package GUI;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Events.ServerEvent;
import Events.ServerEventListener;

import modelos.Jogador;
import modelos.Jogo;
import modelos.Servidor;

public class ServerGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	Servidor server;
	JList listaJogadores;
	JList listaJogos;
	JTextArea displayArea;
	
	
	public ServerGUI(){
		super("Server do jogo Batalha Naval");
		
		
		listaJogadores = new JList();
		JScrollPane containerListaJogadores = new JScrollPane(listaJogadores);
		containerListaJogadores.setSize(250, 100);
		containerListaJogadores.setLocation(20, 20);
		containerListaJogadores.setWheelScrollingEnabled(true);
		JLabel lblJogadores = new JLabel("Jogadores:");
		lblJogadores.setSize(100, 20);
		lblJogadores.setLocation(20, 0);
		
		
		listaJogos = new JList();
		JScrollPane containerListaJogos = new JScrollPane(listaJogos);
		containerListaJogos.setSize(250,100);
		containerListaJogos.setLocation(290, 20);
		containerListaJogos.setWheelScrollingEnabled(true);
		JLabel lblJogos = new JLabel("Jogos:");
		lblJogos.setSize(100, 20);
		lblJogos.setLocation(290, 0);
		
		
		displayArea = new JTextArea();
		displayArea.setAutoscrolls(true);	
		displayArea.setLineWrap(true);
		JScrollPane containerMensagens = new JScrollPane(displayArea);
		containerMensagens.setSize(520, 100);
		containerMensagens.setLocation(20, 150);
		containerMensagens.setAutoscrolls(true);
		containerListaJogadores.setEnabled(true);
		JLabel lblMensagens = new JLabel("Mensagens:");
		lblMensagens.setSize(100, 20);
		lblMensagens.setLocation(20, 130);
		
		
		setSize(600, 300);		
		setLayout(null);
		
		
		add(lblJogadores);
		add(containerListaJogadores);
		add(lblJogos);
		add(containerListaJogos);
		add(lblMensagens);
		add(containerMensagens);
		
		server = new Servidor();
		
		setVisible(true);
	}
	
	public void IniciarServidor(){
		//adiciona os listeners
		server.AddServerEventListener(new ServerEventListener() {
			
			@Override
			public void updateDisplay(ServerEvent evt) {
				AtualizarDisplay(evt.getSource().toString());
				
			}
			
			@Override
			public void playerListChanged(ServerEvent evt) {
				AtualizarListaJogadores();
				
			}
			
			@Override
			public void gamesListChanged(ServerEvent evt) {
				AtualizarListaJogos();
				
			}
		});
		displayArea.append("Listening to Events...");
		server.IniciarServidor();
	}
	

	private void AtualizarListaJogadores() {
		DefaultListModel lista = new DefaultListModel();
		List<Jogador> jogadores = server.getListaJogadorOnline();
		for(Jogador obj : jogadores){
			lista.addElement(obj.getLogin());
		}
		
		listaJogadores.setModel(lista);		
	}
	
	private void AtualizarListaJogos() {
		DefaultListModel lista = new DefaultListModel();
		List<Jogo> jogos = server.getListaJogos();
		for(Jogo obj : jogos){
			lista.addElement(obj);
		}
		
		listaJogos.setModel(lista);			
	}
	
	private void AtualizarDisplay(String mensagem) {
		displayArea.append("\n"+mensagem);		
		displayArea.setCaretPosition(displayArea.getText().length());
	}
}
