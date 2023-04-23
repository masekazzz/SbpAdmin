cd /home/ci-sbp/course_project
git checkout main
git pull
mvn spring-boot:stop
mvn spring-boot:start &> logs/log.txt