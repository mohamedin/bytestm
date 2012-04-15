#!/bin/bash
cd /home/users/mohamed2/JikesRVM_2/dist/test2
c=10000
regex='.*TIME=([0-9]+).*'
        for j in 2  4 8 16;
        do
	   echo -ne $j , >> mineG.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -Xmx1024m -Xms1024m -X:hydra:XXLLxxii$c -X:hydra:package=jstamp jstamp.genome.Genome -g 256 -s 16 -n 100000 -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *"Segmentation fault"*) good=0;;
                                *"internal error"*) good=0;;
                                *"JikesRVM_2"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> mineG.csv
	   done
	   echo >> mineG.csv
	   echo -ne $j , >> deuceG.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.tl2mam2.Context -Xmx1024m -Xms1024m -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar jstamp.genome.Genome -g 256 -s 16 -n 100000 -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> deuceG.csv
	   done
	   echo >> deuceG.csv
        done

