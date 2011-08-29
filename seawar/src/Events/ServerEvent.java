package Events;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.EventObject;


public class ServerEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TipoEvento tipo;

	public ServerEvent(Object source, TipoEvento tipo) {
		super(source);
		this.tipo = tipo;
		
	}

	public TipoEvento getTipo() {
		return tipo;
	}

}
