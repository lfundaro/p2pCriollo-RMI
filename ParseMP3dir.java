import java.util.*;
import java.io.*;
import nanoxml.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;

/**
   Parser de la librería Mp3
*/
public class ParseMP3dir{

    private static void printStuff(ID3v24Tag tag, MP3AudioHeader header){
	System.out.println("TITLE: "+tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
	System.out.println("ARTIST: "+tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
	System.out.println("ALBUM: "+tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
	System.out.println("YEAR: "+tag.getFirst(ID3v24Frames.FRAME_ID_YEAR));
	System.out.println("COMMENT: "+tag.getFirst(ID3v24Frames.FRAME_ID_COMMENT));
	System.out.println("COMPOSER: "+tag.getFirst(ID3v24Frames.FRAME_ID_COMPOSER));
	System.out.println("GENRE: "+tag.getFirst(ID3v24Frames.FRAME_ID_GENRE));
	System.out.println("LANGUAGE: "+tag.getFirst(ID3v24Frames.FRAME_ID_LANGUAGE));
	System.out.println("MOOD: "+tag.getFirst(ID3v24Frames.FRAME_ID_MOOD));
	System.out.println("LENGTH: "+header.getTrackLengthAsString());
	System.out.println("BITRATE: "+header.getBitRate());
	System.out.println();
    }

    /**
     * Añade una canción al árbol XSPF.
     * @param f mp3 a analizar
     * @param tag Tag IDv2.4 del archivo mp3.
     * @param header Frames IDv2.4 con información específica de MP3.
     * @param trackList Elemento XML en donde se agregaran las canciones.
     */
    private static void addSong(File f, ID3v24Tag tag, MP3AudioHeader header,
				XMLElement trackList){

	XMLElement song = new XMLElement();
	song.setName("track");

	XMLElement attr = null;

	attr = genAttr("location");
	attr.setContent(f.getAbsolutePath());
	song.addChild(attr);

	attr = genAttr("title");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
	song.addChild(attr);
	
    	attr = genAttr("creator");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
	song.addChild(attr);

    	attr = genAttr("album");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
	song.addChild(attr);

    	attr = genAttr("duration");
	attr.setContent(header.getTrackLengthAsString());
	song.addChild(attr);

	/*********** Not in the XSPF definition ***********/
    	attr = genAttr("year");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_YEAR));
	song.addChild(attr);

    	// attr = genAttr("composer");
	// attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_COMPOSER));
	// song.addChild(attr);

    	attr = genAttr("genre");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_GENRE));
	song.addChild(attr);

    	attr = genAttr("bitrate");
	attr.setContent(header.getBitRate());
	song.addChild(attr);

	trackList.addChild(song);
    }

    /**
     * Genera un elemento XML con nombre 'name'
     * @param name Nombre del elemento XML
     * @return Elemento XML generado
     */
    private static XMLElement genAttr(String name){
	XMLElement attr = new XMLElement();
	attr.setName(name);
	return attr;
    }

    /**
     * Parseador del directorio con la biblioteca MP3
     * @param lib Directorio con los archivos mp3 a parsear
     * @return Nombre del archivo XSPF generado
     */
    public static String parse(String lib){
        System.out.println("BLA");
	File xspf = null;

	XMLElement playlist  = new XMLElement();
	playlist.setName("playlist");

	XMLElement trackList = new XMLElement();
	trackList.setName("trackList");

	playlist.addChild(trackList);

	LinkedList<File> dirs = new LinkedList<File>();
	//Añadimos el directorio inicial
	File initial_dir = new File(lib);
	System.out.println(initial_dir.getAbsolutePath()+" no existe");
	if(!initial_dir.exists()){
	    System.out.println(initial_dir.getAbsolutePath()+" no existe");
	    System.exit(1);
	}
	    
	if(!initial_dir.isDirectory()){
	    System.out.println(initial_dir.getAbsolutePath()+" no es un directorio");
	    System.exit(1);
	}

	boolean failed = false;
	if(!initial_dir.canExecute()){
	    System.out.println("No se tienen permisos de ejecución sobre "+initial_dir.getAbsolutePath());
	    failed = true;
	}
	if(!initial_dir.canRead()){
	    System.out.println("No se tienen permisos de lectura sobre "+initial_dir.getAbsolutePath());
	    failed = true;
	}
	if(failed){
	    System.exit(1);
	}

	dirs.add(initial_dir);

	while(dirs.size() > 0){
	    File d = dirs.remove();
	    File[] fls = d.listFiles();
	    for(File f: fls){
                System.out.println(f.toString());
		if(f.isDirectory()){
		    dirs.add(f);
		}
		else{
		    MP3File mp3 = null;
		    try{
			mp3 = (MP3File)AudioFileIO.read(f);
		    }
		    catch(Exception e){}
		    if((mp3 != null)&&(mp3.hasID3v2Tag())){
			ID3v24Tag tag = mp3.getID3v2TagAsv24();
			MP3AudioHeader header = mp3.getMP3AudioHeader();
			    
			printStuff(tag,header);
			addSong(f,tag,header,trackList);
		    }
		    else{
			System.out.println("El archivo "+f+" no tiene un tag ID3v2\n");
		    }
		}
	    }
	}

	try{
	    xspf = File.createTempFile("libreria",".xspf",initial_dir);
	    FileWriter xspfWriter = new FileWriter(xspf);
	    xspfWriter.write(playlist.toString());
            System.out.println(playlist.toString());
	    xspfWriter.flush();
	    xspfWriter.close();
	}
	catch(Exception e){
	    System.out.println(e);
	    System.exit(1);
	}

	return xspf.getAbsolutePath();
    }    
}
