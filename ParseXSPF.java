import nanoxml.*;
import java.util.*;
import java.io.*;

/**
 * Parser XSPF
 */
public class ParseXSPF{
    /**
     * Parser de archivos XSPF.
     * @param filename Archivo XSPF que se debe parsear.
     * @return HashMap de la concatenación del título de la canción
     *         con el autor de la canción a objetos de tipo Song.
     */
    @SuppressWarnings("unchecked")
	public static HashMap<String,Song> parse(String filename){
	HashMap<String,Song> sl = new HashMap<String,Song>();
	boolean directory_library = false;
	String library_filename = null;
	File f = new File(filename);

	if(!f.exists()){
	    System.out.println("El directorio "+f.getAbsolutePath()+" no existe");
	    System.exit(1);
	}
	    
	if(!f.isDirectory()){
	    System.out.println("El archivo "+f.getAbsolutePath()+" no es un directorio");
	    System.exit(1);
	}

	boolean failed = false;
	if(!f.canExecute()){
	    System.out.println("No se tienen permisos de ejecución sobre "+f.getAbsolutePath());
	    failed = true;
	}
	if(!f.canRead()){
	    System.out.println("No se tienen permisos de lectura sobre "+f.getAbsolutePath());
	    failed = true;
	}
	if(failed){
	    System.exit(1);
	}

	library_filename = ParseMP3dir.parse(filename);
            
	XMLElement xspf = new XMLElement();
	FileReader reader = null;
	
	try{
	    reader = new FileReader(library_filename);
	    xspf.parseFromReader(reader);
	}
	catch(FileNotFoundException e){
	    System.out.println(e);
	    System.exit(1);
	}
	catch(IOException e){
	    System.out.println(e);
	    System.exit(1);
	}
	
	    
	if(xspf.getName().compareTo("playlist") == 0){
	    //Busco el elemento trackList
	    Enumeration<XMLElement> playlistContents = 
		xspf.enumerateChildren();
	    XMLElement trackList = null;
	    do{
		trackList = playlistContents.nextElement();
	    }while((trackList != null)&&
		   (trackList.getName().compareTo("trackList") != 0));

	    if(trackList == null){
		System.out.println("Error, lista de reproduccion mal"
				   + " formateada: "
				   + "No se encontro trackList");
		System.exit(1);
	    }

	    //Itero sobre las canciones
	    Enumeration<XMLElement> tracks = trackList.enumerateChildren();
	    while(tracks.hasMoreElements()){
		XMLElement track = tracks.nextElement();
		Song s = new Song();
		Enumeration<XMLElement> attrs = track.enumerateChildren();
		while(attrs.hasMoreElements()){
		    XMLElement attr = attrs.nextElement();
		    get_xspf_attr(attr,s);
		}
		sl.put(s.title+"-"+s.creator,s);
	    }
	}
	else{
	    System.out.println("Error, lista de reproduccion mal "
			       + "formateada: "
			       + "No se encontró elemento playlist en el tope "
			       + "del árbol");
	    System.exit(1);
	}
	
	// if(directory_library){
	//     (new File(library_filename)).delete();
	// }

	return sl;
    }

    /**
     * Escribe un atributo de una canción almacenado en el archivo
     * xspf en un objeto Song.
     * @param attr Atributo XSPF.
     * @param s Objeto Song.
     */
    public static void get_xspf_attr(XMLElement attr, Song s){
	String attr_name = attr.getName();
        
	if (attr_name.compareTo("location") == 0) {
	    s.location = attr.getContent(); 
        }
	else if (attr_name.compareTo("title") == 0){
	    s.title = attr.getContent();
	}
	else if (attr_name.compareTo("creator") == 0){
	    s.creator = attr.getContent();
	}
	else if (attr_name.compareTo("album") == 0){
	    s.album = attr.getContent();
	}
	else if (attr_name.compareTo("duration") == 0){
	    s.trackLength = attr.getContent();
	}
	else if (attr_name.compareTo("year") == 0){
	    s.year = attr.getContent();
	}
	else if (attr_name.compareTo("genre") == 0){
	    s.genre = attr.getContent();
	}
	else if (attr_name.compareTo("bitrate") == 0){
	    s.bitRate = attr.getContent();
	}
    }
}
