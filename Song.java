/**
 * Abstracci�n de canci�n
 */
public class Song{
    /**
     *Identificador �nico del nodo que tiene la canci�n
     */
    public String node_id;

    /**
     *Localizaci�n de la canci�n
     */
    public String location;

    /**
     *T�tulo de la canci�n
     */
    public String title;

    /**
     *Creador de la canci�n
     */
    public String creator;
    
    /**
     * 
     * @param l Localizaci�n de la canci�n
     * @param t T�tulo de la canci�n
     * @param c Creador de la canci�n
     * @param n Identificador �nico del nodo que tiene la canci�n
     */
    public Song(String l, String t, String c, String n){
	location = l.toLowerCase();
	title = t.toLowerCase();
	creator = c.toLowerCase();
	node_id = n;
    }

    /**
     *Constructor por defecto
     */
    public Song(){}
    
    /**
     * Retorna una representaci�n en String de esta canci�n
     * @return String representativo de la canci�n
     */
    @Override
    public String toString() {
        String resp = creator+"@@"+title;
        return resp;
    }
            
}