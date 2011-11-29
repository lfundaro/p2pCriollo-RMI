import java.net.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 *Clase del cliente
 */
public class cliente{
    private static int  NULL_HASHID = 0xffffffff;
    private static int node_port = -1;
    private static String node = null;
    private static String download_path = null;
    private static ArrayList<Song> current_songs = new ArrayList<Song>();
    private static HashMap<String,Song> downloaded_songs = new
            HashMap<String,Song>();
    
    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        BufferedReader console = new BufferedReader
                (new InputStreamReader(System.in));
        boolean running = true;
        
        set_params(args);
        P2pProtocol stub = null;
        try {
            Registry registry = LocateRegistry.getRegistry(node, node_port);
            stub = (P2pProtocol) registry.lookup("P2pProtocol");
        } catch(RemoteException re) {
            System.out.println("Error: "+re);
            return;
        } catch(NotBoundException nbe) {
            System.out.println("Error: "+nbe);
        }
        System.out.println("Cliente listo para mandar ordenes");
        
        P2pRequest req = null;
        Random gen = new Random();
        String hash = null;
        try {
            while(running){
                
                String command = null;
                String ans = null;
                String[] resto;
                Song s = new Song();
                print_songs();
                
                try{
                    command = console.readLine();
                    command.trim();
                }
                catch(IOException e){
                    System.exit(1);
                }
                
                if(command.length() <= 0)
                    continue;
                
                switch(command.charAt(0)){
                    case 'C':
                    case 'c':
                        hash =  Integer.toString(gen.nextInt()) +
                                InetAddress.getLocalHost().hashCode() +
                                System.currentTimeMillis();
                        resto = command.split("\\s");
                        // Preparar cadena
                        if (resto.length > 1) {
                            String expr = parseSearchEntry(resto, 2);
                            if (expr.length() == 0) {
                                System.out.println("Comando malformado");
                                usage();
                                break;
                            }
                            // Búsqueda por autor ?
                            if (resto[1].compareTo("-a") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("A@@"+expr).getBytes());
                            }
                            // Búsqueda por título
                            else if (resto[1].compareTo("-t") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("T@@"+expr).getBytes());
                            }
                            // Búsqueda por bitRate
                            else if (resto[1].compareTo("-b") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("B@@"+expr).getBytes());
                            }
                            // Búsqueda por trackLength
                            else if (resto[1].compareTo("-tl") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("TL@@"+expr).getBytes());        
                            }
                            // Búsqueda por album
                            else if (resto[1].compareTo("-abm") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("ABM@@"+expr).getBytes());        
                            }
                            // Búsqueda por año
                            else if (resto[1].compareTo("-y") == 0) {
                                req = new P2pRequest(hash.hashCode(),
                                        ("Y@@"+expr).getBytes());  
                            }
                            // Búsqueda por compositor
                            else {
                                req = new P2pRequest(hash.hashCode(),
                                        ("C@@"+expr).getBytes());  
                            }
                        }
                        // Búsqueda de todos los archivos
                        else {
                            req = new P2pRequest(hash.hashCode(), 
                                    ("W@@").getBytes());
                        }
                        ans = stub.makeConsult(req);
                        current_songs = parse_songs(ans);
                        System.out.println("");
                        break;
                        
                    case 'A':
                    case 'a':
                        hash =  Integer.toString(gen.nextInt()) +
                                InetAddress.getLocalHost().hashCode() +
                                System.currentTimeMillis();
                        req = new P2pRequest(hash.hashCode(), "".getBytes());
                        ans = stub.makeReachable(req);
                        print_reachable(ans);
                        System.out.println("");
                        break;
                        
                    case 'D':
                    case 'd':
                        resto = command.split("\\s");
                        if(resto.length > 1){
                            int index = Integer.parseInt(resto[1]);
                            
                            if(index >= current_songs.size()){
                                System.out.println("La cancion con el id "
                                        +index+" no existe");
                                System.out.println("");
                                break;
                            }
                            
                            s = current_songs.get(index);
                            req = new P2pRequest(NULL_HASHID, 
                                    (s.title+"-"+s.creator).getBytes());
                            // Extraer datos del archivo MP3
                            FileOutputStream fos = new FileOutputStream
                                    (download_path+"/"+
                                    (s.title+"-"+s.creator)+".mp3");
                            byte[] res = stub.getSong(req);
                            fos.write(res);
                            fos.close();
                            
                            if(res != null){
                                Song ds = new Song();
                                ds.title   = s.title;
                                ds.creator = s.creator;
                                downloaded_songs.put(s.title+"-"+s.creator,ds);
                            }
                        }
                        else{
                            System.out.println("Comando Download malformado");
                        }
                        System.out.println("");
                        break;
                        
                    case 'P':
                    case 'p':
                        resto = command.split("\\s");
                        if(resto.length > 1){
                            int index = Integer.parseInt(resto[1]);
                            
                            if(index >= current_songs.size()){
                                System.out.println("La cancion con el id "
                                        +index+" no existe");
                                System.out.println("");
                                break;
                            }
                            
                            s = current_songs.get(index);
                            
                            if(downloaded_songs.get(s.title+"-"+s.creator) == 
                                    null){
                                req = new P2pRequest(NULL_HASHID, 
                                        (s.title+"-"+s.creator).getBytes());
                                FileOutputStream fos = new FileOutputStream
                                        (download_path+"/"+
                                        (s.title+"-"+s.creator)+".mp3");
                                byte[] res = stub.getSong(req);
                                fos.write(res);
                                fos.close();
                                
                                if(res != null){
                                    Song ds = new Song();
                                    ds.title   = s.title;
                                    ds.creator = s.creator;
                                    downloaded_songs.put(s.title+"-"+
                                            s.creator,ds);
                                }
                            }
                        }
                        else{
                            System.out.println("Comando Play malformado");
                        }
                        
                        try{
                            Runtime.getRuntime().exec(new String[]{"cvlc",
                                download_path+"/"+s.title+"-"+s.creator+".mp3"});
                        }
                        catch(IOException e){
                            System.out.println("I/O Error: "+e);
                        }
                        System.out.println("");
                        break;
                        
                    case 'Q':
                    case 'q':
                        running = false;
                        System.out.println("");
                        break;
                        
                    case 'h':
                    case 'H':
                        usage();
                        break;
                        
                    default:
                        System.out.println("Comando invalido");
                        System.out.println("");
                        break;
                }
            }
        } catch(RemoteException re) {
            System.out.println("Error: "+re);
            System.out.println("");
        } catch(UnknownHostException ue) {
            System.out.println("Error: "+ue);
            System.out.println("");
        } catch(IOException e) {
            System.out.println("Error I/O: "+e);
            System.out.println("");
        }
    }
    
    /**
     * Uso de comandos
     */
    private static void usage() {
        System.out.println("Opciones:");
        System.out.println("-a   Mostrar nodos alcanzables.");
        System.out.println("-c [-t | -a | -b | -tl | -abm |"
                + " -y] [expr] Consultar canciones en la red.");
        System.out.println("     -t expr Búsqueda por título "
                + "según expresión.");
        System.out.println("     -a expr Búsqueda por autor "
                + "según expresión.");
        System.out.println("     -b expr Búsqueda por bitRate "
                + "según expresión.");
        System.out.println("     -tl expr Búsqueda por "
                + "trackLength según expr.");
        System.out.println("     -abm expr Búsqueda por album "
                + "según expresión.");
        System.out.println("     -y expr Búsqueda por año "
                + "según expresión");
        System.out.println("-d num Descarga canción identi"
                + "ficada por num");
        System.out.println("-p num Reproduce canción identi"
                + "ficada por num. Si la canción no está en la"
                + " carpeta de descarga, entonces se descarga y"
                + " se reproduce.");
        System.out.println("");
    }
    
    /**
     * Devuelve el número de caracteres.
     * del título con más caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del título mas largo.
     */
    private static int longest_node_id(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).node_id.length();
            if (aux > max)
                max = aux;
        }
        return max;
    }

    /**
     * Devuelve el número de caracteres.
     * del título con más caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del título mas largo.
     */
    private static int longest_title(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).title.length();
            if (aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_creator(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).creator.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_album(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).album.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_duration(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).trackLength.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_year(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).year.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_composer(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).composer.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     * Devuelve el número de caracteres del autor con mas
     * caracteres en su nombre.
     * @param songs Canciones que serán analizadas.
     * @return Número de caracteres del autor mas largo.
     */
    private static int longest_bitrate(ArrayList<Song> songs){
        int max = 0;
        for (int i = 0; i < songs.size(); ++i){
            int aux = songs.get(i).bitRate.length();
            if ( aux > max)
                max = aux;
        }
        return max;
    }
    
    /**
     *
     * @param n Número a procesar.
     * @return Número de dígitos del número.
     */
    private static int number_of_digits(int n){
        if (n == 0)
            return 1;
        
        int digits = 0;
        
        while(n > 0){
            digits += 1;
            n = n/10;
        }
        
        return digits;
    }
    
    /**
     * Genera un String de espacios en blanco.
     * @param n Número de espacios en blanco.
     * @return String de espacios en blanco.
     */
    private static String tab(int n){
        String tab = "";
        for(int i = 0; i < n; ++i){
            tab += " ";
        }
        return tab;
    }
    
    /**
     * Genera un String de espacios en blanco.
     * @param n Número de espacios en blanco.
     * @return String de espacios en blanco.
     */
    private static void print_reachable(String r){
        if (r.length() == 0)
            System.out.println("No se encontraron nodos alcanzables");
        
        String rl[] = r.split("##");
        
        HashSet<String> s = new HashSet<String>();
        for(int i = 0; i < rl.length; ++i)
            s.add(rl[i]);
        
        Iterator<String> i = s.iterator();
        while(i.hasNext())
            System.out.println(i.next());
        
        return;
    }
    
    /**
     * Imprime las canciones que resultaron de la última consulta.
     */
    private static void print_songs(){
        if (current_songs.size() <= 0){
            return;
        }
	String num = "Num";
	String autor = "Autor";
	String title = "Título";
	String node = "Nodo";
	String album = "Album";
	String duration = "Duración";
	String year = "Year";
	String composer = "Compositor";
	String bitrate = "Bitrate";

        int d = max(number_of_digits(current_songs.size()),num.length());
        int ltitle = max(longest_title(current_songs),title.length());
        int lcreator = max(longest_creator(current_songs),autor.length());
        int lnode_id = max(longest_node_id(current_songs),node.length());
        int lalbum = max(longest_album(current_songs),album.length());
        int lduration = max(longest_duration(current_songs),duration.length());
        int lyear = max(longest_year(current_songs),year.length());
        int lcomposer = max(longest_composer(current_songs),composer.length());
        int lbitrate = max(longest_bitrate(current_songs),bitrate.length());

        int sp = 4;

        System.out.println(num+tab(d-num.length()+sp) + autor+tab(lcreator-title.length()+sp) +
			   title+tab(ltitle-title.length()+sp) + node+tab(lnode_id-node.length()+sp) +
			   album+tab(lalbum-album.length()+sp) + duration+tab(lduration-duration.length()+sp) +
			   year+tab(lyear-year.length()+sp) + composer+tab(lcomposer-composer.length()+sp) +
			   bitrate);
        for (int i = 0; i < current_songs.size(); ++i){
            String cre = current_songs.get(i).creator;
            String tit = current_songs.get(i).title;
            String nid = current_songs.get(i).node_id;
            String alb = current_songs.get(i).album;
            String dur = current_songs.get(i).trackLength;
            String yer = current_songs.get(i).year;
            String com = current_songs.get(i).composer;
            String bit = current_songs.get(i).bitRate;
            System.out.println(i + tab(d-number_of_digits(i)+sp)+
			       cre + tab(lcreator -cre.length()+sp)+
			       tit + tab(ltitle   -tit.length()+sp)+
			       nid + tab(lnode_id -nid.length()+sp)+
			       alb + tab(lalbum   -alb.length()+sp)+
			       dur + tab(lduration-dur.length()+sp)+
			       yer + tab(lyear    -yer.length()+sp)+
			       com + tab(lcomposer-com.length()+sp)+
			       bit + tab(lbitrate -bit.length()+sp));
        }
        
        return;
    }
    
    /**
     * Parsea el resultado del comando C.
     * @param ss resultado del comando C.
     * @return Canciones parseadas.
     */
    private static ArrayList<Song> parse_songs(String ss){
        ArrayList<Song> songs = new ArrayList<Song>();
        
        if(ss.length() <= 0){
            System.out.println("No se encontraron canciones");
            return songs;
        }
        
        String ss_strs[] = ss.split("##");
        
        for(int i = 0; i < ss_strs.length; ++i){
            songs.add(parse_song(ss_strs[i]));
        }
        
        return songs;
    }
    
    /**
     * Genera un objeto Song a partir de una entrada del resultado
     * del comando C.
     * @param s Canción a parsear.
     * @return Objeto Song con la información de la canción.
     */
    private static Song parse_song(String s){
        Song res = new Song();
        
        String song_data[] = s.split("@@");
        res.creator = song_data[0];
        res.title = song_data[1];
        res.location = song_data[2];
        res.node_id = song_data[3];
        
        return res;
    }
    
    /**
     * Parsea los argumentos del comando C.
     * @param resto contiene la información de la consulta.
     * @param startPoint índice desde donde se itera para extraer consulta.
     * @return Objeto Song con la información de la canción.
     */
    private static String parseSearchEntry(String[] resto, int startPoint) {
        if (resto.length < 3) return "";
        String expr = new String();
        int i = startPoint;
        for(i = startPoint; i < resto.length - 1; i++){
            expr += resto[i].toLowerCase();
            expr += " ";
        }
        expr += resto[i].toLowerCase();
        return expr;
    }
    
    /**
     * Parsea las opciones de la línea de comandos -p, -n y -d.
     * @param args Línea de comandos.
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
                case 'n':
                    node = args[i+1];
                    break;
                case 'd':
                    download_path = args[i+1];
                    break;
                default:
                    System.out.println("Opción incorrecta");
                    System.exit(1);
                    break;
            }
            i += 2;
        }
        
        //Si no se especifico el puerto o el nodo al cual
        //conectarse salir con error
        if((node_port == -1)||(node == null)){
            System.out.println("Uso: Cliente -p <puerto> -n <nodo> "
                    + "[-d <directorio de descargas>]");
            System.exit(1);
        }
        
        if(download_path == null)
            download_path = ".";
    }
    
    private static int max(int a, int b){
        if(a > b)
            return a;
        else
            return b;
    }
}