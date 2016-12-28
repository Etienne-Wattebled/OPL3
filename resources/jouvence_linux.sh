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
cwd=$(pwd)
cd $1
git log --oneline > gitlog.txt
cd $cwd


echo "[INFO] log export ok"
pwd

commitId="";
# parser les versions
cpt=0;

# On parcours le fichier pour trouver les codes de commit
cd $1
while read -r line
do 

	echo "[INFO] clone $cpt"

	# lit la ligne

  	tmpv=$(echo $line | cut -d" " -f 1)
  	commitId=$tmpv;

	# clone le rep
	echo "clone directory"
	git clone -l -s -n . "$cwd/$1_$cpt"
	cd $cwd
	cd "$1_$cpt"
	git checkout $3
	git checkout $commitId
	cd $cwd/$1

	# si max on sort
	cpt=$((cpt+1));
	if [ $cpt = $2 ]
	  then
	    break;
	fi

done < gitlog.txt

echo "[INFO] fin du process"

