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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import modelos.Celula;
import modelos.Embarcacao;
import modelos.Jogador;
import modelos.Jogo;
import modelos.Log;
import modelos.Tabuleiro;
import modelos.TipoCelula;
import Comunicacao.Constantes;
import Comunicacao.MessageSender;
import Events.ClientEvent;
import Events.ClientEventListener;
import client.ClienteMulti;

import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Point;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class NewClientGUI extends Applet{

	/**
	 * 
	 */
	//Variaveis necess�rias para funcionamento background
	MessageSender enviador;
	Embarcacao barcoTransformar;
	ClienteMulti client;  //  @jve:decl-index=0:
	Socket socket;
	EventoCliente eventosCliente = null;
	Jogo jogo;  //  @jve:decl-index=0:
	
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
	private EventoAtaque listenerAtaque = null;  //  @jve:decl-index=0:
	private EventoDefesa listenerDefesa = null;
	private BotaoCelula botaoPressionado = null;
	private JScrollPane jScrollPane = null;
	private JPanel pnlListas = null;
	private JList lstJogadores = null;
	private JLabel lblTextoJogadores = null;
	private JList lstJogos = null;
	private JLabel lblTextoJogosOnline = null;
	private JButton btnConvidar = null;
	private JButton btnCriarJogo = null;
	private JButton btnEntrarJogo = null;
		
	DefaultListModel listModelJogos;
	DefaultListModel listModelPlayers;
	private JLabel lblBemVindo = null;
	
	public NewClientGUI(){
		super();
		listenerAtaque = new EventoAtaque();
		listenerDefesa = new EventoDefesa();
		listModelJogos = new DefaultListModel();
		listModelPlayers = new DefaultListModel();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		lblBemVindo = new JLabel();
		lblBemVindo.setBounds(new Rectangle(255, 11, 381, 28));
		lblBemVindo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBemVindo.setText("");
		this.setLayout(null);
		this.setSize(733, 574);
		this.add(getBtnConectar(), null);
		this.add(getBtnDesconectar(), null);
		this.add(getPnlTabuleiros(), null);
		//CarregarFrameTabuleiros();//TODO: Excluir no final :)
		this.add(getJScrollPane(), null);
		this.add(getPnlListas(), null);
		this.add(lblBemVindo, null);
		
		
		
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
			//Corrige o tamanho do tabuleiro
			pnlTabDefesa.setSize(Constantes.TAMANHO_TABULEIRO * Constantes.LARGURA_CELULA, Constantes.TAMANHO_TABULEIRO * Constantes.ALTURA_CELULA);
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
			pnlTabuleiroAtaque.setBounds(new Rectangle(396, 72, 310, 241));
			pnlTabuleiroAtaque.setLayout(gridLayout);
			//Corrige o tamanho do tabuleiro
			pnlTabuleiroAtaque.setSize(Constantes.TAMANHO_TABULEIRO * Constantes.LARGURA_CELULA, Constantes.TAMANHO_TABULEIRO * Constantes.ALTURA_CELULA);
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
			btnConectar.setBounds(new Rectangle(15, 15, 107, 26));
			btnConectar.setText("Conectar");
			btnConectar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String login = JOptionPane.showInputDialog("Digite o login");
					if(login !=null && login.isEmpty()){
						return;
					}
					String senha = JOptionPane.showInputDialog("Digite sua senha");
					//Se o usu�rio n�o fornece senha e login, n�o conecta-o ao jogo
					if(login == null || senha == null || login.isEmpty() || senha.isEmpty()){
						imprimirMensagem("Login ou senha n�o fornecidos");
						return;
					}
					//Se instanciar corretamente o objeto do cliente, conecta-o com o login e a senha fornecidos
					if(InstanciarCliente()){
						client.conectar(login,senha);
					}
					else{
						imprimirMensagem("Erro ao tentar se conectar, socket falhou ao ser instanciado");
					}
					//Quando conectado, o cliente disparar� um evento de conectarJogador
				}
			});
		}
		return btnConectar;
	}

	protected boolean InstanciarCliente() {
		
		socket = InicializarSocket();
		//Se inicializou o socket, pode inicializar o client do jogo
		if(socket != null){
			client = new ClienteMulti(socket);
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
			btnDesconectar.setBounds(new Rectangle(135, 15, 111, 25));
			btnDesconectar.setEnabled(false);
			btnDesconectar.setText("Desconectar");
			btnDesconectar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					DesconectarJogador();
					
				}
			});
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
			lblTabuleiroAtaque.setBounds(new Rectangle(394, 50, 311, 22));
			lblTabuleiroAtaque.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			lblTabuleiroAtaque.setText("Tabuleiro Ataque:");
			lblTabuleiroDefesa = new JLabel();
			lblTabuleiroDefesa.setBounds(new Rectangle(2, 50, 291, 21));
			lblTabuleiroDefesa.setText("Tabuleiro Defesa:");
			pnlTabuleiros = new JPanel();
			pnlTabuleiros.setLayout(null);
			pnlTabuleiros.setBounds(new Rectangle(6, 134, 715, 417));
			pnlTabuleiros.setEnabled(false);
			//pnlTabuleiros.setVisible(false);
			pnlTabuleiros.setVisible(false);
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
			txtAreaServer.setText("Aqui voc� receber� as mensagens do servidor");
			txtAreaServer.setBackground(new Color(245, 244, 244));
			txtAreaServer.setEditable(false);
			txtAreaServer.setAutoscrolls(true);
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
					
					if(client.enviarTabuleiro()){
						habilitarBotaoPronto(false);
						habilitarTabuleiroDefesa(false);//Desabilita o tabuleiro de defesa
					}
				}
			});
		}
		return btnPronto;
	}
	
	public void carregarFrameTabuleiros(Jogo jogo){
		//this.add(getPnlTabuleiros(), null);
		
		imprimirMensagem("Servidor: Aguardando jogadores...");
		this.jogo = jogo;
	}


	protected void imprimirMensagem(String string) {
		this.txtAreaServer.append("\n"+string);		
	}

	protected void conectarJogadorEmJogo(Jogador jogador) {
		String nomeJogador = jogador.getLogin();
		//Se quem conectou fui eu, ent�o meu nome vai para o tabuleiro de defesa
		//sen�o o nome do jogador que conectou vai para o tabuleiro de ataque
		if(jogador.equals(this.client.getPerfil())){
			lblTabuleiroDefesa.setText(nomeJogador);
		}
		else {
			lblTabuleiroAtaque.setText(nomeJogador);
		}
		//Imprime na �rea de texto que o jogador X foi conectado ao servidor
		imprimirMensagem(String.format("%s entrou na sala do jogo", nomeJogador));
		
		//Se o jogo ainda n�o est� lotado (com todos os players necess�rios), aguarda na tela inicial para convidar outros
		if(jogo != null && !jogo.isLotado()){
			btnConvidar.setVisible(true);
		}
		else if (jogo == null){//Se o jogo n�o foi criado, mant�m o jogador na tela inicial
			btnConvidar.setVisible(false);
			exibirTelaInicial();
		}
	}

	private void exibirTabuleiros() {
		pnlTabuleiros.setVisible(true);
		pnlListas.setVisible(false);
	}

	private void exibirTelaInicial() {
		pnlTabuleiros.setVisible(false);
		pnlListas.setVisible(true);

		habilitarBotaoDesconectar(true);
		habilitarBotaoConectar(false);
		lblBemVindo.setText("Bem vindo, "+this.client.getPerfil().getLogin());
		AtualizarListas();
	}
	
	private void AtualizarListas() {
		this.client.receberListaJogos();
		this.client.receberListaJogadores();
		
	}

	private void PreencherTabuleiroDefesa(Tabuleiro tabuleiroDefesa) {
		//Pega o grid do tabuleiro e reajusta seu tamanho
		GridLayout gridLayout = (GridLayout)this.pnlTabDefesa.getLayout();
		int largura = tabuleiroDefesa.getMatrizCelula().length;
		int altura = tabuleiroDefesa.getMatrizCelula()[0].length;
		gridLayout.setRows(largura);
		gridLayout.setColumns(altura);
		//Preenche o grid com os bot�es
		botoesDefesa = new GridBotoes(largura, altura);
		for (int i = 0; i < botoesDefesa.getLargura(); i++) {
			for (int j = 0; j < botoesDefesa.getAltura(); j++) {
				//Cria o bot�o informando qual a posi��o ele ir� armazenar
				Celula celula = tabuleiroDefesa.getCelula(i,j);
				BotaoCelula botao = null;
				//Cria a c�lula independente de ter icone ou n�o
				if(celula.hasIcone()){
					botao = new BotaoCelula(celula.getIcone(),i, j);
				}
				else{
					botao = new BotaoCelula(i, j);	
				}
				botao.setSize(Constantes.LARGURA_CELULA, Constantes.ALTURA_CELULA);
				//Define a cor do bot�o
				botao.setBackground(getCorBotao(celula));
				//Define qual a c�lula daquele bot�o (ajuda posteriormente a recuperar a c�lula de maneira mais rapida)
				botao.setCelula(celula);
				//Adiciona o bot�o ao GRID para futuras referencias
				botoesDefesa.setBotao(botao, i, j);
				botao.addMouseListener(listenerDefesa);
				botao.addActionListener(listenerDefesa);
				//Adiciona o bot�o no tabuleiro
				//(Como o BotaoCelula extende o JButton, o painel o reconhece como bot�o
				this.pnlTabDefesa.add(botao);
			}
		}
		//repinta o tabuleiro para exibir os botoes
		this.pnlTabDefesa.repaint();
	}

	private void repintarTabuleiroDefesa(Tabuleiro tabuleiro) {
		//Repinta o tabuleiro em mem�ria
		tabuleiro.repintarTabuleiro();
		//Atualiza a pintura na tela
		for (int i = 0; i < botoesDefesa.getLargura(); i++) {
			for (int j = 0; j < botoesDefesa.getAltura(); j++) {
				//Pega o bot�o que foi escolhido
				BotaoCelula botao = botoesDefesa.getBotao(i, j);				
				Celula celulaTabuleiro = tabuleiro.getCelula(i, j);
				botao.setCelula(celulaTabuleiro);
				//Define a cor do bot�o se for barco
				botao.setBackground(getCorBotao(celulaTabuleiro));
				
				botao.repaint();
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
		//Preenche o grid com bot�es
		botoesAtaque = new GridBotoes(largura, altura);
		for (int i = 0; i < botoesAtaque.getLargura(); i++) {
			for (int j = 0; j < botoesAtaque.getAltura(); j++) {
				//Cria o bot�o informando qual a posi��o ele ir� armazenar
				Celula celula = tabuleiroAtaque.getCelula(i,j);
				BotaoCelula botao = null;
				//Cria a c�lula independente de ter icone ou n�o
				if(celula.hasIcone()){
					botao = new BotaoCelula(celula.getIcone(),i, j);
				}
				else{
					botao = new BotaoCelula(i, j);	
				}
				botao.setSize(32, 32);
				
				//Define o evento que vai disparar quando o bot�o for clicado
				botao.addActionListener(listenerAtaque);
				//Adiciona o bot�o no tabuleiro (como extende o JButton, ele sabe que este objeto � um bot�o)
				this.pnlTabuleiroAtaque.add(botao);
			}
		}
		//Repinta o tabuleiro para exibir os bot�es
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
		//Bot�o de pronto tem que ter o enabled igual o tabuleiro de defesa
		//Assim garantimos que o bot�o pronto ser� desativado e o posicionamento do tabuleiro
		//de defesa tamb�m ser�, n�o permitindo ao jogador alterar a posi��o
		//ap�s o inicio da partida
		this.btnPronto.setEnabled(b);
		habilitarTabuleiroDefesa(b);
	}
	


	protected void DesconectarJogador() {
		this.client.desconectar();
		//Desabilita os bot�es necess�rios da interface
		habilitarTabuleiroAtaque(false);
		habilitarTabuleiroDefesa(false);
		habilitarBotaoPronto(false);
		habilitarBotaoConectar(true);
		habilitarBotaoDesconectar(false);
		pnlListas.setVisible(true);
		pnlTabuleiros.setVisible(false);
		limparTabuleiros();
	}

	private void limparTabuleiros() {
		//TODO: Retirar todos os bot�es da UI
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
		//Pega o bot�o no tabuleiro de defesa
		BotaoCelula botao = getBotaoEmTabuleiroDefesa(x,y);
		//troca a cor do bot�o correspondente a celula atacada
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
				Log.gravarLog("Bot�o n�o encontrado");
			}
			
		}
		else{
			Log.gravarLog("Painel do tabuleiro n�o � do tipo GridLayout");
		}
		return null;
	}

	public boolean enviarAtaqueCliente(int coordX, int coordY) {
		return this.client.Atacar(coordX, coordY);		
	}
	
	public void exibirRespostaAtaque(Celula celula){
		//Executa o m�todo apenas se algum bot�o foi pressionado
		if(botaoPressionado == null){
			return;
		}
		
		Color cor = getCorBotao(celula);
		//Altera o fundo do bot�o pressionado no tabuleiro de ataque
		botaoPressionado.setBackground(cor);
		
		//marca como nulo porque o bot�o pressionado j� teve sua resposta
		botaoPressionado = null;		
	}

	private Color getCorBotao(Celula celula) {
		//M�todo encapsulado dentro da classe respons�vel pelos bot�es
		return GridBotoes.getCorBotao(celula);
	}
	
	//Classe interna que implementa os eventos ocorridos no bot�o de ataque
	class EventoAtaque implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//A premissa para poder fazer um ataque, � o jogador estar online e pronto
			if (client.getPerfil().isOnline() && !btnPronto.isEnabled() && e.getSource() instanceof BotaoCelula) {
				//Pega o bot�o pressionado e envia o ataque
				botaoPressionado = (BotaoCelula) e.getSource();
				if(botaoPressionado != null){
					//Envia o ataque e fica aguardando a resposta, que ativar� o m�todo "exibirRespostaAtaque()"
					if(enviarAtaqueCliente(botaoPressionado.getCoordX(), botaoPressionado.getCoordY())){
					//Desabilita o bot�o para n�o utiliz�-lo novamente
					botaoPressionado.setEnabled(false);
					}
				}
			}
		}
		
	}
	//Classe interna que implementa os eventos ocorridos no bot�es de defesa
	class EventoDefesa implements ActionListener, MouseListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//A premissa para poder transformar uma embarca��o (mover/rotacionar), � o jogador estar online e n�o clicar em "pronto"
			if (btnPronto.isEnabled() && e.getSource() instanceof BotaoCelula) {

				BotaoCelula botaoBarco = (BotaoCelula) e.getSource();
				
				Tabuleiro tabDef = client.getPerfil().getTabuleiroDefesa();
				
				//Se n�o tiver barco selecionado, seleciona o clicado.
				if(barcoTransformar == null){			
					//Se n�o for uma c�lula de embarca��o, cai fora do m�todo, pois n�o se deve selecionar uma �gua
					if(botaoBarco.getCelula().getTipoCelula() != TipoCelula.Embarcacao){
						return;
					}
					
					barcoTransformar = tabDef.getEmbarcacao(botaoBarco.getCoordX(), botaoBarco.getCoordY());
					
					//Se n�o encontrar o barco, � bug do jogo, ent�o sai do m�todo
					if(barcoTransformar == null)
						return;
					
					//marca as celulas como selecionadas
					barcoTransformar.setSelecionado(true);
					
					repintarTabuleiroDefesa(tabDef);					
				}
				else{
					int xMudar = botaoBarco.getCoordX();
					int yMudar = botaoBarco.getCoordY();				
					//marca o barco como n�o selecionado mais
					barcoTransformar.setSelecionado(false);

					if(tabDef.ValidarPosicaoEmbarcacao(barcoTransformar, xMudar, yMudar)){
						//Se validar a mudan�a, muda o barco de posi��o e repinta o tabuleiro
						barcoTransformar.setPosicao(xMudar, yMudar);
					}

					repintarTabuleiroDefesa(tabDef);
					botoesDefesa.repintarTodos();
					//libera a vari�vel para n�o ficar com um barco selecionado aqui
					barcoTransformar = null;
				}
			}		
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3 && barcoTransformar != null){
				//Pega o tabuleiro de defesa (para valida��o)
				Tabuleiro tabDef = client.getPerfil().getTabuleiroDefesa();
				//Pega a c�lula de origem do barco (a 1� celula)
				Celula origem = barcoTransformar.getListaCelulas()[0];
				//Desseleciona o barco para n�o exibir na interface ele "marcado" ap�s a edi��o
				barcoTransformar.setSelecionado(false);
				//Valida a posi��o do barco para atribuir a nova orienta��o
				if(tabDef.ValidarRotacaoEmbarcacao(barcoTransformar)){
					//Se for v�lida rotaciona o barco
					barcoTransformar.rotacionar();
				}
				//Se a rota��o for v�lida, repinta o tabuleiro
				repintarTabuleiroDefesa(tabDef);
				botoesDefesa.repintarTodos();
				
				barcoTransformar = null;
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	//Eventos que acontecem na classe Cliente e s�o ouvidos pela GUI (esta classe)
	class EventoCliente implements ClientEventListener{
			
			@Override
			public void turnoAlterado(Object source, ClientEvent evt) {
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
					String mensagemBarco = "%s a embarca��o %s do advers�rio!";
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
				
				//se o jogador desconectado � o pr�prio jogador
				if(jogador == client.getPerfil()){
					//Esconde as telas que devem ser vistas apenas quando online
					pnlListas.setVisible(false);
					pnlTabuleiros.setVisible(false);
					habilitarBotaoConectar(true);
					habilitarBotaoDesconectar(false);
				}
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
				conectarJogadorEmJogo(jogador);
			}
			
			@Override
			public void fimDeJogo(Object source, ClientEvent evt) {
				Jogador vencedor = (Jogador)source;
				String mensagemVencedor = String.format("%s venceu este jogo!",vencedor.getLogin());
				imprimirMensagem(mensagemVencedor);
				JOptionPane.showMessageDialog(null, mensagemVencedor, "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
				//Finaliza a variavel do jogo
				jogo = null;
				//Retira o ID do jogo do perfil do jogador
				client.getPerfil().setJogoId(0);
				//Exibe a tela principal para outro jogo poder ser iniciado
				exibirTelaInicial();
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
					exibirTabuleiros();
					habilitarTabuleirosParaPosicionamento();
					PreencherTabuleiroDefesa(client.getPerfil().getTabuleiroDefesa());
					PreencherTabuleiroAtaque(client.getPerfil().getTabuleiroAtaque());
				}
				catch(Exception ex){
					DesconectarJogador();
				}
			}
			
			@Override
			public void barcosOponentePosicionados(Object src) {
				imprimirMensagem("Oponente posicionou os tabuleiros e est� pronto para jogar");				
			}
			
			@Override
			public void ativarBot(Object src, Jogador bot) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void listaJogadoresRecebida(Object lista) {
				List<String> jogadores = (List<String>)lista;
				listModelPlayers.clear();
				for (String jogador : jogadores) {
					listModelPlayers.addElement(jogador.trim());
				}
				
				lstJogadores.setModel(listModelPlayers);	
				lstJogadores.repaint();
			}

			@Override
			public void listaJogosRecebida(Object lista) {
				List<String> jogos = (List<String>)lista;
				listModelJogos.clear();
				for (String jogo : jogos) {
					listModelJogos.addElement(jogo.trim());
				}
				
				lstJogos.setModel(listModelJogos);
				lstJogos.repaint();
			}

			@Override
			public void respostaConviteParaJogar(String nome, Object resposta) {
				String strResposta = resposta.toString();
				if(strResposta.equalsIgnoreCase(Constantes.RESPOSTA_POSITIVA)){
					//Inicia o jogo
				}
				else if(strResposta.equalsIgnoreCase(Constantes.RESPOSTA_NEGATIVA)){
					JOptionPane.showMessageDialog(null, String.format("%s n�o aceitou seu convite", nome));
				}
				else {
					JOptionPane.showMessageDialog(null, String.format("%s est� indispon�vel para receber o convite",nome));
				}
			}

			@Override
			public void receberConviteParaJogar(String nomeJogador, int jogoid) {
				String mensagem = String.format("Jogador %s est� chamando voc� para jogar com ele na sala Jogo%s, voc� aceita?", jogoid, nomeJogador);
				int resposta = JOptionPane.showConfirmDialog(null, mensagem, "Convite para jogar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resposta == JOptionPane.YES_OPTION){
					client.enviarRespostaPositiva(jogoid, nomeJogador);
					if(!client.conectarEmJogo(jogoid)){
						imprimirMensagem("Houve um erro ao conect�-lo no jogo que foi convidado");
					}
				}
				else{
					client.enviarRespostaNegativa(jogoid, nomeJogador);
				}
				
			}

			@Override
			public void carregarTelaPrincipal() {
				exibirTelaInicial();				
			}

			@Override
			public void jogoCriado(Jogo jogoObject) {
				btnConvidar.setVisible(true);
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
			jScrollPane.setAutoscrolls(true);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes pnlListas	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlListas() {
		if (pnlListas == null) {
			lblTextoJogosOnline = new JLabel();
			lblTextoJogosOnline.setBounds(new Rectangle(345, 15, 148, 19));
			lblTextoJogosOnline.setText("Jogos criados:");
			lblTextoJogadores = new JLabel();
			lblTextoJogadores.setBounds(new Rectangle(15, 15, 188, 20));
			lblTextoJogadores.setText("Jogadores online:");
			pnlListas = new JPanel();
			pnlListas.setLayout(null);
			pnlListas.setSize(new Dimension(603, 351));
			pnlListas.setLocation(new Point(10, 130));
			pnlListas.setVisible(false);
			pnlListas.add(getLstJogadores(), null);
			pnlListas.add(lblTextoJogadores, null);
			pnlListas.add(getLstJogos(), null);
			pnlListas.add(lblTextoJogosOnline, null);
			pnlListas.add(getBtnConvidar(), null);
			pnlListas.add(getBtnCriarJogo(), null);
			pnlListas.add(getBtnEntrarJogo(), null);
		}
		return pnlListas;
	}

	/**
	 * This method initializes lstJogadores	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstJogadores() {
		if (lstJogadores == null) {
			lstJogadores = new JList();
			lstJogadores.setBounds(new Rectangle(15, 45, 186, 241));
			lstJogadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lstJogadores.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent event) {
					if(event.getFirstIndex() >= 0){
						btnConvidar.setEnabled(true);
					}
					else{
						btnConvidar.setEnabled(false);
					}
				}
			});
		}
		return lstJogadores;
	}

	/**
	 * This method initializes lstJogos	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getLstJogos() {
		if (lstJogos == null) {
			lstJogos = new JList();
			lstJogos.setBounds(new Rectangle(345, 45, 166, 241));
			lstJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lstJogos.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent event) {
					if(event.getFirstIndex() >= 0){
						btnEntrarJogo.setEnabled(true);
					}
					else{
						btnEntrarJogo.setEnabled(false);
					}
				}
			});
		}
		return lstJogos;
	}

	/**
	 * This method initializes btnConvidar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnConvidar() {
		if (btnConvidar == null) {
			btnConvidar = new JButton();
			btnConvidar.setBounds(new Rectangle(15, 285, 91, 31));
			btnConvidar.setEnabled(false);
			btnConvidar.setText("Convidar");
			btnConvidar.setVisible(false);
		}
		return btnConvidar;
	}

	/**
	 * This method initializes btnCriarJogo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnCriarJogo() {
		if (btnCriarJogo == null) {
			btnCriarJogo = new JButton();
			btnCriarJogo.setBounds(new Rectangle(345, 285, 77, 31));
			btnCriarJogo.setText("Novo");
			btnCriarJogo.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					CriarNovoJogo();
				}
			});
		}
		return btnCriarJogo;
	}

	protected void CriarNovoJogo() {
		if(!this.client.criarNovoJogo()){
			imprimirMensagem("N�o foi poss�vel criar um novo jogo");
		}
	}

	/**
	 * This method initializes btnEntrarJogo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnEntrarJogo() {
		if (btnEntrarJogo == null) {
			btnEntrarJogo = new JButton();
			btnEntrarJogo.setBounds(new Rectangle(432, 285, 79, 31));
			btnEntrarJogo.setEnabled(false);
			btnEntrarJogo.setText("Entrar");
			btnEntrarJogo.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					//N�o permite que o usu�rio entre em um jogo sem selecion�-lo antes
					if(lstJogos.getSelectedIndex() < 0){
						btnEntrarJogo.setEnabled(false);
						return;
					}
					String nomeJogo = lstJogos.getSelectedValue().toString();
					String strIdJogo = nomeJogo.substring(4,nomeJogo.indexOf(" "));//pega a string de ID do jogo com base no padr�o "jogo##" (# = numero inteiro)
					int jogoId = Integer.parseInt(strIdJogo);
					if(client.conectarEmJogo(jogoId)){
						btnEntrarJogo.setEnabled(false);
					}
				}
			});
		}
		return btnEntrarJogo;
	}
}  //  @jve:decl-index=0:visual-constraint="9,17"
