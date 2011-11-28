import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Clase del servidor.
 */
public class nodo {
    private static int node_port;
    private static String node_id;
    private static String music_library_filepath;
    private static String known_nodes_filepath;
    //public static Thread mainThread;
    
    /**
     * 
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) throws RemoteException {
        //mainThread = Thread.currentThread();
        
        //Parseo de parámetros
        set_params(args);
        
        try{
            // Crear P2pProtocolHandler genérico 
            P2pProtocolHandler genericHandler = 
                    new P2pProtocolHandler(known_nodes_filepath,	   
                            music_library_filepath, node_id, node_port);
            P2pProtocol stub = (P2pProtocol) 
                    UnicastRemoteObject.exportObject(genericHandler, 0);
            
            // Se hace bind del objeto remoto en el registro.
            Registry registry = LocateRegistry.getRegistry(node_port);
            registry.bind("P2pProtocol", stub);
            
	    System.out.println("Servidor "+node_id+" listo para recibir "
                    + "ordenes");
        }
        catch(AlreadyBoundException abe) {
            System.out.println(": "+abe);
        }
        catch(IOException e){
	    System.out.println("I/O Error: "+e);
	}
    }
    
    /**
     * Parsea los argumentos de la línea de comandos.
     * @param args Argumentos de la línea de comandos.
     */
    private static void set_params(String args[]){
        char op = '\0';
        int i = 0;
        
        while( i < args.length ){
            op = args[i].charAt(1);
            switch(op){
                case 'p':
                    node_port = Integer.parseInt(args[i+1]);
                    break;

                case 'c':
                    known_nodes_filepath = args[i+1];
                    break;

                case 'b':
                    music_library_filepath = args[i+1];
                    break;

                case 'i':
                    node_id = args[i+1];
                    break;

                default:
                    System.out.println("Opcion incorrecta");
                    System.exit(1);
                    break;
            }
            i += 2;
        }
    }
}
