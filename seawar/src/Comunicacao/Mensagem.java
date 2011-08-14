package Comunicacao;

public class Mensagem {

	String ipTo;
	String ipFrom;
	String message;
	TipoMensagem tipo;
	
	public Mensagem(String ipDe, String ipPara, TipoMensagem tipo){
		ipTo = ipPara;
		ipFrom = ipDe;
		this.tipo = tipo;
		
		message = DicionarioMensagem.GerarMensagemPorTipo(tipo);
	}
	

	public void ReceberParametros(Object...args){
		message = String.format(message, args);
	}
}
