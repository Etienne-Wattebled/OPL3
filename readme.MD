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
 
