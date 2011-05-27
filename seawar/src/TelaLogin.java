   import java.applet.*;      
   import java.awt.*;
   import javax.swing.*;
   import java.awt.event.*;
   

    public class TelaLogin extends JApplet
   {
      private JLabel labUsuario;
      private JLabel labSenha; 
      private JTextField textUsuario;
      private JPasswordField senha;
      private JButton ok;
      private JButton cancelar;  
     
       public void init()
      {       
         setLayout(new FlowLayout());
         setSize(200,80);
         labUsuario = new JLabel("Usuário");
         textUsuario = new JTextField("",11);
         labSenha = new JLabel("Senha   ");
         senha = new JPasswordField(11);
         ok = new JButton("Entrar");
         ok.setMnemonic( 'E' );
      	//para desabilitar um JTextField textUsuario.setEditable(false);
         	
         cancelar = new JButton("Cancelar");
         cancelar.setMnemonic( 'C' );
               						
         add(labUsuario);
         add(textUsuario);
         add(labSenha);
         add(senha);
         add(ok);
         //add(cancelar);      	
      }   	   	
   }
    