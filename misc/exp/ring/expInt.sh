#!/bin/bash
cd /home/users/mohamed2/JikesRVM/dist/test2
c=13000
regex='.*TIME=([0-9]+).*'
        for j in 2  4 8 16 32 48;
        do
	   echo -ne $j , >> mineI.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -Xms1024m -Xmx1024m -X:hydra:XXLLxxii$c -X:hydra:package=jstamp jstamp.intruder.Intruder -a 10 -l 128 -n 5038 -s 1 -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *"Segmentation fault"*) good=0;;
                                *"internal error"*) good=0;;
                                *"JikesRVM"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> mineI.csv
	   done
	   echo >> mineI.csv
	   echo -ne $j , >> deuceI.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 120 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.ringstm.Context -Xms1024m -Xmx1024m -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar jstamp.intruder.Intruder -a 10 -l 128 -n 5038 -s 1 -t $j) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> deuceI.csv
	   done
	   echo >> deuceI.csv
        done

