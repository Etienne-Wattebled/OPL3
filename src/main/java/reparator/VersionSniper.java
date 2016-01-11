package reparator;

import spoon.Launcher;

/**
 * Created by jvdur on 11/01/2016.
 */
public class VersionSniper {

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

        spoon.run(new String[]{"-i",pathToSource,"--source-classpath",classPath});
    }

}
