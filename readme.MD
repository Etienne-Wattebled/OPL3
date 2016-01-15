Johan Dufaux
Jean-Vital Durieu



### étape définie basiquement

-Générer un dossier de projet par commit (un nombre de commit passé en parametre?)
  => proposition pour début: script bash (executé dans java?)
  => proposition: si on execute le script depuis java, on pourrait peut être récuperer en java la liste des projets généré pour l'exploiter plus facilement. comme ça l'utilisateur lance le programme java avec un nom de projet et le nombre de commit et on gère derrière le reste. Idée comme ça car je ne sais pas comment on pourrait le faire.

-Spooner chaque projet (un launcher par projet?)
  => on part sur une convention, on transmet au programme java le nom du dossier, 

-créer/spooner un new projet (à partir du dernier commit par exemple)

-copier et renommer chaque fonction de chaque projet (foo du dernier commit = foo1, foo de l'avant dernier = foo2 ...)
  => si pas de modif osef pour l'instant. on fera la comparaison apres pour éviter les fonctions identiques? 

-création de la fonction d'appel avec des try catch throwable (XX try catch imbriqué? Ou appel recursif?)
  => pb est: si une fonction peut throw une exception gérée proprement par le programme, il faudrait la laisser passer. 
  => proposition: remonter tous les appels de fonctions pour voir si l'exception catchée est d'un type catché au dessus. Si oui, on la laisse passer (on la re-throw dans le catch?)
    => (ça serait cool si on pouvait gérer ça à l'execution pour remonter qu'une branche de l'arbre et pas tous les appels possible mais aucune idée).



==> Créer une fonction qui, pour un nom de fonction passé en parametre (et un sniper), retourne la liste des exceptions à ne pas catcher. 


Proc(snipers)

execute(CtClass c){

pour ctmethod in ctclass :
   ctmethod[] methods;
   pour sniper in snipers :
     ctmethods.add(creerFonctionDeVersion(ctmethod, sniper))
   end pour

   creerFonctionDappel(ctmethod, ctmethods);
end pour.
}



creerFonctionDeVersion(ctmethod, sniper){

cherche dans sniper si on a une ctmethod qui correspond à ctmethod
cad 
  même nom
  même type de retour
  même parametres
  même exceptions
}




creerFonctionDappel(ctmethod, ctmethods){
   create b1 = block vide de ctmethod
   pour meth in ctmethods :
      add try catch in b1
      add return appels de meth in try
      add continue in catch
   end pour
}


========================================================================================
nouvelle version souhaitée (inspiré de metamutator)

Ex fonction d'appel:

Class A {

[...]
public static int bar_version = 0;
public static int bar_version_max = 2;

bar(){
  switch bar_version : {
     0 :
       bar_0();
     1 : 
       bar_1();
     2 :
       bar_2();
     default:
       ?? (bar_0?)
    }
  }
}





TO DO:

essayer le programme sur un gros projet (genre jsoup ou spoon ou autre).

faire tout le junit runner. C'est dur donc bon... Voir si on peut s'inspirer du metamutator, ou du github de max ou bien d'antoine. 

Le concept serait de faire en pseudo code:

Loader les class du projet spooner
Loader tous les tests du projet spooner
  pour chaque classe du projet spooner
    pour chaque methode de la classe
      vfield = clazz.getField(methodz.getName()+"_version");
      vmaxfield = clazz.getField(methodz.getName()+"_version");
      while(vfield.getInt(null) < vmaxfield.getInt(null)){
        runtest();
        Analyse les resultats (tout vert? tout rouge?)
        vfield = vfield.getInt + 1;
      }



Vla pour la théorie mais j'ai jamais fait et je sais pas ce que ça peut donner. 
      
 









