package Events;

import java.util.EventObject;

public class ClientEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TipoEvento tipo;
	public ClientEvent(Object arg0, TipoEvento evtTipo) {
		super(arg0);
		tipo = evtTipo;
	}

	public TipoEvento getTipo(){
		return tipo;
	}
	
}
