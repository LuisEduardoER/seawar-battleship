import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.applet.*;

public class ListaJogadores extends JApplet
{
	private JList listaJogadores;
	private String nomeJogadores[] = {"Débora","Fernando","Reginaldo","Reinaldo","Marcus","Vitor"};
	private JLabel lUsuario;
	private JLabel lDadosJogador;
	
	public void init()
	{
		setLayout(new FlowLayout());
		setSize(400,400);
		
		listaJogadores = new JList(nomeJogadores);
		listaJogadores.setVisibleRowCount(20);
		listaJogadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
		lUsuario = new JLabel("Lista de Jogadores Online");
		add(new JScrollPane(listaJogadores));
		
		lDadosJogador = new JLabel("Dados do Jogador");
		
		add(lUsuario);
		add(lDadosJogador);
	}

}
