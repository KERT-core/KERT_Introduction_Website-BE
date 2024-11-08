echo "wait mysql server"
dockerize -wait tcp://kiw_mysql:3306 -timeout 20s

echo "start spring boot server"
java -jar /app.jar 
