javac main/RestaurantSimulation.java
for i in $(seq 1 1000)
do
echo -e "\nRun n.o " $i
java main.RestaurantSimulation
done
