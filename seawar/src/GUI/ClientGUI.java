package GUI;

import guiComponentes.BotaoCelula;
import guiComponentes.GridBotoes;

import java.applet.Applet;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modelos.Celula;
import modelos.Jogador;
import modelos.Jogo;
import modelos.Log;
import modelos.Tabuleiro;
import modelos.TipoCelula;
import Comunicacao.Constantes;
import Comunicacao.MessageSender;
import Events.ClientEvent;
import Events.ClientEventListener;
import client.Cliente;
import java.awt.Dimension;

public class ClientGUI extends Applet{

	/**
	 * 
	 */
	//Variaveis necessárias para funcionamento background
	MessageSender enviador;
	Cliente client;  //  @jve:decl-index=0:
	Socket socket;
	EventoCliente eventosCliente = null;
	Jogo jogo;
	
	//Coisas da Interface
	private static final long serialVersionUID = 1L;
	private JPanel pnlTabDefesa = null;
	private JPanel pnlTabuleiroAtaque = null;
	private JButton btnConectar = null;
	private JButton btnDesconectar = null;
	private JPanel pnlTabuleiros = null;
	private JTextArea txtAreaServer = null;
	private JLabel lblTabuleiroDefesa = null;
	private JLabel lblTabuleiroAtaque = null;
	private JButton btnPronto = null;
	private GridBotoes botoesDefesa = null;  //  @jve:decl-index=0:
	private GridBotoes botoesAtaque = null;
	private EventoAtaque listenerAtaque = null;
	private BotaoCelula botaoPressionado = null;
	private JScrollPane jScrollPane = null;
	
	public ClientGUI(){
		super();
		listenerAtaque = new EventoAtaque();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setLayout(null);
		this.setSize(653, 495);
		this.add(getBtnConectar(), null);
		this.add(getBtnDesconectar(), null);
		this.add(getPnlTabuleiros(), null);
		//CarregarFrameTabuleiros();//TODO: Excluir no final :)
		this.add(getJScrollPane(), null);
		
		
		
	}

	/**
	 * This method initializes pnlTabDefesa	
	 * 	
	 * @return java.awt.Panel	
	 */
	private JPanel getPnlTabDefesa() {
		if (pnlTabDefesa == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(10);
			gridLayout1.setColumns(10);
			pnlTabDefesa = new JPanel();
			pnlTabDefesa.setLayout(gridLayout1);
			pnlTabDefesa.setBounds(new Rectangle(4, 71, 290, 242));
		}
		return pnlTabDefesa;
	}

	/**
	 * This method initializes pnlTabuleiroAtaque	
	 * 	
	 * @return java.awt.Panel	
	 */
	private JPanel getPnlTabuleiroAtaque() {
		if (pnlTabuleiroAtaque == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(10);
			gridLayout.setColumns(10);
			pnlTabuleiroAtaque = new JPanel();
			pnlTabuleiroAtaque.setBounds(new Rectangle(308, 71, 310, 241));
			pnlTabuleiroAtaque.setLayout(gridLayout);
		}
		return pnlTabuleiroAtaque;
	}

	/**
	 * This method initializes btnConectar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnConectar() {
		if (btnConectar == null) {
			btnConectar = new JButton();
			btnConectar.setBounds(new Rectangle(6, 6, 107, 26));
			btnConectar.setText("Conectar");
			btnConectar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String login = JOptionPane.showInputDialog("Digite o login");
					String senha = JOptionPane.showInputDialog("Digite sua senha");
					//Se o usuário não fornece senha e login, não conecta-o ao jogo
					if(login == null || senha == null || login.isEmpty() || senha.isEmpty()){
						imprimirMensagem("Login ou senha não fornecidos");
						return;
					}
					//Se instanciar corretamente o objeto do cliente, conecta-o com o login e a senha fornecidos
					if(InstanciarCliente()){
						client.conectar(login,senha);
					}
					else{
						imprimirMensagem("Erro ao tentar se conectar, socket falhou ao ser instanciado");
					}
					//Quando conectado, o cliente disparará um evento de conectarJogador
				}
			});
		}
		return btnConectar;
	}

	protected boolean InstanciarCliente() {
		
		socket = InicializarSocket();
		//Se inicializou o socket, pode inicializar o client do jogo
		if(socket != null){
			client = new Cliente(socket);
			eventosCliente = new EventoCliente();
			client.AddClientEventListener(eventosCliente);
			return true;
		}
		
		return false;
	}

	private Socket InicializarSocket() {
		
		try {
			Socket clientSocket = new Socket(InetAddress.getByName(Constantes.SERVER_ADDRESS), Constantes.SERVER_PORT);
			return clientSocket;
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method initializes btnDesconectar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnDesconectar() {
		if (btnDesconectar == null) {
			btnDesconectar = new JButton();
			btnDesconectar.setBounds(new Rectangle(121, 7, 111, 25));
			btnDesconectar.setEnabled(false);
			btnDesconectar.setText("Desconectar");
		}
		return btnDesconectar;
	}

	/**
	 * This method initializes pnlTabuleiros	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlTabuleiros() {
		if (pnlTabuleiros == null) {
			lblTabuleiroAtaque = new JLabel();
			lblTabuleiroAtaque.setBounds(new Rectangle(306, 49, 311, 22));
			lblTabuleiroAtaque.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			lblTabuleiroAtaque.setText("Tabuleiro Ataque:");
			lblTabuleiroDefesa = new JLabel();
			lblTabuleiroDefesa.setBounds(new Rectangle(2, 50, 291, 21));
			lblTabuleiroDefesa.setText("Tabuleiro Defesa:");
			pnlTabuleiros = new JPanel();
			pnlTabuleiros.setLayout(null);
			pnlTabuleiros.setBounds(new Rectangle(6, 134, 641, 351));
			pnlTabuleiros.setEnabled(false);
			//pnlTabuleiros.setVisible(false);
			pnlTabuleiros.add(getPnlTabDefesa(), null);
			pnlTabuleiros.add(getPnlTabuleiroAtaque(), null);
			pnlTabuleiros.add(lblTabuleiroDefesa, null);
			pnlTabuleiros.add(lblTabuleiroAtaque, null);
			pnlTabuleiros.add(getBtnPronto(), null);
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
			txtAreaServer.setLineWrap(true);
			txtAreaServer.setText("Aqui você receberá as mensagens do servidor");
			txtAreaServer.setBackground(new Color(245, 244, 244));
			txtAreaServer.setEditable(false);
		}
		return txtAreaServer;
	}

	/**
	 * This method initializes btnPronto	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnPronto() {
		if (btnPronto == null) {
			btnPronto = new JButton();
			btnPronto.setBounds(new Rectangle(10, 11, 83, 28));
			btnPronto.setEnabled(false);
			btnPronto.setText("Pronto");
			btnPronto.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					habilitarTabuleiroDefesa(false);//Desabilita o tabuleiro de defesa
					if(client.enviarTabuleiro())
						habilitarBotaoPronto(false);
				}
			});
		}
		return btnPronto;
	}
	
	public void CarregarFrameTabuleiros(Jogo jogo){
		//this.add(getPnlTabuleiros(), null);
		
		imprimirMensagem("Servidor: Aguardando jogadores...");
		this.jogo = jogo;
	}


	protected void imprimirMensagem(String string) {
		this.txtAreaServer.append("\n"+string);		
	}

	protected void ConectarJogador(Jogador jogador) {
		String nomeJogador = jogador.getLogin();
		//Se quem conectou fui eu, então meu nome vai para o tabuleiro de defesa
		//senão o nome do jogador que conectou vai para o tabuleiro de ataque
		if(jogador.equals(this.client.getPerfil())){
			lblTabuleiroDefesa.setText(nomeJogador);
		}
		else {
			lblTabuleiroAtaque.setText(nomeJogador);
		}
		//Imprime na área de texto que o jogador X foi conectado ao servidor
		imprimirMensagem(String.format("%s conectado ao servidor", nomeJogador));
		

		if(jogo != null && jogo.isLotado() && !jogo.isJogoEncerrado()){
			habilitarTabuleirosParaPosicionamento();
			PreencherTabuleiroDefesa(this.client.getPerfil().getTabuleiroDefesa());
			PreencherTabuleiroAtaque(this.client.getPerfil().getTabuleiroAtaque());
		}
	}

	private void PreencherTabuleiroDefesa(Tabuleiro tabuleiroDefesa) {
		//Pega o grid do tabuleiro e reajusta seu tamanho
		GridLayout gridLayout = (GridLayout)this.pnlTabDefesa.getLayout();
		int largura = tabuleiroDefesa.getMatrizCelula().length;
		int altura = tabuleiroDefesa.getMatrizCelula()[0].length;
		gridLayout.setRows(largura);
		gridLayout.setColumns(altura);
		//Preenche o grid com os botões
		botoesDefesa = new GridBotoes(largura, altura);
		for (int i = 0; i < botoesDefesa.getLargura(); i++) {
			for (int j = 0; j < botoesDefesa.getAltura(); j++) {
				//Cria o botão informando qual a posição ele irá armazenar
				Celula celula = tabuleiroDefesa.getCelula(i,j);
				BotaoCelula botao = null;
				//Cria a célula independente de ter icone ou não
				if(celula.hasIcone()){
					botao = new BotaoCelula(celula.getIcone(),i, j);
				}
				else{
					botao = new BotaoCelula(i, j);	
				}
				botao.setSize(32, 32);
				//Define a cor do botão se for barco
				if(celula.getTipoCelula() == TipoCelula.Embarcacao)
					botao.setBackground(getCorBotao(celula));
				
				//Adiciona o botão ao GRID para futuras referencias
				botoesDefesa.setBotao(botao, i, j);
				
				//Adiciona o botão no tabuleiro
				//(Como o BotaoCelula extende o JButton, o painel o reconhece como botão
				this.pnlTabDefesa.add(botao);
			}
		}
		//repinta o tabuleiro para exibir os botoes
		this.pnlTabDefesa.repaint();
	}

	private void PreencherTabuleiroAtaque(Tabuleiro tabuleiroAtaque) {
		//Pega o grid do tabuleiro e reajusta seu tamanho
		GridLayout gridLayout = (GridLayout)this.pnlTabuleiroAtaque.getLayout();
		int largura = tabuleiroAtaque.getMatrizCelula().length;
		int altura = tabuleiroAtaque.getMatrizCelula()[0].length;
		gridLayout.setRows(largura);
		gridLayout.setColumns(altura);
		//Preenche o grid com botões
		botoesAtaque = new GridBotoes(largura, altura);
		for (int i = 0; i < botoesAtaque.getLargura(); i++) {
			for (int j = 0; j < botoesAtaque.getAltura(); j++) {
				//Cria o botão informando qual a posição ele irá armazenar
				Celula celula = tabuleiroAtaque.getCelula(i,j);
				BotaoCelula botao = null;
				//Cria a célula independente de ter icone ou não
				if(celula.hasIcone()){
					botao = new BotaoCelula(celula.getIcone(),i, j);
				}
				else{
					botao = new BotaoCelula(i, j);	
				}
				botao.setSize(32, 32);
				
				//Define o evento que vai disparar quando o botão for clicado
				botao.addActionListener(listenerAtaque);
				//Adiciona o botão no tabuleiro (como extende o JButton, ele sabe que este objeto é um botão)
				this.pnlTabuleiroAtaque.add(botao);
			}
		}
		//Repinta o tabuleiro para exibir os botões
		this.pnlTabuleiroAtaque.repaint();
	}

	private void habilitarTabuleirosParaPosicionamento() {
		habilitarTabuleiroAtaque(false);		
		habilitarTabuleiroDefesa(true);	
		habilitarBotaoPronto(true);
	}
	private void habilitarTabuleirosParaAtaque() {
		habilitarTabuleiroAtaque(true);		
		habilitarTabuleiroDefesa(false);	
		habilitarBotaoPronto(false);
	}

	protected void habilitarBotaoPronto(boolean b) {
		//Botão de pronto tem que ter o enabled igual o tabuleiro de defesa
		//Assim garantimos que o botão pronto será desativado e o posicionamento do tabuleiro
		//de defesa também será, não permitindo ao jogador alterar a posição
		//após o inicio da partida
		this.btnPronto.setEnabled(b);
		habilitarTabuleiroDefesa(b);
	}
	


	protected void DesconectarJogador() {
		this.client.desconectar();
		//Desabilita os botões necessários da interface
		habilitarTabuleiroAtaque(false);
		habilitarTabuleiroDefesa(false);
		habilitarBotaoPronto(false);
		habilitarBotaoConectar(true);
		habilitarBotaoDesconectar(false);
		limparTabuleiros();
	}

	private void limparTabuleiros() {
		//TODO: Retirar todos os botões da UI
		pnlTabDefesa.removeAll();
		pnlTabuleiroAtaque.removeAll();
	}

	private void habilitarBotaoDesconectar(boolean b) {
		this.btnDesconectar.setEnabled(b);
		
	}

	private void habilitarBotaoConectar(boolean b) {
		this.btnConectar.setEnabled(b);
		
	}

	private void habilitarTabuleiroDefesa(boolean b) {
		this.pnlTabDefesa.setEnabled(b);
	}

	private void habilitarTabuleiroAtaque(boolean b) {
		this.pnlTabuleiroAtaque.setEnabled(b);		
	}
	
	private void exibirAtaqueRecebido(Celula celulaAtacada){
		int x = celulaAtacada.x;
		int y = celulaAtacada.y;
		Color cor = getCorBotao(celulaAtacada);
		//Pega o botão no tabuleiro de defesa
		BotaoCelula botao = getBotaoEmTabuleiroDefesa(x,y);
		//troca a cor do botão correspondente a celula atacada
		botao.setBackground(cor);
		botao.repaint();
	}

	private BotaoCelula getBotaoEmTabuleiroDefesa(int x, int y) {
		if (pnlTabDefesa.getLayout() instanceof GridLayout) {
			//GridLayout grid = (GridLayout) pnlTabuleiro.getLayout();
			try{
				BotaoCelula botao = botoesDefesa.getBotao(x, y);
				
				return botao;
			}
			catch(Exception ex)
			{
				Log.gravarLog("Botão não encontrado");
			}
			
		}
		else{
			Log.gravarLog("Painel do tabuleiro não é do tipo GridLayout");
		}
		return null;
	}

	public boolean enviarAtaqueCliente(int coordX, int coordY) {
		return this.client.Atacar(coordX, coordY);		
	}
	
	public void exibirRespostaAtaque(Celula celula){
		//Executa o método apenas se algum botão foi pressionado
		if(botaoPressionado == null){
			return;
		}
		
		Color cor = getCorBotao(celula);
		//Altera o fundo do botão pressionado no tabuleiro de ataque
		botaoPressionado.setBackground(cor);
		
		//marca como nulo porque o botão pressionado já teve sua resposta
		botaoPressionado = null;		
	}

	private Color getCorBotao(Celula celula) {
		Color cor = Constantes.CorAgua;
		switch(celula.getTipoCelula()){
			case Embarcacao:
				cor = (celula.isAtirada())? Constantes.CorBarcoAcertado : Constantes.CorBarco ;
				break;
			default:
				cor = Constantes.CorAguaAcertada;
				break;
		}
		return cor;
	}
	
	//Classe interna que implementa os eventos ocorridos no botão de ataque
	class EventoAtaque implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//A premissa para poder fazer um ataque, é o jogador estar online e pronto
			if (client.getPerfil().isOnline() && !btnPronto.isEnabled() && e.getSource() instanceof BotaoCelula) {
				//Pega o botão pressionado e envia o ataque
				botaoPressionado = (BotaoCelula) e.getSource();
				//Envia o ataque e fica aguardando a resposta, que ativará o método "exibirRespostaAtaque()"
				if(enviarAtaqueCliente(botaoPressionado.getCoordX(), botaoPressionado.getCoordY())){
				//Desabilita o botão para não utilizá-lo novamente
				botaoPressionado.setEnabled(false);			
				}
			}
		}
		
	}

	class EventoCliente implements ClientEventListener{
			
			@Override
			public void turnoAlterado(Object source, ClientEvent evt) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void tiroRecebido(Object source, ClientEvent evt, Celula celula) {
				exibirAtaqueRecebido(celula);				
			}
			
			@Override
			public void respostaTiro(Object source, ClientEvent evt, Celula celula) {
				List<String> tokens = (List<String>)source;
				String nomeBarco = null;
				boolean afundou = false;
				for(String token : tokens){
					String[] split = token.split(Constantes.VALUE_SEPARATOR);
					//Aqui compara o length, porque pode vir vazio o valor do barco
					if(split[0].equalsIgnoreCase("barco") && split.length == 2){
						//Atualiza o Id do jogo para futuras referencias
						nomeBarco = split[1];
					}
					else if(split[0].equalsIgnoreCase("afundou")){
						//Atualiza o Id do jogo para futuras referencias
						afundou = Boolean.parseBoolean(split[1]);
						
					}
				}
				
					
				
				exibirRespostaAtaque(celula);
				
				//Se o cliente acertou um barco, informa qual barco foi
				if(nomeBarco != null && !nomeBarco.isEmpty()){
					String mensagemBarco = "%s a embarcação %s do adversário!";
					mensagemBarco = String.format(mensagemBarco, (afundou? "Afundou":"Acertou"), nomeBarco);
					exibeMensagem(mensagemBarco);
				}
			}
			
			@Override
			public void jogoIniciado(Object source, ClientEvent evt) {
				habilitarTabuleiroAtaque(true);
				habilitarTabuleiroDefesa(false);
				habilitarBotaoPronto(false);
			}
			
			@Override
			public void jogadorDesconectado(Object source, ClientEvent evt) {
				Jogador jogador = (Jogador)source;
				imprimirMensagem(String.format("%s saiu do jogo", jogador.getLogin()));
			}
			
			@Override
			public void jogadorConectado(Object source, ClientEvent evt) {
				Jogador jogador = null;
				//Exibe o jogador que foi conectado no jogo,
				//se houver erro, exibe o nome dele como "oponente"
				try{
					jogador = (Jogador)source;				
				}catch(Exception ex){
					Log.gravarLog(ex.getMessage());
					jogador = new Jogador();
					jogador.setLogin("Oponente");
				}
				ConectarJogador(jogador);
			}
			
			@Override
			public void fimDeJogo(Object source, ClientEvent evt) {
				Jogador vencedor = (Jogador)source;
				imprimirMensagem(String.format("%s venceu este jogo!",vencedor.getLogin()));
				DesconectarJogador();
			}
			
			@Override
			public void falhaGenerica(Object source, Exception exception) {				
				Log.gravarLog(exception.getMessage());
				imprimirMensagem(exception.getMessage());
			}
			
			@Override
			public void exibeMensagem(Object mensagem) {
				imprimirMensagem(mensagem.toString());
				
			}
			
			@Override
			public void carregarTelaJogo(Object source) {
				try{
					habilitarBotaoConectar(false);
					Jogo jogo = (Jogo)source;					
					CarregarFrameTabuleiros(jogo);
				}
				catch(Exception ex){
					DesconectarJogador();
				}
			}
			
			@Override
			public void barcosOponentePosicionados(Object src) {
				imprimirMensagem("Oponente posicionou os tabuleiros e está pronto para jogar");				
			}
			
			@Override
			public void ativarBot(Object src, Jogador bot) {
				// TODO Auto-generated method stub
				
			}
		
	}
	
	@Override
	public void destroy() {
		this.DesconectarJogador();
		super.destroy();
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(7, 44, 82, 67));
			jScrollPane.setViewportView(getTxtAreaServer());
			jScrollPane.setSize(630, 80);
		}
		return jScrollPane;
	}
}  //  @jve:decl-index=0:visual-constraint="9,17"
