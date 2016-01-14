package reparator;

import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;

import java.util.List;

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

        spoon.run(new String[]{"-i",pathToSource,"--source-classpath",classPath});
    }

    public int getId(){
    	return this.numero;
    }
    
    public Factory getFactory(){
    	return this.spoon.getFactory();
    }

    
    /**
     * Permet de rechercher et récupérer la structure d'une methode
     * @param signature
     * @return
     */
    public List rechercheMethode(final String signature) {

        /*CompilationUnit compileUnit = sp.getCompilationUnit();

        List<CtElement> elements = element.getElements(new Filter<CtElement>() {
            public boolean matches(CtElement element) {
                return element.getSignature() == signature;
            }
        });

        return elements;*/
    	return null;
    }

}
