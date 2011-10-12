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
	//Variaveis necessárias para funcionamento background
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
			txtAreaServer.setText("Aqui você receberá as mensagens do servidor");
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
		//Se quem conectou fui eu, então meu nome vai para o tabuleiro de defesa
		//senão o nome do jogador que conectou vai para o tabuleiro de ataque
		if(jogador.equals(this.client.getPerfil())){
			lblTabuleiroDefesa.setText(nomeJogador);
		}
		else {
			lblTabuleiroAtaque.setText(nomeJogador);
		}
		//Imprime na área de texto que o jogador X foi conectado ao servidor
		imprimirMensagem(String.format("%s entrou na sala do jogo", nomeJogador));
		
		//Se o jogo ainda não está lotado (com todos os players necessários), aguarda na tela inicial para convidar outros
		if(jogo != null && !jogo.isLotado()){
			btnConvidar.setVisible(true);
		}
		else if (jogo == null){//Se o jogo não foi criado, mantém o jogador na tela inicial
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
				botao.setSize(Constantes.LARGURA_CELULA, Constantes.ALTURA_CELULA);
				//Define a cor do botão
				botao.setBackground(getCorBotao(celula));
				//Define qual a célula daquele botão (ajuda posteriormente a recuperar a célula de maneira mais rapida)
				botao.setCelula(celula);
				//Adiciona o botão ao GRID para futuras referencias
				botoesDefesa.setBotao(botao, i, j);
				botao.addMouseListener(listenerDefesa);
				botao.addActionListener(listenerDefesa);
				//Adiciona o botão no tabuleiro
				//(Como o BotaoCelula extende o JButton, o painel o reconhece como botão
				this.pnlTabDefesa.add(botao);
			}
		}
		//repinta o tabuleiro para exibir os botoes
		this.pnlTabDefesa.repaint();
	}

	private void repintarTabuleiroDefesa(Tabuleiro tabuleiro) {
		//Repinta o tabuleiro em memória
		tabuleiro.repintarTabuleiro();
		//Atualiza a pintura na tela
		for (int i = 0; i < botoesDefesa.getLargura(); i++) {
			for (int j = 0; j < botoesDefesa.getAltura(); j++) {
				//Pega o botão que foi escolhido
				BotaoCelula botao = botoesDefesa.getBotao(i, j);				
				Celula celulaTabuleiro = tabuleiro.getCelula(i, j);
				botao.setCelula(celulaTabuleiro);
				//Define a cor do botão se for barco
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
		pnlListas.setVisible(true);
		pnlTabuleiros.setVisible(false);
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
		//Método encapsulado dentro da classe responsável pelos botões
		return GridBotoes.getCorBotao(celula);
	}
	
	//Classe interna que implementa os eventos ocorridos no botão de ataque
	class EventoAtaque implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//A premissa para poder fazer um ataque, é o jogador estar online e pronto
			if (client.getPerfil().isOnline() && !btnPronto.isEnabled() && e.getSource() instanceof BotaoCelula) {
				//Pega o botão pressionado e envia o ataque
				botaoPressionado = (BotaoCelula) e.getSource();
				if(botaoPressionado != null){
					//Envia o ataque e fica aguardando a resposta, que ativará o método "exibirRespostaAtaque()"
					if(enviarAtaqueCliente(botaoPressionado.getCoordX(), botaoPressionado.getCoordY())){
					//Desabilita o botão para não utilizá-lo novamente
					botaoPressionado.setEnabled(false);
					}
				}
			}
		}
		
	}
	//Classe interna que implementa os eventos ocorridos no botões de defesa
	class EventoDefesa implements ActionListener, MouseListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//A premissa para poder transformar uma embarcação (mover/rotacionar), é o jogador estar online e não clicar em "pronto"
			if (btnPronto.isEnabled() && e.getSource() instanceof BotaoCelula) {

				BotaoCelula botaoBarco = (BotaoCelula) e.getSource();
				
				Tabuleiro tabDef = client.getPerfil().getTabuleiroDefesa();
				
				//Se não tiver barco selecionado, seleciona o clicado.
				if(barcoTransformar == null){			
					//Se não for uma célula de embarcação, cai fora do método, pois não se deve selecionar uma água
					if(botaoBarco.getCelula().getTipoCelula() != TipoCelula.Embarcacao){
						return;
					}
					
					barcoTransformar = tabDef.getEmbarcacao(botaoBarco.getCoordX(), botaoBarco.getCoordY());
					
					//Se não encontrar o barco, é bug do jogo, então sai do método
					if(barcoTransformar == null)
						return;
					
					//marca as celulas como selecionadas
					barcoTransformar.setSelecionado(true);
					
					repintarTabuleiroDefesa(tabDef);					
				}
				else{
					int xMudar = botaoBarco.getCoordX();
					int yMudar = botaoBarco.getCoordY();				
					//marca o barco como não selecionado mais
					barcoTransformar.setSelecionado(false);

					if(tabDef.ValidarPosicaoEmbarcacao(barcoTransformar, xMudar, yMudar)){
						//Se validar a mudança, muda o barco de posição e repinta o tabuleiro
						barcoTransformar.setPosicao(xMudar, yMudar);
					}

					repintarTabuleiroDefesa(tabDef);
					botoesDefesa.repintarTodos();
					//libera a variável para não ficar com um barco selecionado aqui
					barcoTransformar = null;
				}
			}		
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3 && barcoTransformar != null){
				//Pega o tabuleiro de defesa (para validação)
				Tabuleiro tabDef = client.getPerfil().getTabuleiroDefesa();
				//Pega a célula de origem do barco (a 1ª celula)
				Celula origem = barcoTransformar.getListaCelulas()[0];
				//Desseleciona o barco para não exibir na interface ele "marcado" após a edição
				barcoTransformar.setSelecionado(false);
				//Valida a posição do barco para atribuir a nova orientação
				if(tabDef.ValidarRotacaoEmbarcacao(barcoTransformar)){
					//Se for válida rotaciona o barco
					barcoTransformar.rotacionar();
				}
				//Se a rotação for válida, repinta o tabuleiro
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
	
	//Eventos que acontecem na classe Cliente e são ouvidos pela GUI (esta classe)
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
				
				//se o jogador desconectado é o próprio jogador
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
				imprimirMensagem("Oponente posicionou os tabuleiros e está pronto para jogar");				
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
					JOptionPane.showMessageDialog(null, String.format("%s não aceitou seu convite", nome));
				}
				else {
					JOptionPane.showMessageDialog(null, String.format("%s está indisponível para receber o convite",nome));
				}
			}

			@Override
			public void receberConviteParaJogar(String nomeJogador, int jogoid) {
				String mensagem = String.format("Jogador %s está chamando você para jogar com ele na sala Jogo%s, você aceita?", jogoid, nomeJogador);
				int resposta = JOptionPane.showConfirmDialog(null, mensagem, "Convite para jogar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resposta == JOptionPane.YES_OPTION){
					client.enviarRespostaPositiva(jogoid, nomeJogador);
					if(!client.conectarEmJogo(jogoid)){
						imprimirMensagem("Houve um erro ao conectá-lo no jogo que foi convidado");
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
			imprimirMensagem("Não foi possível criar um novo jogo");
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
					//Não permite que o usuário entre em um jogo sem selecioná-lo antes
					if(lstJogos.getSelectedIndex() < 0){
						btnEntrarJogo.setEnabled(false);
						return;
					}
					String nomeJogo = lstJogos.getSelectedValue().toString();
					String strIdJogo = nomeJogo.substring(4,nomeJogo.indexOf(" "));//pega a string de ID do jogo com base no padrão "jogo##" (# = numero inteiro)
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
