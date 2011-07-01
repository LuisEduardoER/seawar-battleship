package modelos;
import java.awt.*;
import javax.swing.*;

public class ListaJogadores extends JFrame
{	
	private static final long serialVersionUID = 1L;
	private JList listaJogadores;
	private String nomeJogadores[] = {"Débora","Fernando","Reginaldo","Reinaldo","Marcus","Vitor"};
	private JLabel lUsuario,lDadosJogador;
	private JButton jogar, sair;  
	private GridBagLayout layout;
	private GridBagConstraints cons;
	
	public ListaJogadores(){
		this.init();	
	}
	
	public void init()
	{
		Container c = getContentPane();   
		layout = new GridBagLayout();
		c.setLayout(layout);
		cons = new GridBagConstraints();
		setSize(500,500);
		cons.fill = GridBagConstraints.BOTH;
		Imagens i = new Imagens();
		c.setBackground(i.corBackground());
		
		
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
				       
    
        cons.gridx = 0;   
        cons.gridy = 2;
        sair = new JButton("Sair");
        sair.setMnemonic( 'S' );
        c.add(sair,cons);
        
        
        cons.gridx = 1;   
        cons.gridy = 2;
		jogar = new JButton("Jogar");
        jogar.setMnemonic( 'J' );
        c.add(jogar,cons);
        
	}

}
