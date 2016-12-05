#!/bin/sh

#---------------------------------------#
#	Author		Jean-Vital Durieu		#
#	last update	2016-01-06				#
#	versions 	0.1				 		#
#---------------------------------------#
#
#	USAGE
#
#	filename [dir source] [nbr of history] [branch]
#
#---------------------------------------



###
###	BEGIN
###

# Ecrire les commitversion dans un fichier
cd $1
git log --oneline > gitlog.txt
cd ..

echo "[INFO] log export ok"


commitId="";
# parser les versions
cpt=0;

# On parcours le fichier pour trouver les codes de commit
cd $1
while read -r line
do 

	echo "[INFO] clone $cpt"

	# lit la ligne	
  	tmpv=$($line | cut -d" " -f1)
  	commitId=$tmpv;

	# clone le rep
	echo "clone directory"
	git clone -l -s -n . "../$1_$cpt"
	cd ..
	cd "$1_$cpt"
	git checkout $3
	git checkout commitId
	cd ../$1

	# si max on sort
	cpt=$((cpt+1));
	if [ $cpt = $2 ]
	  then
	    break;
	fi

done < gitlog.txt

echo "[INFO] fin du process"

