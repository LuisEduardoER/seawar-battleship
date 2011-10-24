package modelos;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

import Comunicacao.Constantes;
import Comunicacao.DicionarioMensagem;
import Comunicacao.IMessageListener;
import Comunicacao.MessageSender;
import Comunicacao.TipoMensagem;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Bot.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando,@Marcus
//
//

public class Bot extends Jogador implements Runnable{
	boolean bAcertouEmbarcacao = false;
	boolean bAfundouEmbarcacao = false;
	// pilha de navios que foram acertados mas n�o afundados
	Stack<Celula> naviosNaoAfundados = new Stack<Celula>();
	// Arrays de jogadas feitas para fins heuristicos de jogada
	ArrayList<Celula> celulasEscolhidas = new ArrayList<Celula>();
	ArrayList<Celula> celulasAtacadasEmbarcacao = new ArrayList<Celula>();
//	Servidor objServidor = null;
//	ServidorRestrito objServidorRestrito = null;
	IMessageListener objServidor;

	
	public Bot(IMessageListener server) {
		objServidor = server;
	}

	public Bot(Jogador objJogador,IMessageListener server) {
		this.setTabuleiroAtaque(objJogador.getTabuleiroAtaque());
		this.setTabuleiroDefesa(objJogador.getTabuleiroDefesa());
		this.conexaoJogador = objJogador.getConexao();
		objServidor = server;

	}

	public Celula getProximaCelula() {
		// return this.oProximaCelula;
		int x = getUltimaCelulaAtacadaEmbarcacao().x;
		int y = getUltimaCelulaAtacadaEmbarcacao().y;
		Celula p = new Celula(x, y);

		p.x = x;
		p.y = y - 1; // Ataca na c�lula � esquerda
		if (seJogadaValida(p) && seCapazAtirar(p)) {
			return p;
		}
		p.x = x;
		p.y = y + 1; // Ataca na c�lula � direita
		if (seJogadaValida(p) && seCapazAtirar(p)) {
			return p;
		}
		p.y = y;
		p.x = x + 1; // Ataca na c�lula abaixo
		if (seJogadaValida(p) && seCapazAtirar(p)) {
			return p;
		}
		p.y = y;
		p.x = x - 1; // Ataca na c�lula acima
		if (seJogadaValida(p) && seCapazAtirar(p)) {
			return p;
		}
		return null;
	}

	public boolean acertouEmbarcacao() {
		return bAcertouEmbarcacao;
	}

	public void acertouEmbarcacao(boolean bAcertouEmbarcacao) {
		this.bAcertouEmbarcacao = bAcertouEmbarcacao;
	}

	public boolean afundouEmbarcacao() {
		return bAfundouEmbarcacao;
	}

	public void afundouEmbarcacao(boolean bAfundouEmbarcacao) {
		this.bAfundouEmbarcacao = bAfundouEmbarcacao;
	}

	public Celula popNaviosNaoAfundados() {
		return naviosNaoAfundados.pop();
	}

	public void pushNaviosNaoAfundados(Celula celulaNavioParaAfundar) {
		naviosNaoAfundados.push(celulaNavioParaAfundar);
	}

	public Celula getUltimaEscolha() {
		if (!celulasEscolhidas.isEmpty())
			return celulasEscolhidas.get(celulasEscolhidas.size() - 1);
		else {
			return null;
		}
	}

	public void setCelulasEscolhida(Celula celulasEscolhida) {
		this.celulasEscolhidas.add(celulasEscolhida);
	}

	public Celula getUltimaCelulaAtacadaEmbarcacao() {
		if (!celulasAtacadasEmbarcacao.isEmpty())
			return celulasAtacadasEmbarcacao.get(celulasAtacadasEmbarcacao
					.size() - 1);
		else {
			return null;
		}
	}

	public void setCelulaAtacadaEmbarcacao(Celula celulaAtacadaEmbarcacao) {
		this.celulasAtacadasEmbarcacao.add(celulaAtacadaEmbarcacao);
	}

	public void analisarTabuleiro() {
		Celula atacada = null;
		if (bAcertouEmbarcacao && !bAfundouEmbarcacao) {
			analisarJogada();
			/*
			 * // Realiza ataque ap�s acertar a primeira c�lula de um navio if
			 * (celulasAtacadasEmbarcacao.size() < 2) { atacada =
			 * getProximaCelula(); if (atacada != null) { atacada.acertar();
			 * setCelulasEscolhida(atacada); } } else { // Ap�s acertar duas
			 * c�lulas do navio vai pegando a proxima at� // afundar o navio
			 * atacada = getCelula(); setCelulasEscolhida(atacada); }
			 */
		} else {
			if(!naviosNaoAfundados.empty()){
				bAcertouEmbarcacao = true;
				bAfundouEmbarcacao = false;
				atacada = popNaviosNaoAfundados();
				setCelulasEscolhida(atacada);
				//TODO:aqui ocorre um nulo quando h� 3 barcos junto e acerta 2 sem afund�-los
				//talvez resolve remontando a pilha a afundar
				if(getUltimaCelulaAtacadaEmbarcacao() != null){
					pushNaviosNaoAfundados(getUltimaCelulaAtacadaEmbarcacao());
					celulasAtacadasEmbarcacao.clear();
				}
				setCelulaAtacadaEmbarcacao(atacada);
				analisarJogada();
			}
			else{
			// Realiza tiro aleat�rio at� acertar um navio
			bAfundouEmbarcacao = false;	
			bAcertouEmbarcacao = false;
			atacada = new Celula(0, 0);
			atacada.x = coordRandom();
			atacada.y = coordRandom();
			while (seJogadaValida(atacada) && !seCapazAtirar(atacada)) {
				atacada = new Celula(0, 0);
				atacada.x = coordRandom();
				atacada.y = coordRandom();
			}
			atacada.acertar();
			setCelulasEscolhida(atacada);
			}
		}
	}

	public void analisarJogada() {
		Celula atacada = null;
		// Realiza ataque ap�s acertar a primeira c�lula de um navio
		if (celulasAtacadasEmbarcacao.size() == 1) {
			atacada = getProximaCelula();
			if (atacada != null) {
				atacada.acertar();
				setCelulasEscolhida(atacada);
			}
		} else {
			// Ap�s acertar duas c�lulas do navio vai pegando a proxima at�
			// afundar o navio
			atacada = getCelula();
			setCelulasEscolhida(atacada);
		}
	}

	public boolean seJogadaValida(Celula objCelula) {
		// Verifica se a pr�xima c�lula que ser� atacada � v�lida nas
		// coordenadas dispon�veis, ou seja
		// n�o excede os limites da matriz do tabuleiro, e n�o � menor que zero
		return (getTabuleiroAtaque().getMatrizCelula().length > objCelula.x
				&& getTabuleiroAtaque().getMatrizCelula()[0].length > objCelula.y
				&& objCelula.x >= 0 && objCelula.y >= 0);
	}

	private boolean seCapazAtirar(Celula objCelula) {
		return !getTabuleiroAtaque().seCelulaAtacada(objCelula);
	}
	
	@Override
	public void Atacar(int idJogo, int x, int y){
		//Tabuleiro tabuleiroAtaque = getTabuleiroAtaque();		
		//Celula cel = tabuleiroAtaque.encontrarCelula(x, y);
		
		//Como a c�lula n�o � processada aqui, pode-se jogar uma c�lula DUMMIE com apenas o X e o Y do local atacado
		String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.AtacarOponente);
		mensagem = String.format(mensagem, idJogo, x, y);
		StringTokenizer tokens = new StringTokenizer(mensagem,Constantes.TOKEN_SEPARATOR);
		objServidor.receberTokensMensagem(tokens, this.getConexao().socket);
	}
	
	public void atacar() {
		analisarTabuleiro();
		Celula celulaAtacar = getUltimaEscolha();
		int i = 0;
		int tamanhoPrimeiroNavioAtacado = 0;
		int tamanhoSegundoNavioAtacado = 0;
		while (celulaAtacar != null && i == 0) {
			i = 1;
			System.out.print((char) ((celulaAtacar.x) + 'A'));
			System.out.print(celulaAtacar.y + 1);
			System.out.println("");
			// this.setCelulasEscolhida(celulaAtacar);
			getTabuleiroAtaque().mMatrizCelula[celulaAtacar.x][celulaAtacar.y] = getTabuleiroAtaque()
					.atacar(celulaAtacar.x, celulaAtacar.y);
			// Envia o ataque chamando da classe conexao do m�todo na classe
			// Jogador
			
			if (getTabuleiroAtaque().mMatrizCelula[celulaAtacar.x][celulaAtacar.y].getTipoCelula() == TipoCelula.Embarcacao) {
				if(getUltimaCelulaAtacadaEmbarcacao() != null){
				tamanhoSegundoNavioAtacado = getTabuleiroAtaque().getEmbarcacao(celulaAtacar.x, celulaAtacar.y).getTamanho();
				tamanhoPrimeiroNavioAtacado = getTabuleiroAtaque().getEmbarcacao(getUltimaCelulaAtacadaEmbarcacao().x,getUltimaCelulaAtacadaEmbarcacao().y).getTamanho();
				}
				if ((tamanhoPrimeiroNavioAtacado != tamanhoSegundoNavioAtacado) && tamanhoSegundoNavioAtacado > 1) {
					pushNaviosNaoAfundados(celulaAtacar);
				} 
				else {
					setCelulaAtacadaEmbarcacao(celulaAtacar);
				}
				bAcertouEmbarcacao = true;
			}
			if (getTabuleiroAtaque().getEmbarcacao(celulaAtacar.x,celulaAtacar.y) != null && bAcertouEmbarcacao) {
				bAfundouEmbarcacao = getTabuleiroAtaque().getEmbarcacao(celulaAtacar.x, celulaAtacar.y).getNaufragado();
				if (bAfundouEmbarcacao) {
					// Limpo minha lista de celulasAtacadasEmbarcacao para
					// quando acertar outra embarca��o
					// o bot saber onde jogar
					checarRemover(getTabuleiroAtaque().getEmbarcacao(celulaAtacar.x, celulaAtacar.y).getListaCelulas());
					System.out.println("Voc� afundou o " + getTabuleiroAtaque().getEmbarcacao(celulaAtacar.x, celulaAtacar.y).getNomeEmbarcacao());
					System.out.println(provocar());
				}
			}
			this.Atacar(this.getJogoId(), celulaAtacar.x, celulaAtacar.y);
		}

	}

	private Celula getCelula() {
		int x = getUltimaCelulaAtacadaEmbarcacao().x;
		int y = getUltimaCelulaAtacadaEmbarcacao().y;
		Celula p = new Celula(x, y);
		if (direcaoAtaque()) {
			p.y = y - 1;
			if (seJogadaValida(p) && seCapazAtirar(p)) {
				p.acertar();
				setCelulasEscolhida(p);
				return p;
			} else {
				p.y = y + 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				}
				y = celulasAtacadasEmbarcacao.get(0).y;
				p.y = y + 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				}
				p.y = y - 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				} else {
					return null;
				}
			}
		} else {
			p.x = x - 1;
			if (seJogadaValida(p) && seCapazAtirar(p)) {
				p.acertar();
				setCelulasEscolhida(p);
				return p;
			} else {
				p.x = x + 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				}
				x = celulasAtacadasEmbarcacao.get(0).x;
				p.x = x + 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				}
				p.x = x - 1;
				if (seJogadaValida(p) && seCapazAtirar(p)) {
					p.acertar();
					setCelulasEscolhida(p);
					return p;
				} else {
					return null;
				}
			}
		}
	}
	public void checarRemover(Celula[] arrCelulas){
		for (int i = 0; i < arrCelulas.length; i++) {
			celulasAtacadasEmbarcacao.remove(arrCelulas[i]);
		}
	}

	// TODO: Associar esta provoca��o para enviar no chat(se tiver)
	public String provocar() {

		String[] listaProvocacoes = { 
				"Voc� n�o � p�reo para a minha ira!",
				"N�o ter� tr�gua este jogo!",
				"Achei que voc� seria um oponente formid�vel... que decep��o!",
				"Erm, acho que voc�... ah, deixa pra l�.",
				"Cuidado que eu to chegando!",
				"Virei, cruzei, atirei e... marquei!",
				"Os sete mares est�o com os dias contados",
				"Conhece o mar-morto? Foi eu quem ajudou a matar >:)" };

		Random rand = new Random();
		int indexProvocacao = rand.nextInt(listaProvocacoes.length);

		return listaProvocacoes[indexProvocacao];
	}

	private int coordRandom() {
		Random random = new Random();
		return random.nextInt(getTabuleiroAtaque().getMatrizCelula().length);
	}

	private boolean direcaoAtaque() {
		return (celulasAtacadasEmbarcacao
				.get(celulasAtacadasEmbarcacao.size() - 1).x == celulasAtacadasEmbarcacao
				.get(celulasAtacadasEmbarcacao.size() - 2).x);
	}

	@Override
	public void run() {
		this.atacar();
	}
}
