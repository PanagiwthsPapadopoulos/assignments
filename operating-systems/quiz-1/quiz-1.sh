AEM=10697

while [ ${#AEM} -gt 1 ]; do
    sum=0
    for (( i=0; i<${#AEM}; i++ )); do
        sum=$((sum + ${AEM:i:1}))
    done
    AEM=$sum
done

N=$AEM

randomword=$(cat /dev/urandom | tr -dc 'a-z' | fold -w $((6 + RANDOM % 3)) | head -n 1)

mkdir -p /home/$USER/quiz-1

file_count=$((10 + N))
for i in $(seq 1 $file_count); do
    filename="/home/$USER/quiz-1/${randomword}-${i}.txt"
    touch "$filename"

    for (( j=0; j < (N + 10); j++ )); do
        random_word=$(shuf -e super spider bat -n 1)
        echo "$random_word" >> "$filename"
    done
done

report_file="/home/$USER/quiz-1/report.txt"
> "$report_file"
for i in $(seq 1 $file_count|sort --version-sort); do
    filename="/home/$USER/quiz-1/${randomword}-${i}.txt"
    super_count=$(grep -o "super" "$filename" | wc -l)
    spider_count=$(grep -o "spider" "$filename" | wc -l)
    bat_count=$(grep -o "bat" "$filename" | wc -l)

    echo "$filename" >> "$report_file"
    echo "$super_count super" >> "$report_file"
    echo "$spider_count spider" >> "$report_file"
    echo "$bat_count bat" >> "$report_file"
    echo "" >> "$report_file"
done