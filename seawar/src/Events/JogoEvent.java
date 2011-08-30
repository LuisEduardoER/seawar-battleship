package Events;

import java.util.EventObject;

public class JogoEvent extends EventObject {

	TipoEvento tipo;
	
	public JogoEvent(Object arg0, TipoEvento tipo) {
		super(arg0);
		this.tipo = tipo;
	}
	
	public TipoEvento getTipo(){
		return tipo;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
