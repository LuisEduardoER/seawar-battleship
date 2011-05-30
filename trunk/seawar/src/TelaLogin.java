   import java.awt.*;
   import java.awt.Event.*;
   import javax.swing.*;
   import javax.swing.event.*;
   import java.applet.*;
   

    public class TelaLogin extends JApplet
   {
      private JLabel labUsuario, labSenha, imagem; 
      private JTextField textUsuario;
      private JPasswordField senha;
      private JButton ok;
      private JButton cancelar;  
      private GridBagLayout layout;
  	  private GridBagConstraints cons;   
  	  
  	  
  	  public void init()
      {    	   
    	 Container c = getContentPane();   
   		 layout = new GridBagLayout();
   		 c.setLayout(layout);
   		 cons = new GridBagConstraints();
   		 //setSize(500,500);
   		 cons.fill = GridBagConstraints.BOTH;
   		 Imagens i = new Imagens();
   		 c.setBackground(i.corBackground());  
   	  	   		 
   		   
         labUsuario = new JLabel("Usuário");
         textUsuario = new JTextField("",11);
         labSenha = new JLabel("Senha   ");
         senha = new JPasswordField(11);
         ok = new JButton("Entrar");
         ok.setMnemonic( 'E' );
      	//para desabilitar um JTextField textUsuario.setEditable(false);
         	
         cancelar = new JButton("Cancelar");
         cancelar.setMnemonic( 'C' );
               	
         cons.gridx = 0;   
         cons.gridy = 0;
         c.add(labUsuario,cons);
         
         cons.gridx = 1;   
         cons.gridy = 0;
         c.add(textUsuario,cons);
         
         cons.gridx = 0;   
         cons.gridy = 1;
         c.add(labSenha,cons);
         
         cons.gridx = 1;   
         cons.gridy = 1;
         c.add(senha,cons);
         
         cons.gridx = 0;   
         cons.gridy = 2;
         c.add(ok,cons);
         
         cons.gridx = 1;   
         cons.gridy = 2;
         c.add(cancelar,cons);
         
         
      }
  	  
   }  	  

 