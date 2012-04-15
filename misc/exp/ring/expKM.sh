#!/bin/bash
cd /home/users/mohamed2/JikesRVM/dist/test2
c=50000
regex='.*TIME=([0-9]+).*'
        for j in 2  4 8 16 32 48;
        do
	   echo -ne $j , >> mineK.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 350 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -X:hydra:XXLLxxii$c -X:hydra:package=jstamp jstamp.KMeans.KMeans -m 40 -n 40 -t 0.005 -i jstamp/KMeans/inputs/random-n16384-d24-c16.txt -nthreads $j) 2>&1)
                        case $result in
                                *"Excepsfdsdtion"*) good=0;;
                                *"Segmentation fault"*) good=0;;
                                *"internal error"*) good=0;;
                                *"JikesRVM"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> mineK.csv
	   done
	   echo >> mineK.csv
	   echo -ne $j , >> deuceK.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 350 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.ringstm.Context -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar jstamp.KMeans.KMeans -m 40 -n 40 -t 0.005 -i jstamp/KMeans/inputs/random-n16384-d24-c16.txt -nthreads $j) 2>&1)
                        case $result in
                                *"Exceptidfgon"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> deuceK.csv
	   done
	   echo >> deuceK.csv
        done

        for j in 2  4 8 16 32 48;
        do
	   echo -ne $j , >> mineK.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 350 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -X:hydra:XXLLxxii$c -X:hydra:package=jstamp jstamp.KMeans.KMeans -m 15 -n 15 -t 0.005 -i jstamp/KMeans/inputs/random-n16384-d24-c16.txt -nthreads $j) 2>&1)
                        case $result in
                                *"Exceptifdfdon"*) good=0;;
                                *"Segmentation fault"*) good=0;;
                                *"internal error"*) good=0;;
                                *"JikesRVM"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> mineK.csv
	   done
	   echo >> mineK.csv
	   echo -ne $j , >> deuceK.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 350 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.ringstm.Context -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar jstamp.KMeans.KMeans -m 15 -n 15 -t 0.005 -i jstamp/KMeans/inputs/random-n16384-d24-c16.txt -nthreads $j) 2>&1)
                        case $result in
                                *"Excepifdoiotion"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
               echo -ne ${BASH_REMATCH[1]},  >> deuceK.csv
	   done
	   echo >> deuceK.csv
        done
