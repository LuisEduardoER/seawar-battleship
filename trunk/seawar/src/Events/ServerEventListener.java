package Events;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

//Define uma lista de eventos que podem ocorrer no servidor
//para atualizar a GUI
public interface ServerEventListener extends EventListener {
	public void playerListChanged(ServerEvent evt);

	public void gamesListChanged(ServerEvent evt);

	public void updateDisplay(ServerEvent evt);
}
