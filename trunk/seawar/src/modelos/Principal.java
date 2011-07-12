package modelos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;


public class Principal extends JApplet{
	
	 private static final long serialVersionUID = 1L;
	 private JLabel labUsuario, labSenha, labLista; 
     private JTextField textUsuario;
     private JPasswordField senha;
     private JButton entrar, cancelar;
	 private GridBagLayout layout;
	 private GridBagConstraints cons;
	 private JList listaJogadores;
	 private String nomeJogadores[] = {"Débora","Fernando","Reginaldo","Reinaldo","Marcus","Vitor","______________________________"};
	 private JButton jogar, sair;
	 Insets insets = new Insets(0,0,5,0);
	  
	  public void init()
      {	  
		  Container c = getContentPane();   
	   		 layout = new GridBagLayout();
	   		 c.setLayout(layout);
	   		 cons = new GridBagConstraints();
	   		 setSize(500,500);
	   		 cons.fill = GridBagConstraints.HORIZONTAL;
	   		 Imagens i = new Imagens();
	   		 c.setBackground(i.corBackground());
	   		 cons.insets = new Insets(3,3,3,3);
	   		   
	         labUsuario = new JLabel("Usuário:");
	         textUsuario = new JTextField("",11);
	         labSenha = new JLabel("Senha:");
	         senha = new JPasswordField(11);
	         entrar = new JButton("Entrar");
	         entrar.setMnemonic( 'E' );
	         entrar.addActionListener
	         (      
	             new ActionListener()
	            {
	                public void actionPerformed( ActionEvent event )
	               {                	
	                	/*Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	            		ListaJogadores window = new ListaJogadores();
	            		window.setSize(dim.width, dim.height);
	            		window.setVisible(true);*/
	            		verificaLogin();
	                	
	               }
	            });
	      	//para desabilitar um JTextField textUsuario.setEditable(false);
	        
	         cancelar = new JButton("Cancelar");
	         cancelar.setMnemonic( 'C' );
	         cancelar.setActionCommand("CLOSE");
	         labLista = new JLabel("Lista de Jogadores OnLine");
	         listaJogadores = new JList(nomeJogadores);
	 		 listaJogadores.setVisibleRowCount(10);
	 		 jogar = new JButton("Jogar");
	         jogar.setMnemonic( 'J' );
	         sair = new JButton("Sair");
	         sair.setMnemonic( 'S' );
	         JSeparator s = new JSeparator();
	         	         
	            
	         addComponent(labUsuario, 0,0,1,1);
	         addComponent(textUsuario, 0,1,1,1);
	         addComponent(labSenha, 1,0,1,1);
	         addComponent(senha, 1,1,1,1);
	         addComponent(entrar, 2,0,1,1);
	         addComponent(cancelar, 2,1,1,1);
	         
	         addComponent(s, 3,0,2,1);
	         
	         //verificaLogin();
	         cons.fill = GridBagConstraints.CENTER;
	         addComponent(labLista, 4,0,2,1);
	         addComponent(listaJogadores, 5,0,2,1);
	         cons.fill = GridBagConstraints.HORIZONTAL;
	         addComponent(jogar, 6,0,1,1);
	         addComponent(sair, 6,1,1,1);
	         listaJogadores.setEnabled(false);
	         jogar.setEnabled(false);
	         sair.setEnabled(false);
	   
	         
      }
	 		 private void addComponent( Component component, int row, int column, int width, int height )
	 			   {
	 			      cons.gridx = column; // configura gridx                           
	 			      cons.gridy = row; // configura gridy                              
	 			      cons.gridwidth = width; // configura gridwidth                    
	 			      cons.gridheight = height; // configure gridheight                 
	 			      layout.setConstraints( component, cons ); // configura constraints
	 			      add( component ); // adiciona componente
	 			   } // fim do método addComponent
	 		 
	 		 
	         private void verificaLogin()
	         {
	        	 Jogador j = new Jogador();
	        	 if(j.isOnline() == true)
	        	 {
	        		 listaJogadores.setEnabled(true);
		        	 jogar.setEnabled(true);
		        	 sair.setEnabled(true); 
	        	 }
	         }
	      }
	
	 