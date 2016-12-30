package reparator;

import spoon.Launcher;
import spoon.reflect.factory.Factory;

/**
 * Created by jvdur on 11/01/2016.
 */
public class VersionSniper {
	
	public static int version = 0;
	
    private Launcher spoon;
    private int numero;
    private String pathToSource;

    {
        spoon = new Launcher();
    }

    /**
     * Constructor
     * @param projectPath
     * @param numero
     */
    public VersionSniper(String projectPath, String innerProjectPath, String classPath, int numero) {
        this.numero = numero;
        this.pathToSource = projectPath+'_'+numero+'/'+innerProjectPath;

        
		System.out.println("spoon sources "+pathToSource);
		System.out.println("with classPath = "+classPath);
		if ((classPath == null) || (classPath.isEmpty())) {
	        spoon.run(new String[]{
	        		"-i",pathToSource,
	        		"--output-type","nooutput"
	        });
		} else {
			spoon.run(new String[]{
		        		"-i",pathToSource,
		        		"--source-classpath",classPath,
		        		"--output-type","nooutput"
		     });
		}
    }

    public int getId(){
    	return this.numero;
    }
    
    public Factory getFactory(){
    	return this.spoon.getFactory();
    }

}
