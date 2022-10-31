#function to kill multiple monitors 
startingPort=5000
for i in $(seq 5)
do
	j=$((i + startingPort));
	echo "Killing Vital Monitor: CICU_$i at port: $j"
	npx kill-port $j
done