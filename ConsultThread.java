
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * Se utiliza para hacer consultas en modo broadcast. 
 * El nodo que recibe una consulta, itera sobre la lista 
 * de nodos conocidos. En vez de tener que esperar por cada 
 * nodo vecino a que mande su respuesta, la clase ConsultThread
 * permite crear concurrentemente todas las conexiones a todos 
 * los nodos vecinos.
 */
public class ConsultThread extends Thread {
    int pos;
    String[] result;
    String host;
    P2pRequest req;
    String op;
    
    /**
     * Constructor.
     * @param i posición del arreglo respuesta que le pertenece a este hilo. 
     * @param respuesta arreglo de respuesta. El hilo escribirá su respuesta 
     * en la casilla que le corresponda.
     * @param ip dirección IP para establecer la conexión con el nodo vecino.
     * @param req petición. Contiene la información relevante sobre la consulta:
     * código de operación, hash identificador, etc.
     * @param port puerto al cual se debe conectar el nodo.
     * @param pHandler maneja la conexión con el nodo vecino.
     */
    public ConsultThread(int i, String[] respuesta, String host,
			 P2pRequest req, String op) {
        pos = i;
        result = respuesta;
        this.host = host;
        this.req = req;
        this.op = op;
    }
   
   /**
    * Abre y cierra la conexión con el nodo vecino.
    */
   @Override
   public void run() {
       // Crear conexión con el servidor vecino
       try {
           P2pProtocol stub = null;
           String ans = null;
           try {
               Registry registry = LocateRegistry.getRegistry(host);
               stub = (P2pProtocol) registry.lookup("P2pProtocol");
           } catch(RemoteException re) {
               System.out.println("Error: "+re);
               return;
           } catch(NotBoundException nbe) {
               System.out.println("Error: "+nbe);
           }
           
           if (op.matches("makeConsult")) {
               ans = stub.makeConsult(req);
           } else if (op.matches("makeReachable")) {
               ans = stub.makeReachable(req);
           }
            result[pos] = ans;
        }
        catch(IOException e) {
            result[pos] = "";
        }
    }
}
