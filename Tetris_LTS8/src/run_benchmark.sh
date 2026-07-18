#!/bin/bash
# run_benchmark.sh

echo "Compiling Java sources..."
javac *.java

echo "Starting Benchmark (1,000 Iterations)..."
CSV_FILE="benchmark_data.csv"

# Write headers
echo "Duration(ms),UsedMemory(KB),MaxMemory(KB),CPULoad" > $CSV_FILE

# Run headless iterations
for i in {1..1000}
do
   java Main --headless >> $CSV_FILE
   if [ $((i % 100)) -eq 0 ]; then
       echo "Completed $i / 1000 iterations..."
   fi
done

echo "Benchmark complete. Results saved to $CSV_FILE"