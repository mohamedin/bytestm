#!/bin/bash
cd /home/users/mohamed2/JikesRVM_2/dist/test2
c=16000
regex='.*TIME=([0-9]+).*'
        for j in 2  4 8 16 32 48;
        do
	   echo -ne $j , >> mine3D.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -Xms1024m -Xmx1024m -X:hydra:XXLLxxii$c -X:hydra:package=jstamp jstamp.Labyrinth3D.Labyrinth -i jstamp/Labyrinth3D/inputs/random-x32-y32-z3-n96.txt -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *"Segmentation fault"*) good=0;;
                                *"internal error"*) good=0;;
                                *"JikesRVM_2"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.txt
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> mine3D.csv
	   done
	   echo >> mine3D.csv
	   echo -ne $j , >> deuce3D.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.tl2mam2.Context -Xms1024m -Xmx1024m -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar jstamp.Labyrinth3D.Labyrinth -i jstamp/Labyrinth3D/inputs/random-x32-y32-z3-n96.txt -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;

                                *) good=1;;
                        esac
                done
                echo $result >> roow.txt
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> deuce3D.csv
	   done
	   echo >> deuce3D.csv
       done

