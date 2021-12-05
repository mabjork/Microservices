for i in "$@"
do
  if [ "$i" = "-c" ]
  then
    buildContainers=true
  fi

  if [ "$i" = "-DskipTest" ]
  then
    skipTests=true
  fi
done

while read service; do
  arguments="-q"

  if [[ $skipTests = true ]]
  then
    arguments=$arguments" -DskipTest"
  fi

  cd ./$service
  mvn package "$arguments"
  cd ..

done < services

if [[ $buildContainers = true ]]
then
  docker-compose up
fi
