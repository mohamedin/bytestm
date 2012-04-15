#!/bin/bash
cd /home/users/mohamed2/JikesRVM/dist/test2
c=70000
regex='.*Nb iterations        = ([0-9]+).*'
for i in 20 50 80 100;
do
       echo $i >> mineSL.csv
       echo $i >> deuceSL.csv
        for j in 2 4 8 16 32 48;
        do
	   echo -ne $j , >> mineSL.csv
	   for k in `seq 1 10`
	   do
                good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 60 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm -X:hydra:XXLLxxii$c -X:hydra:package=org.deuce.benchmark org.deuce.benchmark.Driver -n $j org.deuce.benchmark.intset.Benchmark  SkipList -w $i) 2>&1)
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
                echo -ne ${BASH_REMATCH[1]},  >> mineSL.csv
	   done
	   echo >> mineSL.csv
	   echo -ne $j , >> deuceSL.csv
	   for k in `seq 1 10`
	   do
               good=0
                until [  $good -eq 1 ]; do
			let "c=$c+1"
			sleep 60 && ps aux | grep XXLLxxii$c | grep -v grep | awk '{print $2}'|xargs kill -9 &
                        result=$((../BaseBaseMarkSweep_x86_64-linux/rvm  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.ringstm.Context -X:hydra:XXLLxxii$c -cp deucebench_stm.jar:deuceAgent-1.3.0.jar org.deuce.benchmark.Driver -n $j  org.deuce.benchmark.intset.Benchmark  SkipList -w $i) 2>&1)
                        case $result in
                                *"Exception"*) good=0;;
                                *) good=1;;
                        esac
                done
                echo $result >> roow.csv
		[[ $result =~ $regex ]]
                echo -ne ${BASH_REMATCH[1]},  >> deuceSL.csv
	   done
	   echo >> deuceSL.csv
        done
done    
