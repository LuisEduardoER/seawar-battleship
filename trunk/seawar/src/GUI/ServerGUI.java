package GUI;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
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
		listaJogadores.setSize(100, 300);
		listaJogos = new JList();
		listaJogos.setSize(100,300);
		displayArea = new JTextArea();
		displayArea.setSize(400, 300);
		setSize(400, 600);		
		add(listaJogadores, BorderLayout.WEST);
		add(listaJogos, BorderLayout.EAST);
		add(displayArea, BorderLayout.SOUTH);
		
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
		
		listaJogadores = new JList(lista);			
	}
	
	private void AtualizarListaJogos() {
		DefaultListModel lista = new DefaultListModel();
		List<Jogo> jogos = server.getListaJogos();
		for(Jogo obj : jogos){
			lista.addElement(String.format("jogo: %s, #players: %s",obj.getIdJogo(), obj.getListaJogador().length));
		}
		
		listaJogadores = new JList(lista);			
	}
	
	private void AtualizarDisplay(String mensagem) {
		displayArea.append(mensagem);			
	}
}
