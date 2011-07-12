   package modelos;

   import java.awt.*;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import javax.swing.*;
//import javax.swing.border.*;


   public class Principal extends JApplet{
   
      private static final long serialVersionUID = 1L;
      private JLabel labUsuario, labSenha, labLista, labMenssagem; 
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
         cancelar.addActionListener
            (      
               new ActionListener()
               {
                  public void actionPerformed( ActionEvent event )
                  {    
                     labUsuario.setVisible(false);
                     labSenha.setVisible(false);
                     textUsuario.setVisible(false);
                     senha.setVisible(false);
                     entrar.setVisible(false);
                     cancelar.setVisible(false);
                     labMenssagem = new JLabel("OBRIGADO!");             
                     addComponent(labMenssagem, 1,0,2,1);
                  }
               });
            //cancelar.setActionCommand("CLOSE");
         labLista = new JLabel("Lista de Jogadores OnLine");
         labMenssagem = new JLabel("Não conectado!");
         listaJogadores = new JList(nomeJogadores);
         listaJogadores.setVisibleRowCount(10);
         jogar = new JButton("Jogar");
         jogar.setMnemonic( 'J' );
         sair = new JButton("Sair");
         sair.setMnemonic( 'S' );
         JSeparator s = new JSeparator();
            	         
         addComponent(s, 0,0,2,1);   
         addComponent(labUsuario, 1,0,1,1);
         addComponent(textUsuario, 1,1,1,1);
         addComponent(labSenha, 2,0,1,1);
         addComponent(senha, 2,1,1,1);
         addComponent(entrar, 3,0,1,1);
         addComponent(cancelar, 3,1,1,1);
            	         
      
         cons.fill = GridBagConstraints.CENTER;
         addComponent(labLista, 4,0,2,1);
         addComponent(listaJogadores, 5,0,2,1);
         addComponent(labMenssagem, 7,0,2,1);
         cons.fill = GridBagConstraints.HORIZONTAL;
         addComponent(jogar, 6,0,1,1);
         addComponent(sair, 6,1,1,1);
            
         labMenssagem.setVisible(false);
         labLista.setVisible(false);
         listaJogadores.setVisible(false);
         jogar.setVisible(false);
         sair.setVisible(false);
      
            
      }
     //esse metodo faz o valor de x para y e de y para x
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
            labLista.setVisible(true);
            listaJogadores.setVisible(true);
            jogar.setVisible(true);
            sair.setVisible(true);
              	
            labUsuario.setVisible(false);
            labSenha.setVisible(false);
            textUsuario.setVisible(false);
            senha.setVisible(false);
            entrar.setVisible(false);
            cancelar.setVisible(false);
              	 
              	 /*textUsuario.setEnabled(false);
              	  senha.setEnabled(false);
              	 entrar.setEnabled(false);
              	 cancelar.setEnabled(false);*/
         }
           	 
         if(j.isOnline()== false)
         {
            labMenssagem.setVisible(true);
         }
      }
   }
	
