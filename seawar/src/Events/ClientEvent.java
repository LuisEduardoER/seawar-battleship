package Events;

import java.util.EventObject;

public class ClientEvent extends EventObject {

	TipoEvento tipo;
	
	public ClientEvent(Object arg0, TipoEvento tipo) {
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
