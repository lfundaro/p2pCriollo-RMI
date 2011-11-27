import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lorenzo Fundaró <lorenzofundaro [at] yahoo.com>
 */
public interface P2pProtocol extends Remote {
    
//    /**
//     * Obtiene los datos del pedido a partir de una conexión con el cliente.
//     * @param s Socket por donde se obtiene el pedido p2p.
//     * @return Objeto que contiene los datos del pedido.
//     */
//    P2pRequest getRequest(Socket s);
    
    /**
     * Ejecuta un comando C.
     * @param req Parámetros del comando C.
     * @param cs Socket de comunicación.
     */
    String makeConsult(P2pRequest req) throws RemoteException;
    /**
     * Genera un string representativo de la base de datos de canciones.
     * @param nodeID Identificador único del nodo.
     * @return String representativo de la base de datos de canciones.
     */
    String SongDbToString(String nodeID);
    
    /**
     * Ejecuta un comando A.
     * @param req Parámetros del comando A.
     * @param cs Socket de comunicación.
     */
    String makeReachable(P2pRequest req) 
            throws RemoteException;
    
    /**
     * Ejecuta un comando D del lado del servidor.
     * @param req Parámetros del comando D del lado del servidor.
     * @param cs Socket de comunicación.
     */
    byte[] getSong(P2pRequest req);
    
//    /**
//     * Permite descargar una canción desde un nodo de la red.
//     * @param req contiene información relevante sobre la conexión.
//     * @param download_path ruta donde se guarda el archivo luego de descargado.
//     * @param cs socket para establecer canal de comunicación con el servidor.
//     * @return indica falla o éxito en la conexión.
//     */
//    boolean requestSong(P2pRequest req, String download_path, Socket cs);
    
//    /**
//     * Envía una petición de consulta al nodo.
//     * @param req contiene información relevante sobre la conexión.
//     * @param cs socket para establecer canal de comunicación con el servidor.
//     * @return Resultado de la consulta. Puede contener las canciones de toda
//     * la red o las canciones que satisfacen el criterio de búsqueda
//     * especificado.
//     */
//    String requestConsult(P2pRequest req, Socket cs);
    
//    /**
//     * Envía una petición de "Nodos alcanzables" a un nodo.
//     * @param req contiene información relevante sobre la conexión.
//     * @param cs socket para establecer canal de comunicación con el servidor.
//     * @return Nodos alcanzables por el nodo al que se conecta este cliente.
//     */
//    String requestReachable(P2pRequest req, Socket cs);
}
