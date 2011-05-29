import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.applet.*;

public class ListaJogadores extends JApplet
{
	private JList listaJogadores;
	private String nomeJogadores[] = {"D�bora","Fernando","Reginaldo","Reinaldo","Marcus","Vitor"};
	private JLabel lUsuario;
	private JLabel lDadosJogador;
	private JLabel lEspaco;
	private GridBagLayout layout;
	private GridBagConstraints cons;
	
	public void init()
	{
		Container c = getContentPane();   
		layout = new GridBagLayout();
		c.setLayout(layout);
		cons = new GridBagConstraints();
		setSize(500,500);
		cons.fill = GridBagConstraints.BOTH;
		
		cons.gridx = 0;   
        cons.gridy = 0; 
		lUsuario = new JLabel("Lista de Jogadores Online");
		c.add(lUsuario,cons);
				
		cons.gridx = 1;   
        cons.gridy = 0; 
		lDadosJogador = new JLabel("Dados do Jogador");
		c.add(lDadosJogador,cons);
		
		cons.gridx = 0;   
        cons.gridy = 1;
        listaJogadores = new JList(nomeJogadores);
		listaJogadores.setVisibleRowCount(10);
		listaJogadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		c.add(new JScrollPane(listaJogadores),cons);
        
	}

}