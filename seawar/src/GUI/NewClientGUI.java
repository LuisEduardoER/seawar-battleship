package GUI;

import guiComponentes.BotaoCelula;
import guiComponentes.GridBotoes;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
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
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import java.awt.event.KeyEvent;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

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
	private JScrollPane scrollPaneServerMsgs = null;
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
	private JLabel lblTurnoAtacar = null;
	private JPanel pnlLogin = null;
	private JButton btnLogar = null;
	private JTextField txtLogin = null;
	private JLabel lblLogin = null;
	private JLabel lblSenha = null;
	private JPasswordField txtSenha = null;
	private JButton btnDesistir = null;
	private JPanel pnlDisplayBarcos = null;
	private JPanel pnlDisplayBarcosDefAcertados = null;
	private JPanel pnlDisplayBarcosAdvAcertados = null;
	Dictionary<String, JPanel[]> dicMarcadoresJogador;
	Dictionary<String, JPanel[]> dicMarcadoresAdversario;
	private String[] marcadoresAdv;
	private String[] marcadoresJogador;
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
		lblBemVindo.setBounds(new Rectangle(340, 13, 381, 28));
		lblBemVindo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBemVindo.setText("");
		this.setLayout(null);
		this.setSize(733, 786);
		this.add(getBtnDesconectar(), null);
		this.add(getPnlTabuleiros(), null);
		//CarregarFrameTabuleiros();//TODO: Excluir no final :)
		this.add(getScrollPaneServerMsgs(), null);
		this.add(getPnlListas(), null);
		this.add(lblBemVindo, null);
		this.add(getPnlLogin(), null);
		
		
		
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
			pnlTabuleiroAtaque.setBounds(new Rectangle(376, 72, 310, 241));
			pnlTabuleiroAtaque.setLayout(gridLayout);
			//Corrige o tamanho do tabuleiro
			pnlTabuleiroAtaque.setSize(Constantes.TAMANHO_TABULEIRO * Constantes.LARGURA_CELULA, Constantes.TAMANHO_TABULEIRO * Constantes.ALTURA_CELULA);
		}
		return pnlTabuleiroAtaque;
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
			lblTurnoAtacar = new JLabel();
			lblTurnoAtacar.setBounds(new Rectangle(395, 24, 223, 26));
			lblTurnoAtacar.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			lblTurnoAtacar.setForeground(new Color(0, 170, 0));
			lblTurnoAtacar.setText("Atacar");
			lblTurnoAtacar.setVisible(false);
			lblTabuleiroAtaque = new JLabel();
			lblTabuleiroAtaque.setBounds(new Rectangle(354, 50, 311, 22));
			lblTabuleiroAtaque.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			lblTabuleiroAtaque.setText("Tabuleiro Ataque:");
			lblTabuleiroDefesa = new JLabel();
			lblTabuleiroDefesa.setBounds(new Rectangle(2, 50, 291, 21));
			lblTabuleiroDefesa.setText("Tabuleiro Defesa:");
			pnlTabuleiros = new JPanel();
			pnlTabuleiros.setLayout(null);
			pnlTabuleiros.setBounds(new Rectangle(6, 134, 715, 547));
			pnlTabuleiros.setEnabled(false);
			//pnlTabuleiros.setVisible(false);
			pnlTabuleiros.setVisible(false);
			pnlTabuleiros.add(getPnlTabDefesa(), null);
			pnlTabuleiros.add(getPnlTabuleiroAtaque(), null);
			pnlTabuleiros.add(lblTabuleiroDefesa, null);
			pnlTabuleiros.add(lblTabuleiroAtaque, null);
			pnlTabuleiros.add(getBtnPronto(), null);
			pnlTabuleiros.add(lblTurnoAtacar, null);
			pnlTabuleiros.add(getBtnDesistir(), null);
			pnlTabuleiros.add(getPnlDisplayBarcos(), null);
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
			txtAreaServer.setSize(830, 80);
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
					if(client.getPerfil().isMinhaVez()){
						setLabelTurnoAtacar(true);
					}
					else
					{
						setLabelTurnoAtacar(false);
					}
					
					bindDosProgress();
					
					lblTurnoAtacar.setVisible(true);
				}
			});
		}
		return btnPronto;
	}
	
	protected void bindDosProgress() {
		
		for(int i = 0; i < Constantes.NUMERO_EMBARCACOES; i++){
			JLabel lblNomeBarco = new JLabel("");
			FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
			JPanel pnlFlow = new JPanel(flowLayout);	
			pnlFlow.add(lblNomeBarco);
			pnlDisplayBarcosAdvAcertados.add(pnlFlow);		
		}
		
		for(int i = 0; i < Constantes.NUMERO_EMBARCACOES; i++){
			JLabel lblNomeBarco = new JLabel("");
			FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
			JPanel pnlFlow = new JPanel(flowLayout);	
			pnlFlow.add(lblNomeBarco);
			pnlDisplayBarcosDefAcertados.add(pnlFlow);		
		}
	}

	protected void setLabelTurnoAtacar(boolean meuTurno) {

		if(meuTurno){
			lblTurnoAtacar.setText("Atacar");
			lblTurnoAtacar.setForeground(new Color(0, 170,0));
		}
		else{
			lblTurnoAtacar.setText("Aguardar ataque");
			lblTurnoAtacar.setForeground(new Color(255, 0,0));			
		}
		//Mant�m a flag de turno do jogador atualizada para saber se � a vez dele de jogar
		//this.client.getPerfil().setMinhaVez(meuTurno);
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
		lblTurnoAtacar.setVisible(false);
		btnDesistir.setEnabled(true);
		
		pnlTabuleiros.repaint();
		//Rectangle areaTabuleiros = pnlTabuleiros.getBounds();
		//pnlTabuleiros.repaint(areaTabuleiros);
		pnlTabuleiros.revalidate();
	}

	private void exibirTelaInicial() {
		pnlTabuleiros.setVisible(false);
		pnlLogin.setVisible(false);
		pnlListas.setVisible(true);

		habilitarBotaoDesconectar(true);
		mostrarPainelLogin(false);
		lblBemVindo.setText("Bem vindo, "+this.client.getPerfil().getLogin());
		AtualizarListas();
		btnConvidar.setVisible(lstJogadores.getSelectedIndex() < 0 && this.jogo != null);
		btnCriarJogo.setEnabled(true);
		
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
		
		marcadoresJogador = new String[tabuleiroDefesa.arrEmbarcacoes.length];
//		for (int i = 0; i < tabuleiroDefesa.arrEmbarcacoes.length; i++) {
//			Embarcacao barco = tabuleiroDefesa.arrEmbarcacoes[i];
//			if(barco != null){
//				marcadoresJogador[i] = barco.getNomeEmbarcacao();
//			}
//			                                                  
//		}
		
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
				
				//Define uma imagem para o bot�o
				Icon icone = celulaTabuleiro.getIcone();
				if(icone != null)
					botao.setIcon(icone);
				
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
		marcadoresAdv = new String[tabuleiroAtaque.arrEmbarcacoes.length];
		//Repinta o tabuleiro para exibir os bot�es
		this.pnlTabuleiroAtaque.repaint();
	}

	private void habilitarTabuleirosParaPosicionamento() {
		habilitarTabuleiroAtaque(false);		
		habilitarTabuleiroDefesa(true);	
		habilitarBotaoPronto(true);
	}
	protected void habilitarBotaoPronto(boolean b) {
		//Bot�o de pronto tem que ter o enabled igual o tabuleiro de defesa
		//Assim garantimos que o bot�o pronto ser� desativado e o posicionamento do tabuleiro
		//de defesa tamb�m ser�, n�o permitindo ao jogador alterar a posi��o
		//ap�s o inicio da partida
		this.btnPronto.setEnabled(b);
		this.btnDesistir.setEnabled(!b);
		habilitarTabuleiroDefesa(b);
	}
	


	protected void DesconectarJogador() {
		//desconecta o client e limpa as variaveis de jogo
		this.client.desconectar();
		this.client.limparJogo();
		this.jogo = null;
		//Desabilita os bot�es necess�rios da interface
		habilitarTabuleiroAtaque(false);
		habilitarTabuleiroDefesa(false);
		habilitarBotaoPronto(false);
		mostrarPainelLogin(true);
		habilitarBotaoDesconectar(false);
		//Esconde os tabuleiros e tudo o que precisa
		pnlListas.setVisible(false);
		pnlTabuleiros.setVisible(false);
		limparTabuleiros();
	}

	private void limparTabuleiros() {
		pnlTabDefesa.removeAll();
		pnlTabuleiroAtaque.removeAll();
		pnlDisplayBarcosAdvAcertados.removeAll();
		pnlDisplayBarcosDefAcertados.removeAll();
	}

	private void habilitarBotaoDesconectar(boolean b) {
		this.btnDesconectar.setEnabled(b);
		
	}

	private void mostrarPainelLogin(boolean b) {
			pnlLogin.setVisible(b);		
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
			if (client.getPerfil().isOnline() && !btnPronto.isEnabled() && client.getPerfil().isMinhaVez() && e.getSource() instanceof BotaoCelula) {
				//Pega o bot�o pressionado e envia o ataque
				botaoPressionado = (BotaoCelula) e.getSource();
				if(botaoPressionado != null){
					 setLabelTurnoAtacar(false);
					//Envia o ataque e fica aguardando a resposta, que ativar� o m�todo "exibirRespostaAtaque()"
					if(enviarAtaqueCliente(botaoPressionado.getCoordX(), botaoPressionado.getCoordY())){
					//Desabilita o bot�o para n�o utiliz�-lo novamente
						try{
						 botaoPressionado.setEnabled(false);
						}
						catch(Exception ex){
							
						}
					}
//					else{
//						 setLabelTurnoAtacar(true);
//					}					
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
				setLabelTurnoAtacar(Boolean.parseBoolean(source.toString()));
			}
			
			@Override
			public void tiroRecebido(Object source, ClientEvent evt, Celula celula) {
				if(source instanceof Embarcacao){
					Embarcacao barco = (Embarcacao)source;
					adicionarMarcadorBarco(barco);
				}
				exibirAtaqueRecebido(celula);	
			}
			
			@Override
			public void respostaTiro(Object source, ClientEvent evt, Celula celula) {				
				if(source instanceof Embarcacao){
					Embarcacao barco = (Embarcacao)source;
					String nomeBarco = barco.getNomeEmbarcacao();
					boolean afundou = barco.getNaufragado();
					
					
					//setLabelTurnoAtacar(false);
					
					//Se o cliente acertou um barco, informa qual barco foi
					if(nomeBarco != null && !nomeBarco.isEmpty()){
						adicionarMarcadorBarcoAdversario(barco);
						String mensagemBarco = "%s a embarca��o %s do advers�rio!";
						mensagemBarco = String.format(mensagemBarco, (afundou? "Afundou":"Acertou"), nomeBarco);
						exibeMensagem(mensagemBarco);
					}
				}
				exibirRespostaAtaque(celula);
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
					mostrarPainelLogin(true);
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
				client.limparJogo();
				//Retira o ID do jogo do perfil do jogador
				client.getPerfil().setJogoId(0);
				//Exibe a tela principal para outro jogo poder ser iniciado
				exibirTelaInicial();
				limparTabuleiros();
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
					mostrarPainelLogin(false);		
					habilitarTabuleirosParaPosicionamento();
					PreencherTabuleiroDefesa(client.getPerfil().getTabuleiroDefesa());
					PreencherTabuleiroAtaque(client.getPerfil().getTabuleiroAtaque());
					exibirTabuleiros();
	
					
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
					//exibirTabuleiros();
					//Quando a resposta � positiva, o advers�rio j� faz um "conectar em jogo" automaticamente
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
				String mensagem = String.format("Jogador %s est� chamando voc� para jogar com ele na sala Jogo%s, voc� aceita?", nomeJogador, jogoid);
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
				btnCriarJogo.setEnabled(false);
				btnEntrarJogo.setEnabled(false);
			}
	}
	
	@Override
	public void destroy() {
		this.DesconectarJogador();
		super.destroy();
	}

	public void adicionarMarcadorBarco(Embarcacao barco) {
		int i = barco.getTamanho()-1;
		JPanel pnlAdicionar = new JPanel();
		pnlAdicionar.setBackground(Color.GREEN);
		pnlAdicionar.setSize(5, 5);
		
		//Se ainda n�o foi marcado o barco do jogador, exibe o nome dele na tela antes de adicionar o painel verde
		if(marcadoresJogador[i] == null){
			JPanel flowLayout = (JPanel) pnlDisplayBarcosDefAcertados.getComponent(i);
			JLabel lblNomeBarco = (JLabel)flowLayout.getComponent(0);
			lblNomeBarco.setText(barco.getNomeEmbarcacao());
			flowLayout.add(pnlAdicionar);
			//adiciona a marca��o para indicar que o marcador foi adicionado com sucesso
			marcadoresJogador[i] = barco.getNomeEmbarcacao();			
		}

		//Adiciona o painel verde para indicar a marca��o de hit do barco
		JPanel flowLayout = (JPanel) pnlDisplayBarcosDefAcertados.getComponent(i);
		flowLayout.add(pnlAdicionar);
		
		repintarAfundado(barco, flowLayout);
	}
	
	public void adicionarMarcadorBarcoAdversario(Embarcacao barco) {
		int i = barco.getTamanho()-1;
		JPanel pnlAdicionar = new JPanel();
		pnlAdicionar.setBackground(Color.GREEN);
		pnlAdicionar.setSize(5, 5);

		//Se ainda n�o foi marcado o barco do jogador, exibe o nome dele na tela antes de adicionar o painel verde
		if(marcadoresAdv[i] == null){
			JPanel flowLayout = (JPanel) pnlDisplayBarcosAdvAcertados.getComponent(i);
			JLabel lblNomeBarco = (JLabel)flowLayout.getComponent(0);
			lblNomeBarco.setText(barco.getNomeEmbarcacao());
			flowLayout.add(pnlAdicionar);
			//adiciona a marca��o para indicar que o marcador foi adicionado com sucesso
			marcadoresAdv[i] = barco.getNomeEmbarcacao();			
		}

		//Adiciona o painel verde para indicar a marca��o de hit do barco
		JPanel flowLayout = (JPanel) pnlDisplayBarcosAdvAcertados.getComponent(i);
		flowLayout.add(pnlAdicionar);
		
		repintarAfundado(barco, flowLayout);
	}

	private void repintarAfundado(Embarcacao barco, JPanel flowLayout) {
		if(barco.getNaufragado()){
			Component[] paineis = flowLayout.getComponents();
			for (int j = 0; j < flowLayout.getComponentCount(); j++) {
				Component painel = paineis[j];
				if(painel instanceof JPanel){
					painel.setBackground(Color.RED);
				}	
			}			
		}
	}

	/**
	 * This method initializes scrollPaneServerMsgs	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getScrollPaneServerMsgs() {
		if (scrollPaneServerMsgs == null) {
			scrollPaneServerMsgs = new JScrollPane();
			scrollPaneServerMsgs.setBounds(new Rectangle(7, 44, 714, 80));
			scrollPaneServerMsgs.setSize(710, 80);
			scrollPaneServerMsgs.setViewportView(getTxtAreaServer());
			scrollPaneServerMsgs.setAutoscrolls(true);
		}
		return scrollPaneServerMsgs;
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
			btnConvidar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(lstJogadores.getSelectedIndex() >= 0){
						String nomeJogador = lstJogadores.getSelectedValue().toString();
						convidarJogador(nomeJogador);
					}
				}
			});
		}
		return btnConvidar;
	}

	protected void convidarJogador(String nomeJogador) {
		this.client.chamarJogador(nomeJogador);
		
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

	/**
	 * This method initializes pnlLogin	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlLogin() {
		if (pnlLogin == null) {
			lblSenha = new JLabel();
			lblSenha.setBounds(new Rectangle(15, 75, 46, 31));
			lblSenha.setText("Senha:");
			lblLogin = new JLabel();
			lblLogin.setBounds(new Rectangle(15, 30, 46, 31));
			lblLogin.setText("Login:");
			pnlLogin = new JPanel();
			pnlLogin.setLayout(null);
			int painelHeight = 326;
			int painelWidth = 196;
			pnlLogin.setBounds(new Rectangle(200,200, painelHeight, painelWidth));
			pnlLogin.add(getBtnLogar(), null);
			pnlLogin.add(getTxtLogin(), null);
			pnlLogin.add(lblLogin, null);
			pnlLogin.add(lblSenha, null);
			pnlLogin.add(getTxtSenha(), null);
		}
		return pnlLogin;
	}

	/**
	 * This method initializes btnLogar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnLogar() {
		if (btnLogar == null) {
			btnLogar = new JButton();
			btnLogar.setBounds(new Rectangle(120, 135, 76, 29));
			btnLogar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fazerLogin();
				}
			});
			btnLogar.setText("Logar");
		}
		return btnLogar;
	}

	/**
	 * This method initializes txtLogin	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTxtLogin() {
		if (txtLogin == null) {
			txtLogin = new JTextField();
			txtLogin.setBounds(new Rectangle(60, 30, 241, 31));
			txtLogin.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
					
				}
				
				@Override
				public void keyPressed(KeyEvent tecla) {
					if(tecla.getKeyCode() == KeyEvent.VK_TAB || tecla.getKeyCode() == KeyEvent.VK_ENTER)
						txtSenha.requestFocus();					
				}
			});
		}
		return txtLogin;
	}

	/**
	 * This method initializes txtSenha	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getTxtSenha() {
		if (txtSenha == null) {
			txtSenha = new JPasswordField();
			txtSenha.setBounds(new Rectangle(60, 75, 241, 31));
			txtSenha.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent tecla) {
					if(tecla.getKeyCode() == KeyEvent.VK_TAB)
						btnLogar.requestFocus();			
					else if(tecla.getKeyCode() == KeyEvent.VK_ENTER)
						fazerLogin();
				}
			});
		}
		return txtSenha;
	}

	/**
	 * This method initializes btnDesistir	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnDesistir() {
		if (btnDesistir == null) {
			btnDesistir = new JButton();
			btnDesistir.setBounds(new Rectangle(190, 25, 80, 20));
			btnDesistir.setBackground(new Color(255, 200,200));
			btnDesistir.setText("Desistir");
			btnDesistir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					//Verifica se o usu�rio est� em um jogo e se o bot�o de pronto n�o est� mais habilitado (j� foi pressionado)
					if(client.getPerfil().getJogoId() > 0 && !btnPronto.isEnabled()){
						int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente desistir da partida?","Confirmar desist�ncia", JOptionPane.YES_NO_OPTION);
						
						if(resposta == JOptionPane.YES_OPTION){
							client.desistirJogo();
							exibirTelaInicial();
							limparTabuleiros();
						}
					}
					else{
						btnDesistir.setEnabled(false);
					}
				}
			});
		}
		return btnDesistir;
	}

	private void fazerLogin() {
		String login = txtLogin.getText();
		if(login !=null && login.isEmpty()){
			return;
		}
		char[] senhaChar =txtSenha.getPassword();
		
		String senha = "";
		for (int i = 0; i < senhaChar.length; i++) {
			senha += senhaChar[i];
		}
		//Se o usu�rio n�o fornece senha e login, n�o conecta-o ao jogo
		if(login == null || senha == null || login.isEmpty() || senha.isEmpty()){
			imprimirMensagem("Login ou senha n�o fornecidos");
			return;
		}
		//Se instanciar corretamente o objeto do cliente, conecta-o com o login e a senha fornecidos
		if(InstanciarCliente()){
			client.conectar(login,senha);
			txtSenha.setText("");
		}
		else{
			imprimirMensagem("Erro ao tentar se conectar, socket falhou ao ser instanciado");
		}
		//Quando conectado, o cliente disparar� um evento de conectarJogador
	}

	/**
	 * This method initializes pnlDisplayBarcos	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlDisplayBarcos() {
		if (pnlDisplayBarcos == null) {
			pnlDisplayBarcos = new JPanel();
			pnlDisplayBarcos.setLayout(null);
			pnlDisplayBarcos.setBounds(new Rectangle(9, 385, 661, 121));
			pnlDisplayBarcos.add(getPnlDisplayBarcosDefAcertados(), null);
			pnlDisplayBarcos.add(getPnlDisplayBarcosAdvAcertados(), null);
		}
		return pnlDisplayBarcos;
	}

	/**
	 * This method initializes pnlDisplayBarcosDefAcertados	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlDisplayBarcosDefAcertados() {
		if (pnlDisplayBarcosDefAcertados == null) {
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(5);
			gridLayout2.setColumns(0);
			pnlDisplayBarcosDefAcertados = new JPanel();
			pnlDisplayBarcosDefAcertados.setLayout(gridLayout2);
			pnlDisplayBarcosDefAcertados.setBounds(new Rectangle(15, 0, 211, 121));
		}
		return pnlDisplayBarcosDefAcertados;
	}

	/**
	 * This method initializes pnlDisplayBarcosAdvAcertados	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlDisplayBarcosAdvAcertados() {
		if (pnlDisplayBarcosAdvAcertados == null) {
			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setRows(5);
			gridLayout3.setColumns(0);
			pnlDisplayBarcosAdvAcertados = new JPanel();
			pnlDisplayBarcosAdvAcertados.setLayout(gridLayout3);
			pnlDisplayBarcosAdvAcertados.setBounds(new Rectangle(420, 0, 226, 121));
		}
		return pnlDisplayBarcosAdvAcertados;
	}
}  //  @jve:decl-index=0:visual-constraint="9,17"
